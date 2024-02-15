package exceptions;

public class EntityNotExistException extends RuntimeException{
    public EntityNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
