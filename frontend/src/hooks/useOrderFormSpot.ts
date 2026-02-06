import { useState, useEffect, useMemo, useCallback } from 'react';
import { useWallet } from './useWallet';
import { useSpotOrder } from './useOrders';
import { SpotOrderRequest } from '../api/orderService';
import Decimal from 'decimal.js';

interface Props {
    base: string;
    quote: string;
    currentPrice: number;
}

export function useOrderFormSpot({ base, quote, currentPrice }: Props) {
    const [side, setSide] = useState<'BUY' | 'SELL'>('BUY');
    const [orderType, setOrderType] = useState<'LIMIT' | 'MARKET'>('MARKET');
    const [price, setPrice] = useState<string>('');
    const [inputValue, setInputValue] = useState<string>('');
    const [inputMode, setInputMode] = useState<'TOTAL' | 'AMOUNT'>('TOTAL');
    const [sliderValue, setSliderValue] = useState(0);

    const [isSliderMoving, setIsSliderMoving] = useState(false);

    const { data: walletData } = useWallet();
    const { mutate: placeOrder, isPending: isOrderProcessing } = useSpotOrder();

    const toDecimal = (val: string | number) => {
        try {
            const d = new Decimal(val);
            return d.isNaN() ? new Decimal(0) : d;
        } catch {
            return new Decimal(0);
        }
    };

    useEffect(() => {
        setInputValue('');
        setSliderValue(0);
        setPrice('');
    }, [base, quote]);

    useEffect(() => {
        if (currentPrice && !price) {
            setPrice(currentPrice.toString());
        }
    }, [currentPrice]);

    const { availableQuote, availableBase } = useMemo(() => {
        const items = walletData?.items || [];
        const quoteSymbol = quote.toUpperCase();
        const baseSymbol = base.toUpperCase();
        return {
            availableQuote: toDecimal(items.find(i => i.symbol === quoteSymbol)?.available || 0),
            availableBase: toDecimal(items.find(i => i.symbol === baseSymbol)?.available || 0)
        };
    }, [walletData, base, quote]);

    const safePrice = useMemo(() => {
        if (orderType === 'LIMIT') {
            const p = toDecimal(price);
            return p.gt(0) ? p : new Decimal(0);
        }
        return toDecimal(currentPrice || 0);
    }, [orderType, price, currentPrice]);

    const handleSliderChange = useCallback((val: number) => {
        setSliderValue(val);
        setIsSliderMoving(true);

        const percent = new Decimal(val).div(100);

        if (side === 'BUY') {
            const budget = availableQuote.mul(percent);

            if (inputMode === 'TOTAL') {
                setInputValue(budget.toFixed(2, Decimal.ROUND_DOWN));
            } else {
                if (safePrice.gt(0)) {
                    setInputValue(budget.div(safePrice).toFixed(8, Decimal.ROUND_DOWN));
                } else {
                    setInputValue('0');
                }
            }
        } else {
            const amount = availableBase.mul(percent);

            if (inputMode === 'AMOUNT') {
                setInputValue(amount.toFixed(8, Decimal.ROUND_DOWN));
            } else {
                if (safePrice.gt(0)) {
                    setInputValue(amount.mul(safePrice).toFixed(2, Decimal.ROUND_DOWN));
                } else {
                    setInputValue('0');
                }
            }
        }

        setTimeout(() => setIsSliderMoving(false), 100);
    }, [side, availableQuote, availableBase, inputMode, safePrice]);

    useEffect(() => {
        if (isSliderMoving) return;
        if (!inputValue) {
            setSliderValue(0);
            return;
        }

        const val = toDecimal(inputValue);
        if (val.isZero()) {
            setSliderValue(0);
            return;
        }

        let percent = new Decimal(0);

        if (side === 'BUY') {
            if (availableQuote.gt(0)) {
                const totalUSDT = inputMode === 'TOTAL' ? val : val.mul(safePrice);
                percent = totalUSDT.div(availableQuote).mul(100);
            }
        } else {
            if (availableBase.gt(0)) {
                const totalBase = inputMode === 'AMOUNT' ? val : val.div(safePrice);
                percent = totalBase.div(availableBase).mul(100);
            }
        }

        let finalVal = percent.toNumber();
        if (finalVal > 100) finalVal = 100;
        if (finalVal < 0) finalVal = 0;

        if (Math.abs(finalVal - sliderValue) > 1) {
            setSliderValue(Math.floor(finalVal));
        }

    }, [inputValue, side, inputMode, availableQuote, availableBase, safePrice, isSliderMoving]);

    const calculatedAmount = useMemo(() => {
        const val = toDecimal(inputValue);
        if (val.isZero() || safePrice.isZero()) return new Decimal(0);
        return inputMode === 'TOTAL' ? val.div(safePrice) : val;
    }, [inputValue, inputMode, safePrice]);

    const calculatedTotal = useMemo(() => {
        const val = toDecimal(inputValue);
        if (val.isZero() || safePrice.isZero()) return new Decimal(0);
        return inputMode === 'TOTAL' ? val : val.mul(safePrice);
    }, [inputValue, inputMode, safePrice]);

    const toggleInputMode = useCallback(() => {
        const val = toDecimal(inputValue);
        if (val.isZero() || safePrice.isZero()) {
            setInputMode(prev => prev === 'TOTAL' ? 'AMOUNT' : 'TOTAL');
            return;
        }
        if (inputMode === 'TOTAL') {
            setInputValue(val.div(safePrice).toFixed(8, Decimal.ROUND_DOWN));
            setInputMode('AMOUNT');
        } else {
            setInputValue(val.mul(safePrice).toFixed(2, Decimal.ROUND_DOWN));
            setInputMode('TOTAL');
        }
    }, [inputValue, inputMode, safePrice]);

    const handleOrderClick = () => {
        if (safePrice.lte(0)) return;
        const val = toDecimal(inputValue);
        if (val.lte(0)) return;

        const finalAmount = calculatedAmount.toDecimalPlaces(8, Decimal.ROUND_DOWN).toNumber();
        const finalPrice = orderType === 'LIMIT'
            ? toDecimal(price).toDecimalPlaces(2).toNumber()
            : undefined;

        placeOrder({
            symbol: `${base.toUpperCase()}/${quote.toUpperCase()}`,
            amount: finalAmount,
            price: finalPrice,
            orderSide: side,
            orderType: orderType
        } as SpotOrderRequest, {
            onSuccess: () => {
                setSliderValue(0);
                setInputValue('');
            }
        });
    };

    return {
        side, setSide,
        orderType, setOrderType,
        price, setPrice,
        inputValue, setInputValue,
        inputMode, toggleInputMode,
        sliderValue, handleSliderChange,
        availableQuote, availableBase,
        calculatedAmount, calculatedTotal,
        safePrice,
        handleOrderClick,
        isOrderProcessing
    };
}