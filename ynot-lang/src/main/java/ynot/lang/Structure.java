package ynot.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ynot.core.entity.Shell;
import ynot.util.breadcrum.Breadcrum;
import ynot.util.variable.VariableManager.Scope;

/**
 * The system basics commands.
 * 
 * @author equesada
 */
public class Structure {

    // Member(s)

    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(Structure.class);

    // Constructor(s)

    /**
     * The default constructor (java bean specification).
     */
    public Structure() {
    }

    // Getter(s)/Setter(s)

    /**
     * To get an object able to log everywhere.
     * 
     * @return a logger object
     */
    public final Logger getLogger() {
        return logger;
    }

    // Other functions

    /**
     * The if logic.
     * 
     * @param shell
     *            the concerned shell.
     * @param condition
     *            if true we will enter in the next block.
     */
    public final void ifMethod(final Shell shell, final Boolean condition) {
        Scope currentScope = shell.getVariablesScope();
        shell.setVariablesScope(Scope.LOCAL);
        if (condition) {
            String block = shell.goOut();
            shell.setVariable("enter_ifMethod", "true");
            shell.goIn(block);
        } else {
            shell.lock();
        }
        shell.setVariablesScope(currentScope);
    }

    /**
     * The elseif logic.
     * 
     * @param shell
     *            the concerned shell.
     * @param condition
     *            if true we will enter in the next block.
     */
    public final void elseifMethod(final Shell shell, final Boolean condition) {
        Scope currentScope = shell.getVariablesScope();
        shell.setVariablesScope(Scope.LOCAL);
        if (condition && shell.getVariable("enter_ifMethod") == null) {
        	String block = shell.goOut();
            shell.setVariable("enter_ifMethod", "true");
            shell.goIn(block);
        } else {
            shell.lock();
        }
        shell.setVariablesScope(currentScope);
    }

    /**
     * The else logic.
     * 
     * @param shell
     *            the concerned shell.
     */
    public final void elseMethod(final Shell shell) {
        Scope currentScope = shell.getVariablesScope();
        shell.setVariablesScope(Scope.LOCAL);
        if (shell.getVariable("enter_ifMethod") == null) {
        	String block = shell.goOut();
            shell.setVariable("enter_ifMethod", "true");
            shell.goIn(block);
        } else {
            shell.lock();
        }
        shell.setVariablesScope(currentScope);
    }

    /**
     * The endif logic.
     * 
     * @param shell
     *            the concerned shell.
     */
    public final void endifMethod(final Shell shell) {
        shell.unsetVariable("enter_ifMethod");
    }

    /**
     * The while logic.
     * 
     * @param shell
     *            the concerned shell.
     * @param condition
     *            if true we will repeat in the next block.
     */
    public final void whileMethod(final Shell shell, final Boolean condition) {
        String key = "label:" + shell.getCurrent();
        if (condition) {
            shell.setVariable(key, shell.getStep());
        } else {
            shell.unsetVariable(key);
            shell.lock();
        }
    }

    /**
     * The endwhile logic.
     * 
     * @param shell
     *            the concerned shell.
     */
    public final void endwhileMethod(final Shell shell) {
        shell.goIn("whileMethod");
        Integer line = (Integer) shell.getVariable("label:"
                + shell.getCurrent());
        if (line != null) {
            shell.setNextStep(line);
        }
        shell.goOut();
    }

    /**
     * The while logic.
     * 
     * @param shell
     *            the concerned shell.
     * @param list
     *            the list to iterate.
     * @param varName
     *            the name of the variable use for the next element.
     */
    @SuppressWarnings("rawtypes")
    public final void forMethod(final Shell shell, final List list,
            final String varName) {
        String key = shell.getCurrent();
        String keyIndice = shell.getCurrent() + "_indice";
        int indice = 0;
        if (shell.getVariable(keyIndice) != null) {
            indice = (Integer) shell.getVariable(keyIndice);
        }
        if (list != null && indice < list.size()) {
            shell.setVariable(varName, list.get(indice));
            shell.setVariable("label:" + key, shell.getStep());
            shell.setVariable(keyIndice, indice + 1);
        } else {
            shell.lock();
            shell.unsetVariable("label:" + key);
            shell.unsetVariable(varName);
            shell.unsetVariable(keyIndice);
        }
    }

    /**
     * The endwhile logic.
     * 
     * @param shell
     *            the concerned shell.
     */
    public final void endforMethod(final Shell shell) {
        shell.goIn("forMethod");
        Integer line = (Integer) shell.getVariable("label:"
                + shell.getCurrent());
        if (line != null) {
            shell.setNextStep(line);
        }
        shell.goOut();
    }

