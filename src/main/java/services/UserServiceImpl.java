package services;

import dao.CinemaUserDao;
import dao.interfaces.IUserDao;
import dto.UserDto;
import dto.UserRegistrationDto;
import exceptions.ValidationException;
import mapper.user.UserDtoMapper;
import mapper.user.RegistrationUserMapper;
import services.interfaces.UserService;
import validators.UserRegistrationValidator;

import java.util.Optional;

public class UserServiceImpl implements UserService {
    private UserServiceImpl(){}

    private static UserServiceImpl INSTANCE = new UserServiceImpl();

    public static UserServiceImpl getInstance() {
        return INSTANCE;
    }

    private IUserDao userDao = CinemaUserDao.getInstance();
    private RegistrationUserMapper userMapper = RegistrationUserMapper.getInstance();
    private UserRegistrationValidator userRegistrationValidator = UserRegistrationValidator.getInstance();

    @Override
    public Long save(UserRegistrationDto userDto) {
        var validationResult = userRegistrationValidator.isValid(userDto);
        if (!validationResult.isValid()){
            throw new ValidationException(validationResult.getErrors());
        }
        return userDao.save(userMapper.mapFrom(userDto)).getId();
    }

    @Override
    public Optional<UserDto> login(String email, String password) {
        return userDao.getUserByEmailAndPassword(email, password).map(cinemaUser -> UserDtoMapper.getInstance().mapFrom(cinemaUser));
    }
}
