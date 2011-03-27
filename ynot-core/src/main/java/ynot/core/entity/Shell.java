package ynot.core.entity;

import java.lang.reflect.InvocationTargetException;
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
	 * To know if the shell is in lazy mode.
	 */
	private Boolean lazyLoading;

	/**
	 * The defaut namespace of the variables.
	 */
	private static final String DEFAULT_NAMESPACE = "default";

	/**
	 * Load all the commands.
	 * 
	 * @throws UnprovidableCommandException
	 *             if commands aren't supplied
	 */
	private void loadAllCommands() throws UnprovidableCommandException {
		int step = 1;
		while (commandProvider.hasNext()) {
			commands.put(step++, commandProvider.getNext());
		}
	}

	/**
	 * It will load and execute all the commands.
	 * 
	 * @throws UnprovidableCommandException
	 *             if a command is not providable.
	 * @throws UnprovidableResourceException
	 *             if a resource is not providable.
	 * @throws CloneNotSupportedException
	 *             if the clone is not supported.
	 * @throws IllegalAccessException
	 *             if there is an illegal access.
	 * @throws InvocationTargetException
	 *             if there is an issue with the invocation.
	 */
	public final void run() throws UnprovidableCommandException,
			UnprovidableResourceException, CloneNotSupportedException,
			IllegalAccessException, InvocationTargetException {
		if (!lazyLoading) {
			loadAllCommands();
		}
		while (hasStep()) {
			runStep();
			nextStep();
		}
	}

	/**
	 * To know if there is still commands to execute.
	 * 
	 * @return true if it's the case else false.
	 * @throws UnprovidableCommandException
	 *             if a command is not providable.
	 */
	public final boolean hasStep() throws UnprovidableCommandException {
		if (lazyLoading) {
			return commandProvider.hasNext();
		} else {
			return (getStep() <= commands.size());
		}
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
	 * @throws CloneNotSupportedException
	 *             if the clone is not supported.
	 * @throws UnprovidableResourceException
	 *             if a resource is not providable.
	 * @throws InvocationTargetException
	 *             in there is a problem with the invocation.
	 * @throws IllegalAccessException
	 *             if there is an illegal access.
	 * @throws UnprovidableCommandException
	 *             is a command is not providable.
	 */
	public final void runStep() throws UnprovidableResourceException,
			CloneNotSupportedException, IllegalAccessException,
			InvocationTargetException, UnprovidableCommandException {

		List<Command> cmds = getCurrentCommandList();
		while (hasCommandToExecute(cmds)) {
			Command currentCommand = cmds.get(progress.getSubStep() - 1);
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
	 * @param cmds
	 *            the current commands.
	 * 
	 * @return true if it's the case.
	 * @throws UnprovidableCommandException
	 *             if a command is not providable.
	 */
	private boolean hasCommandToExecute(final List<Command> cmds)
			throws UnprovidableCommandException {
		return progress.getSubStep() <= cmds.size();
	}

	/**
	 * Get the current commands to execute.
	 * 
	 * @return the current commands to execute.
	 * @throws UnprovidableCommandException
	 *             if a command is not providable.
	 */
	private List<Command> getCurrentCommandList()
			throws UnprovidableCommandException {
		if (lazyLoading) {
			return commandProvider.getNext();
		} else {
			return commands.get(progress.getStep());
		}
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
	private static final Map<Long, Shell> INSTANCES = new HashMap<Long, Shell>();

	// Constructor(s)

	/**
	 * Default constructor, set an empty list in the command list.
	 * 
	 * @throws UnprovidableCommandException
	 */
	public Shell() {
		setLazyLoading(false);
		INSTANCES.put(Thread.currentThread().getId(), this);
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
		return INSTANCES.get(Thread.currentThread().getId());
	}

	/**
	 * To get the previous namespace.
	 * 
	 * @return the previous namespace.
	 */
	public final String previous() {
		return variableManager.previous();
	}

	/**
	 * @return the lazyLoading
	 */
	public final Boolean getLazyLoading() {
		return lazyLoading;
	}

	/**
	 * @param newLazyLoading
	 *            the lazyLoading to set
	 */
	public final void setLazyLoading(final Boolean newLazyLoading) {
		this.lazyLoading = newLazyLoading;
	}

}
