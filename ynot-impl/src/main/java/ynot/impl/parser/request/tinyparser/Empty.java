package ynot.impl.parser.request.tinyparser;

import java.util.ArrayList;
import java.util.List;

import ynot.core.entity.Request;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.request.RequestParser;


/**
 * It will return a list of on request unactive.
 * @author eric.quesada
 */
public class Empty implements RequestParser<String> {

	@Override
	public final List<Request> parse(final String str) {
		List<Request> ret = new ArrayList<Request>();
		Request req = new Request();
		req.setActive(false);
		req.setWordToUse(str);
		ret.add(req);
		return ret;
	}

	@Override
	public void setListeners(
	        final List<RequestParserListener<String>> listeners) {
	}

}
