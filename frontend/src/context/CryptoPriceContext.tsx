import React, { createContext, useContext, useEffect, useState, ReactNode, useCallback, useRef, useMemo } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export interface PriceData {
    symbol: string;
    price: number;
    change: number;
    volume: number;
}

interface CryptoPriceContextType {
    prices: Record<string, PriceData>;
    isConnected: boolean;
    favorites: string[];
    toggleFavorite: (symbol: string) => void;
}

const SOCKET_URL = 'http://localhost:8080/ws-public';
const CryptoPriceContext = createContext<CryptoPriceContextType | undefined>(undefined);

export const CryptoPriceProvider = ({ children }: { children: ReactNode }) => {
    const [prices, setPrices] = useState<Record<string, PriceData>>({});
    const [isConnected, setIsConnected] = useState(false);

    const [favorites, setFavorites] = useState<string[]>(() => {
        try {
            const saved = localStorage.getItem('cryptoFavorites');
            return saved ? JSON.parse(saved) : [];
        } catch (e) {
            console.error("Błąd odczytu localStorage", e);
            return [];
        }
    });

    const clientRef = useRef<Stomp.Client | null>(null);

    const connect = useCallback(() => {
        const socket = new SockJS(SOCKET_URL);
        const client = Stomp.over(socket);
        client.debug = () => {};

        client.connect({}, () => {
            console.log('WebSocket Connected');
            setIsConnected(true);
            clientRef.current = client;

            client.subscribe('/topic/prices', (message) => {
                const update: PriceData = JSON.parse(message.body);
                const cleanSymbol = update.symbol.replace('/', '').toUpperCase();
                setPrices((prev) => ({
                    ...prev,
                    [cleanSymbol]: { ...update, symbol: cleanSymbol },
                }));
            });
        }, (error) => {
            console.error('WebSocket Error - reconnecting in 5s:', error);
            setIsConnected(false);
            setTimeout(connect, 5000);
        });
    }, []);

    useEffect(() => {
        connect();
        return () => {
            if (clientRef.current?.connected) {
                clientRef.current.disconnect(() => {});
            }
        };
    }, [connect]);

    const toggleFavorite = useCallback((symbol: string) => {
        setFavorites(prev => {
            const newFavs = prev.includes(symbol)
                ? prev.filter(s => s !== symbol)
                : [...prev, symbol];
            localStorage.setItem('cryptoFavorites', JSON.stringify(newFavs));
            return newFavs;
        });
    }, []);

    const contextValue = useMemo(() => ({
        prices,
        isConnected,
        favorites,
        toggleFavorite
    }), [prices, isConnected, favorites, toggleFavorite]);

    return (
        <CryptoPriceContext.Provider value={contextValue}>
            {children}
        </CryptoPriceContext.Provider>
    );
};

export const useCryptoPrices = () => {
    const context = useContext(CryptoPriceContext);
    if (!context) {
        throw new Error('useCryptoPrices must be used within a CryptoPriceProvider');
    }
    return context;
};