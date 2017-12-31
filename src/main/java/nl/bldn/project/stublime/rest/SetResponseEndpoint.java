package nl.bldn.project.stublime.rest;

import java.util.List;

import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.service.StubResponseService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/response")
@AllArgsConstructor
public class SetResponseEndpoint {
    private final StubResponseService responseService;

    @GetMapping
    public List<StubResponse> getAllResponses() {
        return responseService.getAllResponses();
    }

    @PostMapping
    public void setNewResponse(@RequestBody StubResponse response) {
        responseService.setStubResponse(response);
    }
}
