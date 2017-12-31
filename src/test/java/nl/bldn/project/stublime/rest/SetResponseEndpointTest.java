package nl.bldn.project.stublime.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.service.StubResponseService;

import org.junit.Before;
import org.junit.Test;

public class SetResponseEndpointTest {

    private StubResponseService stubResponseService;
    private SetResponseEndpoint sut;

    @Before
    public void setup() {
        stubResponseService = mock(StubResponseService.class);
        sut = new SetResponseEndpoint(stubResponseService);
    }

    @Test
    public void when_getting_all_responses_the_responses_from_the_service_are_returned() {
        List<StubResponse> serviceResponses = new ArrayList<>();
        serviceResponses.add(StubResponse.builder()
                .key(ResponseKey.builder().id(UUID.randomUUID()).build())
                .build());
        when(stubResponseService.getAllResponses()).thenReturn(serviceResponses);

        List<StubResponse> allResponses = sut.getAllResponses();

        assertThat(allResponses).isEqualTo(serviceResponses);
    }

    @Test
    public void when_setting_a_new_response_the_response_is_forwarded_to_the_service() {
        StubResponse stubResponse = StubResponse.builder()
                .key(ResponseKey.builder().id(UUID.randomUUID()).build())
                .build();
        sut.setNewResponse(stubResponse);

        verify(stubResponseService).setStubResponse(stubResponse);
    }

}