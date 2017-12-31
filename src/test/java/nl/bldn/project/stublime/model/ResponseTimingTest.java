package nl.bldn.project.stublime.model;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class ResponseTimingTest {

    @Test
    public void verify_equals_and_hashcode() {
        EqualsVerifier.forClass(ResponseTiming.class).verify();
    }

    @Test
    public void verify_creation() {
        ResponseTiming build = ResponseTiming.builder()
                .minimumDelay(200)
                .maximumDelay(500)
                .variance(1)
                .build();

        assertThat(build.getMinimumDelay()).isEqualTo(200);
        assertThat(build.getMaximumDelay()).isEqualTo(500);
        assertThat(build.getVariance()).isEqualTo(1);
    }

    @Test
    public void verify_has_non_default_tostring() {
        ResponseTiming build = ResponseTiming.builder()
                .minimumDelay(200)
                .build();

        assertThat(build.toString()).contains("minimumDelay");
    }

}