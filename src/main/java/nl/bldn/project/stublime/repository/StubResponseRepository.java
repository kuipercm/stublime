package nl.bldn.project.stublime.repository;

import java.util.List;
import java.util.UUID;

import nl.bldn.project.stublime.model.StubResponse;

public interface StubResponseRepository {
    List<StubResponse> getAllStubResponses();
    StubResponse saveStubResponse(StubResponse stubResponse);
    void deleteResponseById(UUID responseId);
}
