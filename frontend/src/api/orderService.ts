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
    const response = await apiClient.post<OrderResponse>('/orders/spot', data);
    return response.data;
};

export const fetchMyOrders = async (): Promise<OrderResponse[]> => {
    const response = await apiClient.get<OrderResponse[]>('/orders');
    return response.data;
};

export const cancelOrder = async (orderId: number): Promise<void> => {
    await apiClient.post(`/orders/spot/cancel/${orderId}`);
}