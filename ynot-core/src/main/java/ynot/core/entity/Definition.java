package ynot.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represent the definition of a word of dictionary.
 * 
 * @author equesada
 */
public class Definition {

    // Member(s)

    /**
     * The name of the resource that we will use to call the methods.
     */
    private String resourceNameToUse;

    /**
     * The name of provider of the resource.
     */
    private String resourceProviderName;

    /**
     * The list of method to call.
     */
    private List<String> methodNamesToCall;

    /**
     * The parameter types of methods (methodName => parameters).
     */
    @SuppressWarnings("rawtypes")
    private final Map<String, Class[]> parameterTypes;

    /**
     * The predefined values (before execution) of methods. (methodName =>
     * (position => predefinedValues))
     */
    private final Map<String, Map<Integer, Object>> predefinedParameters;

    // Constructor(s)

    /**
     * Default constructor.
     */
    @SuppressWarnings("rawtypes")
    public Definition() {
        super();
        this.methodNamesToCall = new ArrayList<String>();
        this.parameterTypes = new HashMap<String, Class[]>();
        this.predefinedParameters = new HashMap<String, Map<Integer, Object>>();
    }

    // Getter(s)/Setter(s)

    /**
     * The resourceNameToUse getter.
     * 
     * @return the resourceNameToUse member
     */
    public final String getResourceNameToUse() {
        return resourceNameToUse;
    }

    /**
     * The resourceNameToUse setter.
     * 
     * @param newResourceName
     *            the new resourceNameToUse
     */
    public final void setResourceNameToUse(final String newResourceName) {
        this.resourceNameToUse = newResourceName;
    }

    /**
     * To get the method names to call.
     * 
     * @return a set of method names.
     */
    public final List<String> getMethodNamesToCall() {
        return this.methodNamesToCall;
    }

    /**
     * To set the method names to call.
     * 
     * @param methodNames
     *            the new method names to call.
     */
    public final void setMethodNamesToCall(final List<String> methodNames) {
        this.methodNamesToCall = methodNames;
    }

    /**
     * The parameters getter.
     * 
     * @param methodName
     *            the name of the method.
     * @return the parameters member.
     */
    @SuppressWarnings("rawtypes")
    public final Class[] getParameterTypes(final String methodName) {
        return parameterTypes.get(methodName);
    }

    /**
     * The parameters setter.
     * 
     * @param methodName
     *            the name of the method.
     * @param newParameters
     *            the new parameters.
     */
    @SuppressWarnings("rawtypes")
    public final void setParameterTypes(final String methodName,
            final Class[] newParameters) {
        this.parameterTypes.put(methodName, newParameters);
    }

    /**
     * The values getter.
     * 
     * @param methodName
     *            the name of the method.
     * @return the values member.
     */
    public final Map<Integer, Object> getPredefinedValues(
            final String methodName) {
        if (predefinedParameters.get(methodName) == null) {
            predefinedParameters
                    .put(methodName, new HashMap<Integer, Object>());
        }
        return predefinedParameters.get(methodName);
    }

    /**
     * The values setter.
     * 
     * @param methodName
     *            the name of the method.
     * @param newValues
     *            the new values.
     */
    public final void setPredefinedValues(final String methodName,
            final Map<Integer, Object> newValues) {
        this.predefinedParameters.put(methodName, newValues);
    }

    /**
     * The resourceProviderName getter.
     * 
     * @return the resourceProviderName member.
     */
    public final String getResourceProviderName() {
        return resourceProviderName;
    }

    /**
     * The resourceProviderName setter.
     * 
     * @param newResourceProviderName
     *            the new resource provider Name.
     */
    public final void setResourceProviderName(
            final String newResourceProviderName) {
        this.resourceProviderName = newResourceProviderName;
    }

    // Other functions

}
