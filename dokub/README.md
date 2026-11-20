# Branche `dokub` — formation « Kubernetes, les essentiels »

Cette branche adapte taskboard aux labs Kubernetes de la formation DOKUB.
La branche `main` reste celle de la formation MG7080 et n'est pas modifiée.

## Ajouts par rapport à `main`

| Ajout | Rôle | Labs |
|---|---|---|
| `GET /api/version` | Version et pod qui répond, en une ligne de texte | 2, 7, 10 |
| `GET /api/compute?ms=N` | Occupe le CPU N ms (plafond 2 000 ms) | 9 (HPA) |
| Bandeau `APP_BANNER` | Texte lu au démarrage (variable d'environnement) | 5 |
| Bandeau `APP_BANNER_FILE` | Fichier relu à chaque requête (ConfigMap montée en volume) | 5 |
| `FeatureConfigHealthIndicator` | Readiness DOWN si `APP_FEATURE_REQUIRED=true` et `FEATURE_URL` absente | 7 |
| Détails de santé visibles | `curl …/actuator/health/readiness` montre la cause | 4, 7 |
| `MaxRAMPercentage=75` | Tas Java proportionné à la limite mémoire du conteneur | 4 |

## Construire les images sur le master

```bash
git checkout dokub
bash dokub/build-images.sh
```

Produit `taskboard:1.0.0`, `taskboard:2.0.0` et `taskboard:3.0.0` (version défaillante).

## Vérifier une image à la main

```bash
docker run --rm -d --name tb -p 18080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://invalide:5432/x taskboard:3.0.0
sleep 20; curl -s localhost:18080/actuator/health/readiness; docker rm -f tb
```
