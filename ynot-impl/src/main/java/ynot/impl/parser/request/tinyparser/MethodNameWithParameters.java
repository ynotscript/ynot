package ynot.impl.parser.request.tinyparser;

import java.util.ArrayList;
import java.util.List;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableArgumentException;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.argument.ArgumentParser;
import ynot.core.parser.request.RequestParser;
import ynot.impl.parser.argument.SimpleArgumentParser;


/**
 * Mini parser for method with parameters.
 * Example: toto("abc")
 * @author equesada
 */
public class MethodNameWithParameters implements RequestParser<String> {

	/**
	 * The SimpleMethodName mini parser.
	 */
	private final MethodName simpleMethodNameMiniParser;

	/**
	 * The ArgumentParser.
	 */
	private final ArgumentParser<String> argumentParser;

	/**
	 * Default constructor.
	 */
	public MethodNameWithParameters() {
		super();
		simpleMethodNameMiniParser = new MethodName();
		argumentParser = new SimpleArgumentParser();
	}

	@Override
	public final List<Request> parse(final String str)
	    throws UnparsableRequestException {
		List<Request> ret = simpleMethodNameMiniParser.parse(str
		    .substring(0, str.indexOf("(")));
		String params = str.substring(str.indexOf("(")).trim();
		params = params.substring(1, params.length() - 1);
		try {
			ret.get(0).setGivenParameters(
			    updateGivenParameters(params));
		} catch (UnparsableArgumentException e) {
			throw new UnparsableRequestException(e);
		}
		return ret;
	}

	/**
	 * To get arguments treated by the argumentParser.
	 * @param params the initial parameters.
	 * @return the treated parameters.
	 * @throws UnparsableArgumentException if not able to parse arguments.
	 */
	private Object[] updateGivenParameters(final String params)
	    throws UnparsableArgumentException {

		String[] args = splitArguments(params);
		Object[] arguments = new Object[args.length];
		int i = 0;
		for (String oneParam : args) {
			arguments[i++] = argumentParser.parse(oneParam);
		}
		return arguments;
	}

	/**
	 * To split a string into Lists and SubLists.
	 * @param strToSplit the string to split.
	 * @return the tree structure of list.
	 */
	private String[] splitArguments(final String strToSplit) {
		boolean inString = false;
		boolean inBlock = false;
		int inArray = 0;
		boolean nextIsProtected = false;
		List<String> list = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strToSplit.length(); i++) {
			char c = strToSplit.charAt(i);
			if (nextIsProtected) {
				nextIsProtected = false;
				sb.append(c);
				continue;
			}
			if (c == '\\' && inString) {
				nextIsProtected = true;
				continue;
			}
			if (c == ',' && inArray == 0 && !inString && !inBlock) {
				String added = sb.toString().trim();
				if (added.length() > 0) {
					list.add(added);
				}
				sb = new StringBuilder();
				continue;
			}
			sb.append(c);
			if (c == '"' && inArray == 0 && !inBlock) {
				if (inString) {
					inString = false;
					continue;
				} else {
					inString = true;
					continue;
				}
			}
			if (c == '{' && !inString && !inBlock) {
				inArray++;
				continue;
			}
			if (c == '}' && inArray == 1 && !inString && !inBlock) {
				inArray--;
				continue;
			}
			if (c == '[' && !inString && inArray == 0) {
				inBlock = true;
			}
			if (c == ']' && inBlock && !inString && inArray == 0) {
				inBlock = false;
			}
		}
		String added = sb.toString().trim();
		if (added.length() > 0) {
			list.add(added);
		}
		if (list.size() > 0) {
			return list.toArray(new String[list.size()]);
		}
		return new String[0];
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}

}
