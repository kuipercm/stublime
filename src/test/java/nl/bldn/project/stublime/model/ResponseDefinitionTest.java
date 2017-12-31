package nl.bldn.project.stublime.model;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class ResponseDefinitionTest {

    @Test
    public void verify_equals_and_hashcode() {
        EqualsVerifier.forClass(ResponseDefinition.class).verify();
    }

    @Test
    public void verify_default_status_code_is_200() {
        ResponseDefinition build = ResponseDefinition.builder().build();
        assertThat(build.getResponseStatusCode()).isEqualTo(200);
    }

    @Test
    public void verify_creation() {
        ResponseDefinition build = ResponseDefinition.builder()
                .responseContent("hello world")
                .responseStatusCode(404)
                .build();

        assertThat(build.getResponseContent()).isEqualTo("hello world");
        assertThat(build.getResponseStatusCode()).isEqualTo(404);
    }

    @Test
    public void verify_has_non_default_tostring() {
        ResponseDefinition build = ResponseDefinition.builder()
                .responseContent("hello world")
                .responseStatusCode(404)
                .build();

        assertThat(build.toString()).contains("responseContent");
    }

}