package ynot.vm;

import ynot.core.entity.Shell;

/**
 * Launcher to use to launch ynot scripts.
 * 
 * @author ERIC.QUESADA
 * 
 */
public final class Launcher {

    /**
     * To launch a ynot script.
     * 
     * @param args
     *            need to contains the ynot script fillpath.
     * @throws Exception
     *             if something is wrong.
     */
    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            return;
        }
        String script = args[0];
        VirtualMachine vm = new VirtualMachine();        
        Shell shell =  vm.getShell(script);
        shell.run();
    }

    /**
     * To forbid the instanciation.
     */
    private Launcher() {
    }

}
