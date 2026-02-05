import { CheckCircle2 } from 'lucide-react';

interface WalletItem {
    symbol: string;
    name: string;
    amount: number;
    available: number;
    locked: number;
    currentPrice: number;
}

interface WalletAssetsTableProps {
    items: WalletItem[];
    isLoading: boolean;
}

export function WalletItems({ items, isLoading }: WalletAssetsTableProps) {
    return (
        <div className="rounded-3xl border border-nav-border bg-nav-bg backdrop-blur-xl overflow-hidden shadow-lg">
            <div className="px-8 py-6 border-b border-nav-border flex justify-between items-center bg-zinc-500/5">
                <h3 className="font-bold text-text-app text-lg">Twoje Aktywa</h3>
                <span className="text-xs font-bold px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-500 border border-emerald-500/20">
                    {items?.length || 0} Walut
                </span>
            </div>
            <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                    <thead className="text-text-muted border-b border-nav-border">
                    <tr>
                        <th className="px-8 py-4 font-medium uppercase text-xs tracking-wider">Nazwa</th>
                        <th className="px-8 py-4 font-medium uppercase text-xs tracking-wider text-right">Cena</th>
                        <th className="px-8 py-4 font-medium uppercase text-xs tracking-wider text-right">Ilość</th>
                        <th className="px-8 py-4 font-medium uppercase text-xs tracking-wider text-right text-emerald-500">Wartość (USD)</th>
                        <th className="px-8 py-4 font-medium uppercase text-xs tracking-wider text-right">Dostępne</th>
                        <th className="px-8 py-4 font-medium uppercase text-xs tracking-wider text-right">Akcja</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-nav-border">
                    {isLoading && (
                        <tr>
                            <td colSpan={6} className="px-8 py-12 text-center text-text-muted animate-pulse">
                                Wczytywanie portfela...
                            </td>
                        </tr>
                    )}
                    {!isLoading && items?.map((item) => {
                        const price = item.currentPrice || 0;
                        const totalValueUsd = item.amount * price;
                        const availableValueUsd = item.available * price;

                        return (
                            <tr key={item.symbol} className="group hover:bg-zinc-500/5 transition-colors">
                                <td className="px-8 py-4">
                                    <div className="flex items-center gap-4">
                                        <div className="w-9 h-9 rounded-full bg-nav-bg flex items-center justify-center font-bold
                                                text-emerald-500 border border-nav-border shadow-sm group-hover:border-emerald-500/50
                                                transition-colors overflow-hidden">
                                            <img
                                                src={`https://assets.coincap.io/assets/icons/${item.symbol.toLowerCase()}@2x.png`}
                                                alt={item.symbol}
                                                className="w-full h-full object-cover"
                                                onError={(e) => {
                                                    e.currentTarget.style.display = 'none';
                                                    e.currentTarget.parentElement!.innerText = item.symbol[0];
                                                }}/>
                                        </div>
                                        <div>
                                            <div className="font-bold text-text-app">{item.symbol}</div>
                                            <div className="text-xs text-text-muted">{item.name}</div>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-8 py-4 text-right">
                                    <span className="text-text-muted font-mono text-xs">
                                        ${price.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                                    </span>
                                </td>
                                <td className="px-8 py-4 text-right font-mono text-text-app font-bold">
                                    {item.amount.toFixed(4)} <span className="text-xs text-text-muted">{item.symbol}</span>
                                </td>
                                <td className="px-8 py-4 text-right">
                                    <div className="flex flex-col items-end">
                                        <span className="font-bold font-mono text-emerald-500">
                                            ${totalValueUsd.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                                        </span>
                                    </div>
                                </td>
                                <td className="px-8 py-4 text-right">
                                    <div className="flex flex-col items-end">
                                        <div className="inline-flex items-center gap-1.5 text-text-muted font-mono font-medium text-xs">
                                            <CheckCircle2 size={12} className="text-emerald-500"/>
                                            {item.available.toFixed(4)}
                                        </div>
                                        <span className="text-[10px] text-text-muted opacity-60 font-mono mt-0.5">
                                            ≈ ${availableValueUsd.toLocaleString('en-US', { maximumFractionDigits: 2 })}
                                        </span>
                                    </div>
                                </td>
                                <td className="px-8 py-4 text-right">
                                    <button className="text-xs font-bold text-emerald-500 hover:text-emerald-400 hover:underline transition-all
                                                        cursor-pointer">
                                        HANDLUJ
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}