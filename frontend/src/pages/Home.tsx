import { Hero } from '../components/Hero';
import { Ticker } from '../components/Ticker';
import { Features } from '../components/Features';

export function Home() {
    return (
        <div className="flex flex-col gap-0 pt-16">
            <Hero />
            <Ticker />
            <Features />
            <div className="h-20"></div>
        </div>
    );
}