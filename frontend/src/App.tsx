import { Routes, Route, useLocation } from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { Home } from './pages/Home';
import { Login } from './pages/Login';
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
            )}
            />

            {isHomePage && <Navbar />}

            <main className={cn(
                "relative z-10",
                isHomePage ? "pt-20" : ""
            )}>
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/login" element={<Login/>} />
                </Routes>
            </main>

        </div>
    );
}

export default App;