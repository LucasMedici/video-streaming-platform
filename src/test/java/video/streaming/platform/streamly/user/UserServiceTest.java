package video.streaming.platform.streamly.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import video.streaming.platform.streamly.exceptions.EmailAlreadyExistsException;
import video.streaming.platform.streamly.exceptions.UserNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserShouldPersistEncodedPasswordAndDefaultRole() {
        CreateUserDTO dto = new CreateUserDTO("Ana", "ana@mail.com", "123456");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-pwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser(dto);

        assertEquals("Ana", created.getName());
        assertEquals("ana@mail.com", created.getEmail());
        assertEquals("encoded-pwd", created.getPasswordHash());
        assertEquals(UserRoles.USER, created.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserShouldThrowWhenEmailAlreadyExists() {
        CreateUserDTO dto = new CreateUserDTO("Ana", "ana@mail.com", "123456");
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(dto)
        );

        assertTrue(exception.getMessage().contains(dto.email()));
    }

    @Test
    void getUserByIdShouldThrowWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUserShouldPersistUpdatedFields() {
        UUID userId = UUID.randomUUID();
        User user = new User("Old", "old@mail.com", "old-hash", UserRoles.USER);
        CreateUserDTO dto = new CreateUserDTO("Novo Nome", "novo@mail.com", "654321");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any(String.class))).thenReturn("updated-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(userId, dto);

        assertEquals("Novo Nome", updated.getName());
        assertEquals("novo@mail.com", updated.getEmail());
        assertEquals("updated-hash", updated.getPasswordHash());
        verify(userRepository).save(user);
    }
}

