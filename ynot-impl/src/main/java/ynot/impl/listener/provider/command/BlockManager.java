package ynot.impl.listener.provider.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ynot.core.entity.Command;
import ynot.core.entity.Resource;
import ynot.core.entity.Shell;
import ynot.core.listener.provider.command.CommandProviderListener;

/**
 * The AddCommandsListener to manage the namespace.
 * 
 * @author equesada
 */
public class BlockManager implements CommandProviderListener {

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(BlockManager.class);

	/**
	 * The opening commands list.
	 */
	private List<String> openingCommands;
	
	/**
	 * The closing commands list.
	 */
	private List<String> closingCommands;
	
	@Override
	public final boolean postNotice(final List<Command> cmds) {
		return true;
	}

	@Override
	public final List<Command> preNotice(final List<Command> cmds) {

		Command openingCmd = getOpeningCommand(cmds);
		Command closingCmd = getClosingCommand(cmds);

		boolean isOpeningBlock = (openingCmd != null);
		boolean isClosingBlock = (closingCmd != null);

		List<Command> addedCommands = new ArrayList<Command>();

		// If it's an opening block so add an enter command at the
		// beginning.
		if (isOpeningBlock) {
			Command enterCmd = new Command();
			enterCmd.setForced(true);
			enterCmd.setResourceToUse(new Resource("ynot", "shell", null));
			try {
				enterCmd.setMethodToCall(Shell.class.getMethod("goIn",
						String.class));
			} catch (NoSuchMethodException e) {
				logger.error("NoSuchMethodException", e);
			}
			Object[] arg = new Object[1];
			arg[0] = openingCmd.getMethodToCall().getName();
			enterCmd.setArgumentsToGive(arg);
			addedCommands.add(enterCmd);
		}

		// If it's a closing block so add an exit command at the
		// beginning.
		if (isClosingBlock) {
			Command exitCmd = new Command();
			exitCmd.setForced(true);
			exitCmd.setResourceToUse(new Resource("ynot", "shell", null));
			try {
				exitCmd.setMethodToCall(Shell.class.getMethod("goOut"));
			} catch (NoSuchMethodException e) {
				logger.error("NoSuchMethodException", e);
			}
			exitCmd.setArgumentsToGive(new Object[0]);
			addedCommands.add(exitCmd);
		}

		if (addedCommands.size() > 0) {
			for (Command oneCmd : addedCommands) {
				cmds.add(0, oneCmd);
			}
		}

		return cmds;
	}

	/**
	 * Get a closing command containing in the command list.
	 * 
	 * @param cmds
	 *            the list of commands to check.
	 * @return the closing command.
	 */
	private Command getClosingCommand(final List<Command> cmds) {
		List<String> closingList = getClosingCommands();
		for (Command cmd : cmds) {
			if (cmd == null) {
				continue;
			}
			if (closingList.contains(cmd.getMethodToCall().getName())) {
				return cmd;
			}
		}
		return null;
	}

	/**
	 * Get an opening command containing in the command list.
	 * 
	 * @param cmds
	 *            the list of commands to check.
	 * @return the opening command.
	 */
	private Command getOpeningCommand(final List<Command> cmds) {
		List<String> openingList = getOpeningCommands();
		for (Command cmd : cmds) {
			if (cmd == null) {
				continue;
			}
			if (openingList.contains(cmd.getMethodToCall().getName())) {
				return cmd;
			}
		}
		return null;
	}

	/**
	 * @param newOpeningCommands the openingCommands to set
	 */
	public final void setOpeningCommands(
	        final List<String> newOpeningCommands) {
		this.openingCommands = newOpeningCommands;
	}

	/**
	 * @return the openingCommands
	 */
	public final List<String> getOpeningCommands() {
		return openingCommands;
	}

	/**
	 * @param newClosingCommands the closingCommands to set
	 */
	public final void setClosingCommands(
	        final List<String> newClosingCommands) {
		this.closingCommands = newClosingCommands;
	}

	/**
	 * @return the closingCommands
	 */
	public final List<String> getClosingCommands() {
		return closingCommands;
	}
}
