import { Wallet, RefreshCw, Sparkles, Rocket } from 'lucide-react';

interface WalletBalanceCardProps {
    balance: number;
    isLoading: boolean;
    isInitialized: boolean;
    onInitialize: () => void;
    isProcessing: boolean;
}

export function WalletBalanceCard({balance, isLoading, isInitialized, onInitialize, isProcessing}: WalletBalanceCardProps) {
    return (
        <div className={`rounded-3xl border border-nav-border bg-nav-bg p-8 backdrop-blur-xl relative overflow-hidden flex flex-col 
                        justify-between shadow-lg group transition-all duration-500 ${!isInitialized ? 'min-h-[340px]' : 'min-h-[220px]'}`}>
            <div className={`absolute top-0 right-0 w-96 h-96 rounded-full blur-[100px] pointer-events-none -translate-y-1/2 translate-x-1/3 
                            transition-colors duration-1000 ${!isInitialized ? 'bg-orange-500/30' : 'bg-emerald-500/10'}`}>
            </div>
            <div className="relative z-10">
                <div className="flex items-center gap-2 text-text-muted mb-3">
                    <div className={`p-2 rounded-lg ${!isInitialized ? 'bg-orange-500/10 text-orange-600 dark:text-orange-500' : 
                                    'bg-emerald-500/10 text-emerald-500'}`}>
                        <Wallet size={20} />
                    </div>
                    <span className="font-bold text-sm uppercase tracking-wider">
                        {!isInitialized ? 'Status Konta' : 'Szacunkowa Wartość'}
                    </span>
                </div>
                <div className="flex items-baseline gap-2 mb-4 transition-all">
                    <div className="text-5xl md:text-7xl font-bold text-text-app font-mono tracking-tighter">
                        {isLoading ? (
                            <span className="animate-pulse opacity-50 text-4xl">Wczytywanie...</span>
                        ) : (
                            `$${balance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
                        )}
                    </div>
                    {!isLoading && (
                        <span className="text-xl md:text-2xl font-bold text-text-muted font-mono">USD</span>
                    )}
                </div>
                {!isInitialized && !isLoading && (
                    <p className="text-text-muted text-sm max-w-md leading-relaxed">
                        Twoje konto jest obecnie puste. Skorzystaj z jednorazowego bonusu startowego.
                    </p>
                )}
            </div>
            <div className={`relative z-10 ${!isInitialized ? 'mt-6' : 'mt-0'}`}>
                {!isInitialized ? (
                    <div className="
                        p-1 rounded-2xl relative overflow-hidden shadow-sm
                        bg-orange-500/15 border border-orange-600/40
                        dark:bg-orange-500/10 dark:border-orange-500/20">
                        <div className="absolute inset-0 bg-gradient-to-r from-transparent via-orange-500/10 to-transparent
                                    -translate-x-full animate-[shimmer_2s_infinite]"></div>
                        <div className="p-5 flex flex-col sm:flex-row items-center justify-between gap-4 relative z-10">
                            <div>
                                <h4 className="font-extrabold text-lg flex items-center gap-2 text-black dark:text-white">
                                    <Sparkles size={18} className="text-orange-600 dark:text-orange-400 fill-orange-600/20" />
                                    Pakiet Startowy
                                </h4>
                                <p className="text-xs mt-1 font-bold text-black dark:text-white opacity-80">
                                    Odbierz <span className="font-black border-b-2 border-orange-600/50">50,000 USDT</span> na start
                                </p>
                            </div>
                            <button
                                onClick={onInitialize}
                                disabled={isProcessing}
                                className="w-full sm:w-auto bg-gradient-to-r from-orange-600 to-red-600 hover:from-orange-500 hover:to-red-500
                                    text-white font-bold px-8 py-3 rounded-xl transition-all active:scale-95 disabled:opacity-50
                                    disabled:cursor-not-allowed flex items-center justify-center gap-2 group/btn shadow-lg shadow-orange-600/30
                                    border border-black/10 dark:border-white/10">
                                {isProcessing ? (
                                    <>
                                        <RefreshCw size={20} className="animate-spin" />
                                        Przetwarzanie...
                                    </>
                                ) : (
                                    <>
                                        <Rocket size={20} className="group-hover/btn:-translate-y-1 group-hover/btn:translate-x-1
                                                                    transition-transform" />
                                        Inicjuj Portfel
                                    </>
                                )}
                            </button>
                        </div>
                    </div>
                ) : (
                    <div className="flex justify-start">
                        <div className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-emerald-500/10 border border-emerald-500/20
                                        text-emerald-600 dark:text-emerald-400 text-[10px] font-bold uppercase tracking-wider">
                            <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse shadow-[0_0_8px_rgba(16,185,129,0.6)]"></div>
                            Portfel Aktywny
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}