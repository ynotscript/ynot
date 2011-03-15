package ynot.core.exception.parser;

/**
 * This exception is throw when we can parse a command definition.
 * @author equesada
 */
public class UnparsableArgumentException extends UnparsableException {

    /**
     * The serial Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor.
     */
    public UnparsableArgumentException() {
        super();
    }

    /**
     * Constructor using fields.
     * @param message the message of the error
     * @param cause the cause of the error
     */
    public UnparsableArgumentException(final String message,
        final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor using fields.
     * @param message the message of the error
     */
    public UnparsableArgumentException(final String message) {
        super(message);
    }

    /**
     * Constructor using fields.
     * @param cause the cause of the error
     */
    public UnparsableArgumentException(final Throwable cause) {
        super(cause);
    }

}
