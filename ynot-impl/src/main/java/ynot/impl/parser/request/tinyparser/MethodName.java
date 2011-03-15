package ynot.impl.parser.request.tinyparser;

import java.util.ArrayList;
import java.util.List;

import ynot.core.entity.Request;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.request.RequestParser;


/**
 * Mini parser for simple method.
 * Example: toto()
 * @author equesada.
 */
public class MethodName implements RequestParser<String> {

	@Override
	public final List<Request> parse(final String string) {
		List<Request> ret = new ArrayList<Request>();
		String str = string.replaceAll("\\(", "");
		str = str.replaceAll("\\)", "");
		str = str.trim();
		if (str.startsWith("@")) {
			str = str.substring(1);
		}
		if (str.endsWith("@")) {
			str = str.substring(0, str.length() - 1);
		}
		Request req = new Request();
		if (str.contains("\\")) {
			req.setDefinitionProviderName(str.substring(0, str
			    .indexOf("\\")));
			req.setWordToUse(str.substring(str.indexOf("\\") + 1, str
			    .length()));
		} else {
			req.setWordToUse(str);
		}
		ret.add(req);
		return ret;
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}
}
