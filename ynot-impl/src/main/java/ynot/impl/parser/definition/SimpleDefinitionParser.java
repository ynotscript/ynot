package ynot.impl.parser.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ynot.core.entity.Definition;
import ynot.core.exception.parser.UnparsableArgumentException;
import ynot.core.exception.parser.UnparsableDefinitionException;
import ynot.core.listener.parser.definition.DefinitionParserListener;
import ynot.core.parser.argument.ArgumentParser;
import ynot.core.parser.definition.DefinitionParser;
import ynot.impl.parser.argument.SimpleArgumentParser;

/**
 * The default definition parser.
 * 
 * @author equesada
 */
public class SimpleDefinitionParser implements DefinitionParser<String> {

	/**
	 * The parser use to parse given arguments.
	 */
	private final ArgumentParser<String> parser;

	/**
	 * All the listeners to call.
	 */
	private List<DefinitionParserListener<String>> listeners;

	/**
	 * Default constructor (using a SimpleArgumentParser).
	 */
	public SimpleDefinitionParser() {
		parser = new SimpleArgumentParser();
		listeners = new ArrayList<DefinitionParserListener<String>>();
	}

	/**
	 * Constructor using fields.
	 * 
	 * @param newParser
	 *            the parser to use for given arguments.
	 */
	public SimpleDefinitionParser(final ArgumentParser<String> newParser) {
		super();
		this.parser = newParser;
	}

	/**
	 * To parse a line.
	 * 
	 * @param object
	 *            the line to parse
	 * @return the definition object
	 * @throws UnparsableDefinitionException
	 *             if not able to parse
	 */
	@SuppressWarnings("rawtypes")
	public final Definition parse(final String object)
			throws UnparsableDefinitionException {

		String obj = object;
		for (DefinitionParserListener<String> listener : listeners) {
			obj = listener.preNotice(obj);
		}

		// 0 - Init
		Definition def = new Definition();

		String line = obj.trim();

		// 2 - the name of the target object
		String[] parts = line.split(":");
		if (parts.length < 2) {
			throw new UnparsableDefinitionException("Line " + line
					+ " : incorrect number of parts");
		}
		def.setResourceNameToUse(parseObjectName(parts[0]));
		def.setResourceProviderName(parseProviderName(parts[0]));

		for (int j = 1; j < parts.length; j++) {

			// 3 - the name of the method to call
			String patternStr = "^(.+)\\((.*)\\)$";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(parts[j]);
			if (!matcher.find()) {
				throw new UnparsableDefinitionException("Line "
						+ " : incorrect function definition");
			}
			String methodName = parseMethodName(matcher.group(1));

			// 4 - the function profile and given parameters.
			Class[] parameters;
			if (matcher.groupCount() > 1 && !matcher.group(2).equals("")) {
				String[] classes = matcher.group(2).split(",");
				parameters = new Class[classes.length];
				for (int i = 0; i < classes.length; i++) {
					try {
						String onClass;
						try {
							onClass = parseParameters(methodName, classes[i],
									def, i);
						} catch (UnparsableArgumentException e) {
							throw new UnparsableDefinitionException(
									e.getCause());
						}
						parameters[i] = Thread.currentThread()
								.getContextClassLoader().loadClass(onClass);
					} catch (ClassNotFoundException e) {
						throw new UnparsableDefinitionException("Line " + line
								+ " : unkowned class (" + classes[i] + ")", e);
					}
				}
			} else {
				parameters = new Class[0];
			}
			def.setParameterTypes(methodName, parameters);
			def.getMethodNamesToCall().add(methodName);
		}
		for (DefinitionParserListener<String> listener : listeners) {
			if (!listener.postNotice(def)) {
				return null;
			}
		}
		return def;
	}

	/**
	 * To get the name of the provider.
	 * 
	 * @param str
	 *            the identifier resource.
	 * @return the name of the provider.
	 */
	private String parseProviderName(final String str) {
		String part = str.split("\\\\")[0];
		return part.substring(1, part.length());
	}

	/**
	 * Get the class name and load the constant values.
	 * 
	 * @param methodName
	 *            the method name
	 * @param str
	 *            the string to parse
	 * @param def
	 *            the definition to complete with constants
	 * @param nbArg
	 *            the number (position) of the argument
	 * @return the class name.
	 * @throws UnparsableArgumentException
	 *             if the argument is unparsable.
	 */
	private String parseParameters(final String methodName, final String str,
			final Definition def, final int nbArg)
			throws UnparsableArgumentException {
		String className = null;
		if (str.trim().startsWith("[") && str.trim().endsWith("]")) {
			className = str.trim().substring(1, str.trim().length() - 1);
		} else {
			Object arg = parseArgument(str.trim());
			if (arg == null) {
				className = Object.class.getName();
			} else {
				className = arg.getClass().getName();
			}
			def.getPredefinedValues(methodName).put(nbArg, arg);
		}
		return className;
	}

	/**
	 * To get the value of an argument.
	 * 
	 * @param str
	 *            the str to parse.
	 * @return the value.
	 * @throws UnparsableArgumentException
	 *             if not able to parse.
	 */
	private Object parseArgument(final String str)
			throws UnparsableArgumentException {
		return parser.parse(str);
	}

	/**
	 * Get the method name to call.
	 * 
	 * @param str
	 *            the string to parse.
	 * @return the method name.
	 */
	private String parseMethodName(final String str) {
		return str;
	}

	/**
	 * Get the name of the object to use.
	 * 
	 * @param str
	 *            the string to parse.
	 * @return the parsed name
	 */
	private String parseObjectName(final String str) {
		String part = str.split("\\\\")[1];
		return part.substring(0, part.length() - 1);
	}

	/**
	 * To know if the line is a comment.
	 * 
	 * @param currentLine
	 *            the line to check.
	 * @return true or false.
	 */
	public final boolean isComment(final String currentLine) {
		return currentLine.trim().startsWith("#");
	}

	@Override
	public final void setListeners(
			final List<DefinitionParserListener<String>> newListeners) {
		for (DefinitionParserListener<String> listener : newListeners) {
			this.listeners.add(listener);
		}
	}

}
