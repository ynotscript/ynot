package ynot.core.exception.parser;

/**
 * This exception is throw when we can't parse.
 * @author equesada
 */
public class UnparsableException extends Exception {

    /**
 * 
 */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor using fields.
     * @param message the message of the error
     * @param cause the cause of the error
     */
    public UnparsableException(final String message,
        final Throwable cause) {
        super(message, cause);
    }

    /**
     * The default constructor.
     */
    public UnparsableException() {
        super();
    }

    /**
     * Constructor using fields.
     * @param cause the cause of the error
     */
    public UnparsableException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor using fields.
     * @param message the message of the error
     */
    public UnparsableException(final String message) {
        super(message);
    }

}
