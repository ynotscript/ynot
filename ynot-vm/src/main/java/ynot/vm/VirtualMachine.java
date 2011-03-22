package ynot.vm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ynot.core.provider.request.RequestProvider;
import ynot.impl.provider.command.SimpleCommandProvider;
import ynot.impl.provider.request.ScriptRequestProvider;
import ynot.util.reflect.ClassPathHacker;
import ynot.util.reflect.DynamicLoader;

/**
 * Instanciate this class to get the Ynot Shell.
 * 
 * @author ERIC.QUESADA
 * 
 */
public class VirtualMachine {

	/**
	 * Path to use for config when the config isn't given.
	 */
	private static final String DEFAULT_CONFIG_PROPERTIES = "config/config.properties";

	/**
	 * Config of the virtual machine.
	 */
	private Properties config;

	/**
	 * Default constructor using the default config path.
	 * 
	 * @throws IOException
	 *             when the config file is not present.
	 */
	public VirtualMachine() throws IOException {
		this(loadConfiguration(DEFAULT_CONFIG_PROPERTIES));
	}

	/**
	 * Constructor giving the properties file.
	 * 
	 * @param propertiesFile
	 *            the concerned properties file.
	 * @throws IOException
	 *             if the config file doesn't exist in the classpath.
	 */
	public VirtualMachine(final String propertiesFile) throws IOException {
		this(loadConfiguration(propertiesFile));
	}

	/**
	 * Constructor giving the properties.
	 * 
	 * @param newConfig
	 *            the concerned properties.
	 */
	public VirtualMachine(final Properties newConfig) {
		config = newConfig;
	}

	/**
	 * To get a Shell to execute a script.
	 * 
	 * @param script
	 *            the concerned script.
	 * @return the application context.
	 * @throws IOException
	 *             if the script or shell file doesn't exist.
	 */
	public final ApplicationContext getContext(final String script) throws IOException {
		addScriptPathInConfiguration(config, script);
		addScriptPathInClassLoaderPath(config);
		ApplicationContext context = getContext();
		ScriptRequestProvider requestProvirder = (ScriptRequestProvider) context
				.getBean("requestProvider");
		requestProvirder.setScript(script);
		return context;
	}

	/**
	 * o get a Shell taking the request from RequestProvider.
	 * 
	 * @param reqProvider
	 *            the concerned RequestProvider.
	 * @return the application context.
	 * @throws IOException
	 *             if the shell file doesn't exist.
	 */
	public final ApplicationContext getContext(final RequestProvider<String> reqProvider)
			throws IOException {
		ApplicationContext context = getContext();
		SimpleCommandProvider cmdProvider = (SimpleCommandProvider) context
				.getBean("commandProvider");
		cmdProvider.setRequestProvider(reqProvider);
		return context;
	}

	/**
	 * To get the application context (contaings the shell).
	 * 
	 * @return the application context.
	 * @throws MalformedURLException
	 *             if not able to load the libraries.
	 */
	public final ApplicationContext getContext() throws MalformedURLException {
		loadLibrariesInCurrentClassloader(config);
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"config/shell.xml");
		return context;
	}

	/**
	 * To load the main configuration.
	 * 
	 * @param propertiesFile
	 *            the properties file.
	 * 
	 * @return the corresponding properties.
	 * @throws IOException
	 *             if there is a problem. when loading
	 */
	private static Properties loadConfiguration(final String propertiesFile)
			throws IOException {
		InputStream stream = Launcher.class.getClassLoader()
				.getResourceAsStream(propertiesFile);
		Properties properties = new Properties();
		try {
			properties.load(stream);
		} finally {
			stream.close();
		}
		return properties;
	}

	/**
	 * To get the base path of the ynot installation.
	 * 
	 * @return the base path of the ynot installation.
	 */
	public static String getBasePath() {
		String base = null;
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
		} else {
			base = System.getProperty("user.dir");
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
		ClassPathHacker.addFile((String) config.get(ConfigKey.SCRIPT_PATH));
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
		for (File oneLib : libraries) {
			try {
				ClassPathHacker.addFile(oneLib);
			} catch (IOException e) {
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
		String[] libraryLocations = ((String) config.get(ConfigKey.LIBRARIES))
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
	 * Add the ynot script in the main configuration.
	 * 
	 * @param config
	 *            the main config.
	 * @param script
	 *            the current ynot script file to execute.
	 */
	private static void addScriptPathInConfiguration(final Properties config,
			final String script) {
		String scriptPath = ".";
		char lastSepartorChar = '/';
		if (script.contains("\\")) {
			lastSepartorChar = '\\';
		}
		if (script.contains("" + lastSepartorChar)) {
			scriptPath = script.substring(0,
					script.lastIndexOf(lastSepartorChar));
		}
		config.put(ConfigKey.SCRIPT_PATH, scriptPath);
	}

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(VirtualMachine.class);

	/**
	 * Tha pattern to use to match the libraries.
	 */
	private static final String LIBRARY_REGEXP = "^.*\\.jar$";

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
	 * @return the config
	 */
	public final Properties getConfig() {
		return config;
	}

	/**
	 * @param newConfig
	 *            the config to set
	 */
	public final void setConfig(final Properties newConfig) {
		this.config = newConfig;
	}

	/**
	 * CLass to group all the key used in the configuration.
	 * 
	 * @author equesada
	 */
	public final class ConfigKey {

		/**
		 * To forbid the instantiation.
		 */
		private ConfigKey() {
		}

		/**
		 * All the libraries path.
		 */
		public static final String LIBRARIES = "libraries";

		/**
		 * the scriptPath.
		 */
		public static final String SCRIPT_PATH = "scriptPath";

	}
	
	/**
	 * CLass to group all the key used in the context.
	 * 
	 * @author equesada
	 */
	public final class ContextKey {

		/**
		 * To forbid the instantiation.
		 */
		private ContextKey() {
		}
		
		/**
		 * the shell.
		 */
		public static final String SHELL = "shell";
		
		/**
		 * the command provider.
		 */
		public static final String COMMAND_PROVIDER = "commandProvider";
		
		/**
		 * the request provider.
		 */
		public static final String REQUEST_PROVIDER = "requestProvider";
		
		/**
		 * the definition provider.
		 */
		public static final String DEFINITION_PROVIDER = "definitionProvider";

		/**
		 * the resource provider.
		 */
		public static final String RESOURCE_PROVIDER = "resourceProvider";
		
		/**
		 * the request parser.
		 */
		public static final String REQUEST_PARSER = "requestParser";

		/**
		 * the definition parser.
		 */
		public static final String DEFINITION_PARSER = "definitionParser";
		
	}
}
