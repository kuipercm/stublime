package nl.bldn.project.stublime.service;

import static nl.bldn.project.stublime.predicate.BodySignaturePredicateFactory.createBodySignaturePredicate;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.repository.impl.InMemoryStubResponseRepository;
import nl.bldn.project.stublime.validation.StubResponseValidator;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StubResponseService {
    private final InMemoryStubResponseRepository repository;
    private final StubResponseValidator validator;

    public List<StubResponse> getAllResponses() {
        return repository.getAllStubResponses();
    }

    public StubResponse getStubResponse(String resource, HttpMethod method, String requestBody) {
        return repository.getStubResponse(resource, method, requestBody);
    }

    public void setStubResponse(StubResponse stubResponse) {
        validator.validateStubResponse(stubResponse);

        ResponseKey key = stubResponse.getKey();
        if (key.getId() == null) {
            key.setId(UUID.randomUUID());
        }

        String resource = key.getResource();
        key.setCompiledResource(Pattern.compile(resource));

        if (key.getBodyType() != null) {
            key.setBodySignaturePredicate(createBodySignaturePredicate(key.getBodyType(), key.getExpectedBodySignature(), key.getSignatureElementsJoiner(), key.getBodySignatureExpressions()));
        }

        repository.saveStubResponse(stubResponse);
    }


}
