import { apiClient } from './axiosClient';

export interface RegisterRequest {
    name: string;
    surname: string;
    alias: string;
    email: string;
    password: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface UserResponse {
    userId: number;
    alias: string;
    email: string;
    token: string;

}

export const registerUser = async (data: RegisterRequest): Promise<UserResponse> => {
    const response = await apiClient.post<UserResponse>('/auth/register', data);
    return response.data;
};

export const loginUser = async (data: LoginRequest): Promise<UserResponse> => {
    const response = await apiClient.post<UserResponse>('/auth/login', data);
    return response.data;
}