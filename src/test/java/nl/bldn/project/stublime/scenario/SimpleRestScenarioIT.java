package nl.bldn.project.stublime.scenario;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import nl.bldn.project.stublime.repository.StubResponseRepository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import lombok.Getter;
import lombok.Setter;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SimpleRestScenarioIT {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private StubResponseRepository repository;

    private final ResultCatcher resultCatcher = new ResultCatcher();

    @After
    public void cleanup() {
        repository.getAllStubResponses().forEach(response -> repository.deleteResponseById(response.getKey().getId()));
    }

    @Test
    public void when_setting_a_response_without_method_the_stub_responds_with_this_response_on_any_method_to_the_correct_resource() throws Exception {
        mockMvc.perform(post("/response")
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/123\"}, \"response\": {\"responseContent\": \"hello world\"}}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.key.id").isNotEmpty())
                .andDo(mvcResult -> {
                    JsonPath path = JsonPath.compile("$.key.id");
                    String responseId = path.read(mvcResult.getResponse().getContentAsString());
                    resultCatcher.setResponseId(UUID.fromString(responseId));
                });

        mockMvc.perform(get("/stub/sales/id/123"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));

        mockMvc.perform(put("/stub/sales/id/123"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));

        mockMvc.perform(get("/stub/sales/id/456"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void when_setting_a_response_with_method_the_stub_responds_with_this_response_on_only_this_method_to_the_correct_resource() throws Exception {
        mockMvc.perform(post("/response")
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/123\", \"httpMethod\": \"GET\"}, \"response\": {\"responseContent\": \"hello world\"}}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.key.id").isNotEmpty())
                .andDo(mvcResult -> {
                    JsonPath path = JsonPath.compile("$.key.id");
                    String responseId = path.read(mvcResult.getResponse().getContentAsString());
                    resultCatcher.setResponseId(UUID.fromString(responseId));
                });

        mockMvc.perform(get("/stub/sales/id/123"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));

        mockMvc.perform(put("/stub/sales/id/123"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/stub/sales/id/456"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void when_updating_a_response_all_fields_can_be_modified_except_the_uuid() throws Exception {
        mockMvc.perform(post("/response")
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/123\", \"httpMethod\": \"GET\"}, \"response\": {\"responseContent\": \"hello world\"}}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.key.id").isNotEmpty())
                .andDo(mvcResult -> {
                    JsonPath path = JsonPath.compile("$.key.id");
                    String responseId = path.read(mvcResult.getResponse().getContentAsString());
                    resultCatcher.setResponseId(UUID.fromString(responseId));
                });

        mockMvc.perform(get("/stub/sales/id/123"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));

        String responseIdString = resultCatcher.getResponseId().toString();
        mockMvc.perform(put("/response/" + responseIdString)
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/456\", \"httpMethod\": \"PUT\", \"id\": \"" + responseIdString + "\"}, \"response\": {\"responseContent\": \"goodbye\"}}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/stub/sales/id/123"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/stub/sales/id/456"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("goodbye"));

        mockMvc.perform(put("/response/" + responseIdString)
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/123\", \"httpMethod\": \"GET\", \"id\": \"" + UUID.randomUUID() + "\"}, \"response\": {\"responseContent\": \"hello world\"}}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void when_setting_a_response_using_pattern_matching_then_the_response_is_returned_on_any_matching_request() throws Exception {
        mockMvc.perform(post("/response")
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/[0-9]+\", \"httpMethod\": \"GET\"}, \"response\": {\"responseContent\": \"hello world\"}}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/stub/sales/id/123"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));

        mockMvc.perform(get("/stub/sales/id/9999"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));

        mockMvc.perform(get("/stub/sales/id/apple"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void when_setting_a_response_with_a_response_content_type_then_the_response_is_returned_and_contains_a_content_type() throws Exception {
        mockMvc.perform(post("/response")
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"resource\": \"/sales/id/123\", \"httpMethod\": \"GET\"}, \"response\": {\"responseContent\": \"{'name': 'John Doe'}\", \"responseContentType\": \"application/json\"}}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/stub/sales/id/123"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    private static class ResultCatcher {
        @Getter
        @Setter
        private UUID responseId;
    }
}
