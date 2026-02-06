import { useMutation } from '@tanstack/react-query';
import { registerUser, loginUser, RegisterRequest, LoginRequest } from '../api/authService';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const useRegister = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    return useMutation({
        mutationFn: (data: RegisterRequest) => registerUser(data),
        onSuccess: (response) => {
            console.log("Register successfully", response);

            login(response.token, {
                userId: response.userId,
                alias: response.alias,
                email: response.email
            });

            navigate('/dashboard');
        },
        onError: (error: any) => {
            const msg = error.response?.data?.message || "Can not register user";
            alert(msg);
        }
    });
};

export const useLogin = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    return useMutation({
        mutationFn: (data: LoginRequest) => loginUser(data),
        onSuccess: (response) => {
            console.log("Login successfully", response);

            login(response.token, {
                userId: response.userId,
                alias: response.alias,
                email: response.email
            });
            navigate('/dashboard');
        },
        onError: (error: any) => {
            const msg = error.response?.data?.message || "Invalid credentials";
            alert(msg);
        }
    });
};