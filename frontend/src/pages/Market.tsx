import { DashboardLayout } from '../components/DashboardLayout';
import { MarketsWidget } from '../components/market/MarketsWidget';

export function Market() {
    return (
        <DashboardLayout>
                <MarketsWidget />
        </DashboardLayout>
    )
}