package fr.formation.taskboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class TaskController {

    private final TaskRepository repository;
    private final String version;
    private final String banner;
    private final String bannerFile;

    public TaskController(TaskRepository repository,
                          @Value("${app.version:dev}") String version,
                          @Value("${app.banner:}") String banner,
                          @Value("${app.banner-file:}") String bannerFile) {
        this.repository = repository;
        this.version = version;
        this.banner = banner;         // lu UNE fois au démarrage (variable d'environnement APP_BANNER)
        this.bannerFile = bannerFile; // relu à CHAQUE requête (fichier monté depuis une ConfigMap)
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", repository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("hostname", hostname());
        model.addAttribute("version", version);
        model.addAttribute("banner", currentBanner());
        return "index";
    }

    @PostMapping("/tasks")
    public String add(@RequestParam String title) {
        if (title != null && !title.isBlank()) {
            repository.save(new Task(title.trim()));
        }
        return "redirect:/";
    }

    @PostMapping("/tasks/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        repository.findById(id).ifPresent(task -> {
            task.toggle();
            repository.save(task);
        });
        return "redirect:/";
    }

    /**
     * Lab 5 : un fichier monté depuis une ConfigMap est mis à jour par le kubelet
     * sans redémarrer le pod ; une variable d'environnement, elle, est figée au démarrage.
     * Le fichier, s'il est configuré et lisible, est donc prioritaire.
     */
    String currentBanner() {
        if (!bannerFile.isBlank()) {
            try {
                return Files.readString(Path.of(bannerFile)).trim();
            } catch (IOException e) {
                return banner;
            }
        }
        return banner;
    }

    /** Nom de la machine qui sert la page — rend visibles le scale et le rolling update en K8s. */
    private String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "inconnu";
        }
    }
}
