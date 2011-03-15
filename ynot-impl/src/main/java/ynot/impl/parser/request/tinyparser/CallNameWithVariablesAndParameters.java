package ynot.impl.parser.request.tinyparser;

import java.util.List;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.request.RequestParser;
import ynot.util.pattern.Pattern;
import ynot.util.pattern.PatternStorage;
import ynot.util.pattern.PatternWarehouse;


/**
 * Mini parser for call with parameters and returned variables.
 * Example: $a = *function("abc")
 * @author equesada
 */
public class CallNameWithVariablesAndParameters 
		implements RequestParser<String> {

	/**
	 * The CallNameWithParameters mini parser.
	 */
	private final CallNameWithParameters callNameWithParametersMiniParser;

	/**
	 * The assignment pattern.
	 */
	private final String assignmentPattern;
	
	/**
	 * Default constructor.
	 */
	public CallNameWithVariablesAndParameters() {
		super();
		PatternStorage patternStorage = PatternWarehouse.getStorage();
		Pattern assignment = patternStorage.getPattern("assignment");
		assignmentPattern = assignment.getContent();
		callNameWithParametersMiniParser = new CallNameWithParameters();
	}

	@Override
	public final List<Request> parse(final String str)
	    throws UnparsableRequestException {
		List<Request> ret = callNameWithParametersMiniParser
		    .parse(str.substring(
		    		str.indexOf(assignmentPattern)
		    			+ assignmentPattern.length()));
		String[] newVariableNames = getVariableNames(str.substring(0,
		    str.indexOf(assignmentPattern)));
		ret.get(ret.size() - 1).setVariableNames(newVariableNames);
		return ret;
	}

	/**
	 * To get all the variables contained in a string.
	 * @param string the string to parse.
	 * @return all the variable names.
	 */
	private String[] getVariableNames(final String string) {
		String[] varnames = string.split(",");
		for (int i = 0; i < varnames.length; i++) {
			varnames[i] = varnames[i].trim().substring(1);
		}
		return varnames;
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}
}
