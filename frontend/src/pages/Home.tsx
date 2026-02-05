import { Hero } from '../components/home/Hero';
import { Ticker } from '../components/home/Ticker';
import { Features } from '../components/home/Features';

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