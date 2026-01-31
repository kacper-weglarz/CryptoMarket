import { useState, useMemo } from 'react';
import { motion } from 'framer-motion';
import { Eye, EyeOff, TrendingUp, ArrowRightLeft } from 'lucide-react';
import { useWallet } from '../hooks/useWallet';

export function BalanceCard() {
    const [showBalance, setShowBalance] = useState(true);
    const { data: wallet, isLoading } = useWallet();
    const displayBalance = useMemo(() => {
        if (!wallet || !wallet.items) return 0;
        const usdtItem = wallet.items.find(item => item.symbol === 'USDT');
        return usdtItem ? usdtItem.amount : 0;
    }, [wallet]);

    const circleButtonVariants = {
        initial: { scale: 1, borderColor: 'var(--nav-border)' },
        hover: {
            scale: 1.15,
            borderColor: 'rgba(16, 185, 129, 0.6)',
            boxShadow: '0 0 20px rgba(16, 185, 129, 0.4), inset 0 0 10px rgba(16, 185, 129, 0.1)',
            transition: { type: "spring", stiffness: 400, damping: 17 }
        },
        tap: { scale: 0.9 }
    };
    const iconVariants = {
        initial: { rotate: 0, color: 'var(--text-muted)' },
        hover: {
            rotate: 180,
            color: '#34d399',
            transition: { type: "spring", stiffness: 300 }
        }
    };

    return (
        <div className="h-full w-full relative overflow-hidden rounded-3xl border border-nav-border bg-nav-bg p-6 backdrop-blur-xl
                        transition-all duration-300 group flex flex-col justify-center">
            <div className="absolute -right-10 -top-10 h-64 w-64 rounded-full bg-emerald-500/10 blur-[80px] group-hover:bg-emerald-500/20
                            transition-all duration-700 pointer-events-none"></div>
            <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
                <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 text-text-muted mb-1">
                        <span className="text-sm font-medium">Całkowite Saldo (USDT)</span>
                        <button
                            onClick={() => setShowBalance(!showBalance)}
                            className="rounded-full p-1 hover:bg-zinc-500/10 hover:text-text-app transition-colors">
                            {showBalance ? <Eye size={16} /> : <EyeOff size={16} />}
                        </button>
                    </div>
                    <div className="flex flex-wrap items-baseline gap-2">
                        <span className="text-3xl sm:text-4xl lg:text-5xl font-bold text-text-app font-mono tracking-tighter
                                        transition-colors whitespace-nowrap">
                            {isLoading ? (
                                <span className="animate-pulse opacity-50">Wczytywanie...</span>
                            ) : (
                                showBalance
                                    ? `$${displayBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
                                    : '••••••••'
                            )}
                        </span>
                        <span className="text-sm font-bold text-text-muted">USD</span>
                    </div>
                    <div className="mt-3 flex items-center gap-3 text-sm">
                        <span className="text-text-muted">Dzisiejsze PnL:</span>
                        <span className="flex items-center gap-1 rounded-full bg-emerald-500/10 px-2 py-0.5 font-mono font-medium
                                        text-emerald-500 border border-emerald-500/20 whitespace-nowrap">
                            +$0.00 (0.0%) <TrendingUp size={14} />
                        </span>
                    </div>
                </div>
                <div className="flex w-full md:w-auto justify-start md:justify-end">
                    <motion.button
                        initial="initial" whileHover="hover" whileTap="tap"
                        variants={circleButtonVariants}
                        className="group relative flex h-[60px] w-[60px] items-center justify-center rounded-full border
                                border-nav-border bg-nav-bg backdrop-blur-md cursor-pointer"
                        title="Transfer">
                        <motion.div variants={iconVariants}>
                            <ArrowRightLeft size={24} />
                        </motion.div>
                    </motion.button>
                </div>
            </div>
        </div>
    );
}