package ynot.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import ynot.core.entity.Shell;
import ynot.util.reflect.ReflectionManager;

/**
 * To be able to build in the code.
 * 
 * @author equesada
 */
public class Builder {

	// Member(s)

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(Builder.class);

	/**
	 * The namespace list to use.
	 */
	private List<String> loadedPackages;

	// Constructor(s)

	/**
	 * The default constructor.
	 */
	public Builder() {
		loadedPackages = new ArrayList<String>();
	}

	/**
	 * @param newLoadedPackages
	 *            the loadedPackages to set
	 */
	public final void setLoadedPackages(final List<String> newLoadedPackages) {
		this.loadedPackages = newLoadedPackages;
	}

	/**
	 * To add a new namespace.
	 * 
	 * @param ns
	 *            the namespace to add.
	 */
	public final void addNamespace(final String ns) {
		loadedPackages.add(ns);
	}

	// Getter(s)/Setter(s)

	/**
	 * To get an object able to log everywhere.
	 * 
	 * @return a logger object
	 */
	public final Logger getLogger() {
		return Builder.logger;
	}

	// Other functions

	/**
	 * To get the data member of an object from his name.
	 * 
	 * @param object
	 *            the concerned object.
	 * @param memberName
	 *            the member name.
	 * @return the value.
	 * @throws IllegalAccessException
	 *             if there is an illegal access.
	 * @throws NoSuchFieldException
	 *             if the field is missing.
	 */
	private Object getMember(final Object object, final String memberName)
			throws NoSuchFieldException, IllegalAccessException {
		@SuppressWarnings("rawtypes")
		Class clazz = object.getClass();
		if (object instanceof ClassWrapper) {
			clazz = ((ClassWrapper) object).getClazz();
			return ReflectionManager.getMember(null, clazz, memberName);
		} else {
			return ReflectionManager.getMember(object, clazz, memberName);
		}
	}

	/**
	 * To set the data member of an object from his name.
	 * 
	 * @param object
	 *            the concerned object.
	 * @param memberName
	 *            the member name.
	 * @param newValue
	 *            the new value.
	 * @return the new value.
	 * @throws IllegalAccessException
	 *             if there is an illegal access.
	 * @throws NoSuchFieldException
	 *             if the field is missing.
	 */
	private Object setMember(final Object object, final String memberName,
			final Object newValue) throws NoSuchFieldException,
			IllegalAccessException {
		@SuppressWarnings("rawtypes")
		Class clazz = object.getClass();
		if (object instanceof ClassWrapper) {
			clazz = ((ClassWrapper) object).getClazz();
			return ReflectionManager.setMember(null, clazz, memberName,
					newValue);
		} else {
			return ReflectionManager.setMember(object, clazz, memberName,
					newValue);
		}
	}

	/**
	 * To get a wrapper of a class.
	 * 
	 * @param className
	 *            the full classname.
	 * @return the corresponding ClassWrapper.
	 * @throws ClassNotFoundException
	 *             if the class doesn't exist.
	 */
	public final ClassWrapper getClassWrapper(final String className)
			throws ClassNotFoundException {
		// 1 - Get Class from his name
		@SuppressWarnings("rawtypes")
		Class cl = ReflectionManager.getClass(className, getLoadedPackages());
		return new ClassWrapper(cl);
	}

	/**
	 * To call a method (reflection).
	 * 
	 * @param object
	 *            the object to use.
	 * @param methodName
	 *            the method name to call.
	 * @param argList
	 *            the arguments to give.
	 * @return the return of the called method.
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException if there is a problem when invocing.
	 * @throws IllegalAccessException if there an illegal access.
	 * @throws NoSuchMethodException if the method doesn't exist.
	 * @throws NoSuchFieldException if the field doesn't exist.
	 */
	public final Object call(final Object object, final String methodName,
			final List<Object> argList) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {
		if (object == null) {
			throw new IllegalArgumentException("object to use to call is null ");
		}
		List<Object> arguments = argList;
		if (arguments == null) {
			arguments = new ArrayList<Object>();
		}
		Object[] args = new Object[arguments.size()];
		@SuppressWarnings("rawtypes")
		Class clazz = object.getClass();
		if (object instanceof ClassWrapper) {
			clazz = ((ClassWrapper) object).getClazz();
		}
		try {
			Method method = ReflectionManager.getMethod(clazz, methodName,
					arguments.toArray(args));
			return method.invoke(object, args);
		} catch (NoSuchMethodException e) {
			String memberName = methodName;
			if (arguments.isEmpty()) {
				return getMember(object, memberName);
			} else if (arguments.size() == 1) {
				Object newValue = arguments.get(0);
				return setMember(object, memberName, newValue);
			}
			throw new IllegalArgumentException("method " + methodName
					+ " not found or bad parameters");
		}
	}

	/**
	 * To build an object from the parameters.
	 * 
	 * @param className
	 *            the class name to use.
	 * @param objs
	 *            the parameters for the constructors.
	 * @return an instantiate of the class with the parameters.
	 * @throws Exception
	 *             if something is bad.
	 */
	public final Object build(final String className, final Object[] objs)
			throws Exception {
		Object[] objects = objs;
		if (objects == null) {
			objects = new Object[0];
		}
		List<Object> arguments = Arrays.asList(objects);
		return build(className, arguments);
	}

	/**
	 * To create an instance from class name.
	 * 
	 * @param obj
	 * @param classname
	 *            the class name.
	 * @param argList
	 *            the arguments to give to the constructor.
	 * @return the created object.
	 * @throws Exception
	 *             if something is bad.
	 */
	@SuppressWarnings("unchecked")
	public final Object build(final String classname, final List<Object> argList)
			throws Exception {

		// 0 - Initialize
		List<Object> arguments = argList;
		if (arguments == null) {
			arguments = new ArrayList<Object>();
		}
		Object[] args = arguments.toArray(new Object[arguments.size()]);

		// 1 - Get Class from his name
		Class<Object> cl = ReflectionManager.getClass(classname.trim(),
				getLoadedPackages());

		if (cl == null) {
			return null;
		}

		// 2 - Get the constructor from the Class
		Constructor<Object> constructor = ReflectionManager.getConstructor(cl,
				args);

		if (constructor == null) {
			return null;
		}

		// 3 - Create an object with this Constructor
		return ReflectionManager.getObject(constructor, args);
	}

	/**
	 * To get the proxy implementing the interfaces.
	 * 
	 * @param interfaceNames
	 *            the interfaces list.
	 * @param functionName
	 *            the function name to call.
	 * @param shell
	 *            the concerned shell.
	 * @param structure
	 *            the concerned structure.
	 * @return the proxy object.
	 * @throws ClassNotFoundException if the class is not found.
	 */
	public final Object getProxy(final List<String> interfaceNames,
			final String functionName, final Shell shell,
			final Structure structure) throws ClassNotFoundException {
		@SuppressWarnings("rawtypes")
		Class[] interfaces = new Class[interfaceNames.size()];
		for (int i = 0; i < interfaceNames.size(); ++i) {
			String className = interfaceNames.get(i);
			interfaces[i] = ReflectionManager.getClass(className,
					getLoadedPackages());
		}
		return java.lang.reflect.Proxy.newProxyInstance(this.getClass()
				.getClassLoader(), interfaces, new FunctionInvocationHandler(
				shell, structure, functionName));
	}

	/**
	 * To get the list of namespace to test when it is missing.
	 * 
	 * @return the list of namespace.
	 */
	public final List<String> getLoadedPackages() {
		return loadedPackages;
	}

}
