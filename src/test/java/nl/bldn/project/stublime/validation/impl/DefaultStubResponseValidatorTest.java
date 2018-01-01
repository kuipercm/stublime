package nl.bldn.project.stublime.validation.impl;

import nl.bldn.project.stublime.model.ResponseKey;
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

}