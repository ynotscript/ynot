package ynot.lang;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JWindow;

import com.sun.awt.AWTUtilities;

/**
 * To use to display a splash screen.
 * 
 * @author equesada
 */
public class SplashFrame extends JWindow {

	/**
	 * Default serial Id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The picture to use for the splash screen.
	 */
	private Image img;

	/**
	 * The constructor to build a splashFrame.
	 * 
	 * @param splashFile
	 *            the picture to use.
	 * @param opacity
	 *            the opacity.
	 * @throws IOException
	 *             if not able to build.
	 */
	public SplashFrame(final File splashFile, final float opacity)
			throws IOException {
		AWTUtilities.setWindowOpacity(this, opacity);
		AWTUtilities.setWindowOpaque(this, false);
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		img = Toolkit.getDefaultToolkit().createImage(
				splashFile.getCanonicalPath());
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(img, 0);
		try {
			mt.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int imgHeight = img.getHeight(null);
		int imgWidth = img.getWidth(null);
		int imgX = (screenDimension.width - imgWidth) / 2;
		int imgY = (screenDimension.height - imgHeight) / 2;
		setLocation(imgX, imgY);
		setSize(imgWidth, imgHeight);
	}

	@Override
	public final void paint(final Graphics g) {
		g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
	}

	@Override
	public final void update(final Graphics g) {
		paint(g);
	}

}
