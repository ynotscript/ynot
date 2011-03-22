package ynot.core.provider;

import java.util.List;

import ynot.core.exception.provider.UnprovidableException;
import ynot.core.listener.provider.ProviderListener;

/**
 * This interface is use to get back resources.
 * 
 * @author equesada
 * @param <T>
 *            the type of the resource.
 * @param <U>
 *            the type of the resource identifier.
 * @param <V>
 *            the kind of exception extending UnprovidableException.
 * @param <W>
 *            the kind of Listener.
 */
public interface Provider<T, U, V extends UnprovidableException, W extends ProviderListener<T>> {

	/**
	 * Get the name of the provider.
	 * 
	 * @return the provider name;
	 */
	String getName();

	/**
	 * To get a resource from informations.
	 * 
	 * @param info
	 *            the informations.
	 * @throws U
	 *             if unable to provide.
	 * @return the resource.
	 */
	T get(U info) throws V;

	/**
	 * To know if the provider have another resource to give.
	 * 
	 * @return true if there is another resource.
	 * @throws V
	 *             if unable to provide.
	 */
	boolean hasNext() throws V;

	/**
	 * To have the next resource.
	 * 
	 * @throws U
	 *             if unable to provide.
	 * @return the another resource.
	 */
	T getNext() throws V;

	/**
	 * To add a list of listeners.
	 * 
	 * @param listeners
	 *            the list of listeners to add.
	 */
	void setListeners(List<W> listeners);
}
