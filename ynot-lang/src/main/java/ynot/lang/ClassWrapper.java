package ynot.lang;

/**
 * Class to wrap a class object.
 * @author equesada
 */
@SuppressWarnings("rawtypes")
public class ClassWrapper {

    /**
     * The embedded class.
     */
	private final Class clazz;

	/**
	 * Constructor.
	 * @param cl the class to wrap.
	 */
	public ClassWrapper(final Class cl) {
		clazz = cl;
	}

	/**
	 * @return the clazz
	 */
	public final Class getClazz() {
		return clazz;
	}
}
