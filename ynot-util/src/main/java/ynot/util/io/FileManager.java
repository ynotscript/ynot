package ynot.util.io;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Class to manage file through console.
 * 
 * @author equesada
 */
public class FileManager {

	/**
	 * The current directory.
	 */
	private File currentDirectory;

	/**
	 * The file separator.
	 */
	private String fileSeparator;

	/**
	 * Default constructor (take the use home as initialPath).
	 */
	public FileManager() {
		this(System.getProperties().getProperty("user.home"));
		fileSeparator = System.getProperties().getProperty("file.separator");
	}

	/**
	 * To initialize the file manager with an initial path.
	 * 
	 * @param initialPath
	 *            the path to use.
	 */
	public FileManager(final String initialPath) {
		currentDirectory = new File(initialPath);
	}

	/**
	 * To change directory.
	 * 
	 * @param onePath
	 *            the new directory.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	public final void cd(final String onePath) throws FileNotFoundException {
		String dirPath = onePath;
		if (".".equals(dirPath)) {
			dirPath = currentDirectory.getAbsolutePath();
		}
		if ("..".equals(dirPath)) {
			dirPath = currentDirectory.getParentFile().getAbsolutePath();
		}
		cd(new File(dirPath));
	}

	/**
	 * To change directory.
	 * 
	 * @param oneFile
	 *            the new directory.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	public final void cd(final File oneFile) throws FileNotFoundException {
		File dirFile = oneFile;
		if (dirFile == null) {
			throw new FileNotFoundException("The path is null");
		}
		if (!dirFile.isAbsolute()) {
			dirFile = new File(currentDirectory.getAbsolutePath()
					+ fileSeparator + dirFile.getPath());
		}
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			throw new FileNotFoundException(
					"Path doesn't exist or is not a directory :"
							+ dirFile.getPath());
		}
		currentDirectory = dirFile;
	}

	/**
	 * To list the current directory.
	 */
	public final void ls() {
		File[] listOfFiles = currentDirectory.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("(f) " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("(d) " + listOfFiles[i].getName());
			}
		}
	}
}
