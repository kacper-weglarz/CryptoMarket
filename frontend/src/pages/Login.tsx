import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail, Lock, Eye, EyeOff, ArrowLeft, Gem, Github } from 'lucide-react';

export function Login() {
    const [showPassword, setShowPassword] = useState(false);

    return (
        <div className="flex min-h-screen w-full items-center justify-center p-4">

            <Link
                to="/"
                className="absolute left-4 top-4 flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium text-[var(--text-muted)]
                            transition-colors hover:bg-[var(--nav-bg)] hover:text-[var(--text-app)] sm:left-8 sm:top-8">
                <ArrowLeft className="h-4 w-4" />
                Wróć na główną
            </Link>

            <div className="w-full max-w-md rounded-3xl border border-[var(--nav-border)] bg-[var(--nav-bg)]/80 p-8
                            backdrop-blur-xl shadow-2xl sm:p-10">
                <div className="mb-8 text-center">
                    <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br
                                    from-emerald-400 to-emerald-600 shadow-lg shadow-emerald-500/20">
                        <Gem className="h-7 w-7 text-zinc-950" />
                    </div>
                    <h1 className="text-2xl font-bold tracking-tight text-[var(--text-app)]">Witaj ponownie</h1>
                    <p className="mt-2 text-sm text-[var(--text-muted)]">
                        Wprowadź swoje dane, aby uzyskać dostęp do konta
                    </p>
                </div>
                <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
                    <div className="space-y-1.5">
                        <label className="text-xs font-medium text-[var(--text-muted)] ml-1">Email</label>
                        <div className="relative group">
                            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--text-muted)] transition-colors
                                            group-focus-within:text-emerald-500">
                                <Mail className="h-5 w-5" />
                            </div>
                            <input
                                type="email"
                                placeholder="nazwa@przyklad.com"
                                className="w-full rounded-xl border border-[var(--nav-border)] bg-[var(--bg-app)] py-3 pl-10 pr-4
                                            text-sm text-[var(--text-app)] outline-none transition-all placeholder:text-zinc-500 focus:border-emerald-500
                                            focus:ring-1 focus:ring-emerald-500"/>
                        </div>
                    </div>
                    <div className="space-y-1.5">
                        <div className="flex justify-between items-center ml-1">
                            <label className="text-xs font-medium text-[var(--text-muted)]">Hasło</label>
                            <a href="#" className="text-xs font-medium text-emerald-500 hover:text-emerald-400">Zapomniałeś hasła?</a>
                        </div>
                        <div className="relative group">
                            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--text-muted)] transition-colors
                                            group-focus-within:text-emerald-500">
                                <Lock className="h-5 w-5" />
                            </div>
                            <input
                                type={showPassword ? "text" : "password"}
                                placeholder="••••••••"
                                className="w-full rounded-xl border border-[var(--nav-border)] bg-[var(--bg-app)] py-3 pl-10 pr-10 text-sm
                                            text-[var(--text-app)] outline-none transition-all placeholder:text-zinc-500 focus:border-emerald-500
                                            focus:ring-1 focus:ring-emerald-500"/>
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--text-muted)] hover:text-[var(--text-app)]">
                                {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                            </button>
                        </div>
                    </div>
                    <button className="w-full rounded-xl bg-emerald-500 py-3.5 text-sm font-bold text-white shadow-lg shadow-emerald-500/20
                                        transition-all hover:bg-emerald-600 hover:shadow-emerald-500/30 hover:-translate-y-0.5 active:translate-y-0">
                        Zaloguj się
                    </button>
                </form>
                <div className="my-6 flex items-center gap-4">
                    <div className="h-px flex-1 bg-[var(--nav-border)]"></div>
                    <span className="text-xs text-[var(--text-muted)]">LUB</span>
                    <div className="h-px flex-1 bg-[var(--nav-border)]"></div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                    <button className="flex items-center justify-center gap-2 rounded-xl border border-[var(--nav-border)]
                                        bg-[var(--bg-app)] py-2.5 text-sm font-medium text-[var(--text-app)] transition-colors
                                        hover:bg-[var(--nav-bg)] hover:border-zinc-500">
                        <Github className="h-4 w-4" /> Github
                    </button>
                    <button className="flex items-center justify-center gap-2 rounded-xl border border-[var(--nav-border)]
                                        bg-[var(--bg-app)] py-2.5 text-sm font-medium text-[var(--text-app)] transition-colors
                                        hover:bg-[var(--nav-bg)] hover:border-zinc-500">
                        <svg className="h-4 w-4" viewBox="0 0 24 24"><path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26
                                    1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/><path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/><path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/><path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/></svg>
                        Google
                    </button>
                </div>
                <p className="mt-8 text-center text-sm text-[var(--text-muted)]">
                    Nie masz jeszcze konta?{' '}
                    <a href="#" className="font-semibold text-emerald-500 transition-colors hover:text-emerald-400">
                        Zarejestruj się
                    </a>
                </p>

            </div>
        </div>
    );
}