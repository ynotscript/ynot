package ynot.util.reflect;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * To load dynamicly some jar into the classpath.
 * 
 * @author ERIC.QUESADA
 * 
 */
public final class DynamicLoader {

    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(DynamicLoader.class);

    /**
     * Default constructor.
     */
    private DynamicLoader() {
    }

    /**
     * To load a list of directory.
     * 
     * @param directories
     *            the concerned directories.
     * @param regExp
     *            the regular expression of the jar names.
     * @throws MalformedURLException
     *             if something is wrong.
     */
    public static void loadLibraryDirectory(final List<File> directories,
            final String regExp) throws MalformedURLException {
        for (File oneDirectory : directories) {
            loadLibraryDirectory(oneDirectory, regExp);
        }
    }

    /**
     * To load one direcory.
     * 
     * @param directory
     *            the concerned directory.
     * @param regExp
     *            the regular expression of the jar names.
     * @throws MalformedURLException
     *             if something is wrong.
     */
    public static void loadLibraryDirectory(final File directory,
            final String regExp) throws MalformedURLException {
        String[] libraries = directory.list(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return (name.matches(regExp));
            }
        });
        List<File> librariesToLoad = new ArrayList<File>();
        if (libraries == null) {
            libraries = new String[0];
        }
        logger.info("Directory : " + directory.getPath() + " ("
                + libraries.length + " library to load)");
        for (int i = 0; libraries != null && i < libraries.length; i++) {
            String separator = "/";
            if (directory.getAbsolutePath().contains("\\")) {
                separator = "\\";
            }
            String lib = directory.getAbsolutePath() + separator + libraries[i];
            librariesToLoad.add(new File(lib));
        }
        loadLibrary(librariesToLoad);
    }

    /**
     * To load a list of jars.
     * 
     * @param libraries
     *            the concerned jars.
     * @throws MalformedURLException
     *             if something is wrong.
     */
    public static void loadLibrary(final List<File> libraries)
            throws MalformedURLException {
        for (File oneLibrary : libraries) {
            loadLibrary(oneLibrary);
        }
    }

    /**
     * To load one jars.
     * 
     * @param library
     *            the concerned jar.
     * @throws MalformedURLException
     *             if something is wrong.
     */
    public static void loadLibrary(final File library)
            throws MalformedURLException {
        URL urlToAdd = library.toURI().toURL();
        logger.info("Load library - " + urlToAdd);
        URLClassLoader sysLoader = (URLClassLoader) Thread.currentThread()
                .getContextClassLoader();
        URL[] urls = sysLoader.getURLs();
        URL[] newUrls = new URL[urls.length + 1];
        for (int i = 0; i < urls.length; i++) {
            newUrls[i] = urls[i];
        }
        newUrls[urls.length] = urlToAdd;
        ClassLoader loader = new URLClassLoader(newUrls);
        Thread.currentThread().setContextClassLoader(loader);
    }
}
