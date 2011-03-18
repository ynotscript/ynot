package ynot.impl.listener.provider.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ynot.core.entity.Command;
import ynot.core.entity.Resource;
import ynot.core.entity.Shell;
import ynot.core.listener.provider.command.CommandProviderListener;

/**
 * The AddCommandsListener to unlock when go out from a block.
 * 
 * @author equesada
 */
public class UnlockManager implements CommandProviderListener {

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(UnlockManager.class);

	/**
	 * The unlocking commands.
	 */
	private List<String> unlockingCommands;

	@Override
	public final boolean postNotice(final List<Command> cmds) {
		return true;
	}

	@Override
	public final List<Command> preNotice(final List<Command> cmds) {
	    if (null == cmds) {
            return new ArrayList<Command>();
        }
	    
		for (int i = 0; i < cmds.size(); i++) {
			if (isUnlockingCmd(cmds.get(i))) {
				Command unlockCmd = new Command();
				unlockCmd.setForced(true);
				unlockCmd.setResourceToUse(new Resource("ynot", "shell", null));
				try {
					unlockCmd.setMethodToCall(Shell.class.getMethod("unlock"));
				} catch (NoSuchMethodException e) {
					logger.error("NoSuchMethodException", e);
				}
				unlockCmd.setArgumentsToGive(new Object[0]);
				cmds.add(i++, unlockCmd);
			}
		}

		return cmds;
	}

	/**
	 * To know if it's an unlocking command.
	 * 
	 * @param cmd
	 *            the concerning command.
	 * @return true if it's a closing command else true.
	 */
	private boolean isUnlockingCmd(final Command cmd) {
		if (cmd != null
				&& getUnlockingCommands().contains(
						cmd.getMethodToCall().getName())) {
			return true;
		}
		return false;
	}

	/**
	 * @param newUnlockingCommands
	 *            the unlockingCommands to set
	 */
	public final void setUnlockingCommands(
	        final List<String> newUnlockingCommands) {
		this.unlockingCommands = newUnlockingCommands;
	}

	/**
	 * @return the unlockingCommands
	 */
	public final List<String> getUnlockingCommands() {
		return unlockingCommands;
	}

}
