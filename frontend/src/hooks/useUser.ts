import { useQuery } from '@tanstack/react-query';
import { fetchUserProfile } from '../api/userService';
import { useAuth } from '../context/AuthContext';

export const useUser = () => {
    const { token } = useAuth();

    return useQuery({
        queryKey: ['userProfile'],
        queryFn: fetchUserProfile,
        enabled: !!token,
        staleTime: 1000 * 60 * 10,
        retry: 1,
        refetchOnWindowFocus: false,
    });
};