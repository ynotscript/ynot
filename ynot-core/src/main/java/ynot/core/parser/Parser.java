package ynot.core.parser;

import java.util.List;

import ynot.core.exception.parser.UnparsableException;
import ynot.core.listener.parser.ParserListener;

/**
 * This interface is used to parse object.
 * 
 * @author equesada
 * @param <T>
 *            the type of the return object.
 * @param <U>
 *            the type of object to parse.
 * @param <V>
 *            the type of authorized exception.
 * @param <W>
 *            the type of parser listener.
 */
public interface Parser<
    T, 
    U, 
    V extends UnparsableException, 
    W extends ParserListener<T, U>> {

    /**
     * To get an object from another object.
     * 
     * @param object
     *            the object to parse.
     * @return the object to get.
     * @throws V
     *             UnparsableException if not able to parse
     */
    T parse(U object) throws V;

    /**
     * To add a list of listener.
     * 
     * @param listeners
     *            the list of listeners to add.
     */
    void setListeners(List<W> listeners);
}
