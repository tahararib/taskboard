package fr.formation.taskboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureConfigHealthIndicatorTest {

    @Test
    void upQuandLaFonctionNestPasRequise() {
        assertThat(new FeatureConfigHealthIndicator(false, "").health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void downQuandRequiseEtUrlAbsente() {
        var health = new FeatureConfigHealthIndicator(true, " ").health();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("feature", "FEATURE_URL manquante");
    }

    @Test
    void upQuandRequiseEtUrlFournie() {
        assertThat(new FeatureConfigHealthIndicator(true, "http://feature:8080").health().getStatus())
                .isEqualTo(Status.UP);
    }
}
