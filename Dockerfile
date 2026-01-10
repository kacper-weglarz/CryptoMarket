# --- ETAP 1: BUDOWANIE (BUILD) ---
# Używamy obrazu z Mavenem i Javą 17 (zgodnie z Twoim pom.xml)
FROM maven:3.8.5-openjdk-17 AS build

# Ustawiamy katalog roboczy wewnątrz kontenera
WORKDIR /app

# Kopiujemy pliki projektu
COPY pom.xml .
COPY src ./src

# Budujemy aplikację (pomijamy testy dla szybkości)
# UWAGA: Tutaj Maven pobierze zależności zdefiniowane w pom.xml
RUN mvn clean package -DskipTests

# --- ETAP 2: URUCHOMIENIE (RUN) ---
# Używamy lekkiego obrazu samej Javy 17 do uruchomienia
FROM openjdk:17-jdk-slim

# Katalog roboczy dla uruchomionej aplikacji
WORKDIR /app

# Kopiujemy zbudowany plik .jar z etapu pierwszego
COPY --from=build /app/target/*.jar app.jar

# Wystawiamy port 8080 (standardowy dla Spring Boot)
EXPOSE 8080

# Komenda startowa
ENTRYPOINT ["java", "-jar", "app.jar"]