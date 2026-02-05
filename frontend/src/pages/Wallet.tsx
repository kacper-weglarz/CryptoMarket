import { motion } from 'framer-motion';
import { useWalletPortfolio } from '../hooks/useWalletPortfolio';
import { useInitializeWallet} from '../hooks/useWallet';
import { DashboardLayout } from '../components/DashboardLayout';
import { WalletBalanceCard } from '../components/wallet/WalletBalanceCard';
import { WalletStructure } from '../components/wallet/WalletStructure';
import { WalletItems } from '../components/wallet/WalletItems';

export function WalletView() {
    const {
        totalBalance,
        items,
        isInitialized,
        isLoading
    } = useWalletPortfolio();

    const { mutate: initialize, isPending: isInitializing } = useInitializeWallet();

    return (
        <DashboardLayout>
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="space-y-6">
                <div className="grid grid-cols-1 xl:grid-cols-12 gap-6">
                    <div className="xl:col-span-8">
                        <WalletBalanceCard
                            balance={totalBalance}
                            isLoading={isLoading}
                            isInitialized={isInitialized}
                            onInitialize={() => initialize()}
                            isProcessing={isInitializing}/>
                    </div>
                    <div className="xl:col-span-4 h-full">
                        <WalletStructure
                            items={items}
                            isLoading={isLoading}/>
                    </div>
                </div>
                <div>
                    <WalletItems
                        items={items}
                        isLoading={isLoading}/>
                </div>
            </motion.div>
        </DashboardLayout>
    );
}