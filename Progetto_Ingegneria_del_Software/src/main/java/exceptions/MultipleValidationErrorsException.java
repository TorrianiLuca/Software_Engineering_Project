package exceptions;

import java.util.ArrayList;

public class MultipleValidationErrorsException extends Exception {
    private final ArrayList<String> errorMessages;

    public MultipleValidationErrorsException(ArrayList<String> errorMessages) {
        super("Multiple validation errors occurred");
        this.errorMessages = new ArrayList<>(errorMessages);
    }

    public ArrayList<String> getErrorMessages() {
        return errorMessages;
    }
}