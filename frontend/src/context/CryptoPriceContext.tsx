import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export interface PriceData {
    symbol: string;
    price: number;
    change: number;
}

interface CryptoPriceContextType {
    prices: Record<string, PriceData>;
    isConnected: boolean;
}

const CryptoPriceContext = createContext<CryptoPriceContextType | undefined>(undefined);

export const CryptoPriceProvider = ({ children }: { children: ReactNode }) => {
    const [prices, setPrices] = useState<Record<string, PriceData>>({});
    const [isConnected, setIsConnected] = useState(false);

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws-public');
        const client = Stomp.over(socket);

        client.debug = () => {};

        client.connect({}, () => {
            console.log('WebSocket Connected');
            setIsConnected(true);

            client.subscribe('/topic/prices', (message) => {
                const update: PriceData = JSON.parse(message.body);

                setPrices((prev) => ({
                    ...prev,
                    [update.symbol]: update
                }));
            });
        }, (error) => {
            console.error('WebSocket Error:', error);
            setIsConnected(false);
        });

        return () => {
            if (client.connected) client.disconnect(() => {});
        };
    }, []);

    return (
        <CryptoPriceContext.Provider value={{ prices, isConnected }}>
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