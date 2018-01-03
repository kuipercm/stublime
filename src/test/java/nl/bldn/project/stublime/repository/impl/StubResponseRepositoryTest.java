package nl.bldn.project.stublime.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import nl.bldn.project.stublime.model.ResponseDefinition;
import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.repository.StubResponseRepository;

import org.junit.Test;

public class StubResponseRepositoryTest {
    private static final String SALE_ID_123 = "/sale/id/123";
    private static final String SALE_NAME_JOHNSON = "/sale/name/johnson";

    private final StubResponseRepository sut = new InMemoryStubResponseRepository();

    @Test
    public void when_no_responses_set_then_all_responses_is_emptylist() {
        assertThat(sut.getAllStubResponses()).isEmpty();
    }

    @Test
    public void when_contains_responses_set_then_all_responses_is_list_of_responses_maintaining_order() {
        sut.saveStubResponse(createStubResponse(SALE_ID_123));
        sut.saveStubResponse(createStubResponse(SALE_NAME_JOHNSON));

        List<StubResponse> responses = sut.getAllStubResponses();
        assertThat(responses).isNotEmpty();
        assertThat(responses.get(0).getKey().getResource()).isEqualTo(SALE_ID_123);
        assertThat(responses.get(1).getKey().getResource()).isEqualTo(SALE_NAME_JOHNSON);
    }

    private StubResponse createStubResponse(String resource) {
        return StubResponse.builder()
                .key(ResponseKey.builder()
                        .resource(resource)
                        .compiledResource(Pattern.compile(resource))
                        .id(UUID.randomUUID())
                        .build())
                .response(ResponseDefinition.builder().build())
                .responseTiming(ResponseTiming.builder().build())
                .build();
    }
}