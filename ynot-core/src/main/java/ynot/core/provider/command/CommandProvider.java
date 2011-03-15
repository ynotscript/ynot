package ynot.core.provider.command;

import java.util.List;

import ynot.core.entity.Command;
import ynot.core.exception.provider.UnprovidableCommandException;
import ynot.core.listener.provider.command.CommandProviderListener;
import ynot.core.provider.Provider;

/**
 * This interface is use to get back resources.
 * 
 * @param <T>
 *            the type of the command identifier.
 * @author equesada
 */
public interface CommandProvider<T>
        extends
        Provider<
            List<Command>, 
            T, 
            UnprovidableCommandException, 
            CommandProviderListener> {
}
