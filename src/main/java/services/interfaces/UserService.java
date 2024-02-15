package services.interfaces;

import dto.UserDto;
import dto.UserRegistrationDto;

import java.util.Optional;

public interface UserService {
    Long save(UserRegistrationDto user);
    Optional<UserDto> login(String email, String password);

}
