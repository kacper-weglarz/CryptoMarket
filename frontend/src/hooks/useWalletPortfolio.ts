import { useMemo } from 'react';
import { useWallet } from './useWallet';
import { useCryptoPrices } from '../context/CryptoPriceContext';

export const useWalletPortfolio = () => {

    const { data: wallet, isLoading: isWalletLoading } = useWallet();
    const { prices } = useCryptoPrices();

    const portfolioData = useMemo(() => {

        const items = wallet?.items || [];

        let totalBalance = 0;

        const enrichedItems = items.map(item => {
            let valueInUsd = 0;

            if (item.symbol === 'USDT') {
                valueInUsd = item.amount;
            } else {
                const priceKey = `${item.symbol}USDT`;
                const currentPrice = prices[priceKey]?.price || 0;
                valueInUsd = item.amount * currentPrice;
            }
            totalBalance += valueInUsd;
            return {
                ...item,
                valueInUsd,
                currentPrice: item.symbol === 'USDT' ? 1 : (prices[`${item.symbol}USDT`]?.price || 0)
            };
        });

        return {
            totalBalance,
            items: enrichedItems,
            isInitialized: wallet?.initialized || false
        };
    }, [wallet, prices]);

    return {
        ...portfolioData,
        isLoading: isWalletLoading
    };
};