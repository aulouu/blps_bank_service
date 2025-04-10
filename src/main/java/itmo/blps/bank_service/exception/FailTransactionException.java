package itmo.blps.bank_service.exception;

public class FailTransactionException extends RuntimeException {
    public FailTransactionException(String message) {
        super(message);
    }
}
