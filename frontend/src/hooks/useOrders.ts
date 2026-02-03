import { useMutation, useQueryClient } from '@tanstack/react-query';
import { placeSpotOrder, SpotOrderRequest } from '../api/orderService';
import { WALLET_QUERY_KEY } from './useWallet';

export const useSpotOrder = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: SpotOrderRequest) => placeSpotOrder(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY });
            queryClient.invalidateQueries({ queryKey: ['orders'] });
        },
        onError: (error: any) => {
            console.error("Błąd składania zamówienia:", error);
        }
    });
};