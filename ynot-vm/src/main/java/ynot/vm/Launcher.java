package ynot.vm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ynot.lang.ProjectManager;
import ynot.lang.VirtualMachine;
import ynot.util.io.FileManager;

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
	 *            need to contains the ynot script fillpath else it will launch
	 *            the console.
	 */
	public static void main(final String[] args) {
		try {
			String script = getConsoleScript();
			if (args.length > 0) {
				script = args[0];
			}
			ProjectManager.runFromLauncher(script);
		} catch (Exception e) {
			e.printStackTrace();
			InputStreamReader inp = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(inp);
			try {
				br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * To get the ynot script of the console.
	 * 
	 * @return the ynot script of the console.
	 */
	private static String getConsoleScript() {
		String sep = FileManager.FILE_SEPARATOR;
		String script = VirtualMachine.getBasePath() + sep + "tools" + sep
				+ "console.ynot";
		return script;
	}

	/**
	 * To forbid the instanciation.
	 */
	private Launcher() {
	}

}
