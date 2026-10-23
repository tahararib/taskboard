# ── Stage 1 : Build — JDK + Maven, tout ce qu'il faut pour COMPILER ──────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# pom.xml d'abord : le cache des dependances survit tant que le pom ne change pas
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Les sources ensuite : seul ce layer (et les suivants) est invalide par un changement de code
COPY src/ src/
RUN mvn -B -q package -DskipTests

# ── Stage 2 : Runtime — JRE seul, tout ce qu'il faut pour EXECUTER ───────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Utilisateur non-root (securite : pas de root dans un conteneur applicatif)
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
