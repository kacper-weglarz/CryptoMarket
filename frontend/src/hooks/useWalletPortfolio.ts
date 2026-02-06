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
            const amount = Number(item.amount);

            if (item.symbol === 'USDT') {
                valueInUsd = amount;
            } else {
                const priceKey = `${item.symbol}/USDT`.toUpperCase();
                const priceData = prices[priceKey];
                const currentPrice = priceData?.price || 0;

                valueInUsd = amount * currentPrice;
            }

            totalBalance += valueInUsd;

            return {
                ...item,
                amount: amount,
                valueInUsd,
                currentPrice: item.symbol === 'USDT' ? 1 : (prices[`${item.symbol}/USDT`]?.price || 0)
            };
        });

        enrichedItems.sort((a, b) => b.valueInUsd - a.valueInUsd);

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