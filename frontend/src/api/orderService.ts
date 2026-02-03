import { apiClient } from './axiosClient';

export interface SpotOrderRequest {
    symbol: string;
    amount: number;
    price?: number;
    orderSide: 'BUY' | 'SELL';
    orderType: 'LIMIT' | 'MARKET';
}

export interface OrderResponse {
    id: number;
    symbol: string;
    type: string;
    side: string;
    amount: number;
    price: number;
    status: string;
    createdAt: string;
}

export const placeSpotOrder = async (data: SpotOrderRequest): Promise<OrderResponse> => {
    const response = await apiClient.post<OrderResponse>('/order/spot', data);
    return response.data;
};