package ynot.impl.parser.argument;

import java.util.ArrayList;
import java.util.List;

import ynot.core.entity.Resource;
import ynot.core.exception.parser.UnparsableArgumentException;
import ynot.core.listener.parser.argument.ArgumentParserListener;
import ynot.core.parser.argument.ArgumentParser;

/**
 * A simple implementation of the ArgumentParser (the input is a string).
 * 
 * @author equesada
 */
public class SimpleArgumentParser implements ArgumentParser<String> {

	/**
	 * Used to parse a binary number format.
	 */
	private static final int RADIX_BINARY = 2;

	/**
	 * Used to parse an octal number format.
	 */
	private static final int RADIX_OCTAL = 8;

	/**
	 * Used to parse an hexadecimal number format.
	 */
	private static final int RADIX_HEXADECIMAL = 16;

	/**
	 * The prefix to identify a binary.
	 */
	private static final String PREFIX_BINARY = "2x";

	/**
	 * The prefix to identify a float.
	 */
	private static final String PREFIX_FLOAT = "fx";
	
	/**
	 * The prefix to identify an octal.
	 */
	private static final String PREFIX_OCTAL = "8x";

	/**
	 * The prefix to identify an hexadecimal.
	 */
	private static final String PREFIX_HEXADECIMAL = "16x";

	/**
	 * All listeners to call.
	 */
	private final List<ArgumentParserListener<String>> listeners;

	/**
	 * Default constructor.
	 */
	public SimpleArgumentParser() {
		listeners = new ArrayList<ArgumentParserListener<String>>();
	}

	/**
	 * To parse an argument.
	 * 
	 * @param object
	 *            the string argument to parse.
	 * @return the real argument
	 * @throws UnparsableArgumentException
	 *             if not able to parse
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final Object parse(final String object)
			throws UnparsableArgumentException {
		String obj = object;
		for (ArgumentParserListener<String> oneListerner : listeners) {
			obj = oneListerner.preNotice(obj);
		}
		Object ret = null;
		String str = (obj).trim();
		if (str.startsWith("\"") && str.endsWith("\"")) {
			// String type
			ret = str.substring(1, str.length() - 1);
		} else if (str.equals("true") || str.equals("false")) {
			// Boolean type
			ret = Boolean.parseBoolean(str);
		} else if (str.toLowerCase().equals("null")) {
			// Null
			ret = null;
		} else if (str.startsWith("$")) {
			// Variable
			ret = "@" + str + "@";
		} else if (str.startsWith("?") && str.endsWith("?")) {
			// Object from a resource provider
			String providerName = null;
			String resourceName = str.substring(1, str.length() - 1);
			if (resourceName.contains("\\")) {
				providerName = resourceName.substring(0,
						resourceName.indexOf("\\"));
				resourceName = resourceName.substring(
						resourceName.indexOf("\\") + 1, resourceName.length());
			}
			ret = new Resource(providerName, resourceName, null);
		} else if (str.startsWith("{") && str.endsWith("}")) {
			// Array
			String[] parts = splitArguments(str.substring(1, str.length() - 1));
			ret = new ArrayList<Object>();
			for (String onePart : parts) {
				((List) ret).add(parse(onePart));
			}
		} else if (str.matches("[0-9]+\\.\\.[0-9]+")) {
			// List Number
			ret = new ArrayList<Object>();
			String[] numbers = str.split("\\.\\.");
			int start = Integer.parseInt(numbers[0]);
			int stop = Integer.parseInt(numbers[1]);
			for (int i = start; i <= stop; i++) {
				((List) ret).add(i);
			}
		} else if (str.matches("[0-9]+")) {
			// Integer
			ret = Integer.parseInt(str);
		} else if (str.matches("[0-9]+\\.[0-9]+")) {
			// Double
			ret = Double.parseDouble(str);
		} else if (str.matches(PREFIX_FLOAT + "[0-9]+\\.[0-9]+")) {
			// float
			str = str.substring(PREFIX_FLOAT.length());
			ret = Float.parseFloat(str);
		} else if (str.startsWith(PREFIX_BINARY)) {
			// binary
			str = str.substring(PREFIX_BINARY.length());
			str = str.replaceAll("[^0-1]", "");
			ret = Integer.parseInt(str, RADIX_BINARY);
		} else if (str.startsWith(PREFIX_OCTAL)) {
			// octal
			str = str.substring(PREFIX_OCTAL.length());
			str = str.replaceAll("[^0-7]", "");
			ret = Integer.parseInt(str, RADIX_OCTAL);
		} else if (str.startsWith(PREFIX_HEXADECIMAL)) {
			// hexa
			str = str.substring(PREFIX_HEXADECIMAL.length());
			str = str.toUpperCase();
			str = str.replaceAll("[^0-9A-F]", "");
			ret = Integer.parseInt(str, RADIX_HEXADECIMAL);
		} else {
			throw new UnparsableArgumentException("Not able to parse : "
					+ object);
		}

		for (ArgumentParserListener<String> oneListerner : listeners) {
			if (!oneListerner.postNotice(ret)) {
				return null;
			}
		}
		return ret;
	}

	/**
	 * To split a string from the commas.
	 * 
	 * @param strToSplit
	 *            the spring to split.
	 * @return the different parts of the string.
	 */
	private String[] splitArguments(final String strToSplit) {
		boolean inString = false;
		int inArray = 0;
		boolean nextIsProtected = false;
		List<String> list = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strToSplit.length(); i++) {
			char c = strToSplit.charAt(i);
			if (nextIsProtected) {
				nextIsProtected = false;
				sb.append(c);
				continue;
			}
			if (c == '\\') {
				nextIsProtected = true;
				continue;
			}
			if (c == ',' && inArray == 0 && !inString) {
				String added = sb.toString().trim();
				if (added.length() > 0) {
					list.add(added);
				}
				sb = new StringBuilder();
				continue;
			}
			sb.append(c);
			if (c == '"' && inArray == 0) {
				if (inString) {
					inString = false;
					continue;
				} else {
					inString = true;
					continue;
				}
			}
			if (c == '{' && !inString) {
				inArray++;
				continue;
			}
			if (c == '}' && inArray > 0 && !inString) {
				inArray--;
				continue;
			}
		}
		String added = sb.toString().trim();
		if (added.length() > 0) {
			list.add(added);
		}
		if (list.size() > 0) {
			return list.toArray(new String[list.size()]);
		}
		return new String[0];
	}

	@Override
	public final void setListeners(
			final List<ArgumentParserListener<String>> newListeners) {
		for (ArgumentParserListener<String> listener : newListeners) {
			this.listeners.add(listener);
		}
	}

}
