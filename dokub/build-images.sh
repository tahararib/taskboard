#!/usr/bin/env bash
# ============================================================
# dokub/build-images.sh — images taskboard de la formation DOKUB
# À lancer sur le master, depuis la racine du dépôt (branche dokub) :
#   bash dokub/build-images.sh
#
# 1. Tests unitaires dans un conteneur Maven (aucun JDK requis sur le poste)
# 2. Trois images construites depuis le même code :
#      taskboard:1.0.0   version de départ
#      taskboard:2.0.0   mise à jour (lab 7)
#      taskboard:3.0.0   version défaillante : readiness DOWN sans FEATURE_URL (lab 7)
# Les images restent dans le Docker du master ; « kind load » les copie
# ensuite dans les nœuds du cluster (aucun registre externe).
# ============================================================
set -euo pipefail
cd "$(dirname "$0")/.."

echo "== Tests unitaires (maven:3.9-eclipse-temurin-21) =="
# Volume m2 nommé : les dépendances Maven ne sont téléchargées qu'une fois
docker run --rm -v "$PWD":/src -w /src -v dokub-m2:/root/.m2 \
    maven:3.9-eclipse-temurin-21 mvn -B -q test
echo "  [OK] tests passés"

build() {   # build <version> <feature_required>
    docker build -q -t "taskboard:$1" \
        --build-arg APP_VERSION="$1" --build-arg FEATURE_REQUIRED="$2" . >/dev/null
    echo "  [OK] taskboard:$1 (FEATURE_REQUIRED=$2)"
}

echo "== Construction des images =="
build 1.0.0 false
build 2.0.0 false
build 3.0.0 true

docker images taskboard --format 'table {{.Repository}}:{{.Tag}}\t{{.Size}}\t{{.CreatedSince}}'
