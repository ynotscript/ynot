package ynot.core.exception.provider;

/**
 * This exception is throw when we can't provide a command.
 * @author equesada
 */
public class UnprovidableCommandException extends
    UnprovidableException {

    /**
     * The serial Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor.
     */
    public UnprovidableCommandException() {
        super();
    }

    /**
     * Constructor using fields.
     * @param message the message of the error
     * @param cause the cause of the error
     */
    public UnprovidableCommandException(final String message,
        final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor using fields.
     * @param message the message of the error
     */
    public UnprovidableCommandException(final String message) {
        super(message);
    }

    /**
     * Constructor using fields.
     * @param cause the cause of the error
     */
    public UnprovidableCommandException(final Throwable cause) {
        super(cause);
    }

}
