package ynot.util.catcher;

import java.util.List;

/**
 * A matcher to launch the clazz when is match on one of catchs.
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
	 * The parserClazz to instantiate and call.
	 */
	private String parserClazz;
	
	/**
	 * Constructor using fields.
	 * @param newId the id of the catcher.
	 * @param newMatches the list of matches.
	 * @param newClazz the new parserClazz.
	 */
	public Catcher(
			final String newId, 
			final List<String> newMatches, 
			final String newClazz) {
		super();
		setId(newId);
		setMatches(newMatches);
		setParserClazz(newClazz);
	}

	/**
	 * @param newId the new id to set
	 */
	public final void setId(final String newId) {
		this.id = newId;
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
	 * @param newMatches the matches to set
	 */
	public final void setMatches(final List<String> newMatches) {
		this.matches = newMatches;
	}

	/**
	 * @return the parserClazz
	 */
	public final String getParserClazz() {
		return parserClazz;
	}

	/**
	 * @param newClazz the parserClazz to set
	 */
	public final void setParserClazz(final String newClazz) {
		this.parserClazz = newClazz;
	}
	
	/**
	 * To know if the string match with one of the matches.
	 * @param str the string to test.
	 * @return true if it matchs else false.
	 */
	public final boolean match(final String str) {
		for (String onMatch : matches) {
			if (java.util.regex.Pattern.matches(onMatch, str)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected final Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
