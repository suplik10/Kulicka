package cz.kulicka.exception;

/**
 * An exception which can occur while invoking methods of the Binance API.
 */
public class BinanceApiException extends RuntimeException {

    private static final long serialVersionUID = 3788669840036201041L;

    public BinanceApiException() {
        super();
    }

    /**
     * Instantiates a new binance api exception.
     *
     * @param message the message
     */
    public BinanceApiException(String message) {
        super(message);
    }

    /**
     * Instantiates a new binance api exception.
     *
     * @param cause the cause
     */
    public BinanceApiException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new binance api exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public BinanceApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @return the response error object from Binance API, or null if no response object was returned (e.g. server returned 500).
     */
}
