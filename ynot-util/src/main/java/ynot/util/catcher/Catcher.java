package ynot.util.catcher;

import java.util.ArrayList;
import java.util.List;

/**
 * A matcher to launch the clazz when is match on one of catchs.
 * 
 * @author equesada
 */
public class Catcher implements Cloneable {

	/**
	 * Identifier of the catcher.
	 */
	private String id;

	/**
	 * The list of pattern to match.
	 */
	private List<String> matches;

	/**
	 * The list of pattern to unmatch.
	 */
	private List<String> unmatches;

	/**
	 * The parserClazz to instantiate and call.
	 */
	private String parserClazz;

	/**
	 * Constructor using fields.
	 * 
	 * @param newId
	 *            the id of the catcher.
	 * @param newMatches
	 *            the list of matches.
	 * @param newUnmatches
	 *            the list of unmatches.
	 * @param newClazz
	 *            the new parserClazz.
	 */
	public Catcher(final String newId, final List<String> newMatches,
			final List<String> newUnmatches, final String newClazz) {
		super();
		setId(newId);
		setMatches(newMatches);
		setUnmatches(newUnmatches);
		setParserClazz(newClazz);
	}

	/**
	 * @param newId
	 *            the new id to set
	 */
	public final void setId(final String newId) {
		this.id = newId.trim();
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the catchs
	 */
	public final List<String> getMatches() {
		return matches;
	}

	/**
	 * @param newMatches
	 *            the matches to set
	 */
	public final void setMatches(final List<String> newMatches) {
		this.matches = new ArrayList<String>();
		for (String oneMatch : newMatches) {
			this.matches.add(oneMatch.trim());
		}
	}

	/**
	 * @return the parserClazz
	 */
	public final String getParserClazz() {
		return parserClazz;
	}

	/**
	 * @param newClazz
	 *            the parserClazz to set
	 */
	public final void setParserClazz(final String newClazz) {
		this.parserClazz = newClazz.trim();
	}

	/**
	 * To know if the string match with one of the matches.
	 * 
	 * @param str
	 *            the string to test.
	 * @return true if it matchs else false.
	 */
	public final boolean match(final String str) {
		search: for (String onMatch : matches) {
			if (java.util.regex.Pattern.matches(onMatch, str)) {
				for (String onUnmatch : unmatches) {
					if (java.util.regex.Pattern.matches(onUnmatch, str)) {
						continue search;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected final Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * @param newUnmatches
	 *            the unmatches to set
	 */
	public final void setUnmatches(final List<String> newUnmatches) {
		this.unmatches = new ArrayList<String>();
		for (String oneUnmatch : newUnmatches) {
			this.unmatches.add(oneUnmatch.trim());
		}
	}

	/**
	 * @return the unmatches
	 */
	public final List<String> getUnmatches() {
		return unmatches;
	}

}
