import { Navbar } from './components/Navbar';
import { Features } from './components/Features';
import { Ticker } from './components/Ticker';
import { Hero } from './components/Hero';
import { cn } from './utils/utils';

function App() {
    return (
        <div className="min-h-screen relative overflow-hidden bg-[var(--bg-app)] text-[var(--text-app)]">
            <div className="noise-overlay" />
            <div
                className={cn(
                    'fixed inset-0 z-0',
                    'bg-[radial-gradient(ellipse_80%_50%_at_50%_-20%,var(--gradient-color),transparent)]'
                )}
            />
            <Navbar />

            <main className="relative z-10 flex flex-col gap-0 pt-24">

                <Hero/>

                <Ticker />

                <Features />

                <div className="h-20"></div>

            </main>

        </div>
    );
}

export default App;