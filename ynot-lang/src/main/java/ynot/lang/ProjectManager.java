package ynot.lang;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import ynot.core.exception.provider.UnprovidableCommandException;
import ynot.core.exception.provider.UnprovidableResourceException;
import ynot.util.io.FileManager;

/**
 * This class is useful to manager ynot project.
 * 
 * @author equesada
 * 
 */
public final class ProjectManager {

	/**
	 * The associated file maanger.
	 */
	private FileManager fileManager;

	/**
	 * Default constructor.
	 */
	ProjectManager() {
	}

	/**
	 * To run a ynot script.
	 * 
	 * @param script
	 *            the concerned script.
	 * @throws IOException
	 *             if there is problem.
	 * @throws UnprovidableCommandException
	 *             if there is problem.
	 * @throws UnprovidableResourceException
	 *             if there is problem.
	 * @throws CloneNotSupportedException
	 *             if there is problem.
	 * @throws IllegalAccessException
	 *             if there is problem.
	 * @throws InvocationTargetException
	 *             if there is problem.
	 */
	public void run(final String script) throws IOException,
			UnprovidableCommandException, UnprovidableResourceException,
			CloneNotSupportedException, IllegalAccessException,
			InvocationTargetException {
		String newScript = script;
		if (null == newScript || newScript.trim().isEmpty()) {
			newScript = ".*.ynot(ui)?";
		}
		List<File> files = getFileManager().ls(newScript, false);
		if (files.size() > 0) {
			newScript = files.get(0).getCanonicalPath();
		}
		VirtualMachine.run(newScript);
	}

	/**
	 * To run a ynot script.
	 * 
	 * @param script
	 *            the concerned script.
	 * @throws IOException
	 *             if there is problem.
	 * @throws UnprovidableCommandException
	 *             if there is problem.
	 * @throws UnprovidableResourceException
	 *             if there is problem.
	 * @throws CloneNotSupportedException
	 *             if there is problem.
	 * @throws IllegalAccessException
	 *             if there is problem.
	 * @throws InvocationTargetException
	 *             if there is problem.
	 */
	public static void runFromLauncher(final String script) throws IOException,
			UnprovidableCommandException, UnprovidableResourceException,
			CloneNotSupportedException, IllegalAccessException,
			InvocationTargetException {
		VirtualMachine.run(script);
	}

	/**
	 * @param newFileManager
	 *            the fileManager to set
	 */
	public void setFileManager(final FileManager newFileManager) {
		this.fileManager = newFileManager;
	}

	/**
	 * @return the fileManager
	 */
	public FileManager getFileManager() {
		return fileManager;
	}

}
