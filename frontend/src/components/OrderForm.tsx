import { useState, useEffect, useMemo } from 'react';
import { Wallet, Settings2, ArrowRightLeft } from 'lucide-react';
import { useWallet } from '../hooks/useWallet';
import { useSpotOrder } from '../hooks/useOrders';
import { SpotOrderRequest } from '../api/orderService';

interface OrderFormProps {
    type: 'spot' | 'future';
    base: string;
    quote: string;
    currentPrice: number;
}

type InputMode = 'TOTAL' | 'AMOUNT';

export function OrderForm({ type, base, quote, currentPrice }: OrderFormProps) {
    const [orderType, setOrderType] = useState<'LIMIT' | 'MARKET'>('MARKET');
    const [price, setPrice] = useState<string>('');
    const [inputValue, setInputValue] = useState<string>('');
    const [inputMode, setInputMode] = useState<InputMode>('TOTAL');
    const [sliderValue, setSliderValue] = useState(0);
    const [leverage, setLeverage] = useState(20);

    const { data: walletData } = useWallet();
    const { mutate: placeOrder, isPending: isOrderProcessing } = useSpotOrder();

    const { availableQuote, availableBase } = useMemo(() => {
        const items = walletData?.items || [];
        const quoteItem = items.find(item => item.symbol === quote);
        const baseItem = items.find(item => item.symbol === base);
        return {
            availableQuote: quoteItem ? quoteItem.available : 0,
            availableBase: baseItem ? baseItem.available : 0
        };
    }, [walletData, base, quote]);

    useEffect(() => {
        if (currentPrice) {
            setPrice(currentPrice.toFixed(2));
            setInputValue('');
            setSliderValue(0);
        }
    }, [base, quote]);

    const executionPrice = orderType === 'LIMIT' && price
        ? parseFloat(price)
        : currentPrice;


    const safePrice = executionPrice > 0 ? executionPrice : currentPrice;

    const calculatedAmount = inputMode === 'TOTAL'
        ? (parseFloat(inputValue) || 0) / safePrice
        : (parseFloat(inputValue) || 0);

    const calculatedTotal = inputMode === 'TOTAL'
        ? (parseFloat(inputValue) || 0)
        : (parseFloat(inputValue) || 0) * safePrice;

    const effectiveLeverage = type === 'future' ? leverage : 1;
    const marginCost = calculatedTotal / effectiveLeverage;

    const handleSliderChange = (e: React.ChangeEvent<HTMLInputElement>, side: 'BUY' | 'SELL') => {
        const val = parseInt(e.target.value);
        setSliderValue(val);

        if (side === 'BUY') {
            const purchasingPower = availableQuote * effectiveLeverage;
            const budget = (purchasingPower * val) / 100;

            if (inputMode === 'TOTAL') {
                setInputValue(budget.toFixed(2));
            } else {
                setInputValue((budget / safePrice).toFixed(5));
            }
        } else {
            const amountToSell = (availableBase * val) / 100;
            if (inputMode === 'AMOUNT') {
                setInputValue(amountToSell.toFixed(5));
            } else {
                setInputValue((amountToSell * safePrice).toFixed(2));
            }
        }
    };

    const toggleInputMode = () => {
        if (!inputValue) {
            setInputMode(prev => prev === 'TOTAL' ? 'AMOUNT' : 'TOTAL');
            return;
        }
        const val = parseFloat(inputValue);

        if (inputMode === 'TOTAL') {
            setInputValue((val / safePrice).toFixed(5));
            setInputMode('AMOUNT');
        } else {
            setInputValue((val * safePrice).toFixed(2));
            setInputMode('TOTAL');
        }
    };

    const handleOrderClick = (side: 'BUY' | 'SELL') => {
        if (!inputValue || parseFloat(inputValue) <= 0) return;

        let finalAmount = 0;
        if (inputMode === 'AMOUNT') {
            finalAmount = parseFloat(inputValue);
        } else {
            finalAmount = parseFloat(inputValue) / safePrice;
        }

        const requestData: SpotOrderRequest = {
            symbol: `${base}/${quote}`,
            amount: finalAmount,
            price: orderType === 'LIMIT' ? parseFloat(price) : undefined,
            orderSide: side,
            orderType: orderType
        };

        placeOrder(requestData, {
            onSuccess: () => {
                setSliderValue(0);
                setInputValue('');
                console.log("Zamówienie złożone!");
            },
            onError: () => alert("Błąd składania zamówienia!")
        });
    };

    return (
        <div className="bg-[var(--nav-bg)] border border-[var(--nav-border)] rounded-2xl p-5 flex flex-col gap-5 shadow-lg h-full">

            {type === 'future' && (
                <div className="flex gap-2 mb-1">
                    <button className="flex-1 bg-[var(--bg-app)] hover:bg-[var(--nav-border)] border border-[var(--nav-border)]
                    rounded-lg py-1 text-xs font-bold text-[var(--text-muted)] transition-colors">Cross</button>
                    <button className="flex-1 bg-[var(--bg-app)] hover:bg-[var(--nav-border)] border border-[var(--nav-border)]
                    rounded-lg py-1 text-xs font-bold text-[var(--text-app)] transition-colors flex items-center justify-center gap-1">{leverage}x <Settings2 size={12}/></button>
                </div>
            )}
            <div className="flex justify-between text-xs font-mono font-bold mt-2">
                <span className="text-[var(--text-muted)]">Dostępne:</span>
                <div className="flex flex-col items-end">
                     <span className="text-[var(--text-app)] flex items-center gap-1">
                        <Wallet size={12} className="text-emerald-500"/>
                         {availableQuote.toLocaleString('en-US', { minimumFractionDigits: 2 })} {quote}
                    </span>
                    <span className="text-[var(--text-app)] flex items-center gap-1 opacity-70">
                        {availableBase.toLocaleString('en-US', { minimumFractionDigits: 4 })} {base}
                    </span>
                </div>
            </div>
            <div className="flex gap-2 p-1 bg-[var(--bg-app)] rounded-lg">
                <button
                    onClick={() => setOrderType('LIMIT')}
                    className={`flex-1 py-1.5 text-xs font-bold rounded transition-colors ${orderType === 'LIMIT' ?
                        'bg-[var(--nav-bg)] text-[var(--text-app)] shadow-sm' : 'text-[var(--text-muted)] hover:text-[var(--text-app)]'}`}>
                    Limit
                </button>
                <button
                    onClick={() => {
                        setOrderType('MARKET');
                    }}
                    className={`flex-1 py-1.5 text-xs font-bold rounded transition-colors ${orderType === 'MARKET' ?
                        'bg-[var(--nav-bg)] text-[var(--text-app)] shadow-sm' : 'text-[var(--text-muted)] hover:text-[var(--text-app)]'}`}>
                    Market
                </button>
            </div>
            {orderType === 'LIMIT' ? (
                <div className="flex flex-col gap-2">
                    <label className="text-[10px] font-bold text-[var(--text-muted)] uppercase">Cena ({quote})</label>
                    <div className="relative group">
                        <input
                            type="number"
                            value={price}
                            onChange={(e) => setPrice(e.target.value)}
                            className="w-full bg-[var(--bg-app)] border border-[var(--nav-border)] text-[var(--text-app)] font-mono font-bold
                                    text-sm rounded-lg p-3 outline-none focus:ring-1 focus:ring-emerald-500/50 transition-all
                                    [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none
                                    [&::-webkit-inner-spin-button]:appearance-none"/>
                        <span className="absolute right-3 top-3 text-xs text-[var(--text-muted)] font-bold pointer-events-none">{quote}</span>
                    </div>
                </div>
            ) : (
                <div className="flex flex-col gap-2 opacity-60">
                    <label className="text-[10px] font-bold text-[var(--text-muted)] uppercase">Cena ({quote})</label>
                    <div className="w-full bg-[var(--bg-app)] border border-[var(--nav-border)] text-[var(--text-app)] font-mono font-bold
                                text-sm rounded-lg p-3 flex items-center justify-between cursor-not-allowed">
                        <span>Rynkowa</span>
                        <span className="text-xs opacity-50">≈ {currentPrice.toFixed(2)}</span>
                    </div>
                </div>
            )}
            <div className="flex flex-col gap-2">
                <div className="flex justify-between items-center">
                    <label className="text-[10px] font-bold text-[var(--text-muted)] uppercase">
                        {inputMode === 'TOTAL' ? 'Całkowita wartość' : 'Ilość'}
                    </label>
                    <button
                        onClick={toggleInputMode}
                        className="flex items-center gap-1 text-[10px] font-bold text-emerald-500 hover:text-emerald-400 uppercase
                                tracking-wide transition-colors">
                        <ArrowRightLeft size={10} />
                        {inputMode === 'TOTAL' ? `Zmień na ${base}` : `Zmień na ${quote}`}
                    </button>
                </div>
                <div className="relative">
                    <input
                        type="number"
                        value={inputValue}
                        onChange={(e) => {
                            setInputValue(e.target.value);
                            setSliderValue(0);
                        }}
                        placeholder="0.00"
                        className="w-full bg-[var(--bg-app)] border border-[var(--nav-border)] text-[var(--text-app)] font-mono font-bold
                                    text-sm rounded-lg p-3 outline-none focus:ring-1 focus:ring-emerald-500/50 transition-all
                                    [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"/>
                    <span className="absolute right-3 top-3 text-xs text-[var(--text-muted)] font-bold pointer-events-none">
                        {inputMode === 'TOTAL' ? quote : base}
                    </span>
                </div>
                <div className="text-[10px] text-[var(--text-muted)] text-right font-mono">
                    ≈ {inputMode === 'TOTAL'
                    ? `${calculatedAmount.toLocaleString('en-US', {maximumFractionDigits: 5})} ${base}`
                    : `${calculatedTotal.toLocaleString('en-US', {maximumFractionDigits: 2})} ${quote}`
                }
                </div>
            </div>
            <div className="py-2 px-1">
                <input
                    type="range" min="0" max="100" step="1" value={sliderValue}
                    onChange={(e) => handleSliderChange(e, 'BUY')}
                    className="w-full h-1 bg-[var(--nav-border)] rounded-lg appearance-none cursor-pointer accent-emerald-500"/>
                <div className="flex justify-between text-[10px] text-[var(--text-muted)] mt-2 font-mono font-bold select-none">
                    <span className="cursor-pointer hover:text-[var(--text-app)]" onClick={() => handleSliderChange({target: {value: '0'}} as any, 'BUY')}>0%</span>
                    <span className="cursor-pointer hover:text-[var(--text-app)]" onClick={() => handleSliderChange({target: {value: '25'}} as any, 'BUY')}>25%</span>
                    <span className="cursor-pointer hover:text-[var(--text-app)]" onClick={() => handleSliderChange({target: {value: '50'}} as any, 'BUY')}>50%</span>
                    <span className="cursor-pointer hover:text-[var(--text-app)]" onClick={() => handleSliderChange({target: {value: '75'}} as any, 'BUY')}>75%</span>
                    <span className="cursor-pointer hover:text-[var(--text-app)]" onClick={() => handleSliderChange({target: {value: '100'}} as any, 'BUY')}>100%</span>
                </div>
            </div>
            <div className="mt-auto border-t border-[var(--nav-border)] pt-4 flex flex-col gap-4">
                <div className="flex justify-between items-center text-xs">
                    <span className="text-[var(--text-muted)] font-bold">
                        {type === 'future' ? 'Koszt (Margin):' : 'Wartość:'}
                    </span>
                    <span className="font-mono font-bold text-base text-[var(--text-app)]">
                        {marginCost.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}
                        <span className="text-[10px] text-[var(--text-muted)] ml-1">{quote}</span>
                    </span>
                </div>
                <div className="flex gap-3">
                    <button
                        onClick={() => handleOrderClick('BUY')}
                        disabled={isOrderProcessing}
                        className="flex-1 py-3 bg-emerald-500 hover:bg-emerald-400 text-zinc-950 font-black uppercase tracking-wider
                                    rounded-xl transition-all shadow-lg active:scale-95 text-xs sm:text-sm disabled:opacity-50
                                    disabled:cursor-not-allowed">
                        {isOrderProcessing ? '...' : (type === 'future' ? 'Long' : 'Kup')} {base}
                    </button>
                    <button
                        onClick={() => handleOrderClick('SELL')}
                        disabled={isOrderProcessing}
                        className="flex-1 py-3 bg-red-500 hover:bg-red-400 text-zinc-950 font-black uppercase tracking-wider
                                rounded-xl transition-all shadow-lg active:scale-95 text-xs sm:text-sm disabled:opacity-50
                                disabled:cursor-not-allowed">
                        {isOrderProcessing ? '...' : (type === 'future' ? 'Short' : 'Sprzedaj')} {base}
                    </button>
                </div>
            </div>
        </div>
    );
}