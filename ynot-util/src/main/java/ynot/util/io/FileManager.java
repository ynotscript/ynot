package ynot.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

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
	public static final String FILE_SEPARATOR = System.getProperties()
			.getProperty("file.separator");

	/**
	 * Default constructor (take the use home as initialPath).
	 */
	public FileManager() {
		this(System.getProperties().getProperty("user.home"));
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
		if (null == dirPath) {
			dirPath = System.getProperties().getProperty("user.home");
		}
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
					+ FILE_SEPARATOR + dirFile.getPath());
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
	 * 
	 * @param pattern
	 *            pattern to use to filter the files.
	 */
	public final void ls(final String pattern) {
		File[] listOfFiles = null;
		if (null == pattern) {
			listOfFiles = currentDirectory.listFiles();
		} else {
			FilenameFilter filter = new SimpleFilenameFilter(pattern);
			listOfFiles = currentDirectory.listFiles(filter);
		}
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println(listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println(listOfFiles[i].getName() + FILE_SEPARATOR);
			}
		}
	}

	/**
	 * To create a new directory.
	 * 
	 * @param oneDir
	 *            the concerned directory.
	 * @throws IOException
	 *             if not able to create.
	 */
	public final void mkdir(final String oneDir) throws IOException {
		mkdir(new File(oneDir));
	}

	/**
	 * To create a new directory.
	 * 
	 * @param oneDir
	 *            the concerned directory.
	 * @throws IOException
	 *             if not able to create.
	 */
	public final void mkdir(final File oneDir) throws IOException {
		File dirFile = oneDir;
		if (null == dirFile) {
			throw new IOException("The path is null");
		}
		if (!dirFile.isAbsolute()) {
			dirFile = new File(currentDirectory.getAbsolutePath()
					+ FILE_SEPARATOR + dirFile.getPath());
		}
		if (!dirFile.mkdir()) {
			throw new IOException("Not able to create the directory :"
					+ dirFile.getPath());
		}
	}

	/**
	 * To create a new file.
	 * 
	 * @param oneFile
	 *            the concerned file.
	 * @throws IOException
	 *             if not able to create.
	 */
	public final void mkfile(final String oneFile) throws IOException {
		mkfile(new File(oneFile));
	}

	/**
	 * To create a new file.
	 * 
	 * @param oneFile
	 *            the concerned file.
	 * @throws IOException
	 *             if not able to create.
	 */
	public final void mkfile(final File oneFile) throws IOException {
		File fileFile = oneFile;
		if (null == fileFile) {
			throw new IOException("The path is null");
		}
		if (!fileFile.isAbsolute()) {
			fileFile = new File(currentDirectory.getAbsolutePath()
					+ FILE_SEPARATOR + fileFile.getPath());
		}
		if (!fileFile.createNewFile()) {
			throw new IOException("Not able to create the file :"
					+ fileFile.getPath());
		}
	}

	/**
	 * To remove an existing file or directory.
	 * 
	 * @param oneFile
	 *            the concerned file.
	 * @throws IOException
	 *             if not able to remove.
	 */
	public final void rm(final String oneFile) throws IOException {
		rm(new File(oneFile));
	}

	/**
	 * To remove an existing file or directory.
	 * 
	 * @param oneFile
	 *            the concerned file.
	 * @throws IOException
	 *             if not able to remove.
	 */
	public final void rm(final File oneFile) throws IOException {
		File fileFile = oneFile;
		if (null == fileFile) {
			throw new IOException("The path is null");
		}
		if (!fileFile.isAbsolute()) {
			fileFile = new File(currentDirectory.getAbsolutePath()
					+ FILE_SEPARATOR + fileFile.getPath());
		}
		if (!fileFile.delete()) {
			throw new IOException("Not able to remove the file :"
					+ fileFile.getPath());
		}
	}

	/**
	 * To move an existing file or directory.
	 * 
	 * @param currentFileSpaceNewFile
	 *            the current file and the destination file.
	 * @throws IOException
	 *             if not able to move.
	 */
	public final void mv(final String currentFileSpaceNewFile)
			throws IOException {
		String line = currentFileSpaceNewFile.trim();
		Scanner scanner = new Scanner(line);
		if (!scanner.hasNext()) {
			throw new IOException("The from path is null");
		}
		String from = scanner.next();
		if (!scanner.hasNext()) {
			throw new IOException("The destination path is null");
		}
		String to = scanner.next();
		if (scanner.hasNext()) {
			throw new IOException(
					"Too many path, needs only 2 path (from and destination)");
		}
		mv(from, to);
	}

	/**
	 * To move an existing file or directory.
	 * 
	 * @param currentFile
	 *            the current file.
	 * @param newFile
	 *            the destination file.
	 * @throws IOException
	 *             if not able to move.
	 */
	public final void mv(final String currentFile, final String newFile)
			throws IOException {
		mv(new File(currentFile), new File(newFile));
	}

	/**
	 * To move an existing file or directory.
	 * 
	 * @param oneCurrentFile
	 *            the current file.
	 * @param oneNewFile
	 *            the destination file.
	 * @throws IOException
	 *             if not able to move.
	 */
	public final void mv(final File oneCurrentFile, final File oneNewFile)
			throws IOException {
		File currentFile = oneCurrentFile;
		File newFile = oneNewFile;
		if (null == currentFile) {
			throw new IOException("The from path is null");
		}
		if (null == newFile) {
			throw new IOException("The destination path is null");
		}
		if (!currentFile.isAbsolute()) {
			currentFile = new File(currentDirectory.getAbsolutePath()
					+ FILE_SEPARATOR + currentFile.getPath());
		}
		if (!newFile.isAbsolute()) {
			newFile = new File(currentDirectory.getAbsolutePath()
					+ FILE_SEPARATOR + newFile.getPath());
		}
		if (!currentFile.renameTo(newFile)) {
			throw new IOException("Not able to move the file from :"
					+ currentFile.getPath() + " to " + newFile.getPath());
		}
	}

	/**
	 * To display the current directory.
	 */
	public final void pwd() {
		System.out.println(currentDirectory.getAbsolutePath());
	}

	/**
	 * To display the tree from the current directory.
	 */
	public final void tree() {
		displayTreeLevel(0, currentDirectory);
	}

	/**
	 * To display a tree level.
	 * 
	 * @param level
	 *            the current level.
	 * @param dir
	 *            the current directory.
	 */
	private void displayTreeLevel(final int level, final File dir) {
		File[] listOfFiles = dir.listFiles();
		StringBuilder sb = new StringBuilder();
		for (File oneFile : listOfFiles) {
			sb.append(StringUtils.leftPad("", level, "-"));
			sb.append(oneFile.getName());
			if (oneFile.isDirectory()) {
				sb.append(FILE_SEPARATOR);
			}
			System.out.println(sb.toString());
			sb.setLength(0);
			if (oneFile.isDirectory()) {
				displayTreeLevel(level + 1, oneFile);
			}
		}
	}
}
