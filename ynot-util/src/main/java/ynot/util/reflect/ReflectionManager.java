package ynot.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This class group all interesting functions about reflection.
 * 
 * @author equesada
 */
public final class ReflectionManager {

    /**
     * A private constructor to forbid his access.
     */
    private ReflectionManager() {
    }

    /**
     * To get an instance from a constructor and arguments.
     * 
     * @param constructor
     *            the constructor to use.
     * @param args
     *            the arguments to give.
     * @return the new instance.
     * @throws InvocationTargetException
     *             error.
     * @throws IllegalAccessException
     *             error.
     * @throws InstantiationException
     *             error.
     */
    @SuppressWarnings("rawtypes")
    public static Object getObject(final Constructor constructor,
            final Object[] args) throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        if (constructor == null) {
            throw new IllegalArgumentException("constructor is null");
        }
        return constructor.newInstance(args);
    }

    /**
     * To get a constructor from a class and arguments.
     * 
     * @param cl
     *            the class to use.
     * @param args
     *            the argument to give.
     * @return the constructor instance.
     * @throws Exception
     *             if something is wrong.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Constructor getConstructor(final Class cl, final Object[] args)
            throws Exception {
        if (cl == null) {
            throw new IllegalArgumentException("class is null");
        }
        Constructor constructor = null;
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        try {
            constructor = cl.getConstructor(parameterTypes);
        } catch (Exception e) {
            // try to find the good one
            for (Constructor oneConst : cl.getConstructors()) {
                Class[] params = oneConst.getParameterTypes();
                // if not the same number of parameters so bad.
                if (params.length != parameterTypes.length) {
                    continue;
                }
                boolean ok = true;
                for (int i = 0; i < params.length; i++) {
                    if (params[i].equals(parameterTypes[i])) {
                        continue;
                    }
                    try {
                        if (params[i].isPrimitive()
                                && parameterTypes[i].isPrimitive()) {
                            if (!params[i].equals(parameterTypes[i])) {
                                throw new ClassCastException(
                                        "Primitive types differents");
                            }
                        } else if (params[i].isPrimitive()
                                && !parameterTypes[i].isPrimitive()) {
                            parameterTypes[i]
                                    .asSubclass(getEquivalentOfPrimitive(params[i]));
                        } else {
                            parameterTypes[i].asSubclass(params[i]);
                        }
                    } catch (ClassCastException cce) {
                        ok = false;
                        break;
                    }
                    continue;
                }

                if (ok) {
                    constructor = oneConst;
                    break;
                }
            }
        }
        if (constructor == null) {
            throw new Exception("Not able to find constructor");
        }
        return constructor;
    }

    /**
     * To get a method from a class and arguments.
     * 
     * @param cl
     *            the class to use.
     * @param methodName
     *            the method name.
     * @param args
     *            the argument to give.
     * @return the method instance.
     */
    @SuppressWarnings("rawtypes")
    public static Method getMethod(final Class cl, final String methodName,
            final Object[] args) {
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return getMethod(cl, methodName, parameterTypes);
    }

    /**
     * To get a method from a class and arguments.
     * 
     * @param cl
     *            the class to use.
     * @param methodName
     *            the method name.
     * @param parameterTypes
     *            the types of the arguments to give.
     * @return the method instance.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Method getMethod(final Class cl, final String methodName,
            final Class[] parameterTypes) {
        Method method = null;
        try {
            method = cl.getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            // try to find the good one
            for (Method oneMethodToTest : cl.getMethods()) {
                if (!oneMethodToTest.getName().equals(methodName)) {
                    continue;
                }
                Class[] parameterTypesToTest = oneMethodToTest
                        .getParameterTypes();
                // if not the same number of parameters so bad.
                if (parameterTypesToTest.length != parameterTypes.length) {
                    continue;
                }
                parameterTypesToTest = cleanPrimitive(parameterTypesToTest);
                boolean ok = true;
                // now we we check the parameter types
                for (int i = 0; i < parameterTypesToTest.length; i++) {
                    // same parameter type ok
                    if (parameterTypesToTest[i].equals(parameterTypes[i])) {
                        continue;
                    }
                    try {
                        // or we can cast to the class so ok
                        parameterTypes[i].asSubclass(parameterTypesToTest[i]);
                    } catch (ClassCastException cce) {
                        ok = false;
                        break;
                    }
                    continue;
                }

                if (ok) {
                    method = oneMethodToTest;
                    break;
                }
            }
            // try only by name
            if (method == null) {
                for (Method oneMethodToTest : cl.getMethods()) {
                    if (oneMethodToTest.getName().equals(methodName)) {
                        method = oneMethodToTest;
                        break;
                    }
                }
            }
        }
        return method;
    }

    /**
     * To replace all parameter types of primitive types.
     * 
     * @param from
     *            the initial parameter types.
     * @return the final parameter types.
     */
    @SuppressWarnings("rawtypes")
    private static Class[] cleanPrimitive(final Class[] from) {
        Class[] ret = new Class[from.length];
        for (int i = 0; i < ret.length; i++) {
            if (from[i].isPrimitive()) {
                ret[i] = getEquivalentOfPrimitive(from[i]);
            } else {
                ret[i] = from[i];
            }
        }
        return ret;
    }

    /**
     * To get the equivalent class of a primitive type.
     * 
     * @param primitive
     *            the concerned primitive.
     * @return the equivalent class.
     */
    @SuppressWarnings("rawtypes")
    public static Class getEquivalentOfPrimitive(final Class primitive) {

        if (Boolean.TYPE.equals(primitive)) {
            return Boolean.class;
        } else if (Character.TYPE.equals(primitive)) {
            return Character.class;
        } else if (Byte.TYPE.equals(primitive)) {
            return Byte.class;
        } else if (Short.TYPE.equals(primitive)) {
            return Short.class;
        } else if (Integer.TYPE.equals(primitive)) {
            return Integer.class;
        } else if (Long.TYPE.equals(primitive)) {
            return Long.class;
        } else if (Float.TYPE.equals(primitive)) {
            return Float.class;
        } else if (Double.TYPE.equals(primitive)) {
            return Double.class;
        }
        return null;
    }

    /**
     * To get a class from his name.
     * 
     * @param classname
     *            the name of the class.
     * @param nsList
     *            the namespace list to test.
     * @return the the Class object.
     * @throws Exception
     *             if something is wrong.
     */
    @SuppressWarnings("rawtypes")
    public static Class getClass(final String classname,
            final List<String> nsList) throws Exception {
        Class cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader()
                    .loadClass(classname);
        } catch (ClassNotFoundException e) {
            for (String ns : nsList) {
                try {
                    cl = Thread.currentThread().getContextClassLoader()
                            .loadClass(ns + "." + classname);
                    return cl;
                } catch (ClassNotFoundException cnfe) {
                    cl = null;
                }
            }
        }
        if (cl == null) {
            throw new Exception("Not able to find class");
        }
        return cl;
    }

    /**
     * To get the member of an object from his name.
     * 
     * @param obj
     *            the concerned object.
     * @param memberName
     *            the member name.
     * @return the member.
     * @throws Exception
     *             if somethign is wrong.
     */
    public static Object getMember(final Object obj, final String memberName)
            throws Exception {
        @SuppressWarnings("rawtypes")
        Class clazz = obj.getClass();
        return getMember(obj, clazz, memberName);
    }

    /**
     * To get the member of an object from his name.
     * 
     * @param obj
     *            the concerned object.
     * @param clazz
     *            the concerned class.
     * @param memberName
     *            the member name.
     * @return the member.
     * @throws Exception
     *             if somethign is wrong.
     */
    public static Object getMember(final Object obj,
            @SuppressWarnings("rawtypes") final Class clazz,
            final String memberName) throws Exception {
        Field[] fields = clazz.getFields();
        Field field = null;
        for (Field oneField : fields) {
            if (oneField.getName().toLowerCase()
                    .equals(memberName.toLowerCase())) {
                field = oneField;
                break;
            }
        }
        if (field == null) {
            throw new Exception("Field " + memberName + " not found in "
                    + clazz.getName());
        }
        return field.get(obj);
    }

    /**
     * To set the member of an object from his name.
     * 
     * @param obj
     *            the concerned object.
     * @param clazz
     *            the concerned class.
     * @param memberName
     *            the member name.
     * @param newValue
     *            the new value of the member.
     * @return the new value.
     * @throws Exception
     *             if somethign is wrong.
     */
    public static Object setMember(final Object obj,
            @SuppressWarnings("rawtypes") final Class clazz,
            final String memberName, final Object newValue) throws Exception {
        Field[] fields = clazz.getFields();
        Field field = null;
        for (Field oneField : fields) {
            if (oneField.getName().toLowerCase()
                    .equals(memberName.toLowerCase())) {
                field = oneField;
                break;
            }
        }
        if (field == null) {
            throw new Exception("Field " + memberName + " not found in "
                    + clazz.getName());
        }
        field.set(obj, newValue);
        return newValue;
    }

}
