package ynot.core.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represent a command. It will be invoked by the shell.
 * 
 * @author equesada
 */
public class Command implements Cloneable {

    // Members(s)

    /**
     * To indicate that the command has to be executed.
     */
    private boolean isForced;

    /**
     * The resource that will be used to call the method.
     */
    private Object resourceToUse;

    /**
     * The method that will be invoked.
     */
    private Method methodToCall;

    /**
     * The used arguments when the methods is called.
     */
    private Object[] argumentsToGive;

    /**
     * The variables to fill.
     */
    private final List<String> attachedVariables;

    // Constructor(s)

    /**
     * Default constructor.
     */
    public Command() {
        attachedVariables = new ArrayList<String>();
    }

    // Getter(s)/Setter(s)

    /**
     * To get the resource to use when invoking.
     * 
     * @return the "resourceToUse" member
     */
    public final Object getResourceToUse() {
        return resourceToUse;
    }

    /**
     * To set the resource to use when invoking.
     * 
     * @param newResource
     *            The "resourceToUse" member
     */
    public final void setResourceToUse(final Object newResource) {
        this.resourceToUse = newResource;
    }

    /**
     * Getter of the "methodToCall" member.
     * 
     * @return the "methodToCall" member
     */
    public final Method getMethodToCall() {
        return methodToCall;
    }

    /**
     * Setter of the "methodToCall" member.
     * 
     * @param newMethod
     *            The "methodToCall" member
     */
    public final void setMethodToCall(final Method newMethod) {
        this.methodToCall = newMethod;
    }

    /**
     * Getter of the "argumentsToGive" member.
     * 
     * @return the "argumentsToGive" member
     */
    public final Object[] getArgumentsToGive() {
        return argumentsToGive;
    }

    /**
     * Setter of the "argumentsToGive" member.
     * 
     * @param newArgs
     *            The "argumentsToGive" member
     */
    public final void setArgumentsToGive(final Object[] newArgs) {
        this.argumentsToGive = Arrays.copyOf(newArgs, newArgs.length);
    }

    /**
     * Setter of the "isForced" member.
     * 
     * @param newIsForced
     *            the new forced status.
     */
    public final void setForced(final boolean newIsForced) {
        this.isForced = newIsForced;
    }

    /**
     * To know if the command is forced or not.
     * 
     * @return the isForced status.
     */
    public final boolean isForced() {
        return isForced;
    }

    // Other functions

    /**
     * The way to display a command.
     * 
     * @return the string representing the command.
     */
    @Override
    public final String toString() {
        String ret = "";
        ret += "o Resource : " + resourceToUse + "\n";
        ret += "o Method : " + methodToCall + "\n";
        if (argumentsToGive == null) {
            ret += "o Arguments : null\n";
        } else {
            ret += "o Arguments :\n";
            StringBuffer buf = new StringBuffer();
            for (Object oneArg : argumentsToGive) {
                buf.append("----" + oneArg + "\n");
            }
            ret += buf.toString();
        }
        return ret;
    }

    /**
     * To be able to invoke the command process.
     * 
     * @return the result of the invocation.
     * @throws InvocationTargetException
     *             if the target is bad.
     * @throws IllegalAccessException
     *             if the method is not visible.
     */
    @SuppressWarnings("rawtypes")
    public final Object invoke() throws IllegalAccessException,
            InvocationTargetException {
        Object res = null;
        try {
            // default call
            res = methodToCall.invoke(resourceToUse, argumentsToGive);
        } catch (IllegalArgumentException e) {
            // try to cast parameters to call the method
            Class[] types = methodToCall.getParameterTypes();
            Object[] newArgs = new Object[types.length];
            int j = 0;
            int k = 0;
            Object[] objs = null;
            for (int i = 0; i < argumentsToGive.length; i++) {
                if (types[j].equals(argumentsToGive[i].getClass())) {
                    newArgs[j] = argumentsToGive[i];
                    j++;
                } else {
                    if (objs == null) {
                        objs = new Object[argumentsToGive.length - i];
                    }
                    objs[k++] = argumentsToGive[i];
                    newArgs[j++] = objs;
                }
            }
            res = methodToCall.invoke(resourceToUse, newArgs);
        }
        return res;
    }

    /**
     * @return the attachedVariables
     */
    public final List<String> getAttachedVariables() {
        return attachedVariables;
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
