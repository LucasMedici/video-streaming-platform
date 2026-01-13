package video.streaming.platform.streamly.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserDTO createUserDTO){
        var newUser = new User(createUserDTO.name(),
                            createUserDTO.email(),
                            passwordEncoder.encode(createUserDTO.password()),
                            UserRoles.USER);
        return userRepository.save(newUser);
    }
}
