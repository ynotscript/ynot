package ynot.core.listener.parser;

/**
 * Able to be registered to the Parser.
 * 
 * @author equesada
 * @param <T>
 *            the type of the return object.
 * @param <U>
 *            the type of object to parse.
 */
public interface ParserListener<T, U> {

    /**
     * Call before to parse.
     * 
     * @param objToParse
     *            the object to parse.
     * @return the new object to parse.
     */
    U preNotice(U objToParse);

    /**
     * Call after to parse.
     * 
     * @param result
     *            the result of the parse.
     * @return false indicate to jump to the next one else true.
     */
    boolean postNotice(T result);

}
