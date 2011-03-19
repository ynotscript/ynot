package ynot.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ynot.core.exception.provider.UnprovidableCommandException;
import ynot.core.exception.provider.UnprovidableResourceException;
import ynot.core.provider.command.CommandProvider;
import ynot.util.breadcrum.Breadcrum;
import ynot.util.breadcrum.Progress;
import ynot.util.security.Lock;
import ynot.util.variable.VariableManager;
import ynot.util.variable.VariableManager.Scope;

/**
 * The shell logic, it will contain the commands and execute them.
 * 
 * @author equesada
 */
public class Shell implements Cloneable {

	/**
	 * To know if the shell is initialized.
	 */
	private Boolean initialized;

	/**
	 * The defaut namespace of the variables.
	 */
	private static final String DEFAULT_NAMESPACE = "default";

	/**
	 * This method will load all the commands from the commandProvider in a
	 * local list.
	 * 
	 * @throws UnprovidableCommandException
	 *             if commands aren't supplied
	 */
	public final void init() throws UnprovidableCommandException {
		if (!initialized) {
			loadCommands();
			initialized = true;
		}
	}

	/**
	 * Load all the commands.
	 * 
	 * @throws UnprovidableCommandException
	 *             if commands aren't supplied
	 */
	private void loadCommands() throws UnprovidableCommandException {
		int step = 1;
		while (commandProvider.hasNext()) {
			commands.put(step++, commandProvider.getNext());
		}
	}

	/**
	 * It will execute all the commands.
	 */
	public final void run() {
		try {
			init();
			while (hasStep()) {
				runStep();
				nextStep();
			}
		} catch (Exception e) {
			String message = getErrorMessage(e);
			logger.error(message);
		}
	}

	/**
	 * To know if there is still commands to execute.
	 * 
	 * @return true if it's the case else false.
	 */
	public final boolean hasStep() {
		return (getStep() <= commands.size());
	}

	/**
	 * To get the current step.
	 * 
	 * @return the current step.
	 */
	public final int getStep() {
		return progress.getStep();
	}

	/**
	 * Run the current step.
	 * 
	 * @throws Exception
	 *             if there is an error.
	 */
	public final void runStep() throws Exception {

		while (hasCommandToExecute()) {
			Command currentCommand = getCurrentCommand();
			if (hasToBeInvoked(currentCommand)) {
				Command cleanedCommand = cleanCommand(currentCommand);
				Object result = cleanedCommand.invoke();
				assignVariables(cleanedCommand, result);
				if (!nextCommandIsAlwaysInThisStep()) {
					break;
				}
			}
			moveToNextCommand();
		}
	}

	/**
	 * Check if there is at least a command to execute.
	 * 
	 * @return true if it's the case.
	 */
	private boolean hasCommandToExecute() {
		return progress.getSubStep() <= getCurrentCommandList().size();
	}

	/**
	 * Get the current commands to execute.
	 * 
	 * @return the current commands to execute.
	 */
	private List<Command> getCurrentCommandList() {
		return commands.get(progress.getStep());
	}

	/**
	 * Get the current command to execute.
	 * 
	 * @return the current command to execute.
	 */
	private Command getCurrentCommand() {
		List<Command> cmds = getCurrentCommandList();
		return cmds.get(progress.getSubStep() - 1);
	}

	/**
	 * To check if the command has to be executed.
	 * 
	 * @param currentCommand
	 *            the command to check.
	 * @return true if it's the case.
	 */
	private boolean hasToBeInvoked(final Command currentCommand) {
		return !lock.isLocked() || currentCommand.isForced();
	}

	/**
	 * To clean the command with the real arguments.
	 * 
	 * @param currentCommand
	 *            the concerned command.
	 * @return the cleaned command.
	 * @throws CloneNotSupportedException
	 *             if clone not supported.
	 * @throws UnprovidableResourceException
	 *             if some resources are missing.
	 */
	private Command cleanCommand(final Command currentCommand)
			throws CloneNotSupportedException, UnprovidableResourceException {
		Command cmd = (Command) currentCommand.clone();
		Object[] newArgs = new Object[cmd.getArgumentsToGive().length];
		cmd.setResourceToUse(getArg(cmd.getResourceToUse()));
		int i = 0;
		for (Object oneArg : cmd.getArgumentsToGive()) {
			newArgs[i++] = getArg(oneArg);
		}
		cmd.setArgumentsToGive(newArgs);
		return cmd;
	}

