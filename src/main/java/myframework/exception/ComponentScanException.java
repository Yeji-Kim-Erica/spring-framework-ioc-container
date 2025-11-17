package myframework.exception;

public class ComponentScanException extends RuntimeException {
    public ComponentScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
