package validators;

import dto.UserRegistrationDto;
import entity.Role;
import validators.util.ValidationError;
import validators.util.ValidationResult;

import java.util.regex.Pattern;

public class UserRegistrationValidator implements Validator<UserRegistrationDto>{

    private UserRegistrationValidator(){};

    private static UserRegistrationValidator INSTANCE = new UserRegistrationValidator();

    public static UserRegistrationValidator getInstance() {
        return INSTANCE;
    }

    private String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";

    private Pattern emailValidationPattern = Pattern.compile(emailRegex);

    @Override
    public ValidationResult isValid(UserRegistrationDto user) {
        var result = new ValidationResult();
        var matcher = emailValidationPattern.matcher(user.getEmail());
        if (user.getName()==null || user.getName().replaceAll(" ", "").length()<1){
            result.addValidationError(ValidationError.of("invalid.name", "Field name have to be entered"));
        }
        if (!matcher.matches()){
            result.addValidationError(ValidationError.of("invalid.email", "Email format is invalid"));
        }
        if (Role.find(user.getRole()).isEmpty()){
            result.addValidationError(ValidationError.of("invalid.role", "Entered role does not exist"));
        }
        if (user.getPassword().replaceAll(" ", "").length()<4){
            result.addValidationError(ValidationError.of("invalid.password", "Password must have more than 4 symbols"));
        }
        return result;
    }
}
