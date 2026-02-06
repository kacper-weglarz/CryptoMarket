import { apiClient } from './axiosClient';

export interface WalletItem {
    symbol: string;
    name: string;
    amount: number;
    available: number;
    locked: number;
}

export interface WalletResponse {
    id: number;
    initialized: boolean;
    items: WalletItem[];
}

export interface DepositRequest {
    symbol: string;
    amount: number;
}

export const fetchUserWallet = async (): Promise<WalletResponse> => {
    const response = await apiClient.get<WalletResponse>('wallet');
    return response.data;
};

export const depositFunds = async (data: DepositRequest): Promise<WalletResponse> => {
    const response = await apiClient.post<WalletResponse>('/wallet/deposit', data);
    return response.data;
};

export const initializeWallet = async (): Promise<void> => {
    await apiClient.post('/wallet/initialize');
};