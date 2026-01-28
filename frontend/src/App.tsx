import { Navbar } from './components/Navbar';
import { cn } from './utils/utils';

function App() {
    return (

        <div className="min-h-screen relative overflow-hidden">
            <div className="noise-overlay" />
            <div
                className={cn(
                    'fixed inset-0 z-0',
                    'bg-[radial-gradient(ellipse_80%_50%_at_50%_-20%,var(--gradient-color),transparent)]'
                )}
            />

            {/* Navbar */}
            <Navbar />


            <main className="relative z-10">
            </main>

        </div>
    );
}

export default App;