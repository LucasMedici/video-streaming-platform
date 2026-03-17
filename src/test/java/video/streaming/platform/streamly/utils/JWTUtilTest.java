package video.streaming.platform.streamly.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTUtilTest {

    private final JWTUtil jwtUtil = new JWTUtil();

    @Test
    void generateAndExtractShouldReturnSameUsername() {
        String token = jwtUtil.generateToken("user@mail.com", "ROLE_USER");

        String username = jwtUtil.extractTokenUsername(token);

        assertEquals("user@mail.com", username);
    }

    @Test
    void isTokenValidShouldReturnTrueForFreshToken() {
        String token = jwtUtil.generateToken("user@mail.com", "ROLE_USER");

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void extractTokenUsernameShouldThrowForInvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.extractTokenUsername("invalid-token"));
    }
}

