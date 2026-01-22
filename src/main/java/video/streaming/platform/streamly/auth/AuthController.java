package video.streaming.platform.streamly.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import video.streaming.platform.streamly.utils.JWTUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtil jwtUtil;
    public AuthController(AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
    }

    @PostMapping
    public String login(@RequestBody authLoginDTO authLoginDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authLoginDTO.email(), authLoginDTO.password()));
            return jwtUtil.generateToken(authLoginDTO.email());
        }catch (Exception e){
            throw e;
        }
    }
}
