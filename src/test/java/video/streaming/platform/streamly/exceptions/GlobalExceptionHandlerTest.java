package video.streaming.platform.streamly.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEmailAlreadyExistsShouldReturnConflict() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("user@mail.com");

        ResponseEntity<?> response = handler.handleEmailAlreadyExists(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("user@mail.com"));
    }

    @Test
    void handleUserNotFoundShouldReturnNotFound() {
        UUID userId = UUID.randomUUID();
        UserNotFoundException exception = new UserNotFoundException(userId);

        ResponseEntity<?> response = handler.handleUserNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains(userId.toString()));
    }
}

