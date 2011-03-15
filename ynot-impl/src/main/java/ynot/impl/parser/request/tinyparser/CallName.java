package ynot.impl.parser.request.tinyparser;

import java.util.ArrayList;
import java.util.List;

import ynot.core.entity.Request;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.request.RequestParser;


/**
 * Mini parser for simple call.
 * Example: *toto()
 * @author equesada
 */
public class CallName implements RequestParser<String> {

	@Override
	public final List<Request> parse(final String string) {
		List<Request> ret = new ArrayList<Request>();
		String str = string.replaceAll("\\(", "");
		str = str.replaceAll("\\)", "");
		str = str.trim();
		str = str.substring(1);
		Request req = new Request();
		req.setWordToUse("call");
		req.setDefinitionProviderName("ynot");
		Object[] args = new Object[2];
		args[0] = str;
		args[1] = new ArrayList<Object>();
		req.setGivenParameters(args);
		ret.add(req);
		return ret;
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}

}
