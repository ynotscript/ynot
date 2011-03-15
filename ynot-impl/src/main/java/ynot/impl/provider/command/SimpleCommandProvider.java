package ynot.impl.provider.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ynot.core.entity.Command;
import ynot.core.entity.Definition;
import ynot.core.entity.Request;
import ynot.core.entity.Resource;
import ynot.core.exception.provider.UnprovidableCommandException;
import ynot.core.exception.provider.UnprovidableDefinitionException;
import ynot.core.exception.provider.UnprovidableRequestException;
import ynot.core.exception.provider.UnprovidableResourceException;
import ynot.core.listener.provider.command.CommandProviderListener;
import ynot.core.provider.command.CommandProvider;
import ynot.core.provider.definition.DefinitionProvider;
import ynot.core.provider.request.RequestProvider;
import ynot.core.provider.resource.ResourceProvider;
import ynot.util.reflect.ReflectionManager;

/**
 * The default implementation of the command provider.
 * @author equesada
 */
public class SimpleCommandProvider implements CommandProvider<Integer> {

	/**
	 * The logger.
	 */
	private static Logger logger = Logger
			.getLogger(SimpleCommandProvider.class);

	/**
	 * The provider name.
	 */
	private final String providerName;

	/**
	 * The list of commands to execute (step => commands).
	 */
	private final Map<Integer, List<Command>> commands;

	/**
	 * The listeners to call when a command is provided.
	 */
	private final List<CommandProviderListener> listeners;

	/**
	 * The current step (line) of the execution.
	 */
	private Integer currentStep;

	/**
	 * The request provider.
	 */
	private RequestProvider<String> requestProvider;

	/**
	 * To get resource from name (providerName => provider).
	 */
	private final Map<String, ResourceProvider<String>> resourceProviders;

	/**
	 * To get definition from word (providerName => provider).
	 */
	private final Map<String, DefinitionProvider<String>> definitionProviders;

	/**
	 * The default constructor.
	 */
	public SimpleCommandProvider() {
		providerName = "SimpleCommandProvider";
		currentStep = 1;
		commands = new TreeMap<Integer, List<Command>>();
		listeners = new ArrayList<CommandProviderListener>();
		resourceProviders = new HashMap<String, ResourceProvider<String>>();
		definitionProviders = new HashMap<String, DefinitionProvider<String>>();
	}

	@Override
	public final String getName() {
		return providerName;
	}

	@Override
	public final List<Command> get(final Integer line) 
	        throws UnprovidableCommandException {
		try {
			List<Command> cmds = commands.get(line);
			noticePreCommandsListeners(cmds);
			boolean go = noticePostCommandsListeners(cmds);
			if (go) {
				return cleanResources(cmds);
			} else {
				return new ArrayList<Command>();
			}
		} catch (UnprovidableResourceException e) {
			throw new UnprovidableCommandException(
					"UnprovidableResourceException", e);
		}
	}

	/**
	 * Clean a list of commands with the real resources.
	 * @param list the list of commands to clean.
	 * @return the cleaned list.
	 * @throws UnprovidableResourceException if a resource is missing.
	 */
	private List<Command> cleanResources(final List<Command> list)
			throws UnprovidableResourceException {
		ArrayList<Command> cleanedList = new ArrayList<Command>();
		for (Command onCommand : list) {
			cleanArgumentsToGive(onCommand);
			if (cleanResourceToUse(onCommand)) {
				cleanedList.add(onCommand);
			}
		}
		return cleanedList;
	}

	/**
	 * To clean a command with the real resources.
	 * @param onCommand the command to clean.
	 * @return the cleaned command.
	 * @throws UnprovidableResourceException if a resource is missing.
	 */
	private boolean cleanResourceToUse(final Command onCommand)
			throws UnprovidableResourceException {
		if (onCommand == null) {
			return false;
		}
		Object res = onCommand.getResourceToUse();
		if (res instanceof Resource) {
			fillResource((Resource) res);
			onCommand.setResourceToUse(((Resource) res).getContent());
		}
		return true;
	}

