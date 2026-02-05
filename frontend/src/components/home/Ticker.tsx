import { TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { useCryptoPrices } from '../../context/CryptoPriceContext';
import { motion } from 'framer-motion';

const TICKER_ASSETS = [
    'BTCUSDT', 'ETHUSDT', 'SOLUSDT', 'BNBUSDT',
    'ADAUSDT', 'XRPUSDT', 'DOGEUSDT'
];

export function Ticker() {
    const { prices } = useCryptoPrices();

    const renderItem = (symbol: string, index: number) => {
        const data = prices[symbol];
        const rawPrice = data?.price || 0;
        const rawChange = data?.change || 0;
        const decimals = rawPrice < 1.0 && rawPrice > 0 ? 4 : 2;
        const formattedPrice = rawPrice.toLocaleString('en-US', {
            minimumFractionDigits: decimals,
            maximumFractionDigits: decimals,
        });
        const formattedChange = Math.abs(rawChange).toFixed(2);

        let colorClass = "text-zinc-500";
        let Icon = Minus;

        if (rawChange > 0) {
            colorClass = "text-emerald-500";
            Icon = TrendingUp;
        } else if (rawChange < 0) {
            colorClass = "text-red-500";
            Icon = TrendingDown;
        }

        return (
            <div key={`${symbol}-${index}`} className="flex items-center gap-3 mx-6 min-w-max">
                <span className="font-bold text-[var(--text-app)] text-sm">
                    {symbol.split('/')[0]}
                </span>
                <span className={`font-mono text-sm font-medium tabular-nums ${colorClass}`}>
                    ${formattedPrice}
                </span>
                <span className={`flex items-center text-xs font-medium tabular-nums ${colorClass}`}>
                    <Icon className="h-3 w-3 mr-1" />
                    {rawChange > 0 ? '+' : rawChange < 0 ? '-' : ''}{formattedChange}%
                </span>
            </div>
        );
    };

    return (
        <div className="w-full border-y border-[var(--nav-border)] bg-[var(--nav-bg)]/50 backdrop-blur-md overflow-hidden py-3 select-none">
            <motion.div
                className="flex w-max"
                animate={{ x: "-50%" }}
                transition={{
                    ease: "linear",
                    duration: 40,
                    repeat: Infinity,
                }}
                whileHover={{ animationPlayState: "paused" }}>
                {TICKER_ASSETS.map((symbol, i) => renderItem(symbol, i))}
                {TICKER_ASSETS.map((symbol, i) => renderItem(symbol, i + 100))}
            </motion.div>
        </div>
    );
}