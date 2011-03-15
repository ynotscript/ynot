package ynot.core.exception.provider;

/**
 * This exception is throw when we can't provide the object.
 * @author equesada
 */
public class UnprovidableException extends Exception {

    /**
     * The serial Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor using fields.
     * @param message the message of the error
     * @param cause the cause of the error
     */
    public UnprovidableException(final String message,
        final Throwable cause) {
        super(message, cause);
    }

    /**
     * The default constructor.
     */
    public UnprovidableException() {
        super();
    }

    /**
     * Constructor using fields.
     * @param cause the cause of the error
     */
    public UnprovidableException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor using fields.
     * @param message the message of the error
     */
    public UnprovidableException(final String message) {
        super(message);
    }

}
