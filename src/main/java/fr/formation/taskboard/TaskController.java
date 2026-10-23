package fr.formation.taskboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class TaskController {

    private final TaskRepository repository;
    private final String version;

    public TaskController(TaskRepository repository,
                          @Value("${app.version:dev}") String version) {
        this.repository = repository;
        this.version = version;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", repository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("hostname", hostname());
        model.addAttribute("version", version);
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

    /** Nom de la machine qui sert la page — rend visibles le scale et le rolling update en K8s. */
    private String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "inconnu";
        }
    }
}
