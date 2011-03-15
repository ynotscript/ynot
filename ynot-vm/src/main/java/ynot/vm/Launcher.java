package ynot.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ynot.core.entity.Shell;
import ynot.util.reflect.ClassPathHacker;
import ynot.util.reflect.DynamicLoader;

/**
 * @author equesada
 */
public final class Launcher {

	private static Logger logger = Logger.getLogger(Launcher.class);

	private static final String LIBRARY_REGEXP = "^.*\\.jar$";

	public static void main(final String[] args) throws Exception {
		//try {
			Properties config = loadConfiguration();
			if (hasArguments(args)) {
				String script = getScript(args);
				addScriptInConfiguration(config, script);
			}
			addBasePathInConfiguration(config);
			saveConfiguration(config);
			addScriptPathInClassLoaderPath(config);
			loadLibrariesInCurrentClassloader(config);
			Shell shell = getShell();
			shell.run();
//		} catch (Exception e) {
//			logger.error(e);
//		}
	}

	/**
	 * @param args
	 * @return
	 */
	private static boolean hasArguments(final String[] args) {
		return args.length > 0;
	}

	private static String getScript(final String[] args) throws Exception {
		return args[0];
	}

	private static Properties loadConfiguration() throws IOException {
		String base = getBasePath();
		String configFile = "config/config.properties";
		if (base.contains("\\")) {
			configFile = "config\\config.properties";
		}
		Properties properties = new Properties();
		InputStream stream = new FileInputStream(base + configFile);
		try {
			properties.load(stream);
		} finally {
			stream.close();
		}
		return properties;
	}

	private static void addScriptInConfiguration(Properties config,
			String script) {
		config.put("script", script);
		String scriptPath = ".";
		char lastSepartorChar = '/';
		if (script.contains("\\")) {
			lastSepartorChar = '\\';
		}
		if (script.contains(""+lastSepartorChar)){
			scriptPath = script.substring(0, script.lastIndexOf(lastSepartorChar));
		}
		config.put("scriptPath", scriptPath);
	}

	private static void addBasePathInConfiguration(Properties config) {
		config.put("basepath", getBasePath());
	}

	private static void saveConfiguration(Properties properties)
			throws IOException {
		String base = getBasePath();
		String configFile = "config/config.properties";
		if (base.contains("\\")) {
			configFile = "config\\config.properties";
		}
		OutputStream stream = new FileOutputStream(base + configFile);
		properties.store(stream, "updating : " + new Date());
	}

	public static String getBasePath() {
		String base = "./";
		URL path = Launcher.class.getProtectionDomain().getCodeSource()
				.getLocation();
		if (path.toString().endsWith(".jar")) {
			base = path.toString().replace("file:/", "");
			base = base.substring(0, base.lastIndexOf("/") + 1);
			if (!base.matches("^[a-zA-Z]:.*$")) {
				base = "/" + base;
			} else {
				base = base.replaceAll("/", "\\\\");
				base = base.replaceAll("%20", " ");
			}

		}
		return base;
	}

	private static void addScriptPathInClassLoaderPath(Properties config)
			throws IOException {
		ClassPathHacker.addFile((String) config.get("scriptPath"));
	}

	private static void loadLibrariesInCurrentClassloader(Properties config)
			throws MalformedURLException {
		List<File> libraries = getLibrariesDirectories(config);
		DynamicLoader.loadLibraryDirectory(libraries, LIBRARY_REGEXP);
	}

	/**
	 * Get the directories that contain usable libraries in the resource.
	 * 
	 * @param rootPath
	 *            the root path of the ynot project
	 * @param config
	 *            the properties file that contains the path
	 * @return a list of libraries directory;
	 */
	public final static List<File> getLibrariesDirectories(
			final Properties config) {
		logger.info("### Libraries loading ###");
		List<File> listDir = new ArrayList<File>();
		String[] libraryLocations = ((String) config.get("libraries"))
				.split(",");
		for (String oneLibraryLocation : libraryLocations) {
			String oneLibraryLocationPath = replaceVariables(
					oneLibraryLocation, config);
			if (oneLibraryLocationPath.trim().startsWith(".")) {
				oneLibraryLocationPath = getBasePath() + "/"
						+ oneLibraryLocationPath.trim();
			}
			if (oneLibraryLocationPath.contains("\\")) {
				oneLibraryLocationPath = oneLibraryLocationPath.replace("/",
						"\\");
			}
			oneLibraryLocationPath = oneLibraryLocationPath.trim();
			File file = new File(oneLibraryLocationPath);
			if (file.isDirectory()) {
				logger.info("XXXXXXXXXX Add " + oneLibraryLocationPath);
				listDir.add(file);
			}
		}
		return listDir;
	}

	private static String replaceVariables(String oneLibraryLocation,
			Properties config) {
		String ret = oneLibraryLocation;
		if (oneLibraryLocation.contains("%")) {
			for (Object key : config.keySet()) {
				ret = ret.replace("%" + key + "%", (String) config.get(key));
			}
		}
		return ret;
	}

	public final static Shell getShell() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"shell.xml");
		return (Shell) context.getBean("shell");
	}

}
