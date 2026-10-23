#!/usr/bin/env bash
# Genere le wrapper Maven (./mvnw) utilise par le Jenkinsfile — a executer UNE fois
# (P0 du dry run), reseau requis. Passe par l'image Maven : aucun JDK local necessaire.
set -euo pipefail
cd "$(dirname "$0")"
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-21 \
  mvn -B -q wrapper:wrapper -Dmaven=3.9.11
ls -la mvnw .mvn/wrapper/
echo "Wrapper genere — committer mvnw, mvnw.cmd et .mvn/wrapper/."
echo "Puis pre-chauffer la distribution dans Jenkins : lancer un premier build."
