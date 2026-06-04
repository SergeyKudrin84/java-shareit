package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Test
    void saveNewUser_shouldReturnOk() throws Exception {

        UserDto dto = new UserDto();
        dto.setName("Sergey");
        dto.setEmail("test@mail.com");

        when(userClient.saveUser(any()))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient).saveUser(any(UserDto.class));
    }

    @Test
    void saveNewUser_invalidEmail_shouldReturnBadRequest() throws Exception {

        UserDto dto = new UserDto();
        dto.setName("Sergey");
        dto.setEmail("wrong-email");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void deleteUser_shouldCallClient() throws Exception {

        when(userClient.deleteUser(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(1L);
    }

    @Test
    void getUser_shouldCallClient() throws Exception {

        when(userClient.getUserById(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).getUserById(1L);
    }

    @Test
    void updateUser_shouldCallClient() throws Exception {

        UserDto dto = new UserDto();
        dto.setName("Updated");

        when(userClient.updateUser(eq(1L), any()))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(1L), any(UserDto.class));
    }
}
