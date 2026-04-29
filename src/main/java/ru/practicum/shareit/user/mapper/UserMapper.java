package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

//public final class UserMapper{
//    public static UserDto toUserDto(User user){
//        UserDto userDto = new UserDto();
//        userDto.setId(user.getId());
//        userDto.setName(user.getName());
//        userDto.setEmail(user.getEmail());
//        return userDto;
//    }
//
//    public static User toUser(UserDto userDto){
//        User user = new User();
//        user.setId(userDto.getId());
//        user.setName(userDto.getName());
//        user.setEmail(userDto.getEmail());
//        return user;
//    }
//}

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto toUserDto(User user); //map User to UserResponse

    User toUser(UserDto userDto);
}
