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
 * Mini parser for use better syntax when comparing.
 * Example: $a @>=@ $b
 * @author equesada
 */
public class Operation implements RequestParser<String> {

	/**
	 * MethodNameWithParameters mini parser.
	 */
	private final MethodNameWithParameters mnwp;
	
	/**
	 * MethodNameWithParameters mini parser.
	 */	
	private final MethodNameWithVariablesAndParameters mnwvap;
	
	/**
	 * The assignment pattern.
	 */
	private final String assignmentPattern;
	
	/**
	 * The pattern that define an operation.
	 */
	private final java.util.regex.Pattern operationPattern;

	/**
	 * The variables and assignment pattern.
	 */
	private final String variablesAndAssignment;
	
	/**
	 * Default constructor.
	 */
	public Operation() {
		super();
		mnwp = new MethodNameWithParameters();
		mnwvap = new MethodNameWithVariablesAndParameters();
		PatternStorage patternStorage = PatternWarehouse.getStorage();
		Pattern assignment = patternStorage.getPattern("assignment");
		assignmentPattern = assignment.getContent();
		String operation = patternStorage.getPattern("operation").getContent();
		operationPattern =  java.util.regex.Pattern.compile(operation);
		variablesAndAssignment = 
			patternStorage.getPattern("varnames").getContent()
			+ assignmentPattern 
			+ patternStorage.getPattern("any").getContent();
	}

	@Override
	public final List<Request> parse(final String str) 
		throws UnparsableRequestException {
		
		// 1 - Extract the method name @xxx@
		String method = "";
		
        java.util.regex.Matcher m = operationPattern.matcher(str);
        m.find();
        method = str.substring(m.start(), m.end());
        
		String newStr = str.replace(method, ",").trim() + ")";

		if (newStr.matches(variablesAndAssignment)) {
			int i = newStr.indexOf(assignmentPattern);
			newStr = newStr.substring(0, i) + assignmentPattern + " " 
				+ method.substring(1, method.length() - 1) 
				+ "(" + newStr.substring(i 
						+ assignmentPattern.length());
			return mnwvap.parse(newStr);
		} else {
			newStr = method.substring(1, method.length() - 1) 
			    + "(" + newStr;
			return mnwp.parse(newStr);
		}		
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}

}
