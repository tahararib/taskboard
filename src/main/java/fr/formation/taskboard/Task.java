package fr.formation.taskboard;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.time.Instant;

/** Une tâche du tableau — l'unique entité métier de la formation. */
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private boolean done;

    private Instant createdAt;

    protected Task() {
        // requis par JPA
    }

    public Task(String title) {
        this.title = title;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    /** Bascule l'état fait / à faire. */
    public void toggle() {
        this.done = !this.done;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public boolean isDone() { return done; }
    public Instant getCreatedAt() { return createdAt; }
}
