package mapper.user;

import dto.UserDto;
import dto.UserRegistrationDto;
import entity.CinemaUser;
import mapper.Mapper;

public class UserDtoMapper implements Mapper<UserDto, CinemaUser> {
    private UserDtoMapper(){}

    private static UserDtoMapper INSTANCE = new UserDtoMapper();

    public static UserDtoMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public UserDto mapFrom(CinemaUser entity) {
        return new UserDto(entity.getId(), entity.getName(), entity.getEmail(), entity.getRole().name());
    }
}
