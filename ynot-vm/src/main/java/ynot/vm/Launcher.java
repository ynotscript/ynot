package ynot.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ynot.core.entity.Shell;
import ynot.impl.provider.request.ScriptRequestProvider;
import ynot.util.reflect.ClassPathHacker;
import ynot.util.reflect.DynamicLoader;

/**
 * Launcher to use to launch ynot scripts.
 * 
 * @author ERIC.QUESADA
 * 
 */
public final class Launcher {

	/**
	 * To launch a ynot script.
	 * 
	 * @param args
	 *            need to contains the ynot script fillpath.
	 * @throws Exception
	 *             if something is wrong.
	 */
	public static void main(final String[] args) throws Exception {
		Properties config = loadConfiguration();
		if (hasArguments(args)) {
			String script = getScript(args);
			addScriptInConfiguration(config, script);
			addBasePathInConfiguration(config);
			//saveConfiguration(config);
			addScriptPathInClassLoaderPath(config);
			loadLibrariesInCurrentClassloader(config);
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"config/shell.xml");
			ScriptRequestProvider requestProvirder = (ScriptRequestProvider) context
					.getBean("requestProvider");
			requestProvirder.setScript(script);
			Shell shell = (Shell) context.getBean("shell");
			shell.run();
		}
	}

	/**
	 * TO check that there is at least one argument.
	 * 
	 * @param args
	 *            the concerned arguments.
	 * @return true if it's the case else false.
	 */
	private static boolean hasArguments(final String[] args) {
		return args.length > 0;
	}

	/**
	 * To get the script path from the arguments.
	 * 
	 * @param args
	 *            the concerned arguments.
	 * @return the ynot script path from.
	 * @throws Exception
	 *             if there is no argument.
	 */
	private static String getScript(final String[] args) throws Exception {
		return args[0];
	}

	/**
	 * To load the main configuration.
	 * 
	 * @return the correspondind properties.
	 * @throws IOException
	 *             if there is a problem. when loading
	 */
	private static Properties loadConfiguration() throws IOException {
		InputStream stream = Launcher.class.getClassLoader().getResourceAsStream("config/config.properties");
		Properties properties = new Properties();
		try {
			properties.load(stream);
		} finally {
			stream.close();
		}
		return properties;
	}

	/**
	 * Add the ynot script in the main configuration.
	 * 
	 * @param config
	 *            the main config.
	 * @param script
	 *            the current ynot script file to execute.
	 */
	private static void addScriptInConfiguration(final Properties config,
			final String script) {
		// config.put("script", script);
		String scriptPath = ".";
		char lastSepartorChar = '/';
		if (script.contains("\\")) {
			lastSepartorChar = '\\';
		}
		if (script.contains("" + lastSepartorChar)) {
			scriptPath = script.substring(0,
					script.lastIndexOf(lastSepartorChar));
		}
		config.put("scriptPath", scriptPath);
	}

	/**
	 * To add the basepath in the main configuration.
	 * 
	 * @param config
	 *            the main config.
	 */
	private static void addBasePathInConfiguration(final Properties config) {
		config.put("basepath", getBasePath());
	}

	/**
	 * To save the configuration.
	 * 
	 * @param properties
	 *            the concerned configuration.
	 * @throws IOException
	 *             if there is a problem when saving.
	 * @throws URISyntaxException 
	 */
	private static void saveConfiguration(final Properties properties)
			throws IOException, URISyntaxException {
		URL url = Launcher.class.getClassLoader().getResource("config.properties");
		OutputStream stream = new FileOutputStream(new File(url.toURI()));
		properties.store(stream, "updating : " + new Date());
	}

	/**
	 * To get the base path of the ynot installation.
	 * 
	 * @return the base path of the ynot installation.
	 */
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

	/**
	 * To add the scriptPath into the classpath.
	 * 
	 * @param config
	 *            the main config.
	 * @throws IOException
	 *             if there is a problem.
	 */
	private static void addScriptPathInClassLoaderPath(final Properties config)
			throws IOException {
		ClassPathHacker.addFile((String) config.get("scriptPath"));
	}

	/**
	 * Load libraries in the classlaoder from the main config.
	 * 
	 * @param config
	 *            the main config.
	 * @throws MalformedURLException
	 *             if someting is wrong.
	 */
	private static void loadLibrariesInCurrentClassloader(
			final Properties config) throws MalformedURLException {
		List<File> libraries = getLibrariesDirectories(config);
		for(File oneLib : libraries){
			try {
				ClassPathHacker.addFile(oneLib);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		DynamicLoader.loadLibraryDirectory(libraries, LIBRARY_REGEXP);
	}

	/**
	 * Get the directories that contain usable libraries in the resource.
	 * 
	 * @param config
	 *            the properties file that contains the path
	 * @return a list of libraries directory;
	 */
	public static List<File> getLibrariesDirectories(final Properties config) {
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

	/**
	 * Replace variable into the config.
	 * 
	 * @param oneLibraryLocation
	 *            the line where replace.
	 * @param config
	 *            the full configuration.
	 * @return the modified line.
	 */
	private static String replaceVariables(final String oneLibraryLocation,
			final Properties config) {
		String ret = oneLibraryLocation;
		if (oneLibraryLocation.contains("%")) {
			for (Object key : config.keySet()) {
				ret = ret.replace("%" + key + "%", (String) config.get(key));
			}
		}
		return ret;
	}

	/**
	 * To forbid the instanciation.
	 */
	private Launcher() {
	}

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(Launcher.class);

	/**
	 * Tha pattern to use to match the libraries.
	 */
	private static final String LIBRARY_REGEXP = "^.*\\.jar$";

}
