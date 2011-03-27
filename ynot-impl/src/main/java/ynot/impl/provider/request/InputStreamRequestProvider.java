package ynot.impl.provider.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.List;

import ynot.core.entity.Request;
import ynot.core.exception.provider.UnprovidableRequestException;
import ynot.core.listener.provider.request.RequestProviderListener;
import ynot.core.parser.request.RequestParser;
import ynot.core.provider.request.RequestProvider;

/**
 * The provider used to supply requests from an input stream.
 * 
 * @author equesada
 */
public class InputStreamRequestProvider implements RequestProvider<String> {

	/**
	 * The provider name.
	 */
	private final String providerName;

	/**
	 * The reader to use.
	 */
	private InputStream in;

	/**
	 * The reader to use when it's not an Piped version.
	 */
	private BufferedReader reader;

	/**
	 * To set the input stream.
	 * 
	 * @param newStream
	 *            the input stream to use.
	 */
	public final void setInputStream(final InputStream newStream) {
		reader = new BufferedReader(new InputStreamReader(newStream));
		in = newStream;
	}

	/**
	 * The request parser.
	 */
	private RequestParser<String> parser;

	/**
	 * The setter of the request parser.
	 * 
	 * @param newParser
	 *            the new parser to set.
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
	 * 
	 * @param newProviderName
	 *            the name of the provider.
	 */
	public InputStreamRequestProvider(final String newProviderName) {
		providerName = newProviderName;
		listeners = new ArrayList<RequestProviderListener>();
	}

	@Override
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
			String line = null;
			if (in instanceof PipedInputStream) {
				line = readLine((PipedInputStream) in);
			} else {
				line = reader.readLine();
			}
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
	 * To read a line from a pipe.
	 * 
	 * @param inStream
	 *            the piped stream.
	 * @return the line.
	 * @throws IOException
	 *             if not able to read.
	 */
	public final String readLine(final PipedInputStream inStream)
			throws IOException {
		String input = "";
		do {
			int available = inStream.available();
			if (available == 0) {
				break;
			}
			byte[] b = new byte[available];
			inStream.read(b);
			input = input + new String(b, 0, b.length);
		} while (!input.endsWith("\n") && !input.endsWith("\r\n"));
		return input;
	}

	/**
	 * Call the listeners before.
	 * 
	 * @param reqs
	 *            the current requests.
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
	 * 
	 * @param reqs
	 *            the current requests.
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

	@Override
	public final boolean hasNext() throws UnprovidableRequestException {
		try {
			if (in instanceof PipedInputStream) {
				return ((PipedInputStream) in).available() > 0;
			} else {
				return reader.ready();
			}
		} catch (IOException e) {
			throw new UnprovidableRequestException(e);
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
