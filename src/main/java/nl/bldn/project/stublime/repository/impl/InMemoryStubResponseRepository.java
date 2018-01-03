package nl.bldn.project.stublime.repository.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.repository.StubResponseRepository;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryStubResponseRepository implements StubResponseRepository {
    private final Map<UUID, StubResponse> allResponses = new LinkedHashMap<>();

    @Override
    public List<StubResponse> getAllStubResponses() {
        return new ArrayList<>(allResponses.values());
    }

    @Override
    public StubResponse saveStubResponse(StubResponse stubResponse) {
        ResponseKey key = stubResponse.getKey();
        if (key.getId() == null) {
            key.setId(UUID.randomUUID());
        }

        allResponses.put(stubResponse.getKey().getId(), stubResponse);
        return stubResponse;
    }

    @Override
    public void deleteResponseById(UUID responseId) {
        allResponses.remove(responseId);
    }

}