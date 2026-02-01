import { ReactNode, useState, useRef, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import {
    Store, Bell, User, LogOut, Settings, Wallet, Gem,
    LayoutDashboard, LineChart, CandlestickChart,
    Menu, Search, Sun, Moon, PanelLeftClose, PanelLeftOpen
} from 'lucide-react';
import { useTheme } from '../hooks/useTheme';
import { useUser } from '../hooks/useUser';
import { useQueryClient } from '@tanstack/react-query';


interface DashboardLayoutProps {
    children: ReactNode;
}

export function DashboardLayout({ children }: DashboardLayoutProps) {

    const [isCollapsed, setIsCollapsed] = useState(false);
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);
    const location = useLocation();
    const navigate = useNavigate();
    const { theme, toggleTheme } = useTheme();
    const { data: user, isLoading } = useUser();
    const userAlias = user?.alias || 'Użytkownik';
    const fullName = user ? `${user.name} ${user.surname}` : userAlias;
    const queryClient = useQueryClient();

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setIsMenuOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        queryClient.removeQueries({ queryKey: ['userProfile'] });
        navigate('/');
    };

    const navItems = [
        { label: 'Pulpit', path: '/dashboard', icon: LayoutDashboard },
        { label: 'Rynki', path: '/market', icon: LineChart },
        { label: 'Spot', path: '/spot', icon: Store },
        { label: 'Futures', path: '/futures', icon: CandlestickChart },
        { label: 'Portfel', path: '/wallet', icon: Wallet },
    ];

    return (
        <div className="flex min-h-screen bg-zinc-975 text-zinc-100 selection:bg-emerald-500/30 transition-colors duration-300">
            <aside
                className={`fixed left-0 top-0 z-40 h-full border-r border-nav-border bg-zinc-975 transition-all duration-300 ease-in-out
                            ${isCollapsed ? 'w-20' : 'w-64'}`}>
                <div className={`flex h-16 items-center border-b border-nav-border transition-all duration-300 
                                ${isCollapsed ? 'justify-center px-0' : 'px-6 gap-3'}`}>
                    <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg bg-gradient-to-br
                                    from-emerald-400 to-emerald-600 shadow-lg shadow-emerald-500/20">
                        <Gem className="h-7 w-7 text-zinc-950" strokeWidth={2} />
                    </div>
                    <span className={`text-xl font-bold tracking-tight text-zinc-100 whitespace-nowrap overflow-hidden transition-all 
                                        duration-300
                                    ${isCollapsed ? 'w-0 opacity-0' : 'w-auto opacity-100'}`}>
                        Crypto Market
                    </span>
                </div>
                <nav className="flex flex-col gap-1 p-4">
                    {navItems.map((item) => {
                        const isActive = location.pathname === item.path;
                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                title={isCollapsed ? item.label : ''}
                                className={`group flex items-center rounded-xl py-3 text-sm font-medium transition-all duration-200
                                    ${isActive
                                    ? 'bg-emerald-500/10 text-emerald-500 shadow-[inset_0_0_0_1px_rgba(16,185,129,0.2)]'
                                    : 'text-zinc-400 hover:bg-nav-bg hover:text-zinc-100'}
                                    ${isCollapsed ? 'justify-center px-2' : 'px-4 gap-3'}`}>
                                <item.icon className={`h-5 w-5 shrink-0 transition-colors ${isActive ? 'text-emerald-500' :
                                    'text-zinc-400 group-hover:text-zinc-100'}`} />
                                <span className={`whitespace-nowrap overflow-hidden transition-all duration-300
                                                ${isCollapsed ? 'w-0 opacity-0' : 'w-auto opacity-100'}`}>
                                    {item.label}
                                </span>
                            </Link>
                        );
                    })}
                </nav>
                <div className="absolute bottom-4 left-0 w-full px-4">
                    <button
                        onClick={() => setIsCollapsed(!isCollapsed)}
                        className={`flex w-full items-center rounded-xl border border-nav-border bg-zinc-975 py-3 text-sm font-medium 
                                   text-zinc-400 transition-all hover:bg-nav-bg hover:text-zinc-100 
                                   ${isCollapsed ? 'justify-center px-0' : 'px-4 gap-3'}`}>
                        {isCollapsed ? <PanelLeftOpen className="h-5 w-5" /> : <PanelLeftClose className="h-5 w-5" />}
                        <span className={`whitespace-nowrap overflow-hidden transition-all duration-300
                                        ${isCollapsed ? 'w-0 opacity-0' : 'w-auto opacity-100'}`}>
                            Zwiń pasek
                        </span>
                    </button>
                </div>
            </aside>
            <div className={`flex min-h-screen flex-col transition-all duration-300 ease-in-out
                            ${isCollapsed ? 'ml-20 w-[calc(100%-5rem)]' : 'ml-64 w-[calc(100%-16rem)]'}`}>
                <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-nav-border
                                bg-nav-bg px-8 backdrop-blur-xl transition-colors duration-300">
                    <div className="flex items-center gap-4">
                        <div className="relative group">
                            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-zinc-400 group-focus-within:text-emerald-500
                                                transition-colors" />
                            <input
                                type="text"
                                placeholder="Szukaj rynków..."
                                className="h-10 w-64 rounded-xl border border-nav-border bg-zinc-975 pl-10 pr-4 text-sm text-zinc-100
                                            outline-none transition-all focus:border-emerald-500/50 focus:ring-1 focus:ring-emerald-500/50
                                            placeholder:text-zinc-400" />
                        </div>
                    </div>
                    <div className="flex items-center gap-4">
                        <button className="relative rounded-full p-2 text-zinc-400 transition-colors hover:bg-nav-border hover:text-zinc-100">
                            <Bell className="h-5 w-5" />
                            <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.6)]"></span>
                        </button>
                        <div className="h-8 w-px bg-nav-border"></div>
                        <div className="relative" ref={menuRef}>
                            <button
                                onClick={() => setIsMenuOpen(!isMenuOpen)}
                                className={`flex items-center gap-3 rounded-full border border-nav-border bg-zinc-975 py-1 pl-1
                                            pr-4 transition-all hover:border-zinc-400
                                    ${isMenuOpen ? 'ring-2 ring-emerald-500/20 border-emerald-500/30' : ''}`}>
                                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-nav-border border border-nav-border">
                                    <User className="h-4 w-4 text-zinc-400" />
                                </div>
                                <div className="text-left hidden sm:block">
                                    <p className="text-xs font-bold text-zinc-100">{userAlias}</p>
                                </div>
                                <Menu className="ml-2 h-4 w-4 text-zinc-400" />
                            </button>
                            {isMenuOpen && (
                                <div className="absolute right-0 top-full mt-2 w-64 origin-top-right overflow-hidden rounded-2xl border
                                                border-nav-border bg-white dark:bg-zinc-975 p-2 shadow-2xl animate-in fade-in zoom-in-95
                                                duration-200">
                                    <div className="mb-2 border-b border-nav-border px-4 py-3">
                                        <p className="text-sm font-bold text-zinc-100">{isLoading ? 'Ładowanie...' : fullName}</p>
                                        <div className="mt-2 inline-block rounded bg-emerald-500/10 px-2 py-0.5 text-[10px]
                                                        font-bold text-emerald-500 border border-emerald-500/20">
                                            ZWERFIKOWANY
                                        </div>
                                    </div>
                                    <div className="flex flex-col gap-1">
                                        <button onClick={() => {navigate("/wallet"); setIsMenuOpen(false);}}  className="flex w-full items-center gap-3 rounded-lg px-4 py-2.5 text-sm text-zinc-400
                                                            transition-colors hover:bg-nav-border hover:text-emerald-500">
                                            <Wallet className="h-4 w-4"/> Mój Portfel
                                        </button>
                                        <button className="flex w-full items-center gap-3 rounded-lg px-4 py-2.5 text-sm text-zinc-400
                                                            transition-colors hover:bg-nav-border hover:text-emerald-500">
                                            <Settings className="h-4 w-4" /> Ustawienia
                                        </button>
                                        <button
                                            onClick={toggleTheme}
                                            className="flex w-full items-center gap-3 rounded-lg px-4 py-2.5 text-sm text-zinc-400
                                                        transition-colors hover:bg-nav-border hover:text-emerald-500">
                                            {theme === 'light' ? <Moon className="h-4 w-4" /> : <Sun className="h-4 w-4" />}
                                            {theme === 'light' ? 'Tryb Ciemny' : 'Tryb Jasny'}
                                        </button>
                                    </div>
                                    <div className="my-2 h-px bg-nav-border"></div>
                                    <button
                                        onClick={handleLogout}
                                        className="flex w-full items-center gap-3 rounded-lg px-4 py-2.5 text-sm text-red-400
                                                    transition-colors hover:bg-red-500/10 hover:text-red-500">
                                        <LogOut className="h-4 w-4" /> Wyloguj się
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                </header>
                <main className="flex-1 p-8 transition-all duration-300">
                    {children}
                </main>
            </div>
        </div>
    );
}