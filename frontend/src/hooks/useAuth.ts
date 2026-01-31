import { useMutation } from '@tanstack/react-query';
import { registerUser, loginUser, RegisterRequest, LoginRequest } from '../api/authService';
import { useNavigate } from 'react-router-dom';

export const useRegister = () => {
    const navigate = useNavigate();

    return useMutation({
    mutationFn: (data: RegisterRequest) => registerUser(data),

    onSuccess: (response) => {
        console.log("Zarejstrowano użytkownika ", response)

        localStorage.setItem('token', response.token);

        navigate('/login');
    },
        onError: (error) => {
            console.error("Wystąpił błąd rejestracji:", error);
        }
    });
};

export const useLogin = () => {
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (data: LoginRequest) => loginUser(data),

        onSuccess: (response) => {
            console.log("Zalogowano użytkownika", response);
            localStorage.setItem('token', response.token);
            navigate('/dashboard');
        },
        onError: (error) => {
            console.error("Wystąpił błąd logowania:", error);
        }
    });
};