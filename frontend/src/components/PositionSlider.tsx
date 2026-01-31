import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { TrendingUp, TrendingDown, Activity, Layers } from 'lucide-react';

const positions = [
    // { pair: 'BTC/USDT', type: 'Long', leverage: '10x', entry: 42500, current: 43200, pnl: 2.4, pnlValue: 320.00 },
    // { pair: 'ETH/USDT', type: 'Short', leverage: '5x', entry: 2950, current: 2890, pnl: 1.8, pnlValue: 145.50 },
    // { pair: 'SOL/USDT', type: 'Long', leverage: '20x', entry: 92.50, current: 98.45, pnl: 6.4, pnlValue: 840.00 },
];

export function PositionsSlider() {

    const [currentIndex, setCurrentIndex] = useState(0);
    useEffect(() => {
        if (positions.length === 0) return;

        const timer = setInterval(() => {
            setCurrentIndex((prevIndex) => (prevIndex + 1) % positions.length);
        }, 5000);

        return () => clearInterval(timer);
    }, []);

    if (!positions || positions.length === 0) {
        return (
            <div className="h-full w-full flex flex-col items-center justify-center rounded-3xl border border-nav-border bg-nav-bg p-6
                            backdrop-blur-xl text-center">
                <div className="h-16 w-16 rounded-full bg-zinc-100 dark:bg-zinc-800/50 flex items-center justify-center mb-4 transition-colors">
                    <Layers className="h-8 w-8 text-zinc-300 light:text-zinc-700 dark:text-zinc-300 transition-colors" />
                </div>
                <h3 className="text-sm font-bold text-text-muted uppercase tracking-wider mb-1">Brak Pozycji</h3>
                <p className="text-xs text-text-muted opacity-70 max-w-[150px]">
                    Twoje otwarte pozycje pojawią się w tym miejscu.
                </p>
            </div>
        );
    }
    const currentPos = positions[currentIndex];
    const isPositive = currentPos.pnl >= 0;
    return (
        <div className="h-full w-full flex flex-col justify-between rounded-3xl border border-nav-border bg-nav-bg p-6 backdrop-blur-xl
                        relative overflow-hidden group">
            <div className={`absolute top-0 right-0 w-32 h-32 blur-[60px] rounded-full transition-colors duration-500 opacity-20 
                            pointer-events-none
                ${isPositive ? 'bg-emerald-500' : 'bg-red-500'}`}>
            </div>
            <div className="flex items-center justify-between mb-4 relative z-10">
                <div className="flex items-center gap-2">
                    <Activity size={16} className="text-text-muted" />
                    <span className="text-xs font-bold text-text-muted uppercase tracking-wider">Top Pozycja</span>
                </div>
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded border uppercase
                    ${currentPos.type === 'Long'
                    ? 'border-emerald-500/30 text-emerald-500 bg-emerald-500/10'
                    : 'border-red-500/30 text-red-500 bg-red-500/10'}`}>
                    {currentPos.type} {currentPos.leverage}
                </span>
            </div>
            <div className="flex-1 flex flex-col justify-center relative z-10">
                <AnimatePresence mode='wait'>
                    <motion.div
                        key={currentIndex}
                        initial={{ opacity: 0, x: 20 }}
                        animate={{ opacity: 1, x: 0 }}
                        exit={{ opacity: 0, x: -20 }}
                        transition={{ duration: 0.2 }}
                        className="w-full">
                        <div className="flex items-center gap-3 mb-2">
                            <div className="h-10 w-10 rounded-full flex items-center justify-center font-bold border transition-colors
                                            bg-zinc-100 text-zinc-700 border-zinc-200
                                            dark:bg-zinc-800 dark:text-zinc-300 dark:border-zinc-700">
                                {currentPos.pair.split('/')[0]}
                            </div>
                            <div>
                                <h3 className="text-xl font-bold text-text-app">{currentPos.pair}</h3>
                                <p className="text-xs text-text-muted">Entry: ${currentPos.entry}</p>
                            </div>
                        </div>

                        <div className="mt-4">
                            <p className="text-xs text-text-muted mb-1">Niezrealizowany PnL</p>
                            <div className={`text-4xl font-black font-mono flex items-center gap-2
                                ${isPositive ? 'text-emerald-500' : 'text-red-500'}`}>
                                {isPositive ? '+' : ''}${currentPos.pnlValue.toFixed(2)}
                                {isPositive ? <TrendingUp size={24} /> : <TrendingDown size={24} />}
                            </div>
                            <p className={`text-sm font-bold mt-1 ${isPositive ? 'text-emerald-500/70' : 'text-red-500/70'}`}>
                                {isPositive ? '+' : ''}{currentPos.pnl}%
                            </p>
                        </div>
                    </motion.div>
                </AnimatePresence>
            </div>
            <div className="flex justify-center gap-2.5 mt-4 relative z-10">
                {positions.map((_, idx) => (
                    <button
                        key={idx}
                        onClick={() => setCurrentIndex(idx)}
                        className={`h-2.5 rounded-full transition-all duration-300 cursor-pointer border border-transparent
                            ${idx === currentIndex
                            ? 'w-8 bg-zinc-300 shadow-lg'
                            : 'w-2.5 bg-zinc-300 dark:bg-zinc-600 hover:bg-zinc-400 dark:hover:bg-zinc-500'
                        }`} />
                ))}
            </div>
        </div>
    );
}