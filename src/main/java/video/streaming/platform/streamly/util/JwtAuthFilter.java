package video.streaming.platform.streamly.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import video.streaming.platform.streamly.user.User;
import video.streaming.platform.streamly.user.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    JWTUtil jwtUtil;
    private UserRepository userRepository;

    public JwtAuthFilter(JWTUtil jwtUtil, UserRepository userRepository){
        this.jwtUtil = jwtUtil;
        this.userRepository=userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null  || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("token: "+token);
        String username = null;
        if(token.isBlank()){
            filterChain.doFilter(request,response);
            return;
        }

        try{
            username = jwtUtil.extractTokenUsername(token);
            if(username != null && SecurityContextHolder.getContext().getAuthentication()==null) {
                Optional<User> userOptional = userRepository.findByEmail(username);

                if(userOptional.isPresent() && jwtUtil.isTokenValid(token)) {
                    User userDetails = userOptional.get();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }catch (Exception e){
            System.out.println("JWT Inválido: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
