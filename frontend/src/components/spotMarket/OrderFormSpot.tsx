import { Wallet, ArrowRightLeft } from 'lucide-react';
import { useOrderFormSpot } from '../../hooks/useOrderFormSpot';

export function OrderFormSpot({ base, quote, currentPrice }: any) {
    const logic = useOrderFormSpot({ base, quote, currentPrice });


    return (
        <div className="bg-[var(--nav-bg)] border border-[var(--nav-border)] rounded-2xl p-5 flex flex-col gap-5 shadow-lg h-full">
            <div className="flex bg-[var(--bg-app)] p-1 rounded-xl">
                <button onClick={() => logic.setSide('BUY')} className={`flex-1 py-2 text-sm font-black 
                uppercase rounded-lg transition-all ${logic.side === 'BUY' ? 'bg-emerald-500 text-zinc-950 shadow-lg' 
                    : 'text-[var(--text-muted)] hover:text-[var(--text-app)]'}`}>Kup</button>
                <button onClick={() => logic.setSide('SELL')} className={`flex-1 py-2 text-sm font-black 
                uppercase rounded-lg transition-all ${logic.side === 'SELL' ? 'bg-red-500 text-zinc-950 shadow-lg' 
                    : 'text-[var(--text-muted)] hover:text-[var(--text-app)]'}`}>Sprzedaj</button>
            </div>
            <div className="flex justify-between text-xs font-mono font-bold">
                <span className="text-[var(--text-muted)]">Dostępne:</span>
                <div className="flex flex-col items-end">
                    <span className="text-[var(--text-app)] flex items-center gap-1">
                        <Wallet size={12} className="text-emerald-500"/>
                        {logic.side === 'BUY' ? logic.availableQuote.toFixed(2) : logic.availableBase.toFixed(4)}
                        {logic.side === 'BUY' ? quote : base}
                    </span>
                    <span className="text-[var(--text-muted)] opacity-70">
                        ≈ {logic.side === 'BUY' ? logic.availableQuote.div(logic.safePrice).toFixed(5) :
                        logic.availableBase.mul(logic.safePrice).toFixed(2)} {logic.side === 'BUY' ? base : quote}
                    </span>
                </div>
            </div>
            <div className="flex gap-2 p-1 bg-[var(--bg-app)] rounded-lg">
                <button onClick={() => logic.setOrderType('LIMIT')} className={`flex-1 py-1.5 text-xs font-bold rounded 
                ${logic.orderType === 'LIMIT' ? 'bg-[var(--nav-bg)] text-[var(--text-app)] shadow-sm' : 'text-[var(--text-muted)]'}`}>Limit</button>
                <button onClick={() => logic.setOrderType('MARKET')} className={`flex-1 py-1.5 text-xs font-bold rounded
                 ${logic.orderType === 'MARKET' ? 'bg-[var(--nav-bg)] text-[var(--text-app)] shadow-sm' : 'text-[var(--text-muted)]'}`}>Market</button>
            </div>
            <div className="flex flex-col gap-2">
                <label className="text-[10px] font-bold text-[var(--text-muted)] uppercase">Cena ({quote})</label>
                <div className="relative">
                    <input
                        type="number"
                        disabled={logic.orderType === 'MARKET'}
                        value={logic.orderType === 'LIMIT' ? logic.price : ''}
                        placeholder={logic.orderType === 'MARKET' ? `Rynkowa (≈${currentPrice.toFixed(2)})` : '0.00'}
                        onChange={(e) => logic.setPrice(e.target.value)}
                        className="w-full bg-[var(--bg-app)] border border-[var(--nav-border)] text-[var(--text-app)] font-mono
                        font-bold text-sm rounded-lg p-3 outline-none focus:ring-1 focus:ring-emerald-500/50"/>
                    <span className="absolute right-3 top-3 text-xs text-[var(--text-muted)] font-bold">{quote}</span>
                </div>
            </div>
            <div className="flex flex-col gap-2">
                <div className="flex justify-between items-center">
                    <label className="text-[10px] font-bold text-[var(--text-muted)] uppercase">{logic.inputMode === 'TOTAL' ? 'Całkowita wartość' : 'Ilość'}</label>
                    <button onClick={logic.toggleInputMode} className={`flex items-center gap-1 text-[10px] font-bold uppercase 
                    ${logic.side === 'BUY' ? 'text-emerald-500' : 'text-red-500'}`}>
                        <ArrowRightLeft size={10} /> {logic.inputMode === 'TOTAL' ? `Zmień na ${base}` : `Zmień na ${quote}`}
                    </button>
                </div>
                <div className="relative">
                    <input
                        type="number"
                        value={logic.inputValue}
                        onChange={(e) => { logic.setInputValue(e.target.value); }}
                        placeholder="0.00"
                        className="w-full bg-[var(--bg-app)] border border-[var(--nav-border)] text-[var(--text-app)] font-mono
                        font-bold text-sm rounded-lg p-3 outline-none"/>
                    <span className="absolute right-3 top-3 text-xs text-[var(--text-muted)] font-bold">
                                {logic.inputMode === 'TOTAL' ? quote : base}
                    </span>
                </div>
                <div className="text-[10px] text-[var(--text-muted)] text-right font-mono">
                    ≈ {logic.inputMode === 'TOTAL' ? `${logic.calculatedAmount.toFixed(5)} ${base}` :
                    `${logic.calculatedTotal.toFixed(2)} ${quote}`}
                </div>
            </div>
            <div className="py-2 px-1">
                <input
                    type="range"
                    min="0"
                    max="100"
                    step="1"
                    value={logic.sliderValue}
                    onChange={(e) => logic.handleSliderChange(parseInt(e.target.value))}
                    className={`w-full h-1 bg-[var(--nav-border)] rounded-lg appearance-none cursor-pointer ${logic.side === 'BUY' ? 
                                'accent-emerald-500' : 'accent-red-500'}`}/>
                <div className="flex justify-between text-[10px] text-[var(--text-muted)] mt-2 font-mono font-bold">
                    {[0, 25, 50, 75, 100].map(v => (
                        <span
                            key={v}
                            className="cursor-pointer hover:text-[var(--text-app)] transition-colors"
                            onClick={() => logic.handleSliderChange(v)}>{v}%
                        </span>))}
                </div>
            </div>
            <div className="mt-auto border-t border-[var(--nav-border)] pt-4 flex flex-col gap-4">
                <div className="flex justify-between items-center text-xs">
                    <span className="text-[var(--text-muted)] font-bold">Wartość:</span>
                    <span className="font-mono font-bold text-base text-[var(--text-app)]">{logic.calculatedTotal.toFixed(2)}
                        <span className="text-[10px] text-[var(--text-muted)]">{quote}</span></span>
                </div>
                <button
                    onClick={logic.handleOrderClick}
                    disabled={logic.isOrderProcessing}
                    className={`w-full py-4 text-zinc-950 font-black uppercase tracking-wider rounded-xl shadow-lg transition-all 
                    active:scale-95 ${logic.side === 'BUY' ? 'bg-emerald-500 hover:bg-emerald-400' : 'bg-red-500 hover:bg-red-400'} 
                    disabled:opacity-50`}>
                    {logic.isOrderProcessing ? 'Przetwarzanie...' : (logic.side === 'BUY' ? `Kup ${base}` : `Sprzedaj ${base}`)}
                </button>
            </div>
        </div>
    );
}