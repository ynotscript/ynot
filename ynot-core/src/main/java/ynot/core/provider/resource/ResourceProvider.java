package ynot.core.provider.resource;

import ynot.core.entity.Resource;
import ynot.core.exception.provider.UnprovidableResourceException;
import ynot.core.listener.provider.resource.ResourceProviderListener;
import ynot.core.provider.Provider;

/**
 * This interface is use to get back resources.
 * 
 * @param <T>
 *            the type of the resource identifier.
 * @author equesada
 */
public interface ResourceProvider<T>
        extends
        Provider<
            Resource, 
            T, 
            UnprovidableResourceException, 
            ResourceProviderListener> {

}
