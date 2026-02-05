import { useState, useEffect, useMemo, useCallback } from 'react';
import { useWallet } from './useWallet';
import { useSpotOrder } from './useOrders'; // Zakładam że tu jest export const useSpotOrder
import { SpotOrderRequest } from '../api/orderService';
import Decimal from 'decimal.js';

interface Props { base: string; quote: string; currentPrice: number; }

export function useOrderFormSpot({ base, quote, currentPrice }: Props) {
    const [side, setSide] = useState<'BUY' | 'SELL'>('BUY');
    const [orderType, setOrderType] = useState<'LIMIT' | 'MARKET'>('MARKET');
    const [price, setPrice] = useState<string>('');
    const [inputValue, setInputValue] = useState<string>('');
    const [inputMode, setInputMode] = useState<'TOTAL' | 'AMOUNT'>('TOTAL');
    const [sliderValue, setSliderValue] = useState(0);

    const { data: walletData } = useWallet();
    const { mutate: placeOrder, isPending: isOrderProcessing } = useSpotOrder();

    const { availableQuote, availableBase } = useMemo(() => {
        const items = walletData?.items || [];
        return {
            availableQuote: new Decimal(items.find(i => i.symbol === quote)?.available || 0),
            availableBase: new Decimal(items.find(i => i.symbol === base)?.available || 0)
        };
    }, [walletData, base, quote]);

    useEffect(() => {
        if (currentPrice) {
            setPrice(new Decimal(currentPrice).toFixed(2));
            setInputValue('');
            setSliderValue(0);
        }
    }, [base, quote]);

    const safePrice = useMemo(() => {
        const p = (orderType === 'LIMIT' && price) ? price : currentPrice.toString();
        const d = new Decimal(p || 0);
        return d.gt(0) ? d : new Decimal(currentPrice || 1);
    }, [orderType, price, currentPrice]);

    const calculatedAmount = useMemo(() => {
        if (!inputValue) return new Decimal(0);
        return inputMode === 'TOTAL' ? new Decimal(inputValue).div(safePrice) : new Decimal(inputValue);
    }, [inputValue, inputMode, safePrice]);

    const calculatedTotal = useMemo(() => {
        if (!inputValue) return new Decimal(0);
        return inputMode === 'TOTAL' ? new Decimal(inputValue) : new Decimal(inputValue).mul(safePrice);
    }, [inputValue, inputMode, safePrice]);

    const handleSliderChange = useCallback((val: number) => {
        setSliderValue(val);
        const percent = new Decimal(val).div(100);
        if (side === 'BUY') {
            const budget = availableQuote.mul(percent);
            setInputValue(inputMode === 'TOTAL' ? budget.toFixed(2) : budget.div(safePrice).toFixed(8));
        } else {
            const amount = availableBase.mul(percent);
            setInputValue(inputMode === 'AMOUNT' ? amount.toFixed(8) : amount.mul(safePrice).toFixed(2));
        }
    }, [side, availableQuote, availableBase, inputMode, safePrice]);

    const toggleInputMode = useCallback(() => {
        if (!inputValue) {
            setInputMode(prev => prev === 'TOTAL' ? 'AMOUNT' : 'TOTAL');
            return;
        }
        if (inputMode === 'TOTAL') {
            setInputValue(new Decimal(inputValue).div(safePrice).toFixed(8));
            setInputMode('AMOUNT');
        } else {
            setInputValue(new Decimal(inputValue).mul(safePrice).toFixed(2));
            setInputMode('TOTAL');
        }
    }, [inputValue, inputMode, safePrice]);

    const handleOrderClick = () => {
        if (!inputValue || new Decimal(inputValue).lte(0)) return;

        const finalAmount = parseFloat(calculatedAmount.toFixed(8));


        const finalPrice = orderType === 'LIMIT'
            ? parseFloat(parseFloat(price).toFixed(2))
            : undefined;

        placeOrder({
            symbol: `${base}/${quote}`,
            amount: finalAmount,
            price: finalPrice,
            orderSide: side,
            orderType: orderType
        } as SpotOrderRequest, {
            onSuccess: () => { setSliderValue(0); setInputValue(''); }
        });
    };

    return {
        side, setSide: (s: any) => { setSide(s); setInputValue(''); setSliderValue(0); },
        orderType, setOrderType,
        price, setPrice,
        inputValue, setInputValue,
        inputMode, toggleInputMode,
        sliderValue, handleSliderChange,
        availableQuote, availableBase,
        calculatedAmount, calculatedTotal,
        handleOrderClick, isOrderProcessing, safePrice
    };
}