package ynot.core.parser.argument;

import ynot.core.exception.parser.UnparsableArgumentException;
import ynot.core.listener.parser.argument.ArgumentParserListener;
import ynot.core.parser.Parser;

/**
 * This class is used to get an Object value from an input (often string).
 * @param <T> the type of the argument identifier.
 * @author equesada
 */
public interface ArgumentParser<T>
    extends
    Parser<Object, T, UnparsableArgumentException, ArgumentParserListener<T>> {
}
