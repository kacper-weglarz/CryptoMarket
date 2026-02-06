# Etap 1: Budowanie aplikacji
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Kopiujemy pom.xml i pobieramy zależności (cache'owanie warstw)
COPY pom.xml .
RUN mvn dependency:go-offline
# Kopiujemy kod i budujemy paczkę
COPY src ./src
RUN mvn clean package -DskipTests

# Etap 2: Uruchamianie aplikacji
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Kopiujemy tylko gotowy plik .jar z poprzedniego etapu
COPY --from=build /app/target/*.jar app.jar
# Port, na którym działa Spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]