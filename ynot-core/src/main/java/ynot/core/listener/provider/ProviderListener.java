package ynot.core.listener.provider;

/**
 * Able to be registered to the Provider.
 * 
 * @author equesada
 * @param <T>
 *            the type of object to provide.
 */
public interface ProviderListener<T> {

    /**
     * Call before to provide.
     * 
     * @param objToProvide
     *            the object to provide.
     * @return the new object to provide.
     */
    T preNotice(T objToProvide);

    /**
     * Call after to provide.
     * 
     * @param result
     *            the result of the provide.
     * @return false indicate to jump to the next one else true.
     */
    boolean postNotice(T result);

}
