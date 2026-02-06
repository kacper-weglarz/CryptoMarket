import { TrendingUp, TrendingDown } from 'lucide-react';

interface FuturesHeaderProps {
    symbol: string;
    price?: number;
    change?: number;
}

export function FuturesHeader({ symbol, price, change }: FuturesHeaderProps) {
    const isPositive = (change || 0) >= 0;

    return (
        <div className="flex items-center justify-between px-6 py-4 bg-[var(--nav-bg)] border border-[var(--nav-border)] rounded-2xl shadow-lg mb-4">
            <div className="flex items-center gap-4">
                <h1 className="text-2xl font-bold text-[var(--text-app)]">
                    {symbol} <span className="text-sm text-[var(--text-muted)] font-normal ml-2">Perpetual</span>
                </h1>
                <div className="flex items-baseline gap-4 ml-4">
                    <span className={`text-xl font-mono font-bold ${isPositive ? 'text-emerald-500' : 'text-red-500'}`}>
                        ${(price || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </span>
                    <div className={`flex items-center gap-1 text-sm font-bold ${isPositive ? 'text-emerald-500' : 'text-red-500'}`}>
                        {isPositive ? <TrendingUp size={18} /> : <TrendingDown size={18} />}
                        <span>{isPositive ? '+' : ''}{(change || 0).toFixed(2)}%</span>
                    </div>
                </div>
            </div>
        </div>
    );
}