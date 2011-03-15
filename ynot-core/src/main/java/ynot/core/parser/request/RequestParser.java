package ynot.core.parser.request;

import java.util.List;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.core.listener.parser.request.RequestParserListener;
import ynot.core.parser.Parser;

/**
 * To get the important informations from an object.
 * 
 * @param <T>
 *            the type of the request identifier.
 * @author equesada
 */
public interface RequestParser<T>
        extends
        Parser<List<Request>, 
            T, 
            UnparsableRequestException, 
            RequestParserListener<T>> {
}
