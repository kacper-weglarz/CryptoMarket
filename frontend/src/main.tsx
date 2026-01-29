import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import {CryptoPriceProvider} from './network/CryptoPriceContext'
import './index.css'
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <BrowserRouter>
            <CryptoPriceProvider>
                <App />
            </CryptoPriceProvider>
        </BrowserRouter>
    </StrictMode>,
)
