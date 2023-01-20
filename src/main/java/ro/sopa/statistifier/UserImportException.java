package ro.sopa.statistifier;

public class UserImportException extends RuntimeException {

    public UserImportException(String message) {
        super(message);
    }

    public UserImportException(String message, Throwable t) {
        super(message, t);
    }
}
