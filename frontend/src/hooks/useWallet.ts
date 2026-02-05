import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchUserWallet, depositFunds, initializeWallet, DepositRequest } from '../api/walletService';

export const WALLET_QUERY_KEY = ['wallet'];

export const useWallet = () => {
    return useQuery({
        queryKey: WALLET_QUERY_KEY,
        queryFn: fetchUserWallet,
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
            queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY });
        },
        onError: (error: any) => {
            const errorMsg = error.response?.data?.message || "Błąd inicjalizacji portfela.";
            alert(errorMsg);
            console.error("Błąd inicjalizacji:", error);
        }
    });
};

export const useDeposit = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: DepositRequest) => {
            if (!data.amount || data.amount <= 0) {
                return Promise.reject(new Error("Amount must be equal or greater than 0"));
            }
            return depositFunds(data);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY });
        },
        onError: (error: any) => {
            const errorMsg = error.response?.data?.message || error.message;
            alert(errorMsg);
        }
    });
};