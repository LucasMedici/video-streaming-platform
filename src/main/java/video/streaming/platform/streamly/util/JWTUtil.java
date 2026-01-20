package video.streaming.platform.streamly.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {
    private final String SECRET = "my-super-secret-key-with-at-least-32-bytes";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final long EXPIRATION_TIME = 1000*60*60; //1 hour

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +  EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractTokenUsername(String token) {
        Claims body = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return body.getSubject();
    }

    public boolean isTokenValid(String token) {
        Claims tokenBody = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date tokenExpiration = tokenBody.getExpiration();
        Date currentDate = new Date();

        if(currentDate.before(tokenExpiration)){
            return true;
        }
        return false;
    }
}
