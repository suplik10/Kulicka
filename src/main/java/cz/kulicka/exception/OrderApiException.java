package cz.kulicka.exception;

public class OrderApiException extends RuntimeException {

    public OrderApiException() {
        super();
    }

    public OrderApiException(String message) {
        super(message);
    }
}
