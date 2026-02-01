import { useQuery } from '@tanstack/react-query';
import { fetchUserProfile } from '../api/userService';

export const useUser = () => {
    return useQuery({
        queryKey: ['userProfile'],
        queryFn: fetchUserProfile,
        staleTime: 1000 * 60 * 10,
        retry: 1,
    });
};