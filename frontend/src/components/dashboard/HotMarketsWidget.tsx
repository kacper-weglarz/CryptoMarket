import { Star, Activity, WifiOff, ChevronRight } from 'lucide-react';
import { useCryptoPrices } from '../../context/CryptoPriceContext';
import { useNavigate } from 'react-router-dom';

export function HotMarketsWidget() {
    const { prices, isConnected, favorites, toggleFavorite } = useCryptoPrices();
    const navigate = useNavigate();

    const marketList = Object.values(prices || {})
        .sort((a, b) => {
            const isAFav = favorites.includes(a.symbol);
            const isBFav = favorites.includes(b.symbol);
            if (isAFav && !isBFav) return -1;
            if (!isAFav && isBFav) return 1;
            return (b.price || 0) - (a.price || 0);
        })
        .slice(0, 5);

    const formatVol = (val: number) => {
        if (!val) return '0.00';
        return new Intl.NumberFormat('en-US', {
            notation: "compact",
            compactDisplay: "short",
            maximumFractionDigits: 1
        }).format(val);
    };

    return (
        <div className="min-h-96 w-full h-full flex flex-col rounded-3xl border border-[var(--nav-border)] bg-[var(--nav-bg)]
                        overflow-hidden shadow-xl">
            <div className="flex items-center justify-between px-8 py-6 border-b border-[var(--nav-border)] bg-[var(--nav-bg)]/50
                            backdrop-blur-md">
                <h2 className="text-xl font-bold text-[var(--text-app)] flex items-center gap-3">
                    <Activity className="text-emerald-500" size={24} />
                    Popularne kryptowaluty
                </h2>
                <div className={`flex items-center gap-2 text-[10px] font-black uppercase tracking-[0.2em] px-3 py-1 rounded-full 
                                border border-[var(--nav-border)] ${isConnected ? 'bg-emerald-500/10 text-emerald-500 border-emerald-500/20' : 
                                'bg-red-500/10 text-red-500 border-red-500/20'}`}>
                    {isConnected ? "Live Feed" : "Offline"}
                </div>
            </div>
            <div className="overflow-x-auto w-full flex-1 relative">
                {!isConnected && marketList.length === 0 && (
                    <div className="absolute inset-0 flex flex-col items-center justify-center bg-[var(--nav-bg)]/80 backdrop-blur-sm z-20
                                    text-center p-4">
                        <WifiOff size={32} className="text-red-500 mb-2"/>
                        <h3 className="font-bold text-[var(--text-app)]">Brak danych</h3>
                    </div>
                )}
                <table className="w-full border-collapse min-w-[600px]">
                    <thead>
                    <tr className="text-[11px] font-bold text-[var(--text-muted)] uppercase tracking-widest bg-[var(--text-app)]/[0.02]">
                        <th className="px-8 py-4 text-left">Aktywo</th>
                        <th className="px-6 py-4 text-right">Cena</th>
                        <th className="px-6 py-4 text-right">24h Zmiana</th>
                        <th className="px-6 py-4 text-right hidden md:table-cell">Wolumen</th>
                        <th className="px-8 py-4 text-right">Handluj</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-[var(--nav-border)]/30">
                    {marketList.map((coin) => {
                        const isPos = coin.change >= 0;
                        const symbolParts = coin.symbol ? coin.symbol.split('/') : ['?', '?'];
                        const base = symbolParts[0];
                        const quote = symbolParts[1] || 'USDT';
                        const isFav = favorites.includes(coin.symbol);
                        return (
                            <tr key={coin.symbol} className="hover:bg-[var(--text-app)]/[0.03] transition-colors group">
                                <td className="px-8 py-5">
                                    <div className="flex items-center gap-4">
                                        <div
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                toggleFavorite(coin.symbol);
                                            }}
                                            className="cursor-pointer p-1 -ml-1 rounded-full hover:bg-[var(--text-app)]/10 transition-colors">
                                            <Star
                                                size={16}
                                                className={`transition-colors ${isFav ? 'text-yellow-500 fill-yellow-500' : 
                                                            'text-[var(--text-muted)] hover:text-yellow-500'}`}/>
                                        </div>
                                        <div className="flex flex-col">
                                            <div className="flex items-center gap-2">
                                                <span className="font-bold text-[var(--text-app)] text-base">{base}</span>
                                                <span className="text-[var(--text-muted)] text-[10px] font-bold opacity-60">/ {quote}</span>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-6 py-5 text-right font-mono font-bold text-[var(--text-app)]">
                                    ${coin.price?.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 4 }) || '0.00'}
                                </td>
                                <td className="px-6 py-5 text-right">
                                    <span className={`px-3 py-1 rounded-lg font-mono font-bold text-xs ${isPos ? 'bg-emerald-500/10 text-emerald-500' : 'bg-red-500/10 text-red-500'}`}>
                                        {isPos ? '+' : ''}{coin.change?.toFixed(2) || '0.00'}%
                                    </span>
                                </td>
                                <td className="px-6 py-5 text-right hidden md:table-cell">
                                    <span className="text-xs text-[var(--text-muted)] font-mono">{formatVol(coin.volume)}</span>
                                </td>
                                <td className="px-8 py-5 text-right">
                                    <button
                                        onClick={() => navigate(`/spot/${base}-${quote}`)}
                                        className="px-5 py-2 bg-emerald-500 hover:bg-emerald-400 text-zinc-950 rounded-xl font-bold
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
            <div className="p-4 border-t border-[var(--nav-border)] bg-[var(--nav-bg)]/50 backdrop-blur-xl">
                <button
                    onClick={() => navigate('/market')}
                    className="w-full py-3 rounded-xl bg-[var(--bg-app)] hover:bg-[var(--text-app)]/[0.05] text-[10px] font-bold
                              text-[var(--text-muted)] hover:text-[var(--text-app)] uppercase tracking-widest transition-all
                              flex items-center justify-center gap-2 group">
                    Pokaż wszystkie rynki
                    <ChevronRight size={14} className="group-hover:translate-x-1 transition-transform" />
                </button>
            </div>
        </div>
    );
}