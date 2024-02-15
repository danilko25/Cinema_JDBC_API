package exceptions;

import validators.util.ValidationError;

import java.util.List;

public class ValidationException extends RuntimeException {

    public ValidationException(List<ValidationError> errors){
        this.errors = errors;
    }
    private List<ValidationError> errors;

    public List<ValidationError> getErrors() {
        return errors;
    }
}
