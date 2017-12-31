package nl.bldn.project.stublime.rest;

import static nl.bldn.project.stublime.rest.StubEndpoint.STUB_ROOT;
import static org.springframework.http.HttpMethod.resolve;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import nl.bldn.project.stublime.model.ResponseDefinition;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.service.StubDelayService;
import nl.bldn.project.stublime.service.StubResponseService;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = STUB_ROOT + "/**")
@AllArgsConstructor
@Slf4j
public class StubEndpoint {
    static final String STUB_ROOT = "/stub";

    private final StubResponseService responseService;
    private final StubDelayService delayService;

    @RequestMapping(method = {GET, POST, PUT, DELETE})
    public ResponseEntity<String> stub(HttpServletRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String matchedUrlPath = removeStubRootPath((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
        HttpMethod httpMethod = resolve(request.getMethod());
        String requestBody = extractRequestBody(request);

        StubResponse stubResponse = responseService.getStubResponse(matchedUrlPath, httpMethod, requestBody);
        stopWatch.stop();

        if (stubResponse == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        delayService.delayResponse(stubResponse.getResponseTiming(), stopWatch.getLastTaskTimeMillis());

        ResponseDefinition response = stubResponse.getResponse();
        return new ResponseEntity<>(response.getResponseContent(), HttpStatus.valueOf(response.getResponseStatusCode()));
    }

    private static String removeStubRootPath(String urlPath) {
        return urlPath.replace(STUB_ROOT, "");
    }

    private static String extractRequestBody(HttpServletRequest request) {
        String requestBody;
        try (ServletInputStream inputStream = request.getInputStream()) {
            requestBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Exception during reading request inputstream: ", e);
            throw new RuntimeException(e);
        }
        return requestBody;
    }
}
