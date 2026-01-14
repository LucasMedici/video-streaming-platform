package video.streaming.platform.streamly.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper){
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<ResponseUserDTO> createUser(@RequestBody @Valid CreateUserDTO createUserDTO){
        User user = userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.entityToDTO(user));
    }

    @GetMapping
    public ResponseEntity<List<ResponseUserDTO>> getAllUsers(){
        List<User> allUsers = userService.getAllUsers();

        List<ResponseUserDTO> responseUserDTOS = allUsers.stream()
                .map(user -> userMapper.entityToDTO(user))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDTOS);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUserDTO> getUserById(@PathVariable UUID userId){
        User userById = userService.getUserById(userId);
        ResponseUserDTO responseUserDTO = userMapper.entityToDTO(userById);
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseUserDTO> updateUser(@PathVariable UUID userId, @RequestBody CreateUserDTO createUserDTO){
        User user = userService.updateUser(userId, createUserDTO);
        ResponseUserDTO responseUserDTO = userMapper.entityToDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId){
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