	/**
	 * To clean the arguments with the real resources.
	 * @param onCommand the command to clean.
	 * @throws UnprovidableResourceException if a resource is missing.
	 */
	private void cleanArgumentsToGive(final Command onCommand)
			throws UnprovidableResourceException {
		if (onCommand == null) {
			return;
		}
		Object[] args = onCommand.getArgumentsToGive();
		for (int i = 0; i < args.length; i++) {
			Object res = args[i];
			if (res instanceof Resource) {
				fillResource((Resource) res);
				args[i] = ((Resource) res).getContent();
			}
		}
		onCommand.setArgumentsToGive(args);
	}

	@Override
	public final boolean hasNext() {
		return (getStep() <= commands.size());
	}

	@Override
	public final List<Command> getNext() throws UnprovidableCommandException {
		return get(currentStep++);
	}

	/**
	 * To add an invokable commands.
	 * 
	 * @param step
	 *            The step of these commands
	 * @param cmds
	 *            The commands to add
	 */
	private void addCommands(
	        final Integer step, final List<Command> cmds) {
		if (cmds == null || cmds.size() == 0) {
			addCommand(step, null);
		} else {
			for (Command cmd : cmds) {
				addCommand(step, cmd);
			}
		}
	}

	/**
	 * Call the preNotice method on each addCommandListener.
	 * 
	 * @param cmds
	 *            the list of commands of this step.
	 */
	private void noticePreCommandsListeners(final List<Command> cmds) {
	    List<Command> newCmds = cmds;
		for (CommandProviderListener listener : listeners) {
			newCmds = listener.preNotice(newCmds);
		}
	}

