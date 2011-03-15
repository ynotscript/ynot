package ynot.impl.parser.request;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.request.RequestParser;
import ynot.util.catcher.Catcher;
import ynot.util.catcher.CatcherStorage;
import ynot.util.catcher.CatcherWarehouse;
import ynot.util.reflect.ReflectionManager;

/**
 * This parser will call mini parser to parse string.
 * 
 * @author equesada
 */
public class RequestParserHandler implements RequestParser<String> {

	
	/**
	 * Separator used to make several request on the same line.
	 */
	public static final String SUBSTEP_SEPARATOR = "#>#";
	
	/**
	 * The logger.
	 */
	private static Logger logger = Logger
			.getLogger(RequestParserHandler.class);

	/**
	 * All the available mini parsers.
	 */
	private final Map<String, RequestParser<String>> tinyParsers;

	/**
	 * ALl the listeners to call.
	 */
	private final List<RequestParserListener<String>> listeners;

	/**
	 * The storage of catcher.
	 */
	private final CatcherStorage storage;

	/**
	 * The default constructor.
	 */
	public RequestParserHandler() {
		this(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("matchers.xml"));
	}

	/**
	 * Constructor using mini parsers and patterns.
	 * 
	 * @param miniParsersList
	 *            the mini parsers.
	 */
	public RequestParserHandler(final InputStream miniParsersList) {
		storage = CatcherWarehouse.getStorage();
		listeners = new ArrayList<RequestParserListener<String>>();
		tinyParsers = new HashMap<String, RequestParser<String>>();
	}

	/**
	 * To parse a string and give requests.
	 * 
	 * @param string
	 *            the string to parse.
	 * @throws UnparsableRequestException
	 *             if not able to parse.
	 * @return the list of requests.
	 */
	@Override
	public final List<Request> parse(final String string)
			throws UnparsableRequestException {
		String str = string.trim();
		for (RequestParserListener<String> listener : listeners) {
			str = listener.preNotice(str);
		}
		List<Request> ret = new ArrayList<Request>();
		if (str.length() == 0) {
			return ret;
		}
		String[] lines = str.split(SUBSTEP_SEPARATOR);
		for (String oneLine : lines) {
			ret.addAll(parseOneLine(oneLine));
		}

		for (RequestParserListener<String> listener : listeners) {
			if (!listener.postNotice(ret)) {
				return null;
			}
		}
		return ret;
	}

	/**
	 * To parse one line using sub parsers.
	 * 
	 * @param line
	 *            the line to parse.
	 * @return the list of requests.
	 * @throws UnparsableRequestException
	 *             if not able to parse.
	 */
	private List<Request> parseOneLine(final String line)
			throws UnparsableRequestException {
		String catcherId = storage.find(line);
		if (catcherId == null) {
			throw new UnparsableRequestException(line);
		}
		RequestParser<String> subParser = getTinyParser(catcherId);
		if (subParser == null) {
			throw new UnparsableRequestException(line);
		}
		return subParser.parse(line);
	}

	/**
	 * To get the RequestParser<String> from the catcherId.
	 * 
	 * @param catcherId
	 *            the catcherId
	 * @return the corresponding RequestParser<String>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private RequestParser<String> getTinyParser(final String catcherId) {
		if (tinyParsers.get(catcherId) != null) {
			return tinyParsers.get(catcherId);
		}
		Catcher catcher = storage.getCatcher(catcherId);
		String className = catcher.getParserClazz().trim();
		Class cl;
		try {
			cl = ReflectionManager.getClass(className,
					new ArrayList<String>());
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		Constructor co;
		try {
			co = ReflectionManager.getConstructor(cl, new Object[0]);
		} catch (Exception e1) {
			logger.error(e1);
			return null;
		}
		RequestParser<String> ret = null;
		try {
			ret = (RequestParser<String>) ReflectionManager
					.getObject(co, new Object[0]);
		} catch (Exception e) {
			logger.error(e);
		}
		tinyParsers.put(catcherId, ret);
		return ret;
	}

	@Override
	public final void setListeners(
			final List<RequestParserListener<String>> newListeners) {
		for (RequestParserListener<String> listener : newListeners) {
			this.listeners.add(listener);
		}
	}
}
