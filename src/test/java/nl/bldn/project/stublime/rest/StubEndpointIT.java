package nl.bldn.project.stublime.rest;

import static nl.bldn.project.stublime.rest.StubEndpoint.STUB_ROOT;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.bldn.project.stublime.model.ResponseDefinition;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.service.StubDelayService;
import nl.bldn.project.stublime.service.StubResponseService;

import org.junit.Test;
import org.junit.runner.RunWith;
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
@ContextConfiguration(classes = StubEndpointIT.TestConfiguration.class)
public class StubEndpointIT {
    private static final String SALE_ID_123 = "/sale/id/123";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StubResponseService stubResponseService;
    @MockBean
    private StubDelayService stubDelayService;

    @Autowired
    private StubEndpoint sut;

    @Test
    public void when_request_to_stub_endpoint_then_response_is_determined_from_service_using_path_method_and_body() throws Exception {
        when(stubResponseService.getStubResponse(SALE_ID_123, GET, ""))
                .thenReturn(StubResponse.builder()
                        .response(ResponseDefinition.builder()
                                .responseContent("hello world")
                                .responseStatusCode(200)
                                .build())
                        .build());

        mockMvc.perform(get(STUB_ROOT + SALE_ID_123))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("hello world"));
    }

    @Test
    public void when_request_to_stub_endpoint_and_no_response_found_then_response_not_found() throws Exception {
        when(stubResponseService.getStubResponse(SALE_ID_123, GET, ""))
                .thenReturn(null);

        mockMvc.perform(get(STUB_ROOT + SALE_ID_123))
                .andExpect(status().isNotFound());
    }


    @Configuration
    @Import(StubEndpoint.class)
    static class TestConfiguration {

    }
}