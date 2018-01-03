package nl.bldn.project.stublime.scenario;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.bldn.project.stublime.repository.StubResponseRepository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BodyMatchingRestScenarioIT {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private StubResponseRepository repository;

    @After
    public void cleanup() {
        repository.getAllStubResponses().forEach(response -> repository.deleteResponseById(response.getKey().getId()));
    }

    @Test
    public void when_setting_a_response_with_body_predicate_then_the_predicate_must_match_for_the_response_to_be_served() throws Exception {
        mockMvc.perform(post("/response")
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/123\", \"expectedBodySignature\": \"567AppleOrange\", \"bodyType\": \"JSON\", \"bodySignatureExpressions\": [\"$.id\", \"$.items[0].name\", \"$.items[1].name\"]}, \"response\": {\"responseContent\": \"hello world\"}}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/stub/sales/id/123"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/stub/sales/id/123")
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 567, \"customer\": \"John Doe\", \"items\": [{\"id\": 4, \"name\": \"Apple\"}, {\"id\": 5, \"name\": \"Orange\"}]}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));

        mockMvc.perform(get("/stub/sales/id/123")
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 567, \"customer\": \"John Doe\", \"items\": [{\"id\": 4, \"name\": \"Apple\"}, {\"id\": 6, \"name\": \"Banana\"}]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void when_setting_a_response_with_body_predicate_and_a_http_method_then_the_predicate_and_the_httpmethod_must_match_for_the_response_to_be_served() throws Exception {
        mockMvc.perform(post("/response")
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/123\", \"httpMethod\": \"POST\", \"expectedBodySignature\": \"567AppleOrange\", \"bodyType\": \"JSON\", \"bodySignatureExpressions\": [\"$.id\", \"$.items[0].name\", \"$.items[1].name\"]}, \"response\": {\"responseContent\": \"hello world\"}}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/stub/sales/id/123"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/stub/sales/id/123")
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 567, \"customer\": \"John Doe\", \"items\": [{\"id\": 4, \"name\": \"Apple\"}, {\"id\": 5, \"name\": \"Orange\"}]}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/stub/sales/id/123")
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 567, \"customer\": \"John Doe\", \"items\": [{\"id\": 4, \"name\": \"Apple\"}, {\"id\": 5, \"name\": \"Orange\"}]}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));
    }
}
