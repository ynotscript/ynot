/**
 * 
 */
package ynot.impl.parser.request.tinyparser;

import java.util.ArrayList;
import java.util.List;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableArgumentException;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.argument.ArgumentParser;
import ynot.core.parser.request.RequestParser;
import ynot.impl.parser.argument.SimpleArgumentParser;

/**
 * It will return a list with a request that sum 1 to the variable.
 * 
 * @author eric.quesada
 */
public class IncreaseValue implements RequestParser<String> {

	/**
	 * The parser used to parse arguments.
	 */
	private final ArgumentParser<String> argumentParser;

	/**
	 * Default constructor.
	 */
	public IncreaseValue() {
		super();
		argumentParser = new SimpleArgumentParser();
	}

	@Override
	public final List<Request> parse(final String str) {
		List<Request> ret = new ArrayList<Request>();
		try {
			String variable = null;
			if (str.trim().startsWith("+")) {
				int i = str.lastIndexOf('+');
				variable = str.substring(i + 1, str.length()).trim();
			} else {
				int i = str.indexOf('+');
				variable = str.substring(0, i).trim();
			}
			Object arg = argumentParser.parse(variable);
			Request req = new Request();
			req.setDefinitionProviderName("ynot");
			req.setWordToUse("+");
			Object[] params = new Object[2];
			params[0] = arg;
			params[1] = 1;
			req.setGivenParameters(params);
			String[] varNames = new String[1];
			varNames[0] = variable.substring(1);
			req.setVariableNames(varNames);
			ret.add(req);
		} catch (UnparsableArgumentException e) {
			return ret;
		}

		return ret;
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}

}
