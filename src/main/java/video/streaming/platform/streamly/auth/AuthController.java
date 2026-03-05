package video.streaming.platform.streamly.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import video.streaming.platform.streamly.utils.JWTUtil;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Tag to auth operations")
public class AuthController {

    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtil jwtUtil;
    public AuthController(AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
    }

    @Operation(summary = "Log in to the app", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PostMapping
    public String login(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login data", required = true)
                            @RequestBody authLoginDTO authLoginDTO){
        try{
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authLoginDTO.email(), authLoginDTO.password()));
            var userDetails = (org.springframework.security.core.userdetails.UserDetails) authenticate.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            return jwtUtil.generateToken(userDetails.getUsername(), role);
        }catch (Exception e){
            throw e;
        }
    }
}
