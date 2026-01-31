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
    items: WalletItem[];
}

export interface DepositRequest {
    amount: number;
}


export const fetchUserWallet = async (): Promise<WalletResponse> => {
    const response = await apiClient.get<WalletResponse>('/wallet');
    return response.data;
};

export const depositFunds = async (data: DepositRequest): Promise<WalletResponse> => {
    const response = await apiClient.post<WalletResponse>('/wallet', data);
    return response.data;
};