    /**
     * The break logic.
     * 
     * @param shell
     *            the concerned shell.
     */
    public final void breakMethod(final Shell shell) {
        int deepToRemove = 0;
        Stack<String> toAddAfter = new Stack<String>();
        Breadcrum breadcrum = new Breadcrum(shell.getCurrent());
        for (String oneElem : breadcrum.reverse()) {
            if ("forMethod".equals(oneElem) || "whileMethod".equals(oneElem)) {
                while (deepToRemove > 0) {
                    toAddAfter.push(shell.goOut());
                    --deepToRemove;
                }
                shell.unsetVariable("label:" + shell.getCurrent());
                break;
            }
            deepToRemove++;
        }
        shell.lock();
        while (!toAddAfter.isEmpty()) {
            shell.goIn(toAddAfter.pop());
        }
    }

    /**
     * The continue logic.
     * 
     * @param shell
     *            the concerned shell.
     */
    public final void continueMethod(final Shell shell) {
        int deepToRemove = 0;
        Stack<String> toAddAfter = new Stack<String>();
        Breadcrum breadcrum = new Breadcrum(shell.getCurrent());
        for (String oneElem : breadcrum.reverse()) {
            if ("forMethod".equals(oneElem) || "whileMethod".equals(oneElem)) {
                while (deepToRemove > 0) {
                    toAddAfter.push(shell.goOut());
                    --deepToRemove;
                }
                break;
            }
            deepToRemove++;
        }
        shell.lock();
        while (!toAddAfter.isEmpty()) {
            shell.goIn(toAddAfter.pop());
        }
    }

    /**
     * The end of the function/if/while/for.
     * 
     * @param shell
     *            the concerned shell.
     */
    public final void endMethod(final Shell shell) {

        String previous = shell.previous();
        if ("forMethod".equals(previous)) {
            endforMethod(shell);
        } else if ("whileMethod".equals(previous)) {
            endwhileMethod(shell);
        } else if ("functionMethod".equals(previous)) {
            endfunctionMethod(shell);
        } else if ("ifMethod".equals(previous) || "elseMethod".equals(previous)
                || "elseifMethod".equals(previous)) {
            endifMethod(shell);
        }

    }

    /**
     * The function declaration logical.
     * 
     * @param shell
     *            the concerned shell.
     * @param methodName
     *            the method name.
     * @param argumentNames
     *            the name of the arguments to give to the method.
     */
    public final void functionMethod(final Shell shell,
            final String methodName, final List<Object> argumentNames) {

        // go out of the namespace
        String previous = shell.goOut();

        // set the label for the function
        shell.setVariable(
                "label:function_" + methodName + "_" + argumentNames.size(),
                shell.getStep());

        // set the names of the arguments for the function
        shell.setVariable("function_" + methodName + "_" + argumentNames.size()
                + "_args", argumentNames);

        // re-enter in namespace
        shell.goIn(previous);

        // lock to not execute until the end of the function
        shell.lock();

    }

