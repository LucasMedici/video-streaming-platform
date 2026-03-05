package video.streaming.platform.streamly.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Tag to user operations")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper){
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @Operation(summary = "Create User", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ResponseUserDTO> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Create User Data", required = true)
            @RequestBody @Valid CreateUserDTO createUserDTO){
        User user = userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.entityToDTO(user));
    }

    @Operation(summary = "Get All Users", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ResponseUserDTO>> getAllUsers(){
        List<User> allUsers = userService.getAllUsers();

        List<ResponseUserDTO> responseUserDTOS = allUsers.stream()
                .map(user -> userMapper.entityToDTO(user))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDTOS);
    }

    @Operation(summary = "Get User by ID", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUserDTO> getUserById(
            @Parameter (description = "ID do Usuário", required = true)
            @PathVariable UUID userId){
        User userById = userService.getUserById(userId);
        ResponseUserDTO responseUserDTO = userMapper.entityToDTO(userById);
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDTO);
    }

    @Operation(summary = "Update User", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<ResponseUserDTO> updateUser(
            @Parameter(description = "ID do Usuário", required = true)
            @PathVariable UUID userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Update User Data", required = true)
            @RequestBody CreateUserDTO createUserDTO){
        User user = userService.updateUser(userId, createUserDTO);
        ResponseUserDTO responseUserDTO = userMapper.entityToDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDTO);
    }

    @Operation(summary = "Delete User", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do Usuário", required = true)
            @PathVariable UUID userId){
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
