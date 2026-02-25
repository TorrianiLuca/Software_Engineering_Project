package exceptions;

public class DisconnectedPartsException extends RuntimeException {
    public DisconnectedPartsException(String message) {
        super(message);
    }
}
