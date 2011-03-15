package ynot.impl.parser.request.tinyparser;

import java.util.List;

import ynot.core.entity.Request;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.request.RequestParser;
import ynot.util.pattern.Pattern;
import ynot.util.pattern.PatternStorage;
import ynot.util.pattern.PatternWarehouse;


/**
 * Mini parser for method with returned variables.
 * Example: $a = toto()
 * @author equesada
 */
public class MethodNameWithVariables implements RequestParser<String> {

	/**
	 * The MethodName mini parser.
	 */
	private final MethodName simpleMethodNameMiniParser;

	/**
	 * The assignment pattern.
	 */
	private final String assignmentPattern;
	
	/**
	 * Default constructor.
	 */
	public MethodNameWithVariables() {
		super();
		PatternStorage patternStorage = PatternWarehouse.getStorage();
		Pattern assignment = patternStorage.getPattern("assignment");
		assignmentPattern = assignment.getContent();
		simpleMethodNameMiniParser = new MethodName();
	}

	@Override
	public final List<Request> parse(final String str2) {
		String str = str2.trim();
		int i = str.indexOf(assignmentPattern);
		String[] splitted = new String[2];
		splitted[0] = str.substring(0, i);
		splitted[1] = str.substring(i + 2);
		List<Request> ret = simpleMethodNameMiniParser
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
