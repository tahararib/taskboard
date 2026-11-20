package fr.formation.taskboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Contrôle de configuration rattaché à la sonde readiness (branche dokub, lab 7).
 *
 * Quand app.feature.required=true (image v3), l'application exige la variable
 * FEATURE_URL. Si elle manque, la sonde /actuator/health/readiness répond DOWN :
 * le pod démarre mais n'est jamais « prêt », Kubernetes ne lui envoie aucun
 * trafic et le rolling update reste bloqué, sans interruption de service.
 *
 * Nom du composant de santé : « featureConfig » (nom de la classe sans
 * le suffixe HealthIndicator), référencé dans application.properties.
 */
@Component
public class FeatureConfigHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(FeatureConfigHealthIndicator.class);

    private final boolean required;
    private final String featureUrl;
    private boolean warned;

    public FeatureConfigHealthIndicator(@Value("${app.feature.required:false}") boolean required,
                                        @Value("${app.feature.url:}") String featureUrl) {
        this.required = required;
        this.featureUrl = featureUrl;
    }

    @Override
    public Health health() {
        if (!required) {
            return Health.up().withDetail("feature", "non requise").build();
        }
        if (featureUrl == null || featureUrl.isBlank()) {
            if (!warned) {
                log.warn("Configuration incomplète : la variable FEATURE_URL est obligatoire depuis la version 3 ; "
                        + "le pod reste non prêt (readiness DOWN) tant qu'elle n'est pas fournie.");
                warned = true;
            }
            return Health.down().withDetail("feature", "FEATURE_URL manquante").build();
        }
        return Health.up().withDetail("feature", featureUrl).build();
    }
}
