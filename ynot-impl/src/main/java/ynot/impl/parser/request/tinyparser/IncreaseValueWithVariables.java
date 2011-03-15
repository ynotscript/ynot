package ynot.impl.parser.request.tinyparser;

import java.util.List;

import ynot.core.entity.Request;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.request.RequestParser;
import ynot.util.pattern.Pattern;
import ynot.util.pattern.PatternStorage;
import ynot.util.pattern.PatternWarehouse;


/**
 * Mini parser for increase with returned variables.
 * Example: $a = $b++
 * @author equesada
 */
public class IncreaseValueWithVariables implements RequestParser<String> {

	/**
	 * The IncreaseValue mini parser.
	 */
	private final IncreaseValue increaseValueMiniParser;

	/**
	 * The assignment pattern.
	 */
	private final String assignmentPattern;
	
	/**
	 * Default constructor.
	 */
	public IncreaseValueWithVariables() {
		super();
		PatternStorage patternStorage = PatternWarehouse.getStorage();
		Pattern assignment = patternStorage.getPattern("assignment");
		assignmentPattern = assignment.getContent();
		increaseValueMiniParser = new IncreaseValue();
	}

	@Override
	public final List<Request> parse(final String str) {
		String[] splitted = str.split(assignmentPattern);
		String[] varNamesToAdd = getVariableNames(splitted[0]);
		List<Request> ret = increaseValueMiniParser
		    .parse(splitted[1]);
		String[] varNamesCurrent = ret.get(0).getVariableNames();
		int lenght = (varNamesToAdd.length + varNamesCurrent.length);
		String[] newVarNames = new String[lenght];
		int i = 0;
		for (String onVar : varNamesCurrent) {
			newVarNames[i] = onVar;
			i++;
		}
		for (String onVar : varNamesToAdd) {
			newVarNames[i] = onVar;
			i++;
		}
		ret.get(0).setVariableNames(newVarNames);
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
