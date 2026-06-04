package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");

        owner = userRepository.save(owner);
    }

    @Test
    void addNewItem_shouldSavedItem() {

        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("Good drill");
        dto.setAvailable(true);

        ItemDto saved = itemService.addNewItem(owner.getId(), dto);

        Item item = itemRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals("Drill", item.getName());
        assertEquals("Good drill", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner.getId(), item.getOwner().getId());
    }

    @Test
    void addNewItem_shouldThrowWhenOwnerNotFound() {

        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("Good drill");
        dto.setAvailable(true);

        assertThrows(
                NotFoundException.class,
                () -> itemService.addNewItem(99999L, dto)
        );
    }

    @Test
    void updateItem_shouldUpdateDatabase() {

        Item item = new Item();
        item.setName("Old");
        item.setDescription("Old description");
        item.setAvailable(true);
        item.setOwner(owner);

        item = itemRepository.save(item);

        ItemDto patch = new ItemDto();
        patch.setName("New");

        itemService.updateItem(
                owner.getId(),
                item.getId(),
                patch
        );

        Item updated =
                itemRepository.findById(item.getId())
                        .orElseThrow();

        assertEquals("New", updated.getName());
        assertEquals("Old description",
                updated.getDescription());
    }

    @Test
    void updateItem_shouldThrowWhenNotOwner() {

        User anotherUser = new User();
        anotherUser.setName("Another");
        anotherUser.setEmail("another@mail.ru");

        anotherUser = userRepository.save(anotherUser);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        item = itemRepository.save(item);

        Long itemId = item.getId();

        ItemDto patch = new ItemDto();
        patch.setName("Updated");

        Long anotherUserId = anotherUser.getId();

        assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(
                        anotherUserId,
                        itemId,
                        patch
                )
        );
    }

    @Test
    void getItem_shouldReturnSavedItem() {

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        item = itemRepository.save(item);

        ItemWithBookingDto result = itemService.getItem(item.getId());

        assertEquals(item.getId(), result.getId());
        assertEquals("Drill", result.getName());
    }

    @Test
    void searchItem_shouldFindByName() {

        Item item = new Item();
        item.setName("Super Drill");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        itemRepository.save(item);

        List<ItemDto> result =
                itemService.searchItem("drill");

        assertEquals(1, result.size());
        assertEquals("Super Drill",
                result.get(0).getName());
    }

    @Test
    void searchItem_shouldReturnEmptyListForBlankText() {

        List<ItemDto> result =
                itemService.searchItem(" ");

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByOwner_shouldReturnOwnerItems() {

        Item item1 = new Item();
        item1.setName("Drill");
        item1.setDescription("Description");
        item1.setAvailable(true);
        item1.setOwner(owner);

        Item item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Description");
        item2.setAvailable(true);
        item2.setOwner(owner);

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<ItemWithBookingDto> result =
                itemService.getAllByOwner(owner.getId());

        assertEquals(2, result.size());
    }

    @Test
    void getAllByOwner_shouldThrowWhenOwnerNotFound() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.getAllByOwner(99999L)
        );
    }


}
