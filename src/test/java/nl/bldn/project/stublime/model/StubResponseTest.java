package nl.bldn.project.stublime.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class StubResponseTest {

    @Test
    public void verify_equals_and_hashcode() {
        EqualsVerifier.forClass(StubResponse.class).verify();
    }

    @Test
    public void verify_creation() {
        UUID id = UUID.randomUUID();

        StubResponse build = StubResponse.builder()
                .key(ResponseKey.builder().id(id).build())
                .response(ResponseDefinition.builder().responseStatusCode(500).build())
                .responseTiming(ResponseTiming.builder().minimumDelay(200).build())
                .build();


        assertThat(build.getKey()).isNotNull();
        assertThat(build.getKey().getId()).isEqualTo(id);

        assertThat(build.getResponse()).isNotNull();
        assertThat(build.getResponse().getResponseStatusCode()).isEqualTo(500);

        assertThat(build.getResponseTiming()).isNotNull();
        assertThat(build.getResponseTiming().getMinimumDelay()).isEqualTo(200);
    }

    @Test
    public void verify_has_non_default_tostring() {
        StubResponse build = StubResponse.builder().key(ResponseKey.builder().build()).build();

        assertThat(build.toString()).contains("key");
    }


}