	/**
	 * To get the argument.
	 * 
	 * @param arg
	 *            the initial argument.
	 * @return the final argument.
	 * @throws UnprovidableResourceException
	 *             if the resource doesn't exist.
	 */
	@SuppressWarnings("unchecked")
	private Object getArg(final Object arg)
			throws UnprovidableResourceException {
		if (arg == null) {
			return null;
		}
		// we have to be sure to use itself
		if (arg instanceof Shell) {
			return this;
		}
		try {
			List<Object> list = (List<Object>) arg;
			List<Object> listToReturn = new ArrayList<Object>();
			for (Object obj : list) {
				listToReturn.add(getArg(obj));
			}
			return listToReturn;
		} catch (ClassCastException e) {
			String str = arg.toString();
			Object oneArg = null;
			if (str.contains("${")) {
				int firstCharPos = str.indexOf("${") + 2;
				String fullstr = str.substring(0, firstCharPos - 2);
				int endCharPos = str.indexOf("}", firstCharPos);
				String varName = str.substring(firstCharPos, endCharPos);
				Object var = getVariable(varName.trim());
				if (var == null) {
					var = "NULL";
				}
				fullstr += var.toString();
				fullstr += str.substring(endCharPos + 1);
				return getArg(fullstr);
			} else if (str.startsWith("@$") && str.endsWith("@")) {
				oneArg = getVariable(str.substring(2, str.length() - 1));
			} else {
				oneArg = arg;
			}
			return oneArg;
		}
	}

	/**
	 * Assigne the values to the correct variables.
	 * 
	 * @param currentCommand
	 *            the concerned command.
	 * @param result
	 *            the value to set.
	 */
	private void assignVariables(final Command currentCommand,
			final Object result) {
		List<String> varNames = currentCommand.getAttachedVariables();
		if (varNames != null) {
			for (String varName : varNames) {
				setVariable(varName, result);
			}
		}
	}

	/**
	 * To know if the next command has changed.
	 * 
	 * @return true if it's the case.
	 */
	private boolean nextCommandIsAlwaysInThisStep() {
		return !progress.hasJumped();
	}

	/**
	 * To go to the next command (= next subStep).
	 */
	private void moveToNextCommand() {
		progress.goNextSubStep();
	}

	/**
	 * To go to the next step.
	 */
	public final void nextStep() {
		progress.goNextStep();
	}

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(Shell.class);

	/**
	 * The command provider.
	 */
	private CommandProvider<Integer> commandProvider;

	/**
	 * @param newCommandProvider
	 *            the commandProvider to set
	 */
	public final void setCommandProvider(
			final CommandProvider<Integer> newCommandProvider) {
		this.commandProvider = newCommandProvider;
	}

	/**
	 * The list of commands to execute (step => commands).
	 */
	private Map<Integer, List<Command>> commands;

	/**
	 * When it's locked only the forced commands are executed.
	 */
	private Lock lock;

	/**
	 * Current progress of the execution.
	 */
	private Progress progress;

	/**
	 * The variable manager.
	 */
	private VariableManager variableManager;

	/**
	 * All the existing instances of the shell.
	 */
	private static final Map<ClassLoader, Shell> INSTANCES = new HashMap<ClassLoader, Shell>();

	// Constructor(s)

	/**
	 * Default constructor, set an empty list in the command list.
	 * 
	 * @throws UnprovidableCommandException
	 */
	public Shell() {
		initialized = false;
		INSTANCES.put(getClass().getClassLoader(), this);
		progress = new Progress();
		lock = new Lock();
		commands = new TreeMap<Integer, List<Command>>();
		variableManager = new VariableManager(new Breadcrum(DEFAULT_NAMESPACE));
	}

	/**
	 * To clone the shell.
	 * 
	 * @return the cloned shell.
	 * @throws CloneNotSupportedException
	 *             if not able to clone.
	 */
	public final Shell clone() throws CloneNotSupportedException {
		Shell clonedShell = new Shell();
		clonedShell.progress = (Progress) progress.clone();
		clonedShell.lock = (Lock) lock.clone();
		clonedShell.variableManager = (VariableManager) variableManager.clone();
		clonedShell.commands = new TreeMap<Integer, List<Command>>(commands);
		return clonedShell;
	}

	/**
	 * To indicate that we entered in a new namespace.
	 * 
	 * @param namespace
	 *            the concerned namespace.
	 */
	public final void goIn(final String namespace) {
		variableManager.goIn(namespace);
	}

	/**
	 * To indicate that we went out of a namespace.
	 * 
	 * @return the concerned namespace.
	 */
	public final String goOut() {
		return variableManager.goOut();
	}

	/**
	 * To get the current namespace of the variableManager.
	 * 
	 * @return the current namespace.
	 */
	public final String getCurrent() {
		return variableManager.getCurrent();
	}

	/**
	 * To clean all the variables.
	 */
	public final void cleanVariables() {
		variableManager.clean();
	}

