import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { placeSpotOrder, fetchMyOrders, cancelOrder, SpotOrderRequest } from '../api/orderService';
import { WALLET_QUERY_KEY } from './useWallet';
import { useAuth } from '../context/AuthContext';

export const ORDERS_QUERY_KEY = ['orders'];

export const useMyOrders = () => {
    const { token } = useAuth();

    return useQuery({
        queryKey: ORDERS_QUERY_KEY,
        queryFn: fetchMyOrders,
        enabled: !!token,
        staleTime: 1000 * 5,
        retry: 1
    });
};

export const useSpotOrder = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: SpotOrderRequest) => placeSpotOrder(data),
        onSuccess: async () => {
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY }),
                queryClient.invalidateQueries({ queryKey: ORDERS_QUERY_KEY })
            ]);
        },
        onError: (error: any) => {
            const msg = error.response?.data?.message || "Error occured when placing order";
            alert(msg);
        }
    });
};


export const useCancelOrder = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (orderId: number) => cancelOrder(orderId),
        onSuccess: async () => {
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY }),
                queryClient.invalidateQueries({ queryKey: ORDERS_QUERY_KEY })
            ]);
        },
        onError: (error: any) => {
            alert("Can not finalize order " + error.message);
        }
    });
};