package ynot.impl.provider.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import ynot.core.entity.Resource;
import ynot.core.entity.Shell;
import ynot.core.exception.provider.UnprovidableResourceException;
import ynot.core.listener.provider.resource.ResourceProviderListener;
import ynot.core.provider.resource.ResourceProvider;

/**
 * ResourceProvider with spring logical.
 * 
 * @author equesada
 */
public class SpringResourceProvider implements ResourceProvider<String> {

	/**
	 * The name of the provider.
	 */
	private final String providerName;

	/**
	 * the application context (Spring).
	 */
	private final List<ApplicationContext> context;

	/**
	 * The listeners of the provider.
	 */
	private final List<ResourceProviderListener> listeners;

	/**
	 * The main constructor.
	 * 
	 * @param newProviderName
	 *            the provider name.
	 */
	public SpringResourceProvider(final String newProviderName) {
		providerName = newProviderName;
		context = new ArrayList<ApplicationContext>();
		listeners = new ArrayList<ResourceProviderListener>();
	}

	/**
	 * To load an context.
	 * 
	 * @param newContext
	 *            the context to load
	 */
	public final void addContext(final ApplicationContext newContext) {
		this.context.add(newContext);
	}

	/**
	 * To get the bean on a context.
	 * 
	 * @param objectName
	 *            the name of the bean
	 * @return the bean
	 */
	private Object getBean(final String objectName) {
		for (ApplicationContext oneContext : context) {
			if (oneContext.containsBean(objectName)) {
				return oneContext.getBean(objectName);
			}
		}
		return null;
	}

	/**
	 * To load contexts.
	 * 
	 * @param newContexts
	 *            the list of contexts to load
	 */
	public final void setContexts(final List<ApplicationContext> newContexts) {
		for (ApplicationContext oneContext : newContexts) {
			addContext(oneContext);
		}
	}

	@Override
	public final Resource get(final String resourceName)
			throws UnprovidableResourceException {

		Object content = null;

		if ("shell".equals(resourceName) && "ynot".equals(getName())) {
			content = Shell.getInstance();
		}

		if (content == null) {
			content = getBean(resourceName);
		}

		Resource res = new Resource(providerName, resourceName, content);
		if (res == null || res.getContent() == null) {
			throw new UnprovidableResourceException("resource \""
					+ resourceName + "\" for provider \"" + providerName
					+ "\" not found");
		}
		res = preNoticeListeners(res);
		if (postNoticeListeners(res)) {
			return res;
		} else {
			return null;
		}
	}

	/**
	 * To notice the listeners before.
	 * 
	 * @param res
	 *            the current resource.
	 * @return the current resource.
	 */
	private Resource preNoticeListeners(final Resource res) {
		Resource newRes = res;
		for (ResourceProviderListener listener : listeners) {
			newRes = listener.preNotice(newRes);
		}
		return newRes;
	}

	/**
	 * To notice the listeners after.
	 * 
	 * @param res
	 *            the current resource.
	 * @return true if it needs to continue.
	 */
	private boolean postNoticeListeners(final Resource res) {
		for (ResourceProviderListener listener : listeners) {
			if (!listener.postNotice(res)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public final Resource getNext() {
		return null;
	}

	/**
	 * Not used.
	 * 
	 * @return false;
	 */
	@Override
	public final boolean hasNext() {
		return false;
	}

	@Override
	public final String getName() {
		return providerName;
	}

	@Override
	public final void setListeners(
			final List<ResourceProviderListener> newListeners) {
		for (ResourceProviderListener listener : newListeners) {
			this.listeners.add(listener);
		}
	}

}
