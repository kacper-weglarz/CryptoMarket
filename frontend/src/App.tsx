import { Routes, Route, useLocation , Navigate} from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { SignIn } from './pages/SignIn';
import { Dashboard } from './pages/Dashboard';
import { WalletView } from './pages/Wallet';
import { Market } from './pages/Market';
import { SpotMarket } from './pages/SpotMarket';
import { cn } from './utils/utils';


function App() {
    const location = useLocation();
    const isHomePage = location.pathname === '/';

    return (
        <div className="min-h-screen relative overflow-hidden bg-[var(--bg-app)] text-[var(--text-app)]">

            <div className="noise-overlay" />
            <div className={cn(
                'fixed inset-0 z-0',
                'bg-[radial-gradient(ellipse_80%_50%_at_50%_-20%,var(--gradient-color),transparent)]'
            )}/>

            {isHomePage && <Navbar />}

            <main className={cn(
                "relative z-10",
                isHomePage ? "pt-20" : "")}>
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/login" element={<Login/>} />
                    <Route path="/signin" element={<SignIn/>} />
                    <Route path="/dashboard" element={<Dashboard/>} />
                    <Route path="/wallet" element={<WalletView />} />
                    <Route path="/market" element={<Market />} />
                    <Route path="/spot" element={<SpotMarket />} />
                    <Route path="/spot" element={<Navigate to="/spot/BTC-USDT" replace />} />
                    <Route path="/spot/:symbol" element={<SpotMarket />} />
                </Routes>
            </main>

        </div>
    );
}

export default App;
