package ynot.util.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import ynot.util.breadcrum.Breadcrum;

/**
 * To manage variables in the scopes.
 * 
 * @author ERIC.QUESADA
 * 
 */
public class VariableManager implements Cloneable {

    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(VariableManager.class);

    /**
     * The existing scopes.
     * 
     * @author ERIC.QUESADA
     * 
     */
    public enum Scope {

        /**
         * The local scope.
         */
        LOCAL,

        /**
         * The global scope.
         */
        GLOBAL
    }

    /**
     * The global variables.
     */
    private static final Map<String, Object> GLOBAL_VARIABLES;

    static {
        GLOBAL_VARIABLES = new ConcurrentHashMap<String, Object>();
    }

    /**
     * The current scope.
     */
    private Scope variablesScope;

    /**
     * The variables.
     */
    private Map<String, Map<String, Object>> variables;

    /**
     * The current breadcrum.
     */
    private final Breadcrum breacrum;

    /**
     * To build a variableManager.
     * 
     * @param newBreacrum
     *            the breadcrum to use.
     */
    public VariableManager(final Breadcrum newBreacrum) {
        variables = new HashMap<String, Map<String, Object>>();
        variablesScope = Scope.GLOBAL;
        breacrum = newBreacrum;
    }

    /**
     * To clean all the variable manager.
     */
    public final void clean() {
        String namespace = breacrum.toString();
        if (variables.get(namespace) == null) {
            variables.put(namespace, new HashMap<String, Object>());
        }
        variables.get(namespace).clear();
    }

    /**
     * To unset a variable.
     * 
     * @param varName
     *            the variable name.
     */
    public final void unset(final String varName) {
        String namespace = breacrum.toString();
        if (variables.get(namespace) == null) {
            variables.put(namespace, new HashMap<String, Object>());
        }
        variables.get(namespace).remove(varName);
    }

    /**
     * To set a variable.
     * 
     * @param varName
     *            the variable name.
     * @param value
     *            the variable value.
     */
    public final void set(final String varName, final Object value) {
        String namespaceToUse = null;
        String namespace = breacrum.toString();
        if (variablesScope == Scope.GLOBAL) {
            try {
                namespaceToUse = findVariableNamespace(namespace, varName);
            } catch (CloneNotSupportedException e) {
                logger.error(e.getMessage());
            }
        }
        if (namespaceToUse == null) {
            namespaceToUse = namespace;
        }
        if (variables.get(namespaceToUse) == null) {
            variables.put(namespaceToUse, new HashMap<String, Object>());
        }
        variables.get(namespaceToUse).put(varName, value);
    }

    /**
     * To find the namespace of a variable.
     * 
     * @param namespace
     *            the concerned namespace.
     * @param varName
     *            the concerned variable.
     * @return the namespace or null if it's not found.
     * @throws CloneNotSupportedException
     *             if something is wrong.
     */
    private String findVariableNamespace(final String namespace,
            final String varName) throws CloneNotSupportedException {
        if (namespace != null && namespace.length() > 0) {
            Scope currentScope = variablesScope;
            variablesScope = Scope.LOCAL;
            Object value = get(namespace, varName);
            variablesScope = currentScope;
            if (value != null) {
                return namespace;
            }
            Breadcrum newBreacrum = new Breadcrum(namespace);
            newBreacrum.goOut();
            return findVariableNamespace(newBreacrum.toString(), varName);
        }
        return null;
    }

    /**
     * To get a variable value from the current namespace.
     * 
     * @param varName
     *            the variable name.
     * @return the variable value.
     */
    public final Object get(final String varName) {
        return get(breacrum.toString(), varName);
    }

    /**
     * To get a variable value from a namespace.
     * 
     * @param namespace
     *            the concerned namespace.
     * @param varName
     *            the variable name.
     * @return tha variable value.
     */
    private Object get(final String namespace, final String varName) {
        String namespaceToUse = null;
        if (variablesScope == Scope.GLOBAL) {
            try {
                namespaceToUse = findVariableNamespace(namespace, varName);
            } catch (CloneNotSupportedException e) {
                logger.error(e.getMessage());
            }
        }
        if (namespaceToUse == null) {
            namespaceToUse = namespace;
        }
        if (variables.get(namespaceToUse) == null) {
            variables.put(namespaceToUse, new HashMap<String, Object>());
        }
        Object ret = variables.get(namespaceToUse).get(varName);
        return ret;
    }

    /**
     * To set the current scope.
     * 
     * @param newScope
     *            the new scope.
     */
    public final void setScope(final Scope newScope) {
        variablesScope = newScope;
    }

    /**
     * To get the current scope.
     * 
     * @return the current scope.
     */
    public final Scope getScope() {
        return variablesScope;
    }

    /**
     * To get a global variable.
     * 
     * @param key
     *            the variable name.
     * @return the variable value.
     */
    public static Object getGlobal(final String key) {
        return GLOBAL_VARIABLES.get(key);
    }

    /**
     * To set a global variable.
     * 
     * @param key
     *            the variable name.
     * @param value
     *            the variable value.
     */
    public static void setGlobal(final String key, final Object value) {
        GLOBAL_VARIABLES.put(key, value);
    }

    /**
     * To remove a global value.
     * 
     * @param key
     *            the variable name.
     */
    public static void unsetGlobal(final String key) {
        GLOBAL_VARIABLES.remove(key);
    }

    /**
     * To clear all the global variables.
     */
    public static void clearGlobal() {
        GLOBAL_VARIABLES.clear();
    }

    @Override
    public final String toString() {
        StringBuilder fullString = new StringBuilder();
        for (String onePackage : variables.keySet()) {
            fullString.append("*******************************"
                    + "************************************\n");
            fullString.append("/");
            fullString.append(onePackage.replace("_", "/"));
            fullString.append("\n");
            Map<String, Object> vars = variables.get(onePackage);
            for (Entry<String, Object> entry : vars.entrySet()) {
                fullString.append("/");
                fullString.append(onePackage.replace("_", "/"));
                fullString.append("/");
                fullString.append(entry.getKey());
                fullString.append("=");
                fullString.append(entry.getValue());
                fullString.append("\n");
            }
        }
        return fullString.toString();
    }

    /**
     * To fill the variable with an existing variableManager.
     * 
     * @param variableManager
     *            the concerned variableManager.
     */
    public final void fill(final VariableManager variableManager) {
        for (Entry<String, Map<String, Object>> entry : variableManager.variables
                .entrySet()) {
            variables.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * To go into a sub namespace.
     * 
     * @param namespace
     *            the concerned namespace.
     */
    public final void goIn(final String namespace) {
        breacrum.goIn(namespace);
    }

    /**
     * To go out of a namespace.
     * 
     * @return the previous namespace.
     */
    public final String goOut() {
        return breacrum.goOut();
    }

    /**
     * To get the current namespace.
     * 
     * @return the current namespace.
     */
    public final String getCurrent() {
        return breacrum.toString();
    }

    /**
     * To get the previous namespace.
     * 
     * @return the previous namespace.
     */
    public final String previous() {
        return breacrum.previous();
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {
        Breadcrum breadcrumCopy = new Breadcrum(getCurrent());
        VariableManager newVariableManager = new VariableManager(breadcrumCopy);
        newVariableManager.fill(this);
        return newVariableManager;
    }

}
