import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';

interface FearGreedData {
    value: string;
    value_classification: string;
    timestamp: string;
}

export function FearGreedWidget() {
    const [data, setData] = useState<FearGreedData | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch('https://api.alternative.me/fng/')
            .then((res) => res.json())
            .then((json) => {
                setData(json.data[0]);
                setLoading(false);
            })
            .catch((err) => console.error(err));
    }, []);

    const score = data ? parseInt(data.value) : 50;
    const rotation = (score / 100) * 170 - 85;

    const getColor = (s: number) => {
        if (s < 25) return '#ef4444';
        if (s < 45) return '#f97316';
        if (s < 55) return '#eab308';
        if (s < 75) return '#84cc16';
        return '#22c55e';
    };

    const currentColor = getColor(score);

    return (
        <div className="h-full w-full flex flex-col items-center justify-between overflow-hidden rounded-3xl border border-nav-border
                        bg-nav-bg p-5 backdrop-blur-xl transition-all">
            <h3 className="text-xs font-bold text-text-muted uppercase tracking-wider text-center mb-1">
                Strach i Chciwość
            </h3>
            <div className="relative w-full max-w-[220px] flex-1 flex items-end justify-center py-2 pb-3">

                <svg viewBox="0 0 200 110" className="w-full h-full">
                    <defs>
                        <linearGradient id="gaugeGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                            <stop offset="0%" stopColor="#ef4444" />
                            <stop offset="50%" stopColor="#eab308" />
                            <stop offset="100%" stopColor="#22c55e" />
                        </linearGradient>
                    </defs>
                    <path d="M 20 100 A 80 80 0 0 1 180 100" fill="none" stroke="var(--nav-border)" strokeWidth="16" strokeLinecap="round"
                          opacity="0.5" />
                    <motion.path
                        d="M 20 100 A 80 80 0 0 1 180 100"
                        fill="none"
                        stroke="url(#gaugeGradient)"
                        strokeWidth="16"
                        strokeLinecap="round"
                        initial={{ strokeDasharray: "0, 251" }}
                        animate={{ strokeDasharray: "251, 251" }}
                        transition={{ duration: 1.5, ease: "easeOut" }}/>
                </svg>
                <motion.div
                    className="absolute bottom-4 left-1/2 origin-bottom z-10"
                    style={{ height: '72%', width: '4px' }}
                    initial={{ rotate: -85, x: '-50%' }}
                    animate={{ rotate: rotation, x: '-50%' }}
                    transition={{ type: "spring", stiffness: 60, damping: 15, delay: 0.5 }}>
                    <div className="h-full w-full rounded-t-full bg-gradient-to-t bg-zinc-400 "></div>
                    <div className="absolute -bottom-2 -left-[6px] h-5 w-5 rounded-full bg-zinc-400 border-nav-bg z-20"></div>
                </motion.div>
            </div>
            <div className="text-center mt-0">
                {loading ? (
                    <div className="h-8 w-16 animate-pulse bg-zinc-800 rounded mx-auto"></div>
                ) : (
                    <>
                        <motion.div
                            initial={{ scale: 0.5, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            className="text-4xl font-black leading-none"
                            style={{ color: currentColor }}>
                            {score}
                        </motion.div>
                        <p className="text-xs font-bold text-text-muted mt-1 uppercase">
                            {data?.value_classification}
                        </p>
                    </>
                )}
            </div>
        </div>
    );
}