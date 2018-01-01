package nl.bldn.project.stublime.rest;

import static nl.bldn.project.stublime.rest.StubEndpoint.STUB_ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import nl.bldn.project.stublime.model.ResponseDefinition;
import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.service.StubResponseService;
import nl.bldn.project.stublime.timing.StubDelayService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

public class StubEndpointTest {
    private static final String SALE_ID_123 = "/sale/id/123";

    private StubResponseService stubResponseService;
    private StubDelayService stubDelayService;
    private StubEndpoint sut;

    @Before
    public void setup() {
        stubResponseService = mock(StubResponseService.class);
        stubDelayService = mock(StubDelayService.class);

        sut = new StubEndpoint(stubResponseService, stubDelayService);
    }

    @Test
    public void when_request_to_stub_endpoint_then_response_is_determined_from_service_using_path_method_and_body() {
        when(stubResponseService.getStubResponse(SALE_ID_123, GET, ""))
                .thenReturn(StubResponse.builder()
                        .response(ResponseDefinition.builder()
                                .responseContent("hello world")
                                .responseStatusCode(200)
                                .build())
                        .build());

        MockHttpServletRequest request = createRequest(STUB_ROOT + SALE_ID_123, GET);
        ResponseEntity<String> stubResponse = sut.stub(request);

        assertThat(stubResponse.getStatusCode()).isEqualTo(OK);
        assertThat(stubResponse.getBody()).isEqualTo("hello world");
    }

    @Test
    public void when_request_to_stub_endpoint_and_no_response_found_then_response_not_found() {
        when(stubResponseService.getStubResponse(SALE_ID_123, GET, ""))
                .thenReturn(null);

        MockHttpServletRequest request = createRequest(STUB_ROOT + SALE_ID_123, GET);
        ResponseEntity<String> stubResponse = sut.stub(request);

        assertThat(stubResponse.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test(expected = RuntimeException.class)
    public void when_cannot_read_from_request_body_then_throw_exception() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenThrow(new IOException("test exception"));
        when(request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn(SALE_ID_123);
        when(request.getMethod()).thenReturn(GET.name());

        sut.stub(request);
    }

    @Test
    public void when_request_to_stub_endpoint_then_delayer_service_is_called_with_resolved_response_timing() {
        ResponseTiming responseTiming = ResponseTiming.builder().minimumDelay(200).maximumDelay(500).build();
        when(stubResponseService.getStubResponse(SALE_ID_123, GET, ""))
                .thenReturn(StubResponse.builder()
                        .response(ResponseDefinition.builder()
                                .responseContent("hello world")
                                .responseStatusCode(200)
                                .build())
                        .responseTiming(responseTiming)
                        .build());

        MockHttpServletRequest request = createRequest(STUB_ROOT + SALE_ID_123, GET);
        sut.stub(request);

        verify(stubDelayService).delayResponse(eq(responseTiming), any(Long.class));
    }

    private MockHttpServletRequest createRequest(String path, HttpMethod method) {
        MockHttpServletRequest request = new MockHttpServletRequest(method.name(), path);
        request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, path);
        return request;
    }


}