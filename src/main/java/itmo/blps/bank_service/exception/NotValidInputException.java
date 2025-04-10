package itmo.blps.bank_service.exception;

public class NotValidInputException extends RuntimeException {
    public NotValidInputException(String message) {
        super(message);
    }
}
