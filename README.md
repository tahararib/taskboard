# Taskboard — fil rouge de la formation « Ingénierie DevOps » (Initiation 2J)

Mini-application MVC : un tableau de tâches partagé. Spring Boot 3 · Java 21 · PostgreSQL.
Volontairement minuscule : **une** entité, **une** page, zéro dépendance exotique —
on y apprend le DevOps, pas le framework.

## Démarrer en local (la seule toolchain requise est Docker)

```bash
docker compose up -d --build
# → http://localhost:8081          (l'application — 8080 est pris par Jenkins)
# → http://localhost:8081/actuator/health   → {"status":"UP"}
```

## Ce que contient ce repo

| Fichier | Rôle | Utilisé au |
|---|---|---|
| `Dockerfile` | image autonome multi-stage (compile puis emballe) | Lab 2 |
| `Dockerfile.ci` | image d'usine (emballe le jar déjà construit par le pipeline) | Lab 3 |
| `docker-compose.yml` | app + PostgreSQL avec healthcheck | Lab 2 |
| `Jenkinsfile` | pipeline CI : tests + analyse statique + couverture → image → scan Trivy → push | Atelier 4 |

Le déploiement Kubernetes (chart Helm) et la partie GitOps vivent dans le repo
`taskboard-infra` — deux repos, deux responsabilités : le code d'un côté,
l'infrastructure de l'autre.

## Détails qui ont un pourquoi

- **Pas de JDK requis en local** : les builds passent par Docker et par Jenkins ; le wrapper `./mvnw` (à générer une fois via `generer-wrapper.sh`) sert au pipeline.
- **Le pied de page affiche l'hôte qui a servi la page** : en K8s, rafraîchir
  pendant un `scale` ou un rolling update montre la répartition en direct.
- **Schéma géré par JPA (`ddl-auto=update`)** : choix assumé pour l'Initiation ;
  les migrations versionnées (Flyway) sont au programme du niveau Avancé.
