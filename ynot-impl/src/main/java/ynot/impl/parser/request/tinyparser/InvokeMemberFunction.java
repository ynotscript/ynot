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
 * Mini parser for function.
 * Example: $var.function()
 * @author equesada
 */
public class InvokeMemberFunction implements RequestParser<String> {

	/**
	 * The number of parameters in standard request.
	 */
	private static final int PARAMETERS_LENGTH = 3;

	/**
	 * The MethodName mini parser.
	 */
	private final MethodName simpleMethodNameMiniParser;
	
	/**
	 * The parser used to parse arguments.
	 */
	private final ArgumentParser<String> argumentParser;
	
	/**
	 * Default constructor.
	 */
	public InvokeMemberFunction() {
		super();
		simpleMethodNameMiniParser = new MethodName();
		argumentParser = new SimpleArgumentParser();
	}
	
	@Override
	public final List<Request> parse(final String str) {
		int i = str.indexOf('.');
		String simpleMethodStr = str.substring(i + 1);
		Request req = simpleMethodNameMiniParser.parse(simpleMethodStr).get(0);
		Request reqToReturn = new Request();
		reqToReturn.setDefinitionProviderName("ynot");
		reqToReturn.setWordToUse("invoke");
		Object[] givenParameters = new Object[PARAMETERS_LENGTH];
		List<Request> reqs = new ArrayList<Request>();
		try {
			givenParameters[0] = argumentParser.parse(str.substring(0, i));
		} catch (UnparsableArgumentException e) {
			return reqs;
		}
		givenParameters[1] = req.getWordToUse();
		givenParameters[2] = new ArrayList<Object>();
		reqToReturn.setGivenParameters(givenParameters);
		reqs.add(reqToReturn);
		return reqs;
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}

}
