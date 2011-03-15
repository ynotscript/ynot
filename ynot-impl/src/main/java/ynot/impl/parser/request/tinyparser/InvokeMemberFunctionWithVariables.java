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
 * Mini parser for function with returned variables.
 * Example: $a = $var.function()
 * @author equesada
 */
public class InvokeMemberFunctionWithVariables implements
		RequestParser<String> {

	/**
	 * The invokeMemberFunctionMiniParser mini parser.
	 */
	private final InvokeMemberFunction invokeMemberFunctionMiniParser;
	
	/**
	 * The assignment pattern.
	 */
	private final String assignmentPattern;
	
	/**
	 * Default constructor.
	 */
	public InvokeMemberFunctionWithVariables() {
		super();
		PatternStorage patternStorage = PatternWarehouse.getStorage();
		Pattern assignment = patternStorage.getPattern("assignment");
		assignmentPattern = assignment.getContent();
		invokeMemberFunctionMiniParser = new InvokeMemberFunction();
	}
	
	@Override
	public final List<Request> parse(final String str) 
			throws UnparsableRequestException {
		String[] splitted = str.split(assignmentPattern);
		List<Request> ret = invokeMemberFunctionMiniParser
		    .parse(splitted[1]);
		ret.get(0).setVariableNames(getVariableNames(splitted[0]));
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
