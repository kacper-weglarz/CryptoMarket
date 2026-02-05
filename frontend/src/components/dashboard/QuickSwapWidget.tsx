import { useState } from 'react';
import { ArrowDownUp } from 'lucide-react';

export function QuickSwapWidget() {

    const [sellAmount, setSellAmount] = useState('');
    const [buyAmount, setBuyAmount] = useState('');

    const BALANCE_BTC = 0;
    const BTC_PRICE = 0;

    const handleBlur = () => {
        setSellAmount('');
        setBuyAmount('');
    };

    const handleSellChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        let value = e.target.value;
        if (!/^\d*\.?\d*$/.test(value)) return;
        if (parseFloat(value) > BALANCE_BTC) return;

        setSellAmount(value);

        if (value === '' || value === '.') {
            setBuyAmount('');
        } else {
            setBuyAmount((parseFloat(value) * BTC_PRICE).toFixed(2));
        }
    };

    const setMaxBalance = () => {
        setSellAmount(BALANCE_BTC.toString());
        setBuyAmount((BALANCE_BTC * BTC_PRICE).toFixed(2));
    };

    return (
        <div className="w-full p-6 rounded-3xl border border-[var(--nav-border)] bg-[var(--nav-bg)] backdrop-blur-md">
            <h3 className="text-sm font-bold text-[var(--text-muted)] uppercase tracking-wider mb-4">
                Szybka Wymiana
            </h3>
            <div className="space-y-2 relative">
                <div className="p-4 rounded-2xl border border-[var(--nav-border)] bg-[var(--bg-app)]">
                    <div className="flex justify-between text-xs text-[var(--text-muted)] mb-2">
                        <span>Sprzedajesz</span>
                        <div className="flex gap-2">
                            <span>Saldo: {BALANCE_BTC} BTC</span>
                            <button
                                onClick={setMaxBalance}
                                className="text-emerald-500 font-bold hover:text-emerald-400 uppercase text-[10px]
                                            cursor-pointer transition-colors">
                                Max
                            </button>
                        </div>
                    </div>
                    <div className="flex justify-between items-center">
                        <input
                            type="text"
                            value={sellAmount}
                            onChange={handleSellChange}
                            placeholder="0.00"
                            onBlur={handleBlur}
                            className="bg-transparent text-2xl font-bold text-[var(--text-app)] w-full outline-none
                                        placeholder:text-[var(--text-muted)] font-mono"/>
                        <span className="font-bold text-sm px-3 py-1.5 rounded-lg border border-[var(--nav-border)] bg-[var(--nav-bg)]
                                         text-[var(--text-app)]">
                            BTC
                        </span>
                    </div>
                </div>
                <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 z-10">
                    <div className="bg-[var(--bg-app)] border border-[var(--nav-border)] p-2 rounded-full text-[var(--text-muted)]
                                    hover:text-emerald-500 cursor-pointer shadow-xl transition-all active:scale-90">
                        <ArrowDownUp size={16} />
                    </div>
                </div>
                <div className="p-4 rounded-2xl border border-[var(--nav-border)] bg-[var(--bg-app)]">
                    <div className="flex justify-between text-xs text-[var(--text-muted)] mb-2">
                        <span>Otrzymujesz (szac.)</span>
                    </div>
                    <div className="flex justify-between items-center">
                        <input
                            type="text"
                            value={buyAmount}
                            readOnly
                            onBlur={handleBlur}
                            placeholder="0.00"
                            className="bg-transparent text-2xl font-bold text-[var(--text-app)] w-full outline-none
                                        placeholder:text-[var(--text-muted)] font-mono opacity-80 cursor-default"/>
                        <span className="font-bold text-sm px-3 py-1.5 rounded-lg border border-[var(--nav-border)] bg-[var(--nav-bg)]
                                        text-[var(--text-app)]">
                            USDT
                        </span>
                    </div>
                </div>
            </div>
            <button
                className="w-full mt-4 bg-emerald-500 hover:bg-emerald-400 text-zinc-950 font-bold py-3.5 rounded-xl transition-all
                           active:scale-95 shadow-lg shadow-emerald-500/20 disabled:opacity-50 disabled:cursor-not-allowed"
                disabled={!sellAmount || parseFloat(sellAmount) <= 0}>
                Wymień Teraz
            </button>
        </div>
    );
}