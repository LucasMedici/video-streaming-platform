package video.streaming.platform.streamly.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    public AuthController(AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
    }

    @PostMapping
    public String generateToken(@RequestBody authLoginDTO authLoginDTO){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authLoginDTO.email(), authLoginDTO.password())
        );
        return "jwt token";
    }
}
