import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchUserWallet, depositFunds, initializeWallet, DepositRequest } from '../api/walletService';

export const WALLET_QUERY_KEY = ['wallet'];

export const useWallet = () => {
    return useQuery({
        queryKey: WALLET_QUERY_KEY,
        queryFn: fetchUserWallet,
    });
};

export const useInitializeWallet = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: initializeWallet,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY });
        },
        onError: (error) => {
            console.error("Błąd inicjalizacji portfela:", error);
        }
    });
};

export const useDeposit = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: DepositRequest) => depositFunds(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEY });
        },
        onError: (error) => {
            console.error("Błąd wpłaty:", error);
        }
    });
};