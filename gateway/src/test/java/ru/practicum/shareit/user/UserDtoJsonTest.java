package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void serializeUserDto() throws Exception {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Sergey");
        dto.setEmail("test@mail.com");

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Sergey");

        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("test@mail.com");
    }

    @Test
    void deserializeUserDto() throws Exception {

        String content = """
                {
                   "id": 1,
                   "name": "Sergey",
                   "email": "test@mail.com"
                 }
                """;

        UserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Sergey");
        assertThat(dto.getEmail()).isEqualTo("test@mail.com");
    }
}