    /**
     * To call a function.
     * 
     * @param shell
     *            the concerned shell.
     * @param methodName
     *            the method name.
     * @param arguments
     *            the arguments to give to the method.
     * @return the returned object else null;
     * @throws Exception
     *             if the function doesn't exists.
     */
    @SuppressWarnings("unchecked")
    public final Object callMethod(final Shell shell, final String methodName,
            final List<Object> arguments) throws Exception {

        if (null == shell.getVariable("comeFromEnd")) {

            // get the label of the asked function
            Integer goToStep = (Integer) shell.getVariable("label:function_"
                    + methodName + "_" + arguments.size());

            // if function not exist so throw error
            if (goToStep == null) {
                throw new Exception("function \"" + methodName + "\" with "
                        + arguments.size() + " parameters doesn't exist.");
            }

            // update the current list of function with current function name
            List<String> functionMethodList = (List<String>) shell
                    .getVariable("functionMethodList");
            if (functionMethodList == null) {
                functionMethodList = new ArrayList<String>();
            }
            functionMethodList.add(methodName + "_" + arguments.size());
            shell.setVariable("functionMethodList", functionMethodList);

            // create the return object but to null
            List<Object> returnObjectList = (List<Object>) shell
                    .getVariable("returnObjectList");
            if (returnObjectList == null) {
                returnObjectList = new ArrayList<Object>();
            }
            returnObjectList.add("##NULL##");
            shell.setVariable("returnObjectList", returnObjectList);

            // set marker to be able to come back on current position (step &
            // subStep)
            List<Integer> returnStepList = (List<Integer>) shell
                    .getVariable("function_" + methodName + "_"
                            + arguments.size() + "_returnStep");
            if (returnStepList == null) {
                returnStepList = new ArrayList<Integer>();
            }
            returnStepList.add(shell.getStep());
            shell.setVariable("function_" + methodName + "_" + arguments.size()
                    + "_returnStep", returnStepList);

            List<Integer> returnSubStepList = (List<Integer>) shell
                    .getVariable("function_" + methodName + "_"
                            + arguments.size() + "_returnSubStep");
            if (returnSubStepList == null) {
                returnSubStepList = new ArrayList<Integer>();
            }
            returnSubStepList.add(shell.getSubStep());
            shell.setVariable("function_" + methodName + "_" + arguments.size()
                    + "_returnSubStep", returnSubStepList);

            // enter in namespace and set shareNameSpace to false.
            shell.goIn("functionMethod");

            // read variable names
            List<Object> varNames = (List<Object>) shell
                    .getVariable("function_" + methodName + "_"
                            + arguments.size() + "_args");

            // update arguments with value
            Scope currentScope = shell.getVariablesScope();
            shell.setVariablesScope(Scope.LOCAL);
            int i = 0;
            for (Object varName : varNames) {
                shell.setVariable(varName.toString(), arguments.get(i));
                i++;
            }
            shell.setVariablesScope(currentScope);

            // go to the function
            shell.setNextStep(goToStep + 1);
            return null;

        } else {
            // remove the flag that identified it comes from the end function
            shell.unsetVariable("comeFromEnd");

            // get the return value
            List<Object> returnList = (List<Object>) shell
                    .getVariable("returnObjectList");
            Object ret = returnList.get(returnList.size() - 1);
            if ("##NULL##".equals(ret)) {
                ret = null;
            }

            // remove the current function of the list
            List<String> functionMethodList = (List<String>) shell
                    .getVariable("functionMethodList");
            functionMethodList.remove(functionMethodList.size() - 1);
            if (functionMethodList.size() == 0) {
                shell.unsetVariable("functionMethodList");
            } else {
                shell.setVariable("functionMethodList", functionMethodList);
            }

            // remove this return value from the list
            returnList.remove(returnList.size() - 1);
            shell.setVariable("returnObjectList", returnList);

            // remove the return step & substep from the lists
            List<Integer> returnStepList = (List<Integer>) shell
                    .getVariable("function_" + methodName + "_"
                            + arguments.size() + "_returnStep");
            List<Integer> returnSubStepList = (List<Integer>) shell
                    .getVariable("function_" + methodName + "_"
                            + arguments.size() + "_returnSubStep");
            returnStepList.remove(returnStepList.size() - 1);
            returnSubStepList.remove(returnSubStepList.size() - 1);
            shell.setVariable("function_" + methodName + "_" + arguments.size()
                    + "_returnStep", returnStepList);
            shell.setVariable("function_" + methodName + "_" + arguments.size()
                    + "_returnSubStep", returnSubStepList);

            return ret;
        }
    }

    /**
     * The return of the function.
     * 
     * @param shell
     *            the concerned shell.
     * @param object
     *            the object to return.
     */
    @SuppressWarnings("unchecked")
    public final void returnMethod(final Shell shell, final Object object) {

        // set the return value
        List<Object> returnList = (List<Object>) shell
                .getVariable("returnObjectList");
        returnList.set(returnList.size() - 1, object);
        shell.setVariable("returnObjectList", returnList);

        // lock until the end of the function
        String ns = shell.getCurrent();
        shell.lock(ns.substring(0, ns.lastIndexOf("functionMethod")
                + "functionMethod".length()));
    }

    /**
     * The end of the function.
     * 
     * @param shell
     *            the concerned shell.
     */
    @SuppressWarnings("unchecked")
    public final void endfunctionMethod(final Shell shell) {

        List<String> functionMethodList = (List<String>) shell
                .getVariable("functionMethodList");

        // if it was after a call
        if (functionMethodList != null && !functionMethodList.isEmpty()) {

            // get the current method with number of parameters
            Object methodName = functionMethodList.get(functionMethodList
                    .size() - 1);

            // get the step & substep to return
            List<Integer> returnStepList = (List<Integer>) shell
                    .getVariable("function_" + methodName + "_returnStep");
            Integer step = returnStepList.get(returnStepList.size() - 1);

            List<Integer> returnSubStepList = (List<Integer>) shell
                    .getVariable("function_" + methodName + "_returnSubStep");
            Integer subStep = returnSubStepList
                    .get(returnSubStepList.size() - 1);

            // set a flag to say it comes from end function
            shell.setVariable("comeFromEnd", true);

            // modify the current step & substep
            shell.setNextStep(step, subStep);
        }
    }

}
