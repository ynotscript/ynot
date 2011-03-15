package ynot.core.parser.definition;

import ynot.core.entity.Definition;
import ynot.core.exception.parser.UnparsableDefinitionException;
import ynot.core.listener.parser.definition.DefinitionParserListener;
import ynot.core.parser.Parser;

/**
 * To get an definition object from a line.
 * 
 * @param <T>
 *            the type of the definition identifier.
 * @author equesada
 */
public interface DefinitionParser<T>
        extends
        Parser<
            Definition, 
            T, 
            UnparsableDefinitionException, 
            DefinitionParserListener<T>> {
}
