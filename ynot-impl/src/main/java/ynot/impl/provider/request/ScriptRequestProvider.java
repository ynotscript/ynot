package ynot.impl.provider.request;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ynot.core.entity.Request;
import ynot.core.exception.provider.UnprovidableRequestException;
import ynot.core.listener.provider.request.RequestProviderListener;
import ynot.core.parser.request.RequestParser;
import ynot.core.provider.request.RequestProvider;

/**
 * The provider used to supply requests from ynot script.
 * 
 * @author equesada
 */
public class ScriptRequestProvider implements RequestProvider<String> {

	/**
	 * The provider name.
	 */
	private final String providerName;

	/**
	 * The reader to use.
	 */
	private BufferedReader reader;

	/**
	 * To the full path of the script to parse.
	 * @param scriptPath the full path.
	 * @throws FileNotFoundException if the file isn't found.
	 */
	public final void setScript(final String scriptPath) 
	        throws FileNotFoundException {
		InputStream newStream = new FileInputStream(scriptPath);
		reader = new BufferedReader(new InputStreamReader(newStream));
	}

	/**
	 * The request parser.
	 */
	private RequestParser<String> parser;

	/**
	 * The setter of the request parser.
	 * @param newParser the new parser to set.
	 */
	public final void setParser(final RequestParser<String> newParser) {
		this.parser = newParser;
	}

	/**
	 * All the listeners of the provider.
	 */
	private final List<RequestProviderListener> listeners;

	/**
	 * The constructor.
	 * @param newProviderName
	 *            the name of the provider.
	 */
	public ScriptRequestProvider(final String newProviderName) {
		providerName = newProviderName;
		listeners = new ArrayList<RequestProviderListener>();
	}

	/**
	 * Not used.
	 * 
	 * @param info
	 *            key.
	 * @return null.
	 */
	public final List<Request> get(final String info) {
		return null;
	}

	/**
	 * To have the next resource.
	 * 
	 * @return the another resource.
	 * @throws UnprovidableRequestException
	 *             if not able to find requests.
	 */
	public final List<Request> getNext() throws UnprovidableRequestException {
		try {
			String line = reader.readLine();
			if (null == line) {
				return new ArrayList<Request>();
			}
			List<Request> reqs = parser.parse(line);
			reqs = preNoticeListeners(reqs);
			if (postNoticeListeners(reqs)) {
				return reqs;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new UnprovidableRequestException(e);
		}
	}

	/**
	 * Call the listeners before.
	 * @param reqs the current requests.
	 * @return the modified (or not) requests.
	 */
	private List<Request> preNoticeListeners(final List<Request> reqs) {
		List<Request> newReqs = reqs;
		for (RequestProviderListener listener : listeners) {
			newReqs = listener.preNotice(newReqs);
		}
		return newReqs;
	}

	/**
	 * Call the listeners after.
	 * @param reqs the current requests.
	 * @return the modified (or not) requests.
	 */
	private boolean postNoticeListeners(final List<Request> reqs) {
		for (RequestProviderListener listener : listeners) {
			if (!listener.postNotice(reqs)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * To know if the provider have another resource to give.
	 * 
	 * @return true if there is another resource.
	 */
	public final boolean hasNext() {
		try {
			return reader.ready();
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public final String getName() {
		return providerName;
	}

	@Override
	public final void setListeners(
			final List<RequestProviderListener> newListeners) {
		for (RequestProviderListener listener : newListeners) {
			this.listeners.add(listener);
		}
	}

}
