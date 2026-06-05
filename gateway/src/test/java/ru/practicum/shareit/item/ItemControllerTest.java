package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void addNewItem() throws Exception {

        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);

        when(itemClient.addNewItem(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(ItemDto.class)
        )).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient)
                .addNewItem(
                        ArgumentMatchers.eq(1L),
                        ArgumentMatchers.any(ItemDto.class)
                );
    }

    @Test
    void updateItem() throws Exception {

        ItemDto dto = new ItemDto();
        dto.setName("Updated");

        when(itemClient.updateItem(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.eq(10L),
                ArgumentMatchers.any(ItemDto.class)
        )).thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/items/10")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient)
                .updateItem(
                        ArgumentMatchers.eq(1L),
                        ArgumentMatchers.eq(10L),
                        ArgumentMatchers.any(ItemDto.class)
                );
    }

    @Test
    void getItem() throws Exception {

        when(itemClient.getItem(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/1"))
                .andExpect(status().isOk());

        verify(itemClient).getItem(1L);
    }

    @Test
    void getAllByOwner() throws Exception {

        when(itemClient.getAllByOwner(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getAllByOwner(1L);
    }

    @Test
    void searchItem() throws Exception {

        when(itemClient.searchItem(1L, "drill"))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "drill"))
                .andExpect(status().isOk());

        verify(itemClient).searchItem(1L, "drill");
    }

    @Test
    void addComment() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Nice item");

        when(itemClient.addComment(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.eq(10L),
                ArgumentMatchers.any(CommentCreateDto.class)
        )).thenReturn(ResponseEntity.status(OK).build());

        mvc.perform(post("/items/10/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient)
                .addComment(
                        ArgumentMatchers.eq(1L),
                        ArgumentMatchers.eq(10L),
                        ArgumentMatchers.any(CommentCreateDto.class)
                );
    }

    @Test
    void addNewItem_shouldReturnBadRequest_whenNameIsBlank() throws Exception {

        ItemDto dto = new ItemDto();
        dto.setName("");
        dto.setDescription("desc");
        dto.setAvailable(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_shouldReturnBadRequest_whenTextIsBlank() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("");

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
