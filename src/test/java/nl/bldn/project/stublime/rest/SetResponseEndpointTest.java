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
        sut.save(stubResponse);

        verify(stubResponseService).setStubResponse(stubResponse);
    }

    @Test(expected = NullPointerException.class)
    public void when_saving_a_response_and_the_response_is_null_than_an_exception_is_thrown() {
        sut.save(null);
    }

    @Test(expected = NullPointerException.class)
    public void when_saving_a_response_and_the_response_key_is_null_than_an_exception_is_thrown() {
        sut.save(StubResponse.builder().build());
    }

    @Test(expected = NullPointerException.class)
    public void when_updating_a_response_and_the_id_is_null_than_an_exception_is_thrown() {
        UUID id = UUID.randomUUID();
        StubResponse stubResponse = StubResponse.builder()
                .key(ResponseKey.builder()
                        .id(id)
                        .build())
                .build();
        sut.update(null, stubResponse);
    }

    @Test(expected = NullPointerException.class)
    public void when_updating_a_response_and_the_response_is_null_than_an_exception_is_thrown() {
        UUID id = UUID.randomUUID();
        sut.update(id, null);
    }

    @Test(expected = NullPointerException.class)
    public void when_updating_a_response_and_the_response_key_is_null_than_an_exception_is_thrown() {
        UUID id = UUID.randomUUID();
        StubResponse stubResponse = StubResponse.builder().build();
        sut.update(id, stubResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_updating_a_response_and_the_response_id_doesnt_match_the_url_id_is_null_than_an_exception_is_thrown() {
        StubResponse stubResponse = StubResponse.builder().key(ResponseKey.builder().id(UUID.randomUUID()).build()).build();
        sut.update(UUID.randomUUID(), stubResponse);
    }

    @Test
    public void when_updating_a_response_the_response_is_forwarded_to_the_service() {
        UUID id = UUID.randomUUID();
        StubResponse stubResponse = StubResponse.builder()
                .key(ResponseKey.builder()
                        .id(id)
                        .build())
                .build();
        sut.update(id, stubResponse);

        verify(stubResponseService).setStubResponse(stubResponse);
    }

    @Test(expected = NullPointerException.class)
    public void when_deleting_a_response_and_the_id_is_null_than_an_exception_is_thrown() {
        sut.delete(null);
    }

    @Test
    public void when_deleting_a_response_then_the_call_is_forwarded_to_the_service() {
        UUID id = UUID.randomUUID();
        sut.delete(id);

        verify(stubResponseService).deleteStubResponse(id);
    }
}