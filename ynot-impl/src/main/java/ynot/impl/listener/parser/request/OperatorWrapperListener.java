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

/**
 * To wrappe with operation separator the mathematical operators.
 * 
 * @author ERIC.QUESADA
 * 
 */
public class OperatorWrapperListener implements RequestParserListener<String> {

    /**
     * The parser to use to parse arguments.
     */
    protected static final ArgumentParser<String> PARSER;

    static {
        PARSER = new SimpleArgumentParser();
    }

    /**
     * Allowed mathematical characters.
     */
    private static final String ALLOWED_MATH_CHARS;

    /**
     * The math operator separator.
     */
    private static final String MATH_OPERATOR_SEPARATOR;

    /**
     * The assignment sign.
     */
    private static final String ASSIGNMENT_SIGN;

    static {
        PatternStorage patternStorage = PatternWarehouse.getStorage();

        ynot.util.pattern.Pattern allowedMathCharsPattern = patternStorage
                .getPattern("allowedMathChars");
        ALLOWED_MATH_CHARS = allowedMathCharsPattern.getContent();

        ynot.util.pattern.Pattern assignmentPattern = patternStorage
                .getPattern("assignment");
        ASSIGNMENT_SIGN = assignmentPattern.getContent();

        ynot.util.pattern.Pattern mathOperatorSeparatorPattern = patternStorage
                .getPattern("operationSeparator");
        MATH_OPERATOR_SEPARATOR = mathOperatorSeparatorPattern.getContent();
    }

    /**
     * Pattern to use for any character.
     */
    private static final String PATTERN_ANYCHAR = ".*";

    /**
     * Pattern to use for all except the math operators and separator.
     */
    private static final String PATTERN_ALL_EXCEPT_MATH_AND_SEPARATOR = "[^"
            + MATH_OPERATOR_SEPARATOR + ALLOWED_MATH_CHARS + "]";

    /**
     * Pattern to use for the math operators.
     */
    private static final String PATTERN_MATH_OPERATORS = "["
            + ALLOWED_MATH_CHARS + "]+";

    /**
     * The number of groups.
     */
    private static final int NUMBER_OF_GROUPS = 3;

    /**
     * Index of the group before the math group.
     */
    private static final int GROUP_INDEX_BEFORE_MATH_OP = 1;

    /**
     * Index of the math group.
     */
    private static final int GROUP_INDEX_MATH_OP = 2;

    /**
     * Index of the group after the math group.
     */
    private static final int GROUP_INDEX_AFTER_MATH_OP = 3;

    /**
     * The group before the math group.
     */
    private static final String GROUP_BEFORE_MATH_OPERATOR = "("
            + PATTERN_ANYCHAR + PATTERN_ALL_EXCEPT_MATH_AND_SEPARATOR + ")";

    /**
     * The math group.
     */
    private static final String GROUP_MATH_OPERATOR = "("
            + PATTERN_MATH_OPERATORS + ")";

    /**
     * The group after the math group.
     */
    private static final String GROUP_AFTER_MATH_OPERATOR = "("
            + PATTERN_ALL_EXCEPT_MATH_AND_SEPARATOR + PATTERN_ANYCHAR + ")";

    /**
     * All the groups.
     */
    private static final String[] GROUPS = new String[NUMBER_OF_GROUPS];

    static {
        GROUPS[GROUP_INDEX_BEFORE_MATH_OP - 1] = GROUP_BEFORE_MATH_OPERATOR;
        GROUPS[GROUP_INDEX_MATH_OP - 1] = GROUP_MATH_OPERATOR;
        GROUPS[GROUP_INDEX_AFTER_MATH_OP - 1] = GROUP_AFTER_MATH_OPERATOR;
    }

    /**
     * The pattern to find a mathematical operator.
     */
    protected static final Pattern PATTERN = Pattern
            .compile(GROUPS[GROUP_INDEX_BEFORE_MATH_OP]
                    + GROUPS[GROUP_INDEX_MATH_OP]
                    + GROUPS[GROUP_INDEX_AFTER_MATH_OP]);

    @Override
    public final String preNotice(final String objToParse) {
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

    /**
     * Wrappe an operator if there is one math operator in the subStep.
     * 
     * @param currentSubStep the current subStep.
     * @return the subStep with the math operator wrapped.
     */
    private String wrappeOperator(final String currentSubStep) {
        if (!containsOperator(currentSubStep)) {
            return currentSubStep;
        }
        Matcher matcher = PATTERN.matcher(currentSubStep);
        String before = matcher.group(GROUP_INDEX_BEFORE_MATH_OP);
        String cleanedBefore = before;
        if (cleanedBefore.contains(ASSIGNMENT_SIGN)) {
            cleanedBefore = cleanedBefore.substring(cleanedBefore
                    .indexOf(ASSIGNMENT_SIGN) + ASSIGNMENT_SIGN.length());
        }
        String operator = matcher.group(GROUP_INDEX_MATH_OP);
        if (operator.equals(ASSIGNMENT_SIGN)) {
            return currentSubStep;
        }
        String after = matcher.group(GROUP_INDEX_AFTER_MATH_OP);
        try {
            PARSER.parse(cleanedBefore);
            PARSER.parse(after);
            return before + MATH_OPERATOR_SEPARATOR + operator
                    + MATH_OPERATOR_SEPARATOR + after;
        } catch (UnparsableArgumentException e) {
            return currentSubStep;
        }
    }

    /**
     * To check if it contains a mathematical operator.
     * @param stepOrSubStep the step or subStep ti check.
     * @return true if it's the case else false.
     */
    private boolean containsOperator(final String stepOrSubStep) {
        Matcher matcher = PATTERN.matcher(stepOrSubStep);
        return matcher.find();
    }

    @Override
    public final boolean postNotice(final List<Request> result) {
        return true;
    }

}
