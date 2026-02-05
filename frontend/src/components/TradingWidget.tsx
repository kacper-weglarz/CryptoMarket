import { Bitcoin, TrendingUp, TrendingDown, Info, Wallet } from 'lucide-react';
import { useState } from 'react';
import { useCryptoPrices } from '../context/CryptoPriceContext';

export function TradingWidget() {
    const { prices } = useCryptoPrices();
    const btcData = prices['BTC/USDT'];
    const currentPrice = btcData?.price || 0;
    const currentChange = btcData?.change || 0;

    const [leverage, setLeverage] = useState(10);
    const [amount, setAmount] = useState('5000');
    const leveragePercent = (leverage / 20) * 100;
    const marginAmount = Number(amount.replace(/[^0-9.]/g, '')) || 0;
    const positionValueUsd = marginAmount * leverage;
    const isPositiveChange = currentChange >= 0;
    const ChangeIcon = isPositiveChange ? TrendingUp : TrendingDown;
    const changeColor = isPositiveChange ? 'text-emerald-500' : 'text-red-500';

    return (
        <div className="relative mx-auto w-full max-w-[480px]">
            <div className="relative z-10 overflow-hidden rounded-3xl border border-[var(--nav-border)] bg-[var(--nav-bg)]
                        backdrop-blur-xl shadow-2xl transition-all duration-300 hover:shadow-emerald-500/10 hover:border-emerald-500/30">
                <div className="flex items-center justify-between border-b border-[var(--nav-border)] p-6 bg-gradient-to-r
                                from-emerald-500/5 to-transparent">
                    <div className="flex items-center gap-4">
                        <div className="flex h-12 w-12 items-center justify-center rounded-full bg-[#F7931A] shadow-lg shadow-orange-500/20">
                            <Bitcoin className="h-7 w-7 text-white" />
                        </div>
                        <div>
                            <div className="flex items-center gap-2">
                                <span className="font-bold text-[var(--text-app)] text-xl">BTC/USDT</span>
                                <span className="rounded bg-emerald-500/10 px-2 py-0.5 text-[11px] font-bold text-emerald-500 border
                                                border-emerald-500/20">PERP</span>
                            </div>
                            <div className="text-sm text-[var(--text-muted)]">Bitcoin Perpetual</div>
                        </div>
                    </div>
                    <div className="text-right">
                        <div className="text-xl font-bold text-[var(--text-app)] tabular-nums transition-colors duration-300">
                            ${currentPrice > 0
                            ? currentPrice.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
                            : '---'}
                        </div>
                        <div className={`flex items-center justify-end gap-1 text-sm font-medium tabular-nums ${changeColor}`}>
                            <ChangeIcon className="h-4 w-4" />
                            {currentChange > 0 ? '+' : ''}{currentChange.toFixed(2)}%
                        </div>
                    </div>
                </div>
                <div className="p-8 space-y-8">
                    <div>
                        <div className="flex justify-between mb-4">
                            <span className="text-sm text-[var(--text-muted)] flex items-center gap-2 font-medium">
                                Dźwignia <Info className="h-4 w-4 opacity-50"/>
                            </span>
                            <span className="text-sm font-bold text-emerald-400 bg-emerald-500/10 px-3 py-1 rounded-lg border
                                            border-emerald-500/20">
                                {leverage}x
                            </span>
                        </div>
                        <div className="relative h-6 w-full flex items-center">
                            <div className="absolute w-full h-2 bg-[var(--bg-app)] rounded-full overflow-hidden border
                                            border-[var(--nav-border)]">
                                <div
                                    style={{ width: `${leveragePercent}%` }}
                                    className="h-full bg-emerald-500 transition-all duration-75 ease-out"/>
                            </div>
                            <input
                                type="range"
                                min="1"
                                max="20"
                                step="1"
                                value={leverage}
                                onChange={(e) => setLeverage(Number(e.target.value))}
                                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-20"/>
                            <div
                                style={{ left: `calc(${leveragePercent}% - 8px)` }}
                                className="absolute h-5 w-5 rounded-full bg-[var(--bg-app)] border-2 border-emerald-500
                                            shadow-[0_0_15px_rgba(16,185,129,0.5)] transition-all duration-75 ease-out pointer-events-none z-10"/>
                        </div>
                        <div className="flex justify-between mt-2 text-[10px] font-medium text-[var(--text-muted)] uppercase tracking-wider">
                            <span>1x</span>
                            <span>10x</span>
                            <span>20x</span>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-5">
                        <div className="group rounded-xl bg-[var(--bg-app)] border border-[var(--nav-border)] p-4 transition-colors
                                        focus-within:border-zinc-500">
                            <div className="text-[11px] text-[var(--text-muted)] mb-1 font-medium">Cena wejścia:</div>
                            <input
                                type="text"
                                disabled
                                value={currentPrice > 0 ? `$${currentPrice.toLocaleString()}` : 'Czekam...'}
                                className="w-full bg-transparent font-mono text-[var(--text-app)] text-base font-bold outline-none
                                            opacity-70 cursor-not-allowed"/>
                        </div>
                        <div className="group rounded-xl bg-[var(--bg-app)] border border-[var(--nav-border)] p-4 relative ring-1
                                        ring-emerald-500/20 transition-all focus-within:ring-emerald-500 focus-within:border-emerald-500">
                            <div className="text-[11px] text-[var(--text-muted)] mb-1 font-medium">Margin (USD)</div>
                            <input
                                type="text"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                                className="w-full bg-transparent font-mono text-[var(--text-app)] text-base font-bold outline-none"/>
                            <button
                                onClick={() => setAmount('10000')}
                                className="absolute right-3 top-1/2 -translate-y-1/2 text-[10px] font-bold text-emerald-500
                                                hover:text-emerald-400 bg-emerald-500/10 px-2 py-1 rounded transition-colors">
                                MAX
                            </button>
                        </div>
                    </div>
                    <div className="space-y-3">
                        <div className="rounded-xl bg-emerald-500/5 border border-emerald-500/10 p-4 flex justify-between items-center text-sm">
                            <span className="text-[var(--text-muted)] flex items-center gap-2">
                                <Wallet className="h-4 w-4"/> Pozycja (USD):
                            </span>
                            <span className="text-emerald-400 font-bold font-mono text-base tabular-nums">
                                ${positionValueUsd.toLocaleString(undefined, { maximumFractionDigits: 0 })}
                            </span>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                        <button className="group relative overflow-hidden rounded-xl bg-emerald-500 py-4 text-base font-bold
                                            text-white transition-all hover:bg-emerald-600 hover:shadow-lg hover:shadow-emerald-500/20
                                            hover:-translate-y-0.5 active:translate-y-0">
                            <span className="relative z-10 flex items-center justify-center gap-2">
                                Long <TrendingUp className="h-5 w-5"/>
                            </span>
                        </button>
                        <button className="group relative overflow-hidden rounded-xl bg-red-500/10 border border-red-500/20
                                           py-4 text-base font-bold text-red-500 transition-all hover:bg-red-500/20 hover:-translate-y-0.5
                                           active:translate-y-0">
                            <span className="relative z-10 flex items-center justify-center gap-2">
                                Short <TrendingDown className="h-5 w-5"/>
                            </span>
                        </button>
                    </div>

                </div>
            </div>
            <div className="absolute top-1/2 left-1/2 -z-10 h-[350px] w-[350px] -translate-x-1/2 -translate-y-1/2 rounded-full
                            bg-emerald-500/20 blur-[100px]" />
        </div>
    );
}