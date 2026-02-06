FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Etap 2: Serwowanie przez Nginx
FROM nginx:stable-alpine
# Kopiujemy zbudowane pliki do folderu Nginx
COPY --from=build /app/dist /usr/share/nginx/html
# Kopiujemy własną konfigurację Nginx (opcjonalnie, ale zalecane)
# COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]