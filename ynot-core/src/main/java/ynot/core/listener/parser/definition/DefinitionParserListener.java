package ynot.core.listener.parser.definition;

import ynot.core.entity.Definition;
import ynot.core.listener.parser.ParserListener;

/**
 * This interface is used to parse object to get Definition.
 * @param <T> the type of the object to parse.
 * @author equesada
 */
public interface DefinitionParserListener<T> extends
    ParserListener<Definition, T> {

}
