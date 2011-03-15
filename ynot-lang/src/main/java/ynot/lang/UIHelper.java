package ynot.lang;

import org.apache.log4j.Logger;

/**
 * Class to help to build a swing interface from an xml.
 * @author equesada
 */
public final class UIHelper {

	// Member(s)

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(UIHelper.class);

	// Constructor(s)

	/**
	 * The default constructor.
	 */
	public UIHelper() {
	}

	// Getter(s)/Setter(s)

	// Other functions

	/**
	 * To load a view.
	 * @param filename view filename.
	 * @return the corresponding view object.
	 */
	public View load(final String filename) {
		View view = new View();
		if (filename == null || filename.isEmpty()) {
			logger.error("unable to load view from null or empty");
			return view;
		}
		try {
			view.load(filename);
		} catch (Exception e) {
			logger.error("unable to load view from " + filename, e);
		}
		return view;
	}
}
