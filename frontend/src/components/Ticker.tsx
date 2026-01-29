import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown } from 'lucide-react';

const coins = [
    { symbol: 'BTC', price: '$43,210.50', change: 2.4 },
    { symbol: 'ETH', price: '$2,245.10', change: -0.8 },
    { symbol: 'BNB', price: '$310.20', change: 1.2 },
    { symbol: 'SOL', price: '$95.40', change: 5.7 },
    { symbol: 'XRP', price: '$0.52', change: 1.1 },
    { symbol: 'ADA', price: '$0.48', change: -0.5 },
    { symbol: 'DOT', price: '$7.20', change: -1.1 },
    { symbol: 'DOGE', price: '$0.08', change: 8.4 },
    { symbol: 'AVAX', price: '$34.50', change: 3.2 },
];

export function Ticker() {
    return (
        <div className="relative flex w-full overflow-hidden border-y border-[var(--nav-border)] bg-[var(--nav-bg)]/50 py-3 backdrop-blur-sm">
            <div className="absolute left-0 top-0 z-10 h-full w-24 bg-gradient-to-r from-[var(--bg-app)] to-transparent pointer-events-none" />
            <div className="absolute right-0 top-0 z-10 h-full w-24 bg-gradient-to-l from-[var(--bg-app)] to-transparent pointer-events-none" />
            <motion.div
                className="flex whitespace-nowrap"
                animate={{ x: "-50%" }}
                transition={{
                    repeat: Infinity,
                    ease: "linear",
                    duration: 30,
                }}
            >
                {[...coins, ...coins].map((coin, i) => (
                    <div key={i} className="mx-8 flex items-center gap-2 text-sm">
                        <span className="font-bold text-[var(--text-app)]">
              {coin.symbol}
            </span>
                        <span className="font-mono text-[var(--text-muted)]">
              {coin.price}
            </span>
                        <span className={`flex items-center gap-1 font-medium ${
                            coin.change >= 0 ? 'text-emerald-500' : 'text-red-500'
                        }`}>
              {coin.change >= 0 ? '+' : ''}{coin.change}%
                            {coin.change >= 0 ? (
                                <TrendingUp className="h-3 w-3" />
                            ) : (
                                <TrendingDown className="h-3 w-3" />
                            )}
            </span>

                    </div>
                ))}
            </motion.div>
        </div>
    );
}