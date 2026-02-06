import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchUserWallet, depositFunds, initializeWallet, DepositRequest } from '../api/walletService';
import { useAuth } from '../context/AuthContext';

export const WALLET_QUERY_KEY = ['wallet'];

export const useWallet = () => {
    const { token } = useAuth();

    return useQuery({
        queryKey: WALLET_QUERY_KEY,
        queryFn: fetchUserWallet,
        enabled: !!token,
        staleTime: 5000,
        retry: (failureCount, error: any) => {
            if (error.response?.status === 401) return false;
            return failureCount < 2;
        }
    });
};

export const useInitializeWallet = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: initializeWallet,
        onSuccess: () => {
            return queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY });
        },
        onError: (error: any) => {
            const errorMsg = error.response?.data?.message || "Can not initialize wallet";
            alert(errorMsg);
        }
    });
};

export const useDeposit = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: DepositRequest) => {
            if (!data.symbol) {
                return Promise.reject(new Error("Asset symbol is required"));
            }
            if (!data.amount || data.amount <= 0) {
                return Promise.reject(new Error("Amount must be greater than 0"));
            }
            return depositFunds(data);
        },
        onSuccess: () => {

            return queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY });
        },
        onError: (error: any) => {
            const errorMsg = error.response?.data?.message || error.message;
            alert(errorMsg);
        }
    });
};