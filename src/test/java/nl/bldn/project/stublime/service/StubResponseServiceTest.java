package nl.bldn.project.stublime.service;

import static nl.bldn.project.stublime.model.ResponseKey.DEFAULT_EMPTY_STRING_JOINER;
import static nl.bldn.project.stublime.predicate.BodySignaturePredicateFactory.BODY_TYPE_JSON;
import static nl.bldn.project.stublime.predicate.BodySignaturePredicateFactory.createBodySignaturePredicate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import nl.bldn.project.stublime.model.ResponseDefinition;
import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.repository.impl.InMemoryStubResponseRepository;
import nl.bldn.project.stublime.validation.StubResponseValidator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;

public class StubResponseServiceTest {
    private static final String SALE_ID_123 = "/sale/id/123";
    private static final String SALE_NAME_JOHNSON = "/sale/name/johnson";

    private InMemoryStubResponseRepository stubResponseRepository;
    private StubResponseValidator stubResponseValidator;
    private StubResponseService sut;

    @Before
    public void setup() {
        stubResponseRepository = new InMemoryStubResponseRepository();
        stubResponseValidator = mock(StubResponseValidator.class);

        sut = new StubResponseService(stubResponseRepository, stubResponseValidator);
    }

    @Test
    public void when_getting_all_responses_then_all_responses_from_repository_are_returned() {
        StubResponse stubResponse = createStubResponse(SALE_ID_123);
        stubResponseRepository.saveStubResponse(stubResponse);

        List<StubResponse> allResponses = sut.getAllResponses();

        assertThat(allResponses).containsExactly(stubResponse);
    }

    @Test
    public void when_setting_response_then_validator_is_called_with_response_to_be_set() {
        StubResponse stubResponse = createStubResponse(SALE_ID_123);

        sut.setStubResponse(stubResponse);

        verify(stubResponseValidator).validateStubResponse(stubResponse);
    }

    @Test
    public void when_setting_response_and_response_key_does_not_contain_id_then_id_is_set() {
        StubResponse stubResponse = createStubResponse(SALE_ID_123);

        sut.setStubResponse(stubResponse);

        assertThat(stubResponse.getKey().getId()).isNotNull();
    }

    @Test
    public void when_setting_response_then_compiled_pattern_for_resource_is_set() {
        StubResponse stubResponse = createStubResponse(SALE_ID_123);

        sut.setStubResponse(stubResponse);

        assertThat(stubResponse.getKey().getCompiledResource().pattern()).isEqualTo(SALE_ID_123);
    }

    @Test
    public void when_get_response_and_resource_matches_pattern_and_no_other_conditions_then_response_is_returned() {
        StubResponse saved = createStubResponse(SALE_ID_123);
        sut.setStubResponse(saved);

        StubResponse stubResponse = sut.getStubResponse(SALE_ID_123, GET, null);

        assertThat(stubResponse).isEqualTo(saved);
    }

    @Test
    public void when_get_response_and_resource_doesnt_match_pattern_and_no_other_conditions_then_response_is_null() {
        StubResponse saved = createStubResponse(SALE_ID_123);
        sut.setStubResponse(saved);

        StubResponse stubResponse = sut.getStubResponse(SALE_NAME_JOHNSON, GET, null);

        assertThat(stubResponse).isNull();
    }

    @Test
    public void when_get_response_and_resource_matches_pattern_and_must_match_method_and_does_match_then_response_is_returned() {
        StubResponse saved = createStubResponse(SALE_ID_123, POST);
        sut.setStubResponse(saved);

        StubResponse stubResponse = sut.getStubResponse(SALE_ID_123, POST, null);

        assertThat(stubResponse).isEqualTo(saved);
    }

    @Test
    public void when_get_response_and_resource_matches_pattern_and_must_match_method_but_doesnt_then_response_is_null() {
        StubResponse saved = createStubResponse(SALE_ID_123, POST);
        sut.setStubResponse(saved);

        StubResponse stubResponse = sut.getStubResponse(SALE_ID_123, null, null);

        assertThat(stubResponse).isNull();
    }

    @Test
    public void when_get_response_and_resource_matches_pattern_and_must_match_bodysignature_but_does_match_then_response_is_returned() {
        List<String> expressions = new ArrayList<>();
        expressions.add("$.id");
        expressions.add("$.name");

        StubResponse saved = createStubResponseWithBodySignature(SALE_ID_123, POST, "1a", expressions);
        sut.setStubResponse(saved);

        StubResponse stubResponse = sut.getStubResponse(SALE_ID_123, POST, "{\"id\":1, \"name\": \"a\"}");

        assertThat(stubResponse).isEqualTo(saved);
    }

    @Test
    public void when_get_response_and_resource_matches_pattern_and_must_match_bodysignature_but_doesnt_then_response_is_null() {
        List<String> expressions = new ArrayList<>();
        expressions.add("$.id");
        expressions.add("$.name");

        StubResponse saved = createStubResponseWithBodySignature(SALE_ID_123, POST, "1a", expressions);
        sut.setStubResponse(saved);

        StubResponse stubResponse = sut.getStubResponse(SALE_ID_123, POST, "{\"id\":1, \"name\": \"b\"}");

        assertThat(stubResponse).isNull();
    }

    @Test
    public void when_setting_stub_response_with_same_id_twice_then_second_save_replaces_first() {
        StubResponse stubResponse1 = createStubResponse(SALE_ID_123);
        sut.setStubResponse(stubResponse1);

        StubResponse stubResponse2 = createStubResponse(SALE_ID_123);
        stubResponse2.getKey().setId(stubResponse1.getKey().getId());
        sut.setStubResponse(stubResponse2);

        assertThat(sut.getAllResponses()).hasSize(1);
        assertThat(sut.getStubResponse(SALE_ID_123, GET, "").getKey().getId()).isNotNull();
    }

    @Test
    public void when_setting_stub_response_with_pattern_gets_matched() {
        StubResponse stubResponse1 = createStubResponse("sale/id/\\d*");
        sut.setStubResponse(stubResponse1);

        assertThat(sut.getAllResponses()).hasSize(1);
        assertThat(sut.getStubResponse(SALE_ID_123, GET, "")).isNotNull();
        assertThat(sut.getStubResponse("sale/id/456", GET, "")).isNotNull();
        assertThat(sut.getStubResponse(SALE_NAME_JOHNSON, GET, "")).isNull();
    }

    private StubResponse createStubResponse(String resource) {
        return createStubResponse(resource, null);
    }

    private StubResponse createStubResponse(String resource, HttpMethod method) {
        return StubResponse.builder()
                .key(ResponseKey.builder()
                        .resource(resource)
                        .httpMethod(method)
                        .compiledResource(Pattern.compile(resource))
                        .id(UUID.randomUUID())
                        .build())
                .response(ResponseDefinition.builder().build())
                .responseTiming(ResponseTiming.builder().build())
                .build();
    }

    private StubResponse createStubResponseWithBodySignature(String resource, HttpMethod method, String expectedSignature, List<String> bodySignatureExpressions) {
        return StubResponse.builder()
                .key(ResponseKey.builder()
                        .resource(resource)
                        .httpMethod(method)
                        .compiledResource(Pattern.compile(resource))
                        .bodySignaturePredicate(createBodySignaturePredicate(BODY_TYPE_JSON, expectedSignature, DEFAULT_EMPTY_STRING_JOINER, bodySignatureExpressions))
                        .build())
                .response(ResponseDefinition.builder().build())
                .responseTiming(ResponseTiming.builder().build())
                .build();
    }

}