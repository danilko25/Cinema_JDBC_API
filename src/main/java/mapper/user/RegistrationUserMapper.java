package mapper.user;

import dto.UserRegistrationDto;
import entity.CinemaUser;
import entity.Role;
import mapper.Mapper;

public class RegistrationUserMapper implements Mapper<CinemaUser, UserRegistrationDto> {

    private RegistrationUserMapper(){}

    private static RegistrationUserMapper INSTANCE = new RegistrationUserMapper();

    public static RegistrationUserMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public CinemaUser mapFrom(UserRegistrationDto entity) {
        return new CinemaUser(entity.getName(), entity.getEmail(), entity.getPassword(), Role.valueOf(entity.getRole()));
    }
}
