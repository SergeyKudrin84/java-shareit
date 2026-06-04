package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void create() throws Exception {

        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        when(itemRequestClient.create(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(ItemRequestCreateDto.class)
        )).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemRequestClient).create(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(ItemRequestCreateDto.class)
        );
    }

    @Test
    void getOwnRequests() throws Exception {

        when(itemRequestClient.getOwnRequests(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestClient).getOwnRequests(1L);
    }

    @Test
    void getAllRequests() throws Exception {

        when(itemRequestClient.getAllRequests(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(1L);
    }

    @Test
    void getRequestById() throws Exception {

        when(itemRequestClient.getRequestById(1L, 10L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestClient)
                .getRequestById(1L, 10L);
    }

    @Test
    void create_shouldReturnBadRequest_whenDescriptionIsBlank() throws Exception {

        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnBadRequest_whenDescriptionIsNull() throws Exception {

        String json = """
            {
            }
            """;

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}