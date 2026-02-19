import { useParams } from 'react-router-dom';
import { useCryptoPrices } from '../context/CryptoPriceContext';
import { DashboardLayout } from '../components/DashboardLayout';
import { SpotHeader } from '../components/spotMarket/SpotHeader';
import { OrderFormSpot } from '../components/spotMarket/OrderFormSpot';

export function SpotMarket() {
    const { symbol } = useParams();
    const { prices } = useCryptoPrices();

    const parseSymbol = (rawSymbol: string | undefined) => {
        if (!rawSymbol) return { base: 'BTC', quote: 'USDT', clean: 'BTCUSDT', formatted: 'BTC/USDT' };

        const upper = rawSymbol.toUpperCase();
        if (upper.includes('-')) {
            const [base, quote] = upper.split('-');
            return { base, quote, clean: `${base}${quote}`, formatted: `${base}/${quote}` };
        }
        if (upper.endsWith('USDT')) {
            const base = upper.replace('USDT', '');
            return { base, quote: 'USDT', clean: upper, formatted: `${base}/USDT` };
        }
        return { base: upper, quote: 'USDT', clean: `${upper}USDT`, formatted: `${upper}/USDT` };
    };

    const { base, quote, formatted } = parseSymbol(symbol);
    const marketData = prices[formatted];
    const currentPrice = marketData?.price || 0;

    return (
        <DashboardLayout>
            <div className="flex flex-col h-full gap-4 pb-6">
                <SpotHeader
                    symbol={formatted}
                    price={marketData?.price}
                    change={marketData?.change}/>
                <div className="grid grid-cols-1 lg:grid-cols-12 gap-4 flex-1 min-h-[600px]">
                    <div className="lg:col-span-9 border border-[var(--nav-border)] rounded-2xl bg-[var(--nav-bg)] min-h-[400px]
                                    flex items-center justify-center relative overflow-hidden">
                        <div className="text-text-muted">Chart Component Placeholder</div>
                    </div>
                    <div className="lg:col-span-3">
                        <OrderFormSpot
                            base={base}
                            quote={quote}
                            currentPrice={currentPrice}/>
                    </div>
                </div>
            </div>
        </DashboardLayout>
    )
}