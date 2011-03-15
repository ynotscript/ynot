package ynot.util.pattern;

/**
 * A simple pattern.
 * @author equesada
 */
public class Pattern implements Cloneable {

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @param newId the id to set
	 */
	public final void setId(final String newId) {
		this.id = newId;
	}

	/**
	 * @return the content
	 */
	public final String getContent() {
		return content;
	}

	/**
	 * @param newContent the content to set
	 */
	public final void setContent(final String newContent) {
		this.content = newContent;
	}

	/**
	 * The id of the shortcut.
	 */
	private String id;

	/**
	 * the corresponding pattern.
	 */
	private String content;

	/**
	 * Constructor using fields.
	 * @param newId the id of the shortcut.
	 * @param newContent the content of the pattern.
	 */
	public Pattern(final String newId, final String newContent) {
		super();
		this.id = newId;
		this.content = newContent;
	}

	@Override
	protected final Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
