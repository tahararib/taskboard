pipeline {
  // Pas d'agents en Initiation : le pipeline s'execute sur le controller.
  // L'architecture controller/agents est presentee dans les slides (niveau Avance en pratique).
  agent any

  // Scrutation du depot toutes les ~2 min : ne necessite qu'un flux SORTANT en 443.
  // (Un webhook exigerait un flux ENTRANT vers ce poste — impossible en salle sans tunnel.)
  triggers { pollSCM('H/2 * * * *') }

  options {
    timeout(time: 20, unit: 'MINUTES')
    disableConcurrentBuilds()
  }

  environment {
    // Le registre vu du POSTE (daemon Docker Desktop) : localhost:5000.
    // registry.k3d.localhost:5000 est le meme registre vu des NOEUDS du cluster
    // (il ne vit que dans chart/values.yaml).
    REGISTRE = 'localhost:5000'
  }

  stages {
    stage('Construire') {
      steps { sh './mvnw -B package -DskipTests' }
    }
    stage('Tests') {
      steps { sh './mvnw -B test' }
      post { always {
        junit 'target/surefire-reports/*.xml'
      } }
    }
    stage('Analyse statique') {
      steps {
        sh './mvnw -B checkstyle:checkstyle spotbugs:spotbugs'
      }
      post { always {
        recordIssues tools: [checkStyle(), spotBugs()]
      } }
    }
    stage('Couverture') {
      steps { sh './mvnw -B jacoco:report' }
      post { always {
        recordCoverage tools: [[parser: 'JACOCO',
          pattern: 'target/site/jacoco/jacoco.xml']]
      } }
    }
    stage('Image') {
      steps { script {
        // env.TAG (et pas TAG=...) : la valeur traverse toutes les stages,
        // interpolation Groovy comme shell.
        env.TAG = sh(script: 'git rev-parse --short=7 HEAD',
            returnStdout: true).trim()
        sh "docker build -f Dockerfile.ci -t ${REGISTRE}/taskboard:${TAG} ."
      } }
    }
    stage('Scan') {
      steps {
        // Porte de controle BLOQUANTE : la base Trivy est figee au master
        // (--skip-db-update) et l'image verifiee propre a la preparation —
        // aucune CVE nouvelle ne peut rougir la salle en cours de session.
        sh """trivy image --skip-db-update \
          --severity CRITICAL --ignore-unfixed \
          --exit-code 1 --format json --output trivy.json \
          ${REGISTRE}/taskboard:${TAG}"""
      }
      post { always {
        recordIssues tools: [trivy(pattern: 'trivy.json')]
      } }
    }
    stage('Publier') {
      steps { sh "docker push ${REGISTRE}/taskboard:${TAG}" }
    }
  }

  post {
    success { echo "Publié : ${REGISTRE}/taskboard:${env.TAG}" }
    failure { echo 'Pipeline échoué — ouvrir la stage en rouge et lire la première erreur' }
  }
}
