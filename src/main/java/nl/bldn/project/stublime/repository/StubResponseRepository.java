package nl.bldn.project.stublime.repository;

import java.util.List;

import nl.bldn.project.stublime.model.StubResponse;

import org.springframework.http.HttpMethod;

public interface StubResponseRepository {
    List<StubResponse> getAllStubResponses();
    StubResponse getStubResponse(String resource, HttpMethod method, String requestBody);
    void saveStubResponse(StubResponse stubResponse);
}
