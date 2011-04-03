package ynot.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	 * @throws IOException
	 *             if the user home is not set.
	 */
	public FileManager() throws IOException {
		this(null);
	}

	/**
	 * To initialize the file manager with an initial path.
	 * 
	 * @param newInitialPath
	 *            the path to use.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public FileManager(final String newInitialPath) throws IOException {
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
	 * @throws IOException
	 *             if the directory doens't exist.
	 */
	public final void setCurrentDirectory(final File newDir) throws IOException {
		File dir = newDir;
		if (null == dir) {
			dir = new File(USER_HOME);
		}
		dir = getAbsolutePathFile(dir);
		checkItIsDirectory(dir);
		this.currentDirectory = dir.getCanonicalFile();
	}

	/**
	 * To get an absolute path file.
	 * 
	 * @param newFile
	 *            the concerned file.
	 * @return the absolute path file.
	 * @throws IOException
	 *             if there is a problem with the current directory.
	 */
	public final File getAbsolutePathFile(final File newFile)
			throws IOException {
		File file = newFile;
		if (null == file) {
			file = new File(".");
		}
		if (!file.isAbsolute()) {
			file = new File(getCurrentDirectory().getCanonicalPath()
					+ FILE_SEPARATOR + file.getPath());
		}
		return file;
	}

	/**
	 * Check the file exists.
	 * 
	 * @param file
	 *            the concerned file.
	 * @throws IOException
	 *             if the file doesn't exist.
	 */
	private void checkItExists(final File file) throws IOException {
		if (null == file) {
			throw new IOException(
					"Can't check if it exists because file is null");
		}
		if (!file.exists()) {
			throw new IOException("It doesn't exist :" + file.getPath());
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
		if (null == file) {
			throw new IOException(
					"Can't check if it doesn't exist because file is null");
		}
		if (file.exists()) {
			throw new IOException("It already exists :" + file.getPath());
		}
	}

	/**
	 * Check it's a directory.
	 * 
	 * @param dir
	 *            the concerned directory.
	 * @throws IOException
	 *             if it's not a directory or it's doesn't exist.
	 */
	private void checkItIsDirectory(final File dir) throws IOException {
		checkItExists(dir);
		if (!dir.isDirectory()) {
			throw new IOException("This is a file, not a directory :"
					+ dir.getPath());
		}
	}

	/**
	 * To change directory.
	 * 
	 * @param newDirPath
	 *            the new directory.
	 * @param display
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void cd(final String newDirPath) throws IOException {
		cd(newDirPath, true);
	}

	/**
	 * To change directory.
	 * 
	 * @param newDirPath
	 *            the new directory.
	 * @param display
	 *            if true so display when several files can be good.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void cd(final String newDirPath, final boolean display)
			throws IOException {
		File dirPath = null;
		if (null != newDirPath && !newDirPath.trim().isEmpty()) {
			dirPath = new File(newDirPath.trim());
		}
		cd(dirPath, display);
	}

	/**
	 * To change directory.
	 * 
	 * @param oneDir
	 *            the new directory.
	 * @param display
	 *            if true so display when several files can be good.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void cd(final File oneDir, final boolean display)
			throws IOException {
		File dir = oneDir;
		if (null != dir) {
			dir = getAbsolutePathFile(dir);
			if (!dir.exists()) {
				List<File> children = ls(dir.getPath(), false);
				if (1 == children.size()) {
					dir = children.get(0);
				} else if (children.size() > 1) {
					if (display) {
						ls(dir.getPath());
						return;
					}
				}
			}
		}
		setCurrentDirectory(dir);
		if (display) {
			pwd();
		}
	}

	/**
	 * To read a file with maybe a regexp (or not) to find the file.
	 * 
	 * @param oneDirAndPattern
	 *            the path to list with maybe a pattern.
	 * @param display
	 *            if true so display.
	 * @return the found files.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final List<String> less(final String oneDirAndPattern,
			final boolean display) throws IOException {
		List<String> ret = new ArrayList<String>();
		List<File> files = ls(oneDirAndPattern, false);
		if (files.size() == 1) {
			File file = files.get(0);
			if (display) {
				System.out.println(file.getCanonicalPath());
			}
			Scanner scan = new Scanner(file);
			while (scan.hasNext()) {
				String next = scan.nextLine();
				ret.add(next);
				if (display) {
					System.out.println(next);
				}
			}
			return ret;
		}
		throw new IOException("File not found " + oneDirAndPattern);
	}

	/**
	 * To list a directory (or current) with maybe a regexp (or not).
	 * 
	 * @param oneDirAndPattern
	 *            the path to list with maybe a pattern.
	 * @return the found files.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final List<File> ls(final String oneDirAndPattern)
			throws IOException {
		return ls(oneDirAndPattern, true);
	}

	/**
	 * To list a directory (or current) with maybe a regexp (or not).
	 * 
	 * @param oneDirAndPattern
	 *            the path to list with maybe a pattern.
	 * @param display
	 *            if true so display.
	 * @return the found files.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final List<File> ls(final String oneDirAndPattern,
			final boolean display) throws IOException {
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
		return ls(dir, pattern, display);
	}

	/**
	 * To list a directory (or current) with maybe a regexp (or not).
	 * 
	 * @param newDir
	 *            the path to list.
	 * @param newPattern
	 *            the regexp to use.
	 * @param display
	 *            if true so display.
	 * @return the found files.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final List<File> ls(final String newDir, final String newPattern,
			final boolean display) throws IOException {
		File dir = null;
		String pattern = null;
		if (newDir != null && !newDir.trim().isEmpty()) {
			dir = new File(newDir.trim());
		}
		if (newPattern != null && !newPattern.trim().isEmpty()) {
			pattern = newPattern.trim();
		}
		return ls(dir, pattern, display);
	}

	/**
	 * To list a directory (or current) with maybe a regexp (or not).
	 * 
	 * @param newDir
	 *            the path to list.
	 * @param newPattern
	 *            the regexp to use.
	 * @param display
	 *            if true so display.
	 * @return the found files.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final List<File> ls(final File newDir, final String newPattern,
			final boolean display) throws IOException {
		File dir = newDir;
		String pattern = newPattern;
		if (null == dir) {
			dir = getCurrentDirectory();
		}
		List<File> listOfFiles = getChildren(dir, pattern);
		if (listOfFiles.isEmpty() && !pattern.isEmpty()) {
			listOfFiles = getChildren(dir, pattern + ".*");
		}
		if (display) {
			displayFiles(listOfFiles);
		}
		return listOfFiles;
	}

	/**
	 * Get the children of a directory.
	 * 
	 * @param newDir
	 *            the concerned directory.
	 * @param pattern
	 *            the regexp to use or null of no filter.
	 * @return the children.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final List<File> getChildren(final File newDir, final String pattern)
			throws IOException {
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
		return Arrays.asList(listOfFiles);
	}

	/**
	 * To display files.
	 * 
	 * @param listOfFiles
	 *            the concerned files.
	 */
	private void displayFiles(final List<File> listOfFiles) {
		for (File oneFile : listOfFiles) {
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
	 * @param content
	 *            content to write (or null if nothing).
	 * @throws IOException
	 *             if not able to create.
	 */
	public final void mkfile(final String oneFile, final Object content)
			throws IOException {
		File file = null;
		if (null != oneFile && !oneFile.trim().isEmpty()) {
			file = new File(oneFile.trim());
		}
		mkfile(file, content);
	}

	/**
	 * To create a new file.
	 * 
	 * @param oneFile
	 *            the concerned file.
	 * @param content
	 *            content to write (or null if nothing).
	 * @throws IOException
	 *             if not able to create.
	 */
	@SuppressWarnings("rawtypes")
	public final void mkfile(final File oneFile, final Object content)
			throws IOException {
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
		if (null != content) {
			PrintWriter output = new PrintWriter(new FileWriter(fileFile));
			if (content instanceof List) {
				for (Object oneThing : (List) content) {
					output.println(oneThing);
				}
			} else {
				output.println(content);
			}
			output.close();
		}
	}

	/**
	 * To remove a list of files or directories inside a directory (or current)
	 * with a regexp (or all the files inside). Use the -r option to apply
	 * recursive mode and -f to force without any question (you can combine
	 * -rf).
	 * 
	 * @param oneDirAndPattern
	 *            the path to identify the directory to target to with maybe a
	 *            pattern to identify the file to remove.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void rm(final String oneDirAndPattern) throws IOException {
		String dir = null;
		String pattern = null;
		boolean forced = false;
		boolean recursive = false;
		if (null != oneDirAndPattern && !oneDirAndPattern.trim().isEmpty()) {
			String dirAndPatternTrim = oneDirAndPattern.trim();
			if (dirAndPatternTrim.startsWith("-")) {
				Scanner scan = new Scanner(dirAndPatternTrim);
				StringBuilder builder = new StringBuilder();
				while (scan.hasNext()) {
					String next = scan.next();
					if (next.startsWith("-")) {
						builder.append(next);
					} else {
						dirAndPatternTrim = next;
						break;
					}
				}
				String options = builder.toString();
				if (options.contains("f") || options.contains("F")) {
					forced = true;
				}
				if (options.contains("r") || options.contains("R")) {
					recursive = true;
				}
			}
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
		rm(dir, pattern, forced, recursive);
	}

	/**
	 * To remove a list of files inside a directory.
	 * 
	 * @param newDir
	 *            the target directory.
	 * @param newPattern
	 *            the regexp to use to identify files (if null so it will take
	 *            all).
	 * @param forced
	 *            if true so a confirmation will be asked for each deletion.
	 * @param recursive
	 *            if true it will use a recursive mode.
	 * @throws IOException
	 *             if not able to delete.
	 */
	public final void rm(final String newDir, final String newPattern,
			final boolean forced, final boolean recursive) throws IOException {
		File dir = null;
		String pattern = null;
		if (newDir != null && !newDir.trim().isEmpty()) {
			dir = new File(newDir.trim());
		}
		if (newPattern != null && !newPattern.trim().isEmpty()) {
			pattern = newPattern.trim();
		}
		rm(dir, pattern, forced, recursive);
	}

	/**
	 * To remove a list of files inside a directory.
	 * 
	 * @param newDir
	 *            the target directory.
	 * @param newPattern
	 *            the regexp to use to identify files (if null so it will take
	 *            all).
	 * @param forced
	 *            if true so a confirmation will be asked for each deletion.
	 * @param recursive
	 *            if true it will use a recursive mode.
	 * @throws IOException
	 *             if not able to delete.
	 */
	public final void rm(final File newDir, final String newPattern,
			final boolean forced, final boolean recursive) throws IOException {
		File dir = newDir;
		String pattern = newPattern;
		if (null == dir) {
			dir = getCurrentDirectory();
		}
		List<File> listOfFiles = getChildren(dir, pattern);
		rm(listOfFiles, forced, recursive);
	}

	/**
	 * To remove a list of files.
	 * 
	 * @param listOfFiles
	 *            the concerned files.
	 * @param forced
	 *            if true so a confirmation will be asked for each deletion.
	 * @param recursive
	 *            if true it will use a recursive mode.
	 * @throws IOException
	 *             if not able to delete.
	 */
	public final void rm(final List<File> listOfFiles, final boolean forced,
			final boolean recursive) throws IOException {
		for (File oneFileToRemove : listOfFiles) {
			if (recursive && oneFileToRemove.isDirectory()) {
				rm(Arrays.asList(oneFileToRemove.listFiles()), forced,
						recursive);
			}
			if (!forced) {
				System.out.println("Do you want to delete (y/n): "
						+ oneFileToRemove.getCanonicalPath());
				Scanner in = new Scanner(System.in);
				if ("y".equals(in.nextLine().trim().toLowerCase())) {
					rm(oneFileToRemove);
				}
			} else {
				rm(oneFileToRemove);
			}
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
	 * To move or rename an existing file or directory.
	 * 
	 * @param fromFileSpaceToFile
	 *            the from file and the destination file.
	 * @throws IOException
	 *             if not able to move.
	 */
	public final void mv(final String fromFileSpaceToFile) throws IOException {
		String from = null;
		String to = null;
		if (null != fromFileSpaceToFile
				&& !fromFileSpaceToFile.trim().isEmpty()) {
			String line = fromFileSpaceToFile.trim();
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
	 * To move or rename an existing file or directory.
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
	 * To move or rename an existing file or directory.
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
			throw new IOException("The from path is null");
		}
		if (null == to) {
			throw new IOException("The destination path is null");
		}
		from = getAbsolutePathFile(from);
		to = getAbsolutePathFile(to);
		if (!from.exists()) {
			List<File> files = ls(fromFile.getPath(), false);
			if (!files.isEmpty()) {
				for (File oneFile : files) {
					mv(oneFile, toFile);
				}
				return;
			}
		}
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
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void tree(final String dir) throws IOException {
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
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	public final void tree(final File dir) throws IOException {
		tree(0, dir);
	}

	/**
	 * To display a tree level.
	 * 
	 * @param level
	 *            the current level.
	 * @param oneDir
	 *            the current directory.
	 * @throws IOException
	 *             if the directory doesn't exist.
	 */
	private void tree(final int level, final File oneDir) throws IOException {
		File dir = oneDir;
		if (null == dir) {
			dir = getCurrentDirectory();
		}
		dir = getAbsolutePathFile(dir);
		checkItIsDirectory(dir);
		List<File> children = getChildren(dir, null);
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
