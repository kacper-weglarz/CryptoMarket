import { Star, ChevronRight, Activity } from 'lucide-react'

export function HotMarketsWidget() {
    const marketData = [
        { symbol: 'BTC/USDT', price: 98450.00, volume: 35200000000, change: 1.25, isHot: true },
        //{ symbol: 'ETH/USDT', price: 2840.15, volume: 12800000000, change: -0.52, isHot: false },
        //{ symbol: 'SOL/USDT', price: 145.30, volume: 4100000000, change: 5.40, isHot: true },
        //{ symbol: 'BNB/USDT', price: 590.20, volume: 1200000000, change: 0.12, isHot: false },
        //{ symbol: 'ADA/USDT', price: 0.5821, volume: 450000000, change: -2.15, isHot: false },
    ];

    const formatVol = (val: number) => {
        return new Intl.NumberFormat('en-US', {
            notation: "compact",
            compactDisplay: "short",
            maximumFractionDigits: 1
        }).format(val);
    };

    return (
        <div className="w-full rounded-3xl border border-[var(--nav-border)] bg-[var(--nav-bg)] overflow-hidden shadow-xl">
            <div className="flex items-center justify-between px-8 py-6 border-b border-[var(--nav-border)]">
                <h2 className="text-xl font-bold text-[var(--text-app)] flex items-center gap-3">
                    <Activity className="text-emerald-500" size={24} />
                    Trendy rynkowe
                </h2>
                <span className="text-[10px] font-black text-[var(--text-muted)] uppercase tracking-[0.2em] bg-[var(--bg-app)]
                                px-3 py-1 rounded-full border border-[var(--nav-border)]">
                    Live Feed
                </span>
            </div>
            <div className="overflow-x-auto w-full">
                <table className="w-full border-collapse min-w-[600px]">
                    <thead>
                    <tr className="text-[11px] font-bold text-[var(--text-muted)] uppercase tracking-widest bg-[var(--text-app)]/[0.02]">
                        <th className="px-8 py-4 text-left">Aktywo</th>
                        <th className="px-6 py-4 text-right">Cena</th>
                        <th className="px-6 py-4 text-right">24h Zmiana</th>
                        <th className="px-6 py-4 text-right hidden md:table-cell">Wolumen</th>
                        <th className="px-8 py-4 text-right">Akcja</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-[var(--nav-border)]/30">
                    {marketData.map((coin) => {
                        const isPos = coin.change >= 0;
                        const [base, quote] = coin.symbol.split('/');
                        return (
                            <tr key={coin.symbol} className="hover:bg-[var(--text-app)]/[0.03] transition-colors group">
                                <td className="px-8 py-5">
                                    <div className="flex items-center gap-4">
                                        <Star size={16} className="text-[var(--text-muted)] hover:text-yellow-500 cursor-pointer
                                                                    transition-colors" />
                                        <div className="flex flex-col">
                                            <div className="flex items-center gap-2">
                                                <span className="font-bold text-[var(--text-app)] text-base">{base}</span>
                                                <span className="text-[var(--text-muted)] text-[10px] font-bold opacity-60">/ {quote}</span>
                                                {coin.isHot && (
                                                    <span className="h-2 w-2 rounded-full bg-orange-500 shadow-[0_0_8px_rgba(249,115,22,0.6)]
                                                                       animate-pulse" />
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-6 py-5 text-right font-mono font-bold text-[var(--text-app)]">
                                    ${coin.price.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                                </td>
                                <td className="px-6 py-5 text-right">
                                        <span className={`px-3 py-1 rounded-lg font-mono font-bold text-xs ${isPos ? 'bg-emerald-500/10 ' +
                                                        'text-emerald-500' : 'bg-red-500/10 text-red-500'}`}>
                                            {isPos ? '+' : ''}{coin.change.toFixed(2)}%
                                        </span>
                                </td>
                                <td className="px-6 py-5 text-right hidden md:table-cell">
                                    <span className="text-xs text-[var(--text-muted)] font-mono">{formatVol(coin.volume)}</span>
                                </td>
                                <td className="px-8 py-5 text-right">
                                    <button className="px-5 py-2 bg-emerald-500 hover:bg-emerald-400 text-zinc-950 rounded-xl font-bold
                                                    text-[11px] uppercase tracking-tighter transition-all hover:scale-105 active:scale-95
                                                    shadow-lg shadow-emerald-500/10">
                                        Trade
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
            <div className="p-4 border-t border-[var(--nav-border)]">
                <button className="w-full py-3 rounded-xl bg-[var(--bg-app)] hover:bg-[var(--text-app)]/[0.05] text-[10px] font-bold
                        text-[var(--text-muted)] hover:text-[var(--text-app)] uppercase tracking-widest transition-all flex items-center
                        justify-center gap-2 group">
                    Pokaż wszystkie rynki
                    <ChevronRight size={14} className="group-hover:translate-x-1 transition-transform" />
                </button>
            </div>
        </div>
    );
}