package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    @Query("""
            select i
            from Item i
            join fetch i.owner
            left join i.request
            where i.available = true
            and (
                    upper(i.name) like upper(concat('%', ?1, '%'))
                    or upper(i.description) like upper(concat('%', ?1, '%'))
                )
            """)
    List<Item> searchByTextInNameOrDescription(String text);

    @Query("""
            select i
            from Item i
            join fetch i.owner
            left join i.request
            where i.id = ?1
            """)
    Optional<Item> findByIdWithOwnerAndItemRequest(Long itemId);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> requestIds);
}
