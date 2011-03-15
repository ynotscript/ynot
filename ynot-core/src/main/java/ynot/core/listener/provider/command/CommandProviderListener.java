package ynot.core.listener.provider.command;

import java.util.List;

import ynot.core.entity.Command;
import ynot.core.listener.provider.ProviderListener;


/**
 * This interface is used to listen the command provide event.
 * @author equesada
 */
public interface CommandProviderListener extends
    ProviderListener<List<Command>> {

}
