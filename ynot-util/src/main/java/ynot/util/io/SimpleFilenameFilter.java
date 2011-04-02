package ynot.util.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A simple FilenameFilter using a pattern.
 * 
 * @author equesada
 */
public class SimpleFilenameFilter implements FilenameFilter {

	/**
	 * The pattern to use.
	 */
	private String pattern;

	/**
	 * Default constructor.
	 */
	public SimpleFilenameFilter() {
		this("");
	}

	/**
	 * Constructor with pattern.
	 * 
	 * @param newPattern the pattern to use.
	 */
	public SimpleFilenameFilter(final String newPattern) {
		setPattern(newPattern);
	}

	@Override
	public final boolean accept(final File dir, final String name) {
		return name.matches(pattern);
	}

	/**
	 * @return the pattern
	 */
	public final String getPattern() {
		return pattern;
	}

	/**
	 * @param newPattern
	 *            the pattern to set
	 */
	public final void setPattern(final String newPattern) {
		this.pattern = newPattern;
	}

}
