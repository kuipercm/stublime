package nl.bldn.project.stublime.configuration;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class RestControllerExceptionHandlerConfiguration {
    @ResponseStatus(BAD_REQUEST) // 400
    @ExceptionHandler({ IllegalArgumentException.class })
    public void handleBadRequest(Exception e) {
        log(e);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(RuntimeException.class)
    public void handleAnyOtherRuntimeException(Exception e) {
        log(e);
    }

    private static void log(Exception e) {
        log.error(String.format("A %s occurred", e.getClass().getSimpleName()), e);
    }
}
