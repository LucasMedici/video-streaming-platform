package video.streaming.platform.streamly.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User createUser(CreateUserDTO createUserDTO){
        var newUser = new User(createUserDTO.name(),
                            createUserDTO.email(),
                            createUserDTO.password(),
                            UserRoles.USER);
        return userRepository.save(newUser);

    }
}
