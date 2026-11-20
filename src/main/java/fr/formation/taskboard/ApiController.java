package fr.formation.taskboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Endpoints techniques des labs Kubernetes (branche dokub).
 * Réponses en texte brut sur une ligne : lisibles dans une boucle curl
 * et comptables avec « sort | uniq -c ».
 */
@RestController
public class ApiController {

    /** Plafond de calcul par requête : une requête ne peut pas bloquer un pod plus de 2 s. */
    static final int MAX_MS = 2000;

    private final String version;

    public ApiController(@Value("${app.version:dev}") String version) {
        this.version = version;
    }

    /**
     * Version servie et pod qui répond.
     * Labs 2 (répartition), 7 (rolling update, canary), 10 (perte d'un nœud).
     */
    @GetMapping(value = "/api/version", produces = MediaType.TEXT_PLAIN_VALUE)
    public String version() {
        return "version=" + version + " pod=" + hostname() + "\n";
    }

    /**
     * Consomme du CPU pendant « ms » millisecondes (boucle de calcul active).
     * Lab 9 : c'est la charge que mesure le HorizontalPodAutoscaler.
     */
    @GetMapping(value = "/api/compute", produces = MediaType.TEXT_PLAIN_VALUE)
    public String compute(@RequestParam(defaultValue = "100") int ms) {
        int duree = Math.max(1, Math.min(ms, MAX_MS));
        long fin = System.nanoTime() + duree * 1_000_000L;
        double acc = 0;
        long iterations = 0;
        while (System.nanoTime() < fin) {
            acc += Math.sqrt(iterations++);   // calcul sans effet, uniquement pour occuper le CPU
        }
        return "calcul=" + duree + "ms iterations=" + iterations + " pod=" + hostname()
                + (acc < 0 ? "!" : "") + "\n";   // acc lu pour que le JIT ne supprime pas la boucle
    }

    static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "inconnu";
        }
    }
}
