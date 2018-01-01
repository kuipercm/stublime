package nl.bldn.project.stublime.validation.impl;

import static lombok.Lombok.checkNotNull;

import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.validation.StubResponseValidator;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultStubResponseValidator implements StubResponseValidator {
    @Override
    public void validateStubResponse(StubResponse response) {
        checkNotNull(response.getKey(), "responseKey");
        checkNotNull(response.getKey().getResource(), "responseKeyResource");
        if (response.getKey().getResource().isEmpty()) {
            throw new IllegalArgumentException("responseKeyResource cannot be empty");
        }

        checkNotNull(response.getResponse(), "responseValue");
    }
}
