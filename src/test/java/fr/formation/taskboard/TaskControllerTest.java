package fr.formation.taskboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test de la couche web seule — le repository est simule : pas de base requise. */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TaskRepository repository;

    @Test
    void laPageAfficheLesTaches() throws Exception {
        when(repository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(new Task("Déployer sur Kubernetes")));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Déployer sur Kubernetes")));
    }

    @Test
    void ajouterUneTacheRedirigeVersLAccueil() throws Exception {
        mvc.perform(post("/tasks").param("title", "Nouvelle tâche"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).save(any(Task.class));
    }
}
