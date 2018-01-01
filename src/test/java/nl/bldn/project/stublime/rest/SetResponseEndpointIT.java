package nl.bldn.project.stublime.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.service.StubResponseService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = SetResponseEndpointIT.TestConfiguration.class)
public class SetResponseEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StubResponseService service;

    @Autowired
    private SetResponseEndpoint sut;

    @Before
    public void setup() {
        when(service.setStubResponse(any(StubResponse.class))).then((Answer<StubResponse>) invocationOnMock -> invocationOnMock.getArgumentAt(0, StubResponse.class));
    }

    @Test
    public void when_executing_get_then_all_responses_are_returned() throws Exception {
        UUID keyId = UUID.randomUUID();

        List<StubResponse> serviceResponses = new ArrayList<>();
        serviceResponses.add(StubResponse.builder()
                .key(ResponseKey.builder().id(keyId).build())
                .build());
        when(service.getAllResponses()).thenReturn(serviceResponses);

        mockMvc.perform(get("/response"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].key.id").value(keyId.toString()));
    }

    @Test
    public void when_posting_new_response_then_success_status_and_the_uuid_is_returned() throws Exception {
        UUID keyId = UUID.randomUUID();

        mockMvc.perform(post("/response").contentType(APPLICATION_JSON).content("{\"key\": {\"id\": \"" + keyId.toString() + "\"}}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.key.id").value(keyId.toString()));

        verify(service).setStubResponse(StubResponse.builder()
                .key(ResponseKey.builder().id(keyId).build())
                .build());
    }

    @Test
    public void when_updating_an_existing_response_then_success_status_is_returned() throws Exception {
        UUID keyId = UUID.randomUUID();

        mockMvc.perform(put("/response/{responseId}", keyId)
                .contentType(APPLICATION_JSON)
                .content("{\"key\": {\"id\": \"" + keyId.toString() + "\"}}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.key.id").value(keyId.toString()));

        verify(service).setStubResponse(StubResponse.builder()
                .key(ResponseKey.builder().id(keyId).build())
                .build());
    }

    @Test
    public void when_deleting_an_existing_response_then_success_status_is_returned() throws Exception {
        UUID keyId = UUID.randomUUID();

        mockMvc.perform(delete("/response/{responseId}", keyId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        verify(service).deleteStubResponse(keyId);
    }


    @Configuration
    @Import(SetResponseEndpoint.class)
    static class TestConfiguration {

    }
}