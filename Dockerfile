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

# ── Branche dokub : une seule base de code, trois images (voir dokub/build-images.sh) ──
# APP_VERSION       → propriété app.version, affichée par la page et /api/version
# FEATURE_REQUIRED  → true pour la v3 : la readiness exige FEATURE_URL (lab 7)
ARG APP_VERSION=1.0.0
ARG FEATURE_REQUIRED=false
ENV APP_VERSION=${APP_VERSION} \
    APP_FEATURE_REQUIRED=${FEATURE_REQUIRED} \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
# MaxRAMPercentage=75 : le tas Java prend 75 % de la limite mémoire du conteneur
# (25 % par défaut, trop peu pour Spring Boot sous une limite de 512 Mi).

# Utilisateur non-root (securite : pas de root dans un conteneur applicatif)
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
