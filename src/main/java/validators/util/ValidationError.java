package validators.util;

public class ValidationError {
    private ValidationError(String code, String message){
        this.code = code;
        this.message = message;
    };

    public static ValidationError of(String code, String message){
        return new ValidationError(code, message);
    }
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
