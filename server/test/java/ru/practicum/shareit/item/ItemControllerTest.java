package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Test
    void addNewItem_shouldReturnItem() throws Exception {

        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");

        when(itemService.addNewItem(eq(1L), any()))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {

        ItemDto dto = new ItemDto();
        dto.setName("Updated");

        when(itemService.updateItem(eq(1L), eq(1L), any()))
                .thenReturn(dto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {

        ItemWithBookingDto dto = ItemWithBookingDto.builder()
                .id(1L)
                .name("Drill")
                .build();

        when(itemService.getItem(1L))
                .thenReturn(dto);

        mvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateItem_shouldReturn404WhenItemNotFound() throws Exception {
        when(itemService.getItem(1L))
                .thenThrow(new NotFoundException("Предмет не найден"));

        mvc.perform(get("/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByOwner_shouldReturnItems() throws Exception {

        when(itemService.getAllByOwner(1L))
                .thenReturn(List.of());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void search_shouldReturnItems() throws Exception {

        when(itemService.searchItem("drill"))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {

        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("good");

        CommentDto result = new CommentDto();
        result.setId(1L);

        when(itemService.addComment(eq(1L), eq(1L), any()))
                .thenReturn(result);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}