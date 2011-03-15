package ynot.core.exception.provider;

/**
 * This exception is throw when we can't provide a request.
 * @author equesada
 */
public class UnprovidableResourceException extends
    UnprovidableException {

    /**
     * The serial Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor.
     */
    public UnprovidableResourceException() {
        super();
    }

    /**
     * Constructor using fields.
     * @param message the message of the error
     * @param cause the cause of the error
     */
    public UnprovidableResourceException(final String message,
        final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor using fields.
     * @param message the message of the error
     */
    public UnprovidableResourceException(final String message) {
        super(message);
    }

    /**
     * Constructor using fields.
     * @param cause the cause of the error
     */
    public UnprovidableResourceException(final Throwable cause) {
        super(cause);
    }

}
