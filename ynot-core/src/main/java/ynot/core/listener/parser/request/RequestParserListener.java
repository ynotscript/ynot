package ynot.core.listener.parser.request;

import java.util.List;

import ynot.core.entity.Request;
import ynot.core.listener.parser.ParserListener;


/**
 * This interface is used to parse object to get Requests.
 * @param <T> the type of the object to parse.
 * @author equesada
 */
public interface RequestParserListener<T> extends
    ParserListener<List<Request>, T> {

}
