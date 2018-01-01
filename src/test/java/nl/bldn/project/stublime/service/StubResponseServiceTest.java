package nl.bldn.project.stublime.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

import java.util.ArrayList;
import java.util.List;

import nl.bldn.project.stublime.model.ResponseDefinition;
import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.repository.impl.InMemoryStubResponseRepository;
import nl.bldn.project.stublime.validation.StubResponseValidator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpMethod;

public class StubResponseServiceTest {
    private static final String SALE_ID_123 = "/sale/id/123";

    private InMemoryStubResponseRepository stubResponseRepository;
    private StubResponseValidator stubResponseValidator;
    private StubResponseService sut;

    @Before
    public void setup() {
        stubResponseRepository = mock(InMemoryStubResponseRepository.class);
        stubResponseValidator = mock(StubResponseValidator.class);

        sut = new StubResponseService(stubResponseRepository, stubResponseValidator);
    }

    @Test
    public void when_getting_all_responses_then_all_responses_from_repository_are_returned() {
        List<StubResponse> stubResponses = new ArrayList<>();
        stubResponses.add(createStubResponse());

        when(stubResponseRepository.getAllStubResponses()).thenReturn(stubResponses);

        List<StubResponse> allResponses = sut.getAllResponses();

        assertThat(allResponses).isEqualTo(stubResponses);
    }

    @Test
    public void when_getting_single_response_then_response_from_repository_is_returned() {
        StubResponse stubResponse = createStubResponse();

        when(stubResponseRepository.getStubResponse(anyString(), any(HttpMethod.class), anyString()))
                .thenReturn(stubResponse);

        StubResponse singleResponse = sut.getStubResponse("", GET, "");

        assertThat(singleResponse).isEqualTo(stubResponse);
    }

    @Test
    public void when_setting_response_then_validator_is_called_with_response_to_be_set() {
        StubResponse stubResponse = createStubResponse();

        sut.setStubResponse(stubResponse);

        verify(stubResponseValidator).validateStubResponse(stubResponse);
    }

    @Test
    public void when_setting_response_and_response_key_does_not_contain_id_then_id_is_set() {
        StubResponse stubResponse = createStubResponse();

        sut.setStubResponse(stubResponse);

        ArgumentCaptor<StubResponse> captor = ArgumentCaptor.forClass(StubResponse.class);
        verify(stubResponseRepository).saveStubResponse(captor.capture());

        assertThat(captor.getValue().getKey().getId()).isNotNull();
    }

    @Test
    public void when_setting_response_then_compiled_pattern_for_resource_is_set() {
        StubResponse stubResponse = createStubResponse();

        sut.setStubResponse(stubResponse);

        ArgumentCaptor<StubResponse> captor = ArgumentCaptor.forClass(StubResponse.class);
        verify(stubResponseRepository).saveStubResponse(captor.capture());

        assertThat(captor.getValue().getKey().getCompiledResource().pattern()).isEqualTo(SALE_ID_123);
    }

    private StubResponse createStubResponse() {
        return StubResponse.builder()
                .key(ResponseKey.builder()
                        .resource(SALE_ID_123)
                        .build())
                .response(ResponseDefinition.builder().build())
                .responseTiming(ResponseTiming.builder().build())
                .build();
    }

}