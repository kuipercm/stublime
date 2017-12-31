package nl.bldn.project.stublime.model;

import static java.util.Collections.emptyList;
import static nl.bldn.project.stublime.model.ResponseKey.DEFAULT_DASH_STRING_JOINER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import nl.bldn.project.stublime.predicate.JsonBodySignaturePredicate;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class ResponseKeyTest {

    @Test
    public void verify_equals_and_hashcode() {
        EqualsVerifier.forClass(ResponseKey.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void verify_default_joiner_is_empty_string() {
        ResponseKey build = ResponseKey.builder().build();
        assertThat(build.getSignatureElementsJoiner()).isEqualTo("");
    }

    @Test
    public void verify_default_expression_list_is_empty_list() {
        ResponseKey build = ResponseKey.builder().build();
        assertThat(build.getBodySignatureExpressions()).isNotNull();
        assertThat(build.getBodySignatureExpressions()).isEmpty();
    }

    @Test
    public void verify_creation() {
        UUID id = UUID.randomUUID();

        List<String> bodySignatureExpressions = new ArrayList<>();
        bodySignatureExpressions.add("$.id");
        bodySignatureExpressions.add("$.name");

        ResponseKey build = ResponseKey.builder()
                .resource("/sales")
                .httpMethod(POST)
                .expectedBodySignature("123-abc")
                .bodySignatureExpressions(bodySignatureExpressions)
                .signatureElementsJoiner(DEFAULT_DASH_STRING_JOINER)
                .bodyType("JSON")
                .build();

        assertThat(build.getResource()).isEqualTo("/sales");
        assertThat(build.getHttpMethod()).isEqualTo(POST);
        assertThat(build.getExpectedBodySignature()).isEqualTo("123-abc");
        assertThat(build.getSignatureElementsJoiner()).isEqualTo("-");
        assertThat(build.getBodySignatureExpressions()).isEqualTo(bodySignatureExpressions);
        assertThat(build.getBodyType()).isEqualTo("JSON");

        assertThat(build.getId()).isNull();
        assertThat(build.getCompiledResource()).isNull();
        assertThat(build.getBodySignaturePredicate()).isNull();

        build.setId(id);
        build.setCompiledResource(Pattern.compile(build.getResource()));
        build.setBodySignaturePredicate(new JsonBodySignaturePredicate(build.getExpectedBodySignature(), build.getSignatureElementsJoiner(), emptyList()));

        assertThat(build.getId()).isEqualTo(id);
        assertThat(build.getCompiledResource()).isNotNull();
        assertThat(build.getBodySignaturePredicate()).isNotNull();
    }

    @Test
    public void verify_has_non_default_tostring() {
        ResponseKey build = ResponseKey.builder().expectedBodySignature("123-abc").build();

        assertThat(build.toString()).contains("expectedBodySignature");
    }

}