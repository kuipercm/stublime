package nl.bldn.project.stublime.validation.impl;

import nl.bldn.project.stublime.model.ResponseDefinition;
import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.validation.StubResponseValidator;

import org.junit.Test;

public class DefaultStubResponseValidatorTest {
    private final StubResponseValidator sut = new DefaultStubResponseValidator();

    @Test(expected = NullPointerException.class)
    public void response_with_null_key_throws_exception() {
        sut.validateStubResponse(StubResponse.builder().build());
    }

    @Test(expected = NullPointerException.class)
    public void response_with_null_key_resource_throws_exception() {
        sut.validateStubResponse(StubResponse.builder()
                .key(ResponseKey.builder().build())
                .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void response_with_empty_key_resource_throws_exception() {
        sut.validateStubResponse(StubResponse.builder()
                .key(ResponseKey.builder().resource("").build()).build());
    }

    @Test(expected = NullPointerException.class)
    public void response_with_null_response_throws_exception() {
        sut.validateStubResponse(StubResponse.builder()
                .key(ResponseKey.builder().resource("/sales").build())
                .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_responsetiming_set_then_minimum_response_time_should_be_larger_or_equal_to_zero() {
        sut.validateStubResponse(StubResponse.builder()
                .key(ResponseKey.builder().resource("/sales").build())
                .response(ResponseDefinition.builder().build())
                .responseTiming(ResponseTiming.builder().minimumDelay(-1).build())
                .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_responsetiming_set_then_maximum_response_time_should_be_larger_or_equal_to_zero() {
        sut.validateStubResponse(StubResponse.builder()
                .key(ResponseKey.builder().resource("/sales").build())
                .response(ResponseDefinition.builder().build())
                .responseTiming(ResponseTiming.builder().maximumDelay(-1).build())
                .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_responsetiming_set_then_maximum_response_time_should_be_larger_or_equal_to_minimum_response_time() {
        sut.validateStubResponse(StubResponse.builder()
                .key(ResponseKey.builder().resource("/sales").build())
                .response(ResponseDefinition.builder().build())
                .responseTiming(ResponseTiming.builder().maximumDelay(0).minimumDelay(1).build())
                .build());
    }

}