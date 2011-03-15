package ynot.core.provider.request;

import java.util.List;

import ynot.core.entity.Request;
import ynot.core.exception.provider.UnprovidableRequestException;
import ynot.core.listener.provider.request.RequestProviderListener;
import ynot.core.provider.Provider;

/**
 * This interface is use to get back resources.
 * 
 * @param <T>
 *            the type of the request identifier.
 * @author equesada
 */
public interface RequestProvider<T>
        extends
        Provider<List<Request>, 
        T, 
        UnprovidableRequestException, 
        RequestProviderListener> {

}
