import { DashboardLayout } from '../components/DashboardLayout';
import { BalanceCard } from '../components/dashboard/BalanceCard';
import { FearGreedWidget } from '../components/dashboard/FearGreedWidget';
import { QuickSwapWidget } from '../components/dashboard/QuickSwapWidget';
import { PositionsSlider} from '../components/dashboard/PositionSlider';
import { HotMarketsWidget } from '../components/dashboard/HotMarketsWidget';

export function Dashboard() {
    return (
        <DashboardLayout>
            <div className="grid grid-cols-1 xl:grid-cols-12 gap-6 items-start h-full w-full">
                <div className="w-full xl:col-span-9 flex flex-col gap-6">
                    <div className="grid grid-cols-1 xl:grid-cols-9 gap-6">
                        <div className="xl:col-span-5">
                            <BalanceCard />
                        </div>
                        <div className="xl:col-span-4 hidden xl:block">
                            <PositionsSlider />
                        </div>
                    </div>
                    <div className="w-full">
                        <div className="w-full">
                            <HotMarketsWidget />
                        </div>
                    </div>
                </div>

                <div className="w-full xl:col-span-3 flex flex-col gap-6 h-full">
                    <div className="h-[280px]">
                        <FearGreedWidget />
                    </div>
                    <div className="flex-1">
                        <QuickSwapWidget />
                    </div>
                </div>
            </div>
        </DashboardLayout>
    );
}