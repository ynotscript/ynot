package ynot.impl.provider.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;

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
 * 
 * @author equesada
 */
public class SimpleCommandProvider implements CommandProvider<Integer> {

	/**
	 * The provider name.
	 */
	private final String providerName;

	/**
	 * The listeners to call when a command is provided.
	 */
	private final List<CommandProviderListener> listeners;

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
		providerName = ClassUtils.getShortClassName(this.getClass());
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
		return null;
	}

	@Override
	public final boolean hasNext() throws UnprovidableCommandException {
		try {
			return requestProvider.hasNext();
		} catch (UnprovidableRequestException e) {
			throw new UnprovidableCommandException(e);
		}
	}

	@Override
	public final List<Command> getNext() throws UnprovidableCommandException {
		try {
			List<Command> cmds = getNextCommands();
			noticePreCommandsListeners(cmds);
			boolean go = noticePostCommandsListeners(cmds);
			if (go) {
				return cleanResources(cmds);
			} else {
				return new ArrayList<Command>();
			}
		} catch (Exception e) {
			throw new UnprovidableCommandException(e);
		}
	}

	/**
	 * To get the next commands.
	 * 
	 * @return the next commands.
	 * @throws UnprovidableRequestException
	 *             if a request is not providable.
	 * @throws UnprovidableDefinitionException
	 *             if a definition is not providable.
	 * @throws UnprovidableResourceException
	 *             if a resource is not providable.
	 * @throws NoSuchMethodException
	 *             if a method is missing.
	 */
	public final List<Command> getNextCommands()
			throws UnprovidableRequestException,
			UnprovidableDefinitionException, UnprovidableResourceException,
			NoSuchMethodException {
		List<Command> commands = new ArrayList<Command>();
		List<Request> requests = requestProvider.getNext();
		for (Request req : requests) {
			if (!req.isActive()) {
				commands.add(null);
				continue;
			}
			Definition def = getDefinition(req);
			Resource res = getResource(def);
			int argIndex = 0;
			for (String methodName : def.getMethodNamesToCall()) {
				Command cmd = new Command();
				argIndex = fillCommand(cmd, res, def, methodName, req, argIndex);
				commands.add(cmd);
			}
		}
		return commands;
	}

	/**
	 * To get the definition of a request.
	 * 
	 * @param req
	 *            the concerned request.
	 * @return the definition.
	 * @throws UnprovidableDefinitionException
	 *             if a definition is missing.
	 * @throws UnprovidableResourceException
	 *             if a resource is missing.
	 */
	private Definition getDefinition(final Request req)
			throws UnprovidableDefinitionException,
			UnprovidableResourceException {
		String pName = req.getDefinitionProviderName();
		String word = req.getWordToUse();
		Definition def;
		if (null == pName) {
			def = getDefinition(word);
		} else {
			def = getDefinition(pName, word);
		}
		updateDefinitionWithResources(def);
		return def;
	}

	/**
	 * To get a definition of a word.
	 * 
	 * @param word
	 *            the concerned word.
	 * @return the definition.
	 * @throws UnprovidableDefinitionException
	 *             if there is no definition.
	 */
	private Definition getDefinition(final String word)
			throws UnprovidableDefinitionException {
		for (String pName : getDefinitionProviderNames()) {
			try {
				return getDefinition(pName, word);
			} catch (UnprovidableDefinitionException e) {
				continue;
			}
		}
		throw new UnprovidableDefinitionException("<" + word + ">");
	}

	/**
	 * To get a definition of a word giving the provider.
	 * 
	 * @param pName
	 *            the concerned provider.
	 * @param word
	 *            the concerned word.
	 * @return the definition.
	 * @throws UnprovidableDefinitionException
	 *             if the definition is missing.
	 */
	private Definition getDefinition(final String pName, final String word)
			throws UnprovidableDefinitionException {
		DefinitionProvider<String> defProvider = definitionProviders.get(pName);
		Definition def = defProvider.get(word);
		return def;
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
	 * To get the resource of a definition.
	 * 
	 * @param def
	 *            the concerned definition.
	 * @return The definition.
	 * @throws UnprovidableResourceException
	 *             if the resource is missing.
	 */
	private Resource getResource(final Definition def)
			throws UnprovidableResourceException {
		String rpn = def.getResourceProviderName();
		ResourceProvider<String> resourceProvider = getResourceProvider(rpn);
		return resourceProvider.get(def.getResourceNameToUse());
	}

	/**
	 * To get the resource provider.
	 * 
	 * @param newProviderName
	 *            the name of the provider
	 * @return the resource provider.
	 * @throws UnprovidableResourceException
	 *             if the resource provider doesn't exist.
	 */
	public final ResourceProvider<String> getResourceProvider(
			final String newProviderName) throws UnprovidableResourceException {
		ResourceProvider<String> ret = resourceProviders.get(newProviderName);
		if (null == ret) {
			String msg = "Resource Provider missing \"" + newProviderName
					+ "\"";
			throw new UnprovidableResourceException(msg);
		}
		return ret;
	}

	/**
	 * To build a command.
	 * 
	 * @param cmd
	 *            the current command to fill.
	 * @param res
	 *            the concerned resource.
	 * @param def
	 *            the concerned definition.
	 * @param methodName
	 *            the methodName to call.
	 * @param req
	 *            the current request.
	 * @param argIndex
	 *            the current argument index.
	 * @return the command.
	 * @throws NoSuchMethodException
	 *             if the method doesn't exist.
	 * @throws UnprovidableResourceException
	 *             if a resource is missing.
	 */
	private int fillCommand(final Command cmd, final Resource res,
			final Definition def, final String methodName, final Request req,
			final int argIndex) throws NoSuchMethodException,
			UnprovidableResourceException {
		Object obj = res.getContent();
		cmd.setResourceToUse(obj);
		Method method = getMethod(def, methodName, obj);
		cmd.setMethodToCall(method);
		// Set the argument to give to the command
		Integer newArgIndex = setArgumentsToGive(cmd,
				updateResources(req.getGivenParameters()),
				def.getPredefinedValues(methodName),
				method.getParameterTypes().length, argIndex);
		// flag the variable to fill (on return)
		setAttachedVarName(cmd, req.getVariableNames());
		return newArgIndex;
	}

	/**
	 * To get a method.
	 * 
	 * @param def
	 *            the concerned definition.
	 * @param methodName
	 *            the method name.
	 * @param obj
	 *            the current object.
	 * @return the method.
	 * @throws NoSuchMethodException
	 *             if the method doesn't exist.
	 */
	private Method getMethod(final Definition def, final String methodName,
			final Object obj) throws NoSuchMethodException {
		Method method = ReflectionManager.getMethod(obj.getClass(), methodName,
				def.getParameterTypes(methodName));
		return method;
	}

	/**
	 * Clean a list of commands with the real resources.
	 * 
	 * @param list
	 *            the list of commands to clean.
	 * @return the cleaned list.
	 * @throws UnprovidableResourceException
	 *             if a resource is missing.
	 */
	private List<Command> cleanResources(final List<Command> list)
			throws UnprovidableResourceException {
		ArrayList<Command> cleanedList = new ArrayList<Command>();
		for (Command onCommand : list) {
			cleanArgumentsToGive(onCommand);
			if (cleanResources(onCommand)) {
				cleanedList.add(onCommand);
			}
		}
		return cleanedList;
	}

	/**
	 * To clean a command with the real resources.
	 * 
	 * @param onCommand
	 *            the command to clean.
	 * @return the cleaned command.
	 * @throws UnprovidableResourceException
	 *             if a resource is missing.
	 */
	private boolean cleanResources(final Command onCommand)
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
	 * 
	 * @param onCommand
	 *            the command to clean.
	 * @throws UnprovidableResourceException
	 *             if a resource is missing.
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
	 * Update the parameters with the given parameters.
	 * 
	 * @param givenParameters
	 *            to parameter to update.
	 * @return the filled parameters.
	 * @throws UnprovidableResourceException
	 *             if a resource is missing.
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
	private void setAttachedVarName(final Command cmd, final String[] varNames) {
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
	 * 
	 * @param newResourceProviders
	 *            the new resourceProviders.
	 */
	public final void setResourceProviders(
			final List<ResourceProvider<String>> newResourceProviders) {
		for (ResourceProvider<String> resPro : newResourceProviders) {
			addResourceProvider(resPro.getName(), resPro);
		}
	}

	/**
	 * The getter of the resourceProviders.
	 * 
	 * @param newDefinitionProviders
	 *            the current resourceProviders.
	 */
	public final void setDefinitionProviders(
			final List<DefinitionProvider<String>> newDefinitionProviders) {
		for (DefinitionProvider<String> defPro : newDefinitionProviders) {
			addDefinitionProvider(defPro.getName(), defPro);
		}
	}

}
