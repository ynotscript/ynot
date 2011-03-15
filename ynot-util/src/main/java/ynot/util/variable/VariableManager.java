package ynot.util.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import ynot.util.breadcrum.Breadcrum;

public class VariableManager implements Cloneable {

    private static Logger logger = Logger.getLogger(VariableManager.class);

    public enum Scope {
        LOCAL, GLOBAL
    }

    private static final Map<String, Object> globalVariables = new ConcurrentHashMap<String, Object>();
    private Scope variablesScope;
    private Map<String, Map<String, Object>> variables;
    private final Breadcrum breacrum;

    public VariableManager(Breadcrum newBreacrum) {
        variables = new HashMap<String, Map<String, Object>>();
        variablesScope = Scope.GLOBAL;
        breacrum = newBreacrum;
    }

    public final void clean() {
        String namespace = breacrum.toString();
        if (variables.get(namespace) == null) {
            variables.put(namespace, new HashMap<String, Object>());
        }
        variables.get(namespace).clear();
    }

    public final void unset(String varName) {
        String namespace = breacrum.toString();
        if (variables.get(namespace) == null) {
            variables.put(namespace, new HashMap<String, Object>());
        }
        variables.get(namespace).remove(varName);
    }

    public final void set(String varName, Object value) {
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

    private final String findVariableNamespace(final String namespace,
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

    public final Object get(String varName) {
        return get(breacrum.toString(), varName);
    }

    private Object get(String namespace, String varName) {
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

    public final void setScope(Scope newScope) {
        variablesScope = newScope;
    }

    public final Scope getScope() {
        return variablesScope;
    }

    public static Object getGlobal(String key) {
        return globalVariables.get(key);
    }

    public static void setGlobal(String key, Object value) {
        globalVariables.put(key, value);
    }

    public static void unsetGlobal(String key) {
        globalVariables.remove(key);
    }

    public static void clearGlobal() {
        globalVariables.clear();
    }

    @Override
    public final String toString() {
        StringBuilder fullString = new StringBuilder();
        for (String onePackage : variables.keySet()) {
            fullString
                    .append("*******************************" 
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

    public final void fill(VariableManager variableManager) {
        for (Entry<String, Map<String, Object>> entry : variableManager.variables
                .entrySet()) {
            variables.put(entry.getKey(), entry.getValue());
        }
    }

    public final void goIn(String namespace) {
        breacrum.goIn(namespace);
    }

    public final String goOut() {
        return breacrum.goOut();
    }

    public final String getCurrent() {
        return breacrum.toString();
    }

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
