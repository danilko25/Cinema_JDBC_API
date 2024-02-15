package validators.util;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private List<ValidationError> errors = new ArrayList<>();

    public List<ValidationError> getErrors(){
        return errors;
    }

    public boolean isValid(){
        return errors.isEmpty();
    }

    public void addValidationError(ValidationError error){
        errors.add(error);
    }

}
