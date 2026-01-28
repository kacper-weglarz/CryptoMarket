import { Gem, Sun, Moon } from 'lucide-react';
import { useTheme } from '../utils/useTheme';

export function Navbar() {
    const { theme, toggleTheme } = useTheme();

    return (

        <nav className="fixed top-0 left-0 right-0 z-50 flex w-full items-center justify-between border-b border-nav-border bg-nav-bg px-8 py-4 backdrop-blur-xl transition-colors duration-300">

            {/* LOGO */}
            <div className="flex items-center gap-3">
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-emerald-400 to-emerald-600 shadow-lg shadow-emerald-500/20">
                    <Gem className="h-7 w-7 text-zinc-950" strokeWidth={2} />
                </div>
                <span className="text-2xl font-bold tracking-tight text-text-app">
                    Crypto Market
                </span>
            </div>

            {/* MENU */}
            <div className="hidden items-center gap-12 md:flex">
                {['Features', 'Market', 'Informacje'].map((item) => (
                    <a
                        key={item}
                        href="#"
                        className="group relative text-lg font-medium text-text-muted ">
                        {item}
                        <span className="absolute -bottom-1 left-0 h-0.5 w-0 bg-emerald-400 transition-all duration-300 group-hover:w-full"></span>
                    </a>
                ))}
            </div>

            <div className="flex items-center gap-4">
                {/* Btn Motywu */}
                <button
                    onClick={toggleTheme}
                    className="flex h-10 w-10 items-center justify-center rounded-full border border-zinc-800 bg-zinc-900/10 text-text-muted transition-colors hover:text-text-app">
                    {theme === 'light' ? <Moon className="h-5 w-5" /> : <Sun className="h-5 w-5" />}
                </button>

                {/* Btn logowania/rejestracji */}
                <button className="rounded-full bg-btn-bg px-8 py-3 text-base font-bold text-btn-text transition-all hover:scale-105 hover:bg-emerald-400 hover:text-zinc-950 hover:shadow-lg hover:shadow-emerald-500/20 active:scale-95">
                    Zaloguj się
                </button>
                <button className="rounded-full bg-btn-bg px-8 py-3 text-base font-bold text-btn-text transition-all hover:scale-105 hover:bg-emerald-400 hover:text-zinc-950 hover:shadow-lg hover:shadow-emerald-500/20 active:scale-95">
                    Zarejestruj się
                </button>
            </div>
        </nav>
    );
}