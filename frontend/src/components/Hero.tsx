import { ArrowRight, TrendingUp } from 'lucide-react';
import { TradingWidget } from './TradingWidget'; //

export function Hero() {
    return (
        <section className="py-16 px-6 relative z-10">
            <div className="mx-auto max-w-7xl grid lg:grid-cols-2 gap-12 items-center">
                <div className="flex flex-col items-start text-left">
                    <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-emerald-500/20 bg-emerald-500/10
                                    px-4 py-1.5 text-sm font-medium text-emerald-500">
                        <TrendingUp className="h-4 w-4" />
                        <span>Portfel Demo: 100,000 USD na start</span>
                    </div>
                    <h1 className="text-5xl font-extrabold tracking-tight text-[var(--text-app)] sm:text-6xl lg:text-7xl leading-[1.1]">
                        Odkryj Rynek <br />
                        <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-500">
                            Krypto Bez Ryzyka
                        </span>
                    </h1>
                    <p className="mt-6 max-w-xl text-lg text-[var(--text-muted)] leading-relaxed">
                        Testuj swoje strategie inwestycyjne na realnych danych rynkowych.
                        Naucz się handlować Bitcoinem i setkami innych tokenów, korzystając z wirtualnych funduszy.
                    </p>
                    <div className="mt-10 flex flex-wrap items-center gap-4">
                        <button className="flex items-center gap-2 rounded-full bg-emerald-500 px-8 py-4 text-base font-bold text-white
                                            transition-all hover:bg-emerald-600 hover:shadow-lg hover:shadow-emerald-500/25 hover:-translate-y-1">
                            Otwórz Darmowe Konto
                            <ArrowRight className="h-5 w-5" />
                        </button>

                        <button className="rounded-full border border-[var(--nav-border)] bg-[var(--nav-bg)]/50 px-8 py-4 text-base font-bold
                                        text-[var(--text-app)] transition-all hover:bg-[var(--nav-bg)] hover:border-zinc-600 backdrop-blur-md">
                            Jak to działa?
                        </button>
                    </div>
                </div>
                <div className="relative flex justify-center lg:justify-end">

                    <TradingWidget />

                    <div className="absolute -top-20 -right-20 -z-10 h-[400px] w-[400px] rounded-full bg-emerald-500/20 blur-[120px]" />
                    <div className="absolute -bottom-20 -left-20 -z-10 h-[300px] w-[300px] rounded-full bg-cyan-500/20 blur-[100px]" />
                </div>

            </div>
        </section>
    );
}