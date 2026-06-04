package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User author;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");
        owner = userRepository.save(owner);

        author = new User();
        author.setName("Author");
        author.setEmail("author@mail.ru");
        author = userRepository.save(author);

        item1 = new Item();
        item1.setName("Drill");
        item1.setDescription("Power drill");
        item1.setAvailable(true);
        item1.setOwner(owner);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Steel hammer");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2 = itemRepository.save(item2);
    }

    @Test
    void findByItemIdOrderByCreatedDesc_shouldReturnCommentsSortedDesc() {

        Comment oldComment = Comment.builder()
                .text("Old comment")
                .item(item1)
                .author(author)
                .created(LocalDateTime.now().minusDays(2))
                .build();

        Comment newComment = Comment.builder()
                .text("New comment")
                .item(item1)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(oldComment);
        commentRepository.save(newComment);

        List<Comment> comments =
                commentRepository.findByItemIdOrderByCreatedDesc(item1.getId());

        assertThat(comments).hasSize(2);

        assertThat(comments.get(0).getText())
                .isEqualTo("New comment");

        assertThat(comments.get(1).getText())
                .isEqualTo("Old comment");
    }

    @Test
    void findByItemIdOrderByCreatedDesc_shouldReturnEmptyList() {

        List<Comment> comments =
                commentRepository.findByItemIdOrderByCreatedDesc(item1.getId());

        assertThat(comments).isEmpty();
    }

    @Test
    void findByItemIdInOrderByCreatedDesc_shouldReturnCommentsForSeveralItems() {

        Comment comment1 = Comment.builder()
                .text("Comment item1")
                .item(item1)
                .author(author)
                .created(LocalDateTime.now().minusHours(2))
                .build();

        Comment comment2 = Comment.builder()
                .text("Comment item2")
                .item(item2)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        List<Comment> comments =
                commentRepository.findByItemIdInOrderByCreatedDesc(
                        List.of(item1.getId(), item2.getId())
                );

        assertThat(comments).hasSize(2);

        assertThat(comments.get(0).getText())
                .isEqualTo("Comment item2");

        assertThat(comments.get(1).getText())
                .isEqualTo("Comment item1");
    }

    @Test
    void findByItemIdInOrderByCreatedDesc_shouldReturnOnlyRequestedItems() {

        Comment comment1 = Comment.builder()
                .text("Comment item1")
                .item(item1)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .text("Comment item2")
                .item(item2)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        List<Comment> comments =
                commentRepository.findByItemIdInOrderByCreatedDesc(
                        List.of(item1.getId())
                );

        assertThat(comments).hasSize(1);
        assertThat(comments.getFirst().getItem().getId())
                .isEqualTo(item1.getId());
    }
}