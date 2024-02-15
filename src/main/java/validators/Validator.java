package validators;

import validators.util.ValidationResult;

public interface Validator<T> {

    ValidationResult isValid(T entity);
}
