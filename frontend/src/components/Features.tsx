import { Zap, ShieldCheck, GraduationCap } from 'lucide-react';

const features = [
    {
        icon: <Zap className="h-6 w-6 text-emerald-500 transition-colors group-hover:text-white" />,
        title: "Dane Real-Time",
        description: "Wszystkie ceny są pobierane bezpośrednio z globalnych giełd. Brak opóźnień, czysta praktyka na żywym organizmie."
    },
    {
        icon: <ShieldCheck className="h-6 w-6 text-emerald-500 transition-colors group-hover:text-white" />,
        title: "Zero Ryzyka",
        description: "Handlujesz wirtualną walutą. Testuj najbardziej ryzykowne strategie bez obaw o stan swojego prawdziwego konta."
    },
    {
        icon: <GraduationCap className="h-6 w-6 text-emerald-500 transition-colors group-hover:text-white" />,
        title: "Centrum Edukacji",
        description: "Zrozum wykresy świecowe, wskaźniki RSI i MACD dzięki."
    }
];

export function Features() {
    return (
        <section className="py-24 relative z-10">
            <div className="mx-auto max-w-7xl px-6">

                <div className="text-center mb-16">
                    <h2 className="text-3xl font-bold tracking-tight text-[var(--text-app)] sm:text-4xl">
                        Wszystko, czego potrzebujesz <br />
                        <span className="text-emerald-500">do bezpiecznej nauki</span>
                    </h2>
                    <p className="mt-4 text-lg text-[var(--text-muted)]">
                        Symulator giełdy odzwierciedlający prawdziwe emocje i mechanizmy rynkowe.
                    </p>
                </div>

                <div className="grid md:grid-cols-3 gap-8">
                    {features.map((feature, index) => (
                        <div
                            key={index}
                            className="group relative overflow-hidden rounded-2xl border border-[var(--nav-border)] bg-[var(--nav-bg)]/50 p-8
                                        transition-all duration-300 hover:border-emerald-500/50 hover:bg-[var(--nav-bg)] backdrop-blur-sm">
                            <div className="mb-6 inline-flex h-14 w-14 items-center justify-center rounded-xl bg-emerald-500/10 transition-colors
                                            group-hover:bg-emerald-500">
                                {feature.icon}
                            </div>
                            <h3 className="text-xl font-bold text-[var(--text-app)] mb-4">
                                {feature.title}
                            </h3>
                            <p className="text-[var(--text-muted)] leading-relaxed">
                                {feature.description}
                            </p>
                            <div className="absolute -right-10 -top-10 h-32 w-32 rounded-full bg-emerald-500/10 blur-3xl transition-all
                                            group-hover:bg-emerald-500/20" />
                        </div>
                    ))}
                </div>

            </div>
        </section>
    );
}