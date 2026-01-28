/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            fontFamily: {
                display: ['Outfit', 'sans-serif'],
                mono: ['JetBrains Mono', 'monospace'],
            },
            colors: {
                zinc: {
                    925: '#101014',
                    975: '#09090b',
                },
                gain: {
                    DEFAULT: '#34d399',
                    bg: 'rgba(52, 211, 153, 0.1)',
                },
                loss: {
                    DEFAULT: '#f43f5e',
                    bg: 'rgba(244, 63, 94, 0.1)',
                },
            },
        },
    },
    plugins: [],
}