	/**
	 * To unset a variable.
	 * 
	 * @param varName
	 *            the variable name.
	 */
	public final void unsetVariable(final String varName) {
		variableManager.unset(varName);
	}

	/**
	 * To set a variable.
	 * 
	 * @param varName
	 *            the variable name.
	 * @param value
	 *            the value to set.
	 */
	public final void setVariable(final String varName, final Object value) {
		variableManager.set(varName, value);
	}

	/**
	 * To get the currently value of a variable.
	 * 
	 * @param varName
	 *            the name of the variable.
	 * @return the value of the asked variable.
	 */
	public final Object getVariable(final String varName) {
		return variableManager.get(varName);
	}

	/**
	 * To get an object able to log everywhere.
	 * 
	 * @return a logger object
	 */
	public final Logger getLogger() {
		return Shell.logger;
	}

	/**
	 * The step setter.
	 * 
	 * @param newStep
	 *            the new step.
	 */
	public final void setNextStep(final int newStep) {
		progress.setNext(newStep);
	}

	/**
	 * The step & subStep setter.
	 * 
	 * @param newStep
	 *            new step
	 * @param newSubStep
	 *            new subStep
	 */
	public final void setNextStep(final int newStep, final int newSubStep) {
		progress.setNext(newStep, newSubStep);
	}

	/**
	 * To sub step getter.
	 * 
	 * @return the current sub step.
	 */
	public final int getSubStep() {
		return progress.getSubStep();
	}

	/**
	 * To the set the scope of the variables.
	 * 
	 * @param newScope
	 *            the new scope.
	 */
	public final void setVariablesScope(final Scope newScope) {
		variableManager.setScope(newScope);
	}

	/**
	 * To get the current scope of the variables.
	 * 
	 * @return the current scope.
	 */
	public final Scope getVariablesScope() {
		return variableManager.getScope();
	}

	/**
	 * Lock the shell with the current namespace like password.
	 */
	public final void lock() {
		lock(variableManager.getCurrent());
	}

	/**
	 * Unlock the shell with the current namespace like password.
	 */
	public final void unlock() {
		unlock(variableManager.getCurrent());
	}

	/**
	 * To lock the shell with a password.
	 * 
	 * @param password
	 *            the concerned password.
	 */
	public final void lock(final String password) {
		lock.tryToLock(password);
	}

	/**
	 * To unlock the shell with a password.
	 * 
	 * @param password
	 *            the concerned password.
	 */
	public final void unlock(final String password) {
		lock.tryToUnlock(password);
	}

	/**
	 * To get a displayable message of the error.
	 * 
	 * @param e
	 *            the error.
	 * @return the displayable message.
	 */
	private String getErrorMessage(final Exception e) {
		String message = "\n===== Script =====\n" + "o step    = "
				+ progress.getStep() + "\n" + "o substep = "
				+ progress.getSubStep() + "\n";
		Throwable cause = e.getCause();
		if (cause != null) {
			StackTraceElement[] elems = cause.getStackTrace();
			StackTraceElement c = elems[0];
			if (cause.getMessage() != null) {
				message += "o msg     = " + cause.getMessage() + "\n";
			} else if (e.getMessage() != null) {
				message += "o msg     = " + e.getMessage() + "\n";
			} else {
				e.printStackTrace();
			}
			message += "====== File ======\n";
			message += "o name = " + c.getFileName() + "\n";
			message += "o line = " + c.getLineNumber() + "\n";
		} else {
			message += "o msg     = " + e.getMessage() + "\n";
		}
		message += "==== Command =====\n";
		message += getCurrentCommand().toString();
		message += "==================\n";
		return message;
	}

	/**
	 * To get a global value.
	 * 
	 * @param key
	 *            the key of the variable to get.
	 * @return the value of the asked key.
	 */
	public static Object get(final String key) {
		return VariableManager.getGlobal(key);
	}

	/**
	 * To set a global value.
	 * 
	 * @param key
	 *            the key to set
	 * @param value
	 *            the value to put.
	 */
	public static void set(final String key, final Object value) {
		VariableManager.setGlobal(key, value);
	}

	/**
	 * To unset a global value.
	 * 
	 * @param key
	 *            the concerned key.
	 */
	public static void unset(final String key) {
		VariableManager.unsetGlobal(key);
	}

	/**
	 * To clear all the global variables.
	 */
	public static void clear() {
		VariableManager.clearGlobal();
	}

	/**
	 * To get the current instance of the shell.
	 * 
	 * @return the current instance.
	 */
	public static Shell getInstance() {
		return INSTANCES.get(Shell.class.getClassLoader());
	}

	/**
	 * To get the previous namespace.
	 * 
	 * @return the previous namespace.
	 */
	public final String previous() {
		return variableManager.previous();
	}

}
