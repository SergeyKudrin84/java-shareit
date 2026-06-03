package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        log.info("Create item request {}", dto);
        User user = findUserById(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        log.info("Save item request {}", savedItemRequest);
        return itemRequestMapper.toRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        log.info("Get all requests for user {}", userId);
        findUserById(userId);
        List<ItemRequest> requests = requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);
        List<Long> requestsIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> allItems = itemRepository.findByRequestIdIn(requestsIds);
        Map<Long, List<ItemForRequestDto>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(
                                itemRequestMapper::toItemForRequestDto,
                                Collectors.toList()
                        )
                ));
        log.info("found {} all requests for user {}", requests.size(), userId);
        return requests.stream()
                .map(rq ->{
                    Long rqId = rq.getId();
                    ItemRequestDto itemRequestDto = itemRequestMapper.toRequestDto(rq);
                    itemRequestDto.setItems(itemsByRequestId.getOrDefault(rqId, List.of()));
                    return itemRequestDto;
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        log.info("Get own requests for user {}", userId);
        findUserById(userId);
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        log.info("found {} own requests for user {}", requests.size(), userId);
        return itemRequestMapper.toRequestDtoList(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Get request by id {}", requestId);
        findUserById(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    String message = "Запрос не найден";
                    log.warn(message);
                    return new NotFoundException(message);
                });
        List<Item> items = itemRepository.findByRequestId(requestId);
        ItemRequestDto  itemRequestDto = itemRequestMapper.toRequestDto(request);
        itemRequestDto.setItems(itemRequestMapper.toItemForRequestDtoList(items));
        log.info("found requests {}", itemRequestDto);
        return itemRequestDto;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String message = "Пользователь не найден";
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }
}
