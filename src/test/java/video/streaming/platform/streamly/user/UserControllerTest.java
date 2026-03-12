package video.streaming.platform.streamly.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Nested
    class createUser{

        @Test
        void shouldReturn201IfUserAccepted() throws Exception {
            var request = new CreateUserDTO(
                    "Lucas",
                    "lucas@gmail.com",
                    "123456"
            );
            var user = new User(
                    "Lucas",
                    "lucas@gmail.com",
                    "encodedPassword",
                    UserRoles.USER
            );
            when(userService.createUser(any(CreateUserDTO.class)))
                    .thenReturn(user);

            mockMvc.perform(post("/users")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isCreated());

            verify(userService, times(1))
                    .createUser(any(CreateUserDTO.class));
        }

    }
}