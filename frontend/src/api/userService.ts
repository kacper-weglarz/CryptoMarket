import { apiClient } from './axiosClient';

export interface UserProfile {
    name: string;
    surname: string;
    alias: string;
    email: string;
}

export const fetchUserProfile = async (): Promise<UserProfile> => {
    const response = await apiClient.get<UserProfile>('/user/profile');
    return response.data;
};