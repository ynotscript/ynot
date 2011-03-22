package ynot.vm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.context.ApplicationContext;

import ynot.core.entity.Shell;
import ynot.vm.VirtualMachine.ContextKey;

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
	 * @throws IOException
	 *             if something is wrong.
	 */
	public static void main(final String[] args) throws IOException {
		if (args.length == 0) {
			return;
		}
		try {
			String script = args[0];
			VirtualMachine vm;
			vm = new VirtualMachine();
			ApplicationContext context = vm.getContext(script);
			Shell shell = (Shell) context.getBean(ContextKey.SHELL);
			shell.run();
		} catch (Exception e) {
			e.printStackTrace();
			InputStreamReader inp = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(inp);
			br.readLine();
		}

	}

	/**
	 * To forbid the instanciation.
	 */
	private Launcher() {
	}

}
