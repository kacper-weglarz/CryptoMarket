import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Menu, X, TrendingUp, TrendingDown } from 'lucide-react';
import { useCryptoPrices } from '../../context/CryptoPriceContext';

interface SpotHeaderProps {
    symbol: string;
    price?: number;
    change?: number;
}

export function SpotHeader({ symbol, price, change  }: SpotHeaderProps) {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const { prices } = useCryptoPrices();
    const navigate = useNavigate();
    const menuRef = useRef<HTMLDivElement>(null);
    const safePrice = price ?? prices[symbol]?.price ?? 0;
    const safeChange = change ?? prices[symbol]?.change ?? 0;

    const isPositive = safeChange >= 0;
    const marketList = Object.values(prices || {});

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setIsMenuOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleSelectCoin = (newSymbol: string) => {
        const urlSymbol = newSymbol.replace('/', '-');
        navigate(`/spot/${urlSymbol}`);
        setIsMenuOpen(false);
    };

    return (
        <div className="relative z-40">
            <div className="flex items-center justify-between px-6 py-4 bg-[var(--nav-bg)] border border-[var(--nav-border)]
                            rounded-2xl shadow-lg mb-4">
                <div className="flex items-center gap-6">
                    <button
                        onClick={() => setIsMenuOpen(!isMenuOpen)}
                        className="p-2 -ml-2 hover:bg-[var(--text-app)]/10 rounded-lg transition-colors text-[var(--text-app)]">
                        {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
                    </button>

                    <h1 className="text-2xl font-bold text-[var(--text-app)] tracking-tight">
                        {symbol}
                    </h1>

                    <div className="flex items-baseline gap-4 ml-4">
                        <span className={`text-xl font-mono font-bold ${isPositive ? 'text-emerald-500' : 'text-red-500'}`}>
                            ${safePrice.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                        </span>
                        <div className={`flex items-center gap-1 text-sm font-bold ${isPositive ? 'text-emerald-500' : 'text-red-500'}`}>
                            {isPositive ? <TrendingUp size={18} /> : <TrendingDown size={18} />}
                            <span>
                                {safeChange > 0 ? '+' : ''}{safeChange.toFixed(2)}%
                            </span>
                        </div>
                    </div>
                </div>
            </div>
            {isMenuOpen && (
                <div
                    ref={menuRef}
                    className="absolute top-[80px] left-0 w-[280px] bg-[var(--nav-bg)] border border-[var(--nav-border)] rounded-2xl
                                shadow-2xl overflow-hidden flex flex-col max-h-[400px]">
                    <div className="p-3 border-b border-[var(--nav-border)] bg-[var(--nav-bg)] font-bold text-xs text-[var(--text-muted)]
                                uppercase tracking-wider">
                        Wybierz rynek
                    </div>
                    <div className="overflow-y-auto flex-1 p-2">
                        {marketList.map((coin) => (
                            <button
                                key={coin.symbol}
                                onClick={() => handleSelectCoin(coin.symbol)}
                                className="w-full flex items-center justify-between p-3 hover:bg-[var(--text-app)]/5 rounded-xl
                                        transition-colors group text-left">
                                <span className="font-bold text-[var(--text-app)]">{coin.symbol}</span>
                                <span className={`font-mono text-sm ${coin.change >= 0 ? 'text-emerald-500' : 'text-red-500'}`}>
                                    ${(coin.price || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                                </span>
                            </button>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}