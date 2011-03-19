package ynot.impl.provider.request;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * The provider used to supply requests from ynot script.
 * 
 * @author equesada
 */
public class ScriptRequestProvider extends InputStreamRequestProvider {

	/**
	 * To the full path of the script to parse.
	 * 
	 * @param scriptPath
	 *            the full path.
	 * @throws FileNotFoundException
	 *             if the file isn't found.
	 */
	public final void setScript(final String scriptPath)
			throws FileNotFoundException {
		InputStream newStream = new FileInputStream(scriptPath);
		setInputStream(newStream);
	}

	/**
	 * The constructor.
	 * 
	 * @param newProviderName
	 *            the name of the provider.
	 */
	public ScriptRequestProvider(final String newProviderName) {
		super(newProviderName);
	}

}
