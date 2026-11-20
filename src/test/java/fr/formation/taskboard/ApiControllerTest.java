package fr.formation.taskboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
@TestPropertySource(properties = "app.version=9.9.9")
class ApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void versionRenvoieLaVersionEtLePod() throws Exception {
        mvc.perform(get("/api/version"))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("version=9.9.9 pod=")));
    }

    @Test
    void computeEstPlafonneA2Secondes() throws Exception {
        mvc.perform(get("/api/compute").param("ms", "999999"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("calcul=" + ApiController.MAX_MS + "ms")));
    }

    @Test
    void computeAccepteUneDureeCourte() throws Exception {
        mvc.perform(get("/api/compute").param("ms", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("calcul=5ms")));
    }
}
