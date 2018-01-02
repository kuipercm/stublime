package nl.bldn.project.stublime.rest;

import static lombok.Lombok.checkNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.UUID;

import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.service.StubResponseService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/response", produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class SetResponseRestController {
    private final StubResponseService responseService;

    @GetMapping
    public List<StubResponse> getAllResponses() {
        return responseService.getAllResponses();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public StubResponse save(@RequestBody StubResponse response) {
        checkNotNull(response, "response");
        checkNotNull(response.getKey(), "responseKey");

        return responseService.setStubResponse(response);
    }

    @PutMapping(path = "/{responseId}", consumes = APPLICATION_JSON_VALUE)
    public StubResponse update(@PathVariable UUID responseId, @RequestBody StubResponse response) {
        checkNotNull(responseId, "responseId");
        checkNotNull(response, "response");
        checkNotNull(response.getKey(), "responseKey");

        if (!responseId.equals(response.getKey().getId())) {
            throw new IllegalArgumentException("Mismatching response and responseId: cannot update response");
        }

        return responseService.setStubResponse(response);
    }

    @DeleteMapping("/{responseId}")
    public void delete(@PathVariable UUID responseId) {
        checkNotNull(responseId, "responseId");

        responseService.deleteStubResponse(responseId);
    }
}
