package ynot.lang;

import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

/**
 * Class to help to build a swing interface from an XML file.
 * 
 * @author equesada
 */
public final class UIHelper {

	/**
	 * Opacity to use for the splash screens.
	 */
	private static final float SPLASH_SCREEN_OPACITY = 0.90f;

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(UIHelper.class);

	static {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			logger.error("UIManager.getInstalledLookAndFeels()", e);
		}
	}

	/**
	 * The default constructor.
	 */
	public UIHelper() {
	}

	/**
	 * To load a view.
	 * 
	 * @param filename
	 *            view filename.
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

	/**
	 * To build a splash frame.
	 * 
	 * @param splashFile
	 *            the picture to use.
	 * @return the concerned splash frame.
	 * @throws IOException
	 *             if not able to build.
	 */
	public static SplashFrame splash(final File splashFile) throws IOException {
		return new SplashFrame(splashFile, SPLASH_SCREEN_OPACITY);
	}

}