	/**
	 * Call the postNotice method on each addCommandListener.
	 * 
	 * @param cmds
	 *            the list of commands of this step.
	 * @return true if the command have to be added.
	 */
	private boolean noticePostCommandsListeners(final List<Command> cmds) {
		for (CommandProviderListener listener : listeners) {
			if (!listener.postNotice(cmds)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * To add an invokable command.
	 * 
	 * @param step
	 *            The step of this command
	 * @param cmd
	 *            The command to add
	 */
	private void addCommand(final Integer step, final Command cmd) {
		if (commands.get(step) == null) {
			commands.put(step, new LinkedList<Command>());
		}
		commands.get(step).add(cmd);
	}

	/**
	 * To step getter.
	 * 
	 * @return the current step.
	 */
	private int getStep() {
		return currentStep;
	}

	/**
	 * To parse a ynot script and prepare commands.
	 */
	public final void init() {

		try {
			int step = 0;
			ResourceProvider<String> resourceProvider = null;

			// While any request is present
			while (requestProvider.hasNext()) {

				step++;
				int subStep = -1;

				// for each request of this step
				List<Request> requestsOfThisStep = null;
				try {
					requestsOfThisStep = requestProvider.getNext();
				} catch (UnprovidableRequestException e) {
					String msg = e.getMessage();
					String message = getMessage(step, subStep, null, msg);
					logger.error(message);
					continue;
				}
				List<Command> commandsOfThisStep = new ArrayList<Command>();

				for (Request req : requestsOfThisStep) {

					subStep++;

					// if the request is not active, put a null command
					if (!req.isActive()) {
						commandsOfThisStep.add(null);
						continue;
					}

					// get the definition
					Definition def = null;

					try {
						def = getDefinition(req);
					} catch (UnprovidableDefinitionException e) {
						String msg = "this word is unknown " + e.getMessage();
						String message = getMessage(step, subStep, null, msg);
						logger.error(message);
						return;
					}

					// get the resource
					String rpn = def.getResourceProviderName();
					resourceProvider = getResourceProvider(rpn);

					if (resourceProvider == null) {
						String msg = "Resource Provider missing \""
								+ def.getResourceProviderName() + "\"";
						String message = getMessage(step, subStep, def, msg);
						logger.error(message);
						return;
					}

					Resource res = null;

					try {
						res = resourceProvider.get(def.getResourceNameToUse());
					} catch (UnprovidableResourceException e) {
						String msg = "Resource missing";
						String message = getMessage(step, subStep, def, msg);
						logger.error(message);
						return;
					}

					// update the definition to set the real resources
					try {
						updateDefinitionWithResources(def);
					} catch (UnprovidableResourceException e) {
						String msg = e.getMessage();
						String message = getMessage(step, subStep, def, msg);
						logger.error(message);
						return;
					}

					Object obj = res.getContent();

					// For each method to call on the resource add a "command"
					// object.
					int start = 0;
					for (String methodName : def.getMethodNamesToCall()) {

						// Get the Method object
						Method method = ReflectionManager.getMethod(
								obj.getClass(), methodName,
								def.getParameterTypes(methodName));

						if (method == null) {
							logger.error("Method not found on resource "
									+ obj.getClass().getName() + " ::word '"
									+ req.getWordToUse() + "' ::methodName '"
									+ methodName + "' ::parameterNumber '"
									+ def.getParameterTypes(methodName).length
									+ "'");
							continue;
						}

						// Build the Command object
						Command cmd = new Command();
						cmd.setResourceToUse(obj);
						cmd.setMethodToCall(method);

						// Set the argument to give to the command
						start = setArgumentsToGive(cmd,
								updateResources(req.getGivenParameters()),
								def.getPredefinedValues(methodName),
								method.getParameterTypes().length, start);

						// flag the variable to fill (on return)
						setAttachedVarName(cmd, req.getVariableNames());

						// Add the command at the end of the list
						commandsOfThisStep.add(cmd);
					}
				}

				addCommands(step, commandsOfThisStep);

			}
		} catch (SecurityException e) {
			logger.error("SecurityException", e);
		} catch (UnprovidableResourceException e) {
			logger.error("UnprovidableResourceException", e);
		}
	}

	/**
	 * Update the parameters with the given parameters.
	 * @param givenParameters to parameter to update.
	 * @return the filled parameters.
	 * @throws UnprovidableResourceException if a resource is missing.
	 */
	private Object[] updateResources(final Object[] givenParameters)
			throws UnprovidableResourceException {
		if (null == givenParameters) {
			return null;
		}
		Object[] ret = new Object[givenParameters.length];
		for (int i = 0; i < givenParameters.length; i++) {
			ret[i] = givenParameters[i];
			if (ret[i] instanceof Resource) {
				fillResource((Resource) ret[i]);
				ret[i] = ((Resource) ret[i]).getContent();
			}
		}
		return ret;
	}

	/**
	 * To get the definition from a request.
	 * 
	 * @param req
	 *            the request to check.
	 * @return the definition.
	 * @throws UnprovidableDefinitionException
	 *             if definition not provided.
	 */
	private Definition getDefinition(final Request req)
			throws UnprovidableDefinitionException {
		Definition def = null;
		if (req.getDefinitionProviderName() != null) {
			def = definitionProviders.get(req.getDefinitionProviderName()).get(
					req.getWordToUse());
			if (def != null) {
				return def;
			}
			throw new UnprovidableDefinitionException("<" + req.getWordToUse()
					+ ">");
		} else {
			for (String pName : getDefinitionProviderNames()) {
				try {
					def = definitionProviders.get(pName)
							.get(req.getWordToUse());
					if (def != null) {
						return def;
					}
				} catch (UnprovidableDefinitionException e) {
					continue;
				}
			}
		}
		throw new UnprovidableDefinitionException("<" + req.getWordToUse()
				+ ">");
	}

	/**
	 * The requestProvider setter.
	 * 
	 * @param newRequestProvider
	 *            the new Request Provider.
	 */
	public final void setRequestProvider(
			final RequestProvider<String> newRequestProvider) {
		this.requestProvider = newRequestProvider;
	}

	/**
	 * The requestProvider getter.
	 * 
	 * @return the requestProvider member.
	 */
	public final RequestProvider<String> getRequestProvider() {
		return requestProvider;
	}

	/**
	 * To get the resource provider.
	 * 
	 * @param newProviderName
	 *            the name of the provider
	 * @return the resource provider.
	 */
	public final ResourceProvider<String> getResourceProvider(
			final String newProviderName) {
		return resourceProviders.get(newProviderName);
	}

	/**
	 * To get the name of the resource providers.
	 * 
	 * @return the name of providers.
	 */
	public final Set<String> getResourceProviderNames() {
		return resourceProviders.keySet();
	}

	/**
	 * To get the name of the definition providers.
	 * 
	 * @return the name of providers.
	 */
	public final Set<String> getDefinitionProviderNames() {
		return definitionProviders.keySet();
	}

	/**
	 * To add a new resource provider.
	 * 
	 * @param newProviderName
	 *            the name of the provider.
	 * @param newResourceProvider
	 *            the new resource provider.
	 */
	public final void addResourceProvider(final String newProviderName,
			final ResourceProvider<String> newResourceProvider) {
		this.resourceProviders.put(newProviderName, newResourceProvider);
	}

	/**
	 * To remove a resource provider.
	 * 
	 * @param newProviderName
	 *            the name of the provider.
	 */
	public final void removeResourceProvider(final String newProviderName) {
		this.resourceProviders.remove(newProviderName);
	}

	/**
	 * To add a new definition provider.
	 * 
	 * @param newProviderName
	 *            the name of the provider.
	 * @param newDefinitionProvider
	 *            the new definition provider.
	 */
	public final void addDefinitionProvider(final String newProviderName,
			final DefinitionProvider<String> newDefinitionProvider) {
		this.definitionProviders.put(newProviderName, newDefinitionProvider);
	}

	/**
	 * To remove a definition provider.
	 * 
	 * @param newProviderName
	 *            the name of the provider.
	 */
	public final void removeDefinitionProvider(final String newProviderName) {
		this.definitionProviders.remove(newProviderName);
	}

	/**
	 * Add an addCommandListener.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public final void addListener(final CommandProviderListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove an addCommandListener.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public final void removeListener(final CommandProviderListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Clear all the addComandListner.
	 */
	public final void clearListener() {
		listeners.clear();
	}

	/**
	 * @param step
	 *            the current step of the error.
	 * @param subStep
	 *            the current sub step of the error.
	 * @param def
	 *            the current definition.
	 * @param msg
	 *            the message.
	 * @return the full message.
	 */
	private String getMessage(final int step, final int subStep,
			final Definition def, final String msg) {
		String message = "\n===== Script =====\n" + "o step    = " + step
				+ "\n" + "o substep = " + subStep + "\n" + "o msg     = " + msg
				+ "\n";
		if (def != null) {
			message += def.getResourceNameToUse() + "\" from "
					+ def.getResourceProviderName() + "\n";
		}
		message += "==================\n";
		return message;
	}

	/**
	 * Update definition with available resources.
	 * 
	 * @param def
	 *            the definition to update.
	 * @throws UnprovidableResourceException
	 *             thrown when missing resources.
	 */
	@SuppressWarnings("rawtypes")
	private void updateDefinitionWithResources(final Definition def)
			throws UnprovidableResourceException {

		Map<String, Class[]> update = new HashMap<String, Class[]>();

		for (String methodName : def.getMethodNamesToCall()) {

			Map<Integer, Object> values = def.getPredefinedValues(methodName);

			Class[] parameterTypes = def.getParameterTypes(methodName);

			for (Entry<Integer, Object> entry : values.entrySet()) {
				Integer position = entry.getKey();
				Object predefinedValue = entry.getValue();

				if (predefinedValue instanceof Resource) {
					fillResource((Resource) predefinedValue);
					Object realValue = ((Resource) predefinedValue)
							.getContent();
					parameterTypes[position] = realValue.getClass();
					values.put(position, realValue);
				}
			}

			update.put(methodName, parameterTypes);
		}

		for (Entry<String, Class[]> onUpdate : update.entrySet()) {
			def.setParameterTypes(onUpdate.getKey(), onUpdate.getValue());
		}
	}

	/**
	 * Update the arguments get back from the script with values.
	 * 
	 * @param cmd
	 *            the command to complete.
	 * @param arguments
	 *            the argument given in the script.
	 * @param values
	 *            the values containing in the definition.
	 * @param length
	 *            the number of parameter need for the method.
	 * @param start
	 *            where to start to read given parameters.
	 * @return the merge of this arguments.
	 */
	private Integer setArgumentsToGive(final Command cmd,
			final Object[] arguments, final Map<Integer, Object> values,
			final int length, final int start) {
		int startAt = start;
		Object[] args = new Object[length];
		for (int i = 0; i < length; i++) {
			if (values.containsKey(Integer.valueOf(i))) {
				args[i] = values.get(Integer.valueOf(i));
			} else {
				if (startAt >= arguments.length) {
					args[i] = null;
				} else {
					args[i] = arguments[startAt];
					startAt++;
				}
			}
			if (args[i] == null) {
				continue;
			}
		}
		cmd.setArgumentsToGive(args);
		return Integer.valueOf(startAt);
	}

	/**
	 * To indicate that a command has to fill variables.
	 * 
	 * @param cmd
	 *            the concerned command.
	 * @param varNames
	 *            the target variable.
	 */
	private void setAttachedVarName(
	        final Command cmd, final String[] varNames) {
		if (varNames == null) {
			return;
		}
		for (String varName : varNames) {
			cmd.getAttachedVariables().add(varName);
		}
	}

	/**
	 * Try to complete the resource.
	 * 
	 * @param resource
	 *            the resource to complete.
	 * @throws UnprovidableResourceException
	 *             if not able to fill.
	 */
	private void fillResource(final Resource resource)
			throws UnprovidableResourceException {
		if (resource.getContent() != null) {
			return;
		}
		if (resource.getProviderName() == null) {
			for (String oneProviderName : getResourceProviderNames()) {
				Resource obj;
				try {
					obj = getResourceProvider(oneProviderName).get(
							resource.getResourceName());
				} catch (UnprovidableResourceException e) {
					continue;
				}
				resource.setContent(obj.getContent());
				resource.setProviderName(obj.getProviderName());
				return;
			}
			throw new UnprovidableResourceException("Resource \""
					+ resource.getResourceName() + "\" not found");
		} else {
			try {
				resource.setContent(getResourceProvider(
						resource.getProviderName()).get(
						resource.getResourceName()).getContent());
			} catch (UnprovidableResourceException e) {
				throw new UnprovidableResourceException(e);
			}
		}
	}

	@Override
	public final void setListeners(
	        final List<CommandProviderListener> newListeners) {
		for (CommandProviderListener listener : newListeners) {
			addListener(listener);
		}
	}

	/**
	 * The setter of the resourceProviders.
	 * @param newResourceProviders the new resourceProviders.
	 */
	public final void setResourceProviders(
			final List<ResourceProvider<String>> newResourceProviders) {
		for (ResourceProvider<String> resPro : newResourceProviders) {
			addResourceProvider(resPro.getName(), resPro);
		}
	}

	/**
	 * The getter of the resourceProviders.
	 * @param newDefinitionProviders the current resourceProviders.
	 */
	public final void setDefinitionProviders(
			final List<DefinitionProvider<String>> newDefinitionProviders) {
		for (DefinitionProvider<String> defPro : newDefinitionProviders) {
			addDefinitionProvider(defPro.getName(), defPro);
		}
	}

}
