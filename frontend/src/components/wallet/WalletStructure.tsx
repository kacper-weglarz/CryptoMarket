import { useMemo } from 'react';
import { PieChart as PieIcon } from 'lucide-react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

const CHART_COLORS = [
    '#10b981', '#3b82f6', '#f59e0b', '#8b5cf6',
    '#ec4899', '#06b6d4', '#6366f1', '#ef4444',
];

const transparentize = (value: string, opacity: number) => {
    let c: any;
    if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(value)) {
        c = value.substring(1).split('');
        if (c.length === 3) {
            c = [c[0], c[0], c[1], c[1], c[2], c[2]];
        }
        c = '0x' + c.join('');
        return 'rgba(' + [(c >> 16) & 255, (c >> 8) & 255, c & 255].join(',') + ',' + opacity + ')';
    }
    return value;
};

interface WalletItem {
    symbol: string;
    amount: number;
    valueInUsd: number;
}

interface WalletStructureChartProps {
    items: WalletItem[];
    isLoading: boolean;
}

export function WalletStructure({ items, isLoading }: WalletStructureChartProps) {

    const activeItems = useMemo(() => {
        return (items || [])
            .filter(i => i.valueInUsd > 1)
            .sort((a, b) => b.valueInUsd - a.valueInUsd);
    }, [items]);

    const chartData = useMemo(() => {
        return {
            labels: activeItems.map(i => i.symbol),
            datasets: [
                {
                    data: activeItems.map(i => i.valueInUsd),
                    backgroundColor: activeItems.map((_, i) => CHART_COLORS[i % CHART_COLORS.length]),
                    hoverBackgroundColor: activeItems.map((_, i) => transparentize(CHART_COLORS[i % CHART_COLORS.length], 0.8)),
                    borderColor: 'var(--nav-bg)',
                    borderWidth: 2,
                    hoverOffset: 20,
                },
            ],
        };
    }, [activeItems]);

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            tooltip: {
                backgroundColor: 'rgba(9, 9, 11, 0.95)',
                titleColor: '#e4e4e7',
                bodyColor: '#e4e4e7',
                borderColor: 'rgba(63, 63, 70, 0.5)',
                borderWidth: 1,
                padding: 12,
                cornerRadius: 12,
                callbacks: {
                    label: function(context: any) {
                        return ` ${context.label}: $${context.parsed.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
                    }
                }
            },
        },
        layout: {
            padding: 20
        },
        cutout: 0,
    };
    return (
        <div className="rounded-3xl border border-nav-border bg-nav-bg p-6 backdrop-blur-xl shadow-lg flex flex-col h-full overflow-hidden">
            <div className="flex justify-between items-center mb-2">
                <h3 className="font-bold text-text-app flex items-center gap-2">
                    <PieIcon size={18} className="text-emerald-500"/>
                    Struktura (USD)
                </h3>
            </div>
            <div className="flex-1 flex flex-col items-center justify-center relative min-h-[260px]">
                <div className="relative w-64 h-64">
                    {!isLoading && activeItems.length > 0 && (
                        <Pie data={chartData} options={chartOptions} />
                    )}
                    {activeItems.length === 0 && !isLoading && (
                        <div className="absolute inset-0 flex items-center justify-center text-text-muted text-xs">
                            Brak danych
                        </div>
                    )}
                </div>
            </div>
            <div className="mt-4 space-y-3">
                {activeItems.slice(0, 4).map((item, index) => (
                    <div key={item.symbol} className="flex justify-between text-sm items-center border-b border-nav-border/50 pb-2
                                                        last:border-0 last:pb-0">
                        <span className="text-text-muted flex items-center gap-2 text-xs font-bold uppercase">
                            <div
                                className="w-3 h-3 rounded-full shadow-sm"
                                style={{ backgroundColor: CHART_COLORS[index % CHART_COLORS.length] }}
                            ></div>
                            {item.symbol}
                        </span>
                        <div className="text-right">
                            <div className="font-bold text-text-app font-mono text-xs">
                                ${item.valueInUsd.toLocaleString('en-US', { maximumFractionDigits: 0 })}
                            </div>
                            <div className="text-[10px] text-text-muted">
                                {item.amount.toFixed(4)} {item.symbol}
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}