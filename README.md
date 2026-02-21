# 🚀 CryptoMarket

![GitHub Repo Size](https://img.shields.io/github/repo-size/kacper-weglarz/cryptomarket)
![GitHub Stars](https://img.shields.io/github/stars/kacper-weglarz/cryptomarket)
![GitHub License](https://img.shields.io/github/license/kacper-weglarz/cryptomarket)

**CryptoMarket** is a modern full-stack **paper trading** application that allows users to simulate cryptocurrency trading in a safe environment. It combines a high-performance **Spring Boot** backend with a reactive **React** frontend.

---

## ✨ Key Features

- **Spot Trading** – Market (instant) and Limit (pending) orders
- **Wallet System** – Fund management, asset locking, and automatic settlement
- **Real-time Data** – Live prices & notifications via **WebSockets (STOMP/SockJS)**
- **Interactive Dashboard** – Charts (**Chart.js**), Fear & Greed Index, and "Hot Markets"
- **Security** – Authentication & authorization with **Spring Security + JWT**
- **Automation** – Scheduled tasks to monitor & execute Limit orders automatically

---

## 🛠 Technologies

### Backend
- **Java 17** & **Spring Boot 4.0**
- **Spring Data JPA** – ORM and database communication
- **Spring Security + JWT** – API security
- **Lombok** – Reduce boilerplate code
- **PostgreSQL** – Persistent relational database

### Frontend
- **React 19** & **TypeScript**
- **Tailwind CSS 4** – Modern utility-first UI
- **TanStack Query (React Query)** – Server-state management
- **Lucide React** – Icon library
- **Vite** – Fast build tool

### Infrastructure
- **Docker & Docker Compose** – Containerized backend, frontend, and database

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/kacper-weglarz/cryptomarket.git
cd cryptomarket
```
### 2. Configure environment
```bash
cp .env.example .env
```
Open .env and fill in your actual values (e.g., JWT_SECRET, DB_PASSWORD).

```bash
cp backend/src/main/resources/application.properties.example backend/src/main/resources/application.properties
```
The file is pre-configured to use placeholders (e.g., ${DB_USER}). These values are automatically injected from your .env file when running via Docker Compose.

### 3. Start the containers
 ```bash
docker-compose up --build
```
### 5. Access the application

Frontend:
 ```bash
http://localhost:5173
```
Backend:
 ```bash
http://localhost:8080
```

<h2>📂 Project Structure </h2>
<h3>/backend</h3>
<p> Java API, integration tests (Testcontainers), trading business logic</p>
<h3>/frontend</h3>
<p> React client, dashboard components, API services</p>
<h3>compose.yaml</h3>
<p>Container orchestration</p>

Author: Kacper Węglarz

---
