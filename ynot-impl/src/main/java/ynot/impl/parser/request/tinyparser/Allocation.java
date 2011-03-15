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
 * Mini parser for simple assignment.
 * Example: $a = "abc"
 * @author equesada
 */
public class Allocation implements RequestParser<String> {

	/**
	 * The MethodNameWithVariablesAndParameters mini parser.
	 */
	private final MethodNameWithVariablesAndParameters mnwvap;
	
	/**
	 * The assignment pattern.
	 */
	private final String assignmentPattern;

	/**
	 * Default constructor.
	 */
	public Allocation() {
		super();
		PatternStorage patternStorage = PatternWarehouse.getStorage();
		Pattern assignment = patternStorage.getPattern("assignment");
		assignmentPattern = assignment.getContent();
		mnwvap = new MethodNameWithVariablesAndParameters();
	}

	@Override
	public final List<Request> parse(final String string)
	    throws UnparsableRequestException {
		String str = string.replaceFirst(
				assignmentPattern, 
				assignmentPattern + " ynot\\\\assign(");
		str = str + ")";
		return mnwvap.parse(str);
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}

}
