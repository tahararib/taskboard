package fr.formation.taskboard;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Insère trois tâches d'exemple au premier démarrage (base vide uniquement). */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(TaskRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Task("Construire l'image Docker"));
                repository.save(new Task("Déployer sur Kubernetes"));
                repository.save(new Task("Mettre en place le pipeline CI"));
            }
        };
    }
}
