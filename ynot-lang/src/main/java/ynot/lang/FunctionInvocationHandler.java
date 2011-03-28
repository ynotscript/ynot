package ynot.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import ynot.core.entity.Shell;
import ynot.util.breadcrum.Breadcrum;

/**
 * Object to use when implementing interfaces to call a ynot function.
 * @author equesada
 */
public class FunctionInvocationHandler implements InvocationHandler, Cloneable {

    /**
     * The logger.
     */
    private static Logger logger = Logger
            .getLogger(FunctionInvocationHandler.class);

    /**
     * The current shell.
     */
    private Shell realShell;
    
    /**
     * The function to call.
     */
    private String functionName;
    
    /**
     * The current structure.
     */
    private Structure structure;
    
    /**
     * The arguments to give.
     */
    private List<Object> arguments;

    /**
     * Constructor.
     * @param newShell the current shell.
     * @param newStructure the current structure.
     * @param newFunctionName the function to call.
     */
    public FunctionInvocationHandler(
            final Shell newShell, 
            final Structure newStructure,
            final String newFunctionName) {
        realShell = newShell;
        functionName = newFunctionName;
        structure = newStructure;
    }

    @Override
    public final Object invoke(
            final Object proxy, 
            final Method method, 
            final Object[] newArguments)
            throws Throwable {
        try {
            List<Object> args = new ArrayList<Object>();
            args.add(proxy);
            args.add(method);
            args.add(Arrays.asList(newArguments));
            clone(args).run();
        } catch (CloneNotSupportedException e) {
            logger.error("CloneNotSupportedException", e);
        }
        return null;
    }

    /**
     * To get a cloned version with these arguments.
     * @param args the concerned arguments.
     * @return the cloned version.
     * @throws CloneNotSupportedException if cloned not supported.
     */
    private FunctionInvocationHandler clone(final List<Object> args)
            throws CloneNotSupportedException {
        FunctionInvocationHandler cloned = clone();
        cloned.arguments = args;
        return cloned;
    }

    /**
     * It will call the concerned function.
     */
    public final void run() {
        try {
            // You need to clone the shell to have 2 different
            // runCurrentStep call in same time
            Shell shell = realShell.clone();
            structure.callMethod(shell, functionName, arguments);
            Breadcrum enteringBreadcrum = new Breadcrum(shell.getCurrent());
            shell.nextStep();
            while (shell.hasStep()) {
                Breadcrum currentBreadcrum = new Breadcrum(shell.getCurrent());
                if (currentBreadcrum.compareTo(enteringBreadcrum) == -1) {
                    break;
                }
                shell.runStep();
                shell.nextStep();
            }
            structure.callMethod(shell, functionName, arguments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The clone method.
     * @return the cloned object.
     * @throws CloneNotSupportedException if clone is not supported.
     */
    public final FunctionInvocationHandler clone()
            throws CloneNotSupportedException {
        return (FunctionInvocationHandler) super.clone();
    }

}
