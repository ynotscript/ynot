package ynot.impl.listener.parser.request;

import static ynot.impl.parser.request.RequestParserHandler.SUBSTEP_SEPARATOR;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableArgumentException;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.argument.ArgumentParser;
import ynot.impl.parser.argument.SimpleArgumentParser;
import ynot.util.pattern.PatternStorage;
import ynot.util.pattern.PatternWarehouse;

public class OperatorWrapperListener implements RequestParserListener<String> {

	protected static final String OPERATOR_PATTERN = "(.*[^@:+\\-<>=\\!%/\\^\\*])([+\\-<>=\\!%/\\^\\*]+)([^@:+\\-<>=\\!%/\\^\\*].*)";
	protected static final Pattern OPERATOR_PATTERN_OBJ = Pattern
			.compile(OPERATOR_PATTERN);
	protected static final ArgumentParser<String> argumentParser = new SimpleArgumentParser();
	protected static final String assignmentSign;

	static {
		PatternStorage patternStorage = PatternWarehouse.getStorage();
		ynot.util.pattern.Pattern assignmentPattern = patternStorage
				.getPattern("assignment");
		assignmentSign = assignmentPattern.getContent();
	}

	@Override
	public String preNotice(String objToParse) {
		if (objToParse == null || objToParse.trim().length() == 0) {
			return objToParse;
		}
		if (!containsOperator(objToParse)) {
			return objToParse;
		}
		StringBuilder sb = new StringBuilder();
		String[] subSteps = objToParse.split(SUBSTEP_SEPARATOR);
		for (int i = 0; i < subSteps.length; i++) {
			String currentSubStep = subSteps[i];
			currentSubStep = wrappeOperator(currentSubStep);
			sb.append(currentSubStep);
			if ((i + 1) < subSteps.length) {
				sb.append(SUBSTEP_SEPARATOR);
			}
		}
		return sb.toString();
	}

	private String wrappeOperator(String currentSubStep) {
		Matcher matcher = OPERATOR_PATTERN_OBJ.matcher(currentSubStep);
		if (!matcher.find()) {
			return currentSubStep;
		}
		String before = matcher.group(1);
		String cleanedBefore = before;
		if (cleanedBefore.contains(assignmentSign)) {
			cleanedBefore = cleanedBefore.substring(cleanedBefore
					.indexOf(assignmentSign) + assignmentSign.length());
		}
		String operator = matcher.group(2);
		if (operator.equals(assignmentSign)){
			return currentSubStep;
		}
		String after = matcher.group(3);
		try {
			argumentParser.parse(cleanedBefore);
			argumentParser.parse(after);
			return before + "@" + operator + "@" + after;
		} catch (UnparsableArgumentException e) {
			return currentSubStep;
		}
	}

	private boolean containsOperator(String objToParse) {
		return objToParse.matches(OPERATOR_PATTERN);
	}

	@Override
	public boolean postNotice(List<Request> result) {
		return true;
	}

}
