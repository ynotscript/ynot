package ynot.util.reflect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * To hack the class path loader system.
 * 
 * @author ERIC.QUESADA
 * 
 */
public final class ClassPathHacker {

    /**
     * To forbid the instanciation.
     */
    private ClassPathHacker() {
    }

    /**
     * The parameters.
     */
    @SuppressWarnings("rawtypes")
    private static final Class[] PARAMETERS = new Class[] { URL.class };

    /**
     * To add a file into the classpath.
     * 
     * @param s
     *            the path of the file (String format).
     * @throws IOException
     *             if something is wrong.
     */
    public static void addFile(final String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }

    /**
     * To add a file into the classpath.
     * 
     * @param f
     *            the file (File format).
     * @throws IOException
     *             if something is wrong.
     */
    @SuppressWarnings("deprecation")
    public static void addFile(final File f) throws IOException {
        addURL(f.toURL());
    }

    /**
     * To add a file into the classpath.
     * 
     * @param u
     *            the file (URL format).
     * @throws IOException
     *             if something is wrong.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void addURL(final URL u) throws IOException {

        URLClassLoader sysloader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", PARAMETERS);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { u });
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException(
                    "Error, could not add URL to system classloader");
        }

    }

}