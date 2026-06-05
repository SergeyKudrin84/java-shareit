package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplateBuilder builder;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void shouldCreateClient() {

        when(builder.uriTemplateHandler(any()))
                .thenReturn(builder);

        when(builder.requestFactory(any(Supplier.class)))
                .thenReturn(builder);

        when(builder.build())
                .thenReturn(restTemplate);

        UserClient client =
                new UserClient("http://localhost:9090", builder);

        assertNotNull(client);
    }
}