package fr.formation.taskboard;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Test unitaire pur — aucune dependance, aucune base : la base de la pyramide. */
class TaskTest {

    @Test
    void uneTacheNeufEstAFaire() {
        Task task = new Task("Écrire le Jenkinsfile");
        assertThat(task.isDone()).isFalse();
    }

    @Test
    void toggleBasculeLEtat() {
        Task task = new Task("Écrire le Jenkinsfile");
        task.toggle();
        assertThat(task.isDone()).isTrue();
        task.toggle();
        assertThat(task.isDone()).isFalse();
    }
}
