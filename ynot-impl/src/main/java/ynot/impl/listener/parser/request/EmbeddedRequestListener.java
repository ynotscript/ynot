package ynot.impl.listener.parser.request;

import static ynot.impl.parser.request.RequestParserHandler.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ynot.core.entity.Request;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.util.pattern.Pattern;
import ynot.util.pattern.PatternStorage;
import ynot.util.pattern.PatternWarehouse;

/**
 * Listener to manage embedded commands.
 * 
 * @author equesada
 */
public class EmbeddedRequestListener implements RequestParserListener<String> {

    @Override
    public final String preNotice(final String objToParse) {
        if (objToParse == null || objToParse.trim().length() == 0) {
            return objToParse;
        }
        List<String> ret = new ArrayList<String>();
        ret.add(objToParse);
        PatternStorage patternStorage = PatternWarehouse.getStorage();
        Pattern assignmentPattern = patternStorage.getPattern("assignment");
        String assignment = assignmentPattern.getContent();
        for (int i = 0; i < ret.size(); i++) {
            if (containsBlock(ret.get(i))) {
                String block = getBlock(ret.get(i));
                String varName = "$tmp" + new Date().getTime() + i;
                String newValue = ret.get(i)
                        .replace("[" + block + "]", varName);
                ret.set(i, newValue);
                if (block.trim().startsWith("$")
                        && block.trim().contains(" " + assignment + " ")) {
                    ret.add(i, varName + ", " + block);
                } else {
                    ret.add(i, varName + " " + assignment + " " + block);
                }
                i = 0;
            }
        }

        return getString(ret);
    }

    /**
     * Concatenate all the strings.
     * 
     * @param ret
     *            all the strings.
     * @return the full string.
     */
    private String getString(final List<String> ret) {
        StringBuilder strBld = new StringBuilder();
        boolean isFirst = true;
        for (String oneLine : ret) {
            if (!isFirst) {
                strBld.append(SUBSTEP_SEPARATOR);
            }
            strBld.append(oneLine);
            isFirst = false;
        }
        return strBld.toString();
    }

    /**
     * To get the inner block.
     * 
     * @param param
     *            the string to analyze.
     * @return the inner block.
     */
    private String getBlock(final String param) {
        int start = 0;
        boolean inString = false;
        boolean nextIsProtected = false;
        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);
            if (nextIsProtected) {
                nextIsProtected = false;
                continue;
            }
            if (c == '\\') {
                nextIsProtected = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (c == '[' && !inString) {
                start = i;
            }
            if (c == ']' && !inString) {
                return param.substring(start + 1, i);
            }
        }
        return null;
    }

    @Override
    public final boolean postNotice(final List<Request> result) {
        return true;
    }

    /**
     * To know if it contains inner block.
     * 
     * @param param
     *            the string to analyze.
     * @return true if there is an inner block else false.
     */
    private boolean containsBlock(final String param) {
        boolean inString = false;
        boolean nextIsProtected = false;
        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);
            if (nextIsProtected) {
                nextIsProtected = false;
                continue;
            }
            if (c == '\\') {
                nextIsProtected = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (c == '[' && !inString) {
                return true;
            }
        }
        return false;
    }

}
