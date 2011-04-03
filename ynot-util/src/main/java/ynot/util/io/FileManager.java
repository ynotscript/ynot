package ynot.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;
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
	 * The system properties.
	 */
	public static final Properties SYSTEM_PROPERTIES = System.getProperties();

	/**
	 * The file separator.
	 */
	public static final String FILE_SEPARATOR = SYSTEM_PROPERTIES
			.getProperty("file.separator");

	/**
	 * The user home.
	 */
	public static final String USER_HOME = SYSTEM_PROPERTIES
			.getProperty("user.home");

	/**
	 * Default constructor (take the user home as initialPath).
	 * 
	 * @throws FileNotFoundException
	 *             if the user home is not set.
	 */
	public FileManager() throws FileNotFoundException {
		this(null);
	}

	/**
	 * To initialize the file manager with an initial path.
	 * 
	 * @param newInitialPath
	 *            the path to use.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	public FileManager(final String newInitialPath)
			throws FileNotFoundException {
		File initialPath = null;
		if (null != newInitialPath && !newInitialPath.trim().isEmpty()) {
			initialPath = new File(newInitialPath.trim());
		}
		setCurrentDirectory(initialPath);
	}

	/**
	 * To set the new current directory.
	 * 
	 * @param newDir
	 *            the currentDirectory to set
	 * @throws FileNotFoundException
	 *             if the directory doens't exist.
	 */
	public final void setCurrentDirectory(final File newDir)
			throws FileNotFoundException {
		File dir = newDir;
		if (null == dir) {
			dir = new File(USER_HOME);
		}
		dir = getAbsolutePathFile(dir);
		checkItIsDirectory(dir);
		try {
			this.currentDirectory = dir.getCanonicalFile();
		} catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}

	/**
	 * To get an absolute path file.
	 * 
	 * @param newFile
	 *            the concerned file.
	 * @return the absolute path file.
	 * @throws FileNotFoundException
	 *             if there is a problem with the current directory.
	 */
	private File getAbsolutePathFile(final File newFile)
			throws FileNotFoundException {
		try {
			File file = newFile;
			if (!file.isAbsolute()) {
				file = new File(currentDirectory.getCanonicalPath()
						+ FILE_SEPARATOR + file.getPath());
			}
			return file;
		} catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}

	/**
	 * Check the file exists.
	 * 
	 * @param file
	 *            the concerned file.
	 * @throws FileNotFoundException
	 *             if the file doesn't exist.
	 */
	private void checkItExists(final File file) throws FileNotFoundException {
		if (null == file || !file.exists()) {
			throw new FileNotFoundException("It doesn't exist :"
					+ file.getPath());
		}
	}

	/**
	 * Check the file doesn't exist.
	 * 
	 * @param file
	 *            the concerned file.
	 * @throws IOException
	 *             if the file already exists.
	 */
	private void checkItDoesntExist(final File file) throws IOException {
		if (file.exists()) {
			throw new IOException("It already exists :" + file.getPath());
		}
	}

	/**
	 * Check it's a directory.
	 * 
	 * @param dir
	 *            the concerned directory.
	 * @throws FileNotFoundException
	 *             if it's not a directory or it's doesn't exist.
	 */
	private void checkItIsDirectory(final File dir)
			throws FileNotFoundException {
		checkItExists(dir);
		if (!dir.isDirectory()) {
			throw new FileNotFoundException("This is a file, not a directory :"
					+ dir.getPath());
		}
	}

	/**
	 * To change directory.
	 * 
	 * @param newDirPath
	 *            the new directory.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	public final void cd(final String newDirPath) throws FileNotFoundException {
		File dirPath = null;
		if (null != newDirPath && !newDirPath.trim().isEmpty()) {
			dirPath = new File(newDirPath.trim());
		}
		cd(dirPath);
	}

	/**
	 * To change directory.
	 * 
	 * @param oneDir
	 *            the new directory.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	public final void cd(final File oneDir) throws FileNotFoundException {
		setCurrentDirectory(oneDir);
	}

	/**
	 * To list a directory (or current) with maybe a regexp (or not).
	 * 
	 * @param oneDirAndPattern
	 *            the path to list with maybe a pattern.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void ls(final String oneDirAndPattern) throws IOException {
		String dir = null;
		String pattern = null;
		if (null != oneDirAndPattern && !oneDirAndPattern.trim().isEmpty()) {
			String dirAndPatternTrim = oneDirAndPattern.trim();
			if (dirAndPatternTrim.endsWith(FILE_SEPARATOR)) {
				dir = dirAndPatternTrim;
			} else if (dirAndPatternTrim.contains(FILE_SEPARATOR)) {
				int separatorPos = dirAndPatternTrim
						.lastIndexOf(FILE_SEPARATOR) + 1;
				dir = dirAndPatternTrim.substring(0, separatorPos).trim();
				pattern = dirAndPatternTrim.substring(separatorPos).trim();
			} else {
				pattern = dirAndPatternTrim;
			}
		}
		ls(dir, pattern);
	}

	/**
	 * To list a directory (or current) with maybe a regexp (or not).
	 * 
	 * @param newDir
	 *            the path to list.
	 * @param newPattern
	 *            the regexp to use.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void ls(final String newDir, final String newPattern)
			throws IOException {
		File dir = null;
		String pattern = null;
		if (newDir != null && !newDir.trim().isEmpty()) {
			dir = new File(newDir.trim());
		}
		if (newPattern != null && !newPattern.trim().isEmpty()) {
			pattern = newPattern.trim();
		}
		ls(dir, pattern);
	}

	/**
	 * To list a directory (or current) with maybe a regexp (or not).
	 * 
	 * @param newDir
	 *            the path to list.
	 * @param newPattern
	 *            the regexp to use.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void ls(final File newDir, final String newPattern)
			throws IOException {
		File dir = newDir;
		String pattern = newPattern;
		if (null == dir) {
			dir = currentDirectory;
		}
		File[] listOfFiles = getChildren(dir, pattern);
		displayFiles(listOfFiles);
	}

	/**
	 * Get the children of a directory.
	 * 
	 * @param newDir
	 *            the concerned directory.
	 * @param pattern
	 *            the regexp to use.
	 * @return the children.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	private File[] getChildren(final File newDir, final String pattern)
			throws FileNotFoundException {
		File dir = newDir;
		dir = getAbsolutePathFile(dir);
		checkItIsDirectory(dir);
		File[] listOfFiles = null;
		if (null == pattern) {
			listOfFiles = dir.listFiles();
		} else {
			FilenameFilter filter = new SimpleFilenameFilter(pattern);
			listOfFiles = dir.listFiles(filter);
		}
		return listOfFiles;
	}

	/**
	 * To display files.
	 * 
	 * @param files
	 *            the concerned files.
	 */
	private void displayFiles(final File[] files) {
		for (File oneFile : files) {
			displayFile(oneFile);
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
		File dir = null;
		if (null != oneDir && !oneDir.trim().isEmpty()) {
			dir = new File(oneDir.trim());
		}
		mkdir(dir);
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
		dirFile = getAbsolutePathFile(dirFile);
		checkItDoesntExist(dirFile);
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
		File file = null;
		if (null != oneFile && !oneFile.trim().isEmpty()) {
			file = new File(oneFile.trim());
		}
		mkfile(file);
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
		fileFile = getAbsolutePathFile(fileFile);
		checkItDoesntExist(fileFile);
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
		File file = null;
		if (null != oneFile && !oneFile.trim().isEmpty()) {
			file = new File(oneFile.trim());
		}
		rm(file);
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
		fileFile = getAbsolutePathFile(fileFile);
		checkItExists(fileFile);
		if (!fileFile.delete()) {
			throw new IOException("Not able to remove :" + fileFile.getPath());
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
		String from = null;
		String to = null;
		if (null != currentFileSpaceNewFile
				&& !currentFileSpaceNewFile.trim().isEmpty()) {
			String line = currentFileSpaceNewFile.trim();
			Scanner scanner = new Scanner(line);
			if (scanner.hasNext()) {
				from = scanner.next().trim();
				if (scanner.hasNext()) {
					to = scanner.next().trim();
				}
			}
		}
		mv(from, to);
	}

	/**
	 * To move an existing file or directory.
	 * 
	 * @param fromStr
	 *            the from file.
	 * @param toStr
	 *            the destination file.
	 * @throws IOException
	 *             if not able to move.
	 */
	public final void mv(final String fromStr, final String toStr)
			throws IOException {
		File from = null;
		File to = null;
		if (null != fromStr && !fromStr.trim().isEmpty()) {
			from = new File(fromStr.trim());
		}
		if (null != toStr && !toStr.trim().isEmpty()) {
			to = new File(toStr.trim());
		}
		mv(from, to);
	}

	/**
	 * To move an existing file or directory.
	 * 
	 * @param fromFile
	 *            the current file.
	 * @param toFile
	 *            the destination file.
	 * @throws IOException
	 *             if not able to move.
	 */
	public final void mv(final File fromFile, final File toFile)
			throws IOException {
		File from = fromFile;
		File to = toFile;
		if (null == from) {
			throw new FileNotFoundException("The from path is null");
		}
		if (null == to) {
			throw new FileNotFoundException("The destination path is null");
		}
		from = getAbsolutePathFile(from);
		to = getAbsolutePathFile(to);
		checkItExists(from);
		boolean success = false;
		if (!to.exists()) {
			success = from.renameTo(to);
		} else if (to.exists() && to.isDirectory()) {
			File newPath = new File(to.getCanonicalPath() + FILE_SEPARATOR
					+ from.getName());
			success = from.renameTo(newPath);
		} else {
			throw new IOException("This file already exist :" + to.getPath());
		}
		if (!success) {
			throw new IOException("Not able to move the file from :"
					+ from.getPath() + " to " + to.getPath());
		}
	}

	/**
	 * To display the current directory.
	 * 
	 * @throws IOException
	 *             if there is a problem with the current directory.
	 */
	public final void pwd() throws IOException {
		System.out.println(getCurrentDirectory().getCanonicalPath());
	}

	/**
	 * To display the tree of a directory (current if null or empty).
	 * 
	 * @param dir
	 *            the concerned directory.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	public final void tree(final String dir) throws FileNotFoundException {
		File file = null;
		if (null != dir && !dir.trim().isEmpty()) {
			file = new File(dir.trim());
		}
		tree(file);
	}

	/**
	 * To display the tree of a directory (current if null).
	 * 
	 * @param dir
	 *            the concerned directory.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	public final void tree(final File dir) throws FileNotFoundException {
		tree(0, dir);
	}

	/**
	 * To display a tree level.
	 * 
	 * @param level
	 *            the current level.
	 * @param oneDir
	 *            the current directory.
	 * @throws FileNotFoundException
	 *             if the directory doesn't exist.
	 */
	private void tree(final int level, final File oneDir)
			throws FileNotFoundException {
		File dir = oneDir;
		if (null == dir) {
			dir = getCurrentDirectory();
		}
		dir = getAbsolutePathFile(dir);
		checkItIsDirectory(dir);
		File[] children = getChildren(dir, null);
		for (File oneFile : children) {
			displayFile(level, oneFile);
			if (oneFile.isDirectory()) {
				tree(level + 1, oneFile);
			}
		}
	}

	/**
	 * To display a file.
	 * 
	 * @param file
	 *            the concerned file.
	 */
	private void displayFile(final File file) {
		displayFile(0, file);
	}

	/**
	 * To display a file with the level.
	 * 
	 * @param level
	 *            the concerned level.
	 * @param file
	 *            the concerned file.
	 */
	private void displayFile(final int level, final File file) {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.leftPad("", level, "-"));
		sb.append(file.getName());
		if (file.isDirectory()) {
			sb.append(FILE_SEPARATOR);
		}
		System.out.println(sb.toString());
		sb.setLength(0);
	}

	/**
	 * @return the currentDirectory
	 */
	public final File getCurrentDirectory() {
		return currentDirectory;
	}

}
