package video.streaming.platform.streamly.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import video.streaming.platform.streamly.exceptions.EmailAlreadyExistsException;
import video.streaming.platform.streamly.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserDTO createUserDTO){

        if(userRepository.existsByEmail(createUserDTO.email())){
            throw new EmailAlreadyExistsException(createUserDTO.email());
        }

        var newUser = new User(createUserDTO.name(),
                            createUserDTO.email(),
                            passwordEncoder.encode(createUserDTO.password()),
                            UserRoles.USER);
        return userRepository.save(newUser);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(UUID userId){
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User updateUser(UUID userId, CreateUserDTO createUserDTO){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        user.setName(createUserDTO.name());
        user.setEmail(createUserDTO.email());
        user.setPasswordHash(passwordEncoder.encode(createUserDTO.name()));
        return userRepository.save(user);
    }

    public void deleteUser(UUID userID){
        if(userRepository.findById(userID).isEmpty()){
            throw new UserNotFoundException(userID);
        }

        userRepository.deleteById(userID);
    }
}
