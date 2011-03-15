package ynot.core.provider.definition;

import ynot.core.entity.Definition;
import ynot.core.exception.provider.UnprovidableDefinitionException;
import ynot.core.listener.provider.definition.DefinitionProviderListener;
import ynot.core.provider.Provider;

/**
 * This interface is use to get back resources.
 * 
 * @param <T>
 *            the type of the definition identifier.
 * @author equesada
 */
public interface DefinitionProvider<T>
        extends
        Provider<
            Definition, 
            T, 
            UnprovidableDefinitionException, 
            DefinitionProviderListener> {
}
