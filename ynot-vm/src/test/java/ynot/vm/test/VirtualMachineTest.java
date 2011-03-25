package ynot.vm.test;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import ynot.core.entity.Shell;
import ynot.core.parser.request.RequestParser;
import ynot.impl.provider.request.InputStreamRequestProvider;
import ynot.vm.VirtualMachine;
import ynot.vm.VirtualMachine.ConfigKey;
import ynot.vm.VirtualMachine.ContextKey;

/**
 * Class to test the VirtualMachine.
 * 
 * @author equesada
 */
// @Ignore
public class VirtualMachineTest {

	/**
	 * A simple test.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testSimple() {
		try {
			VirtualMachine vm = new VirtualMachine();
			String libs = (String) vm.getConfig().get(ConfigKey.LIBRARIES);
			libs += ",./src/test/resources/dictionaries/demo/lib";
			vm.getConfig().put(ConfigKey.LIBRARIES, libs);
			InputStreamRequestProvider reqProvider = new InputStreamRequestProvider(
					"test");
			String reqs = "import(\"ynot.impl.provider.request\")\n"
					+ "import(\"ynot.vm\")\n"
					+ "$vm := new(\"VirtualMachine\")\n"
					+ "$provider := new(\"InputStreamRequestProvider\", {\"console\"})\n"
					+ "$req := null\n" + "while([\"bye\" != $req])\n"
					+ "write ynot>\n"
					+ "$req := readLine\n"
					+ "$reqBytes := $req.getBytes(\"UTF-8\")\n"
					+ "$bais := new(\"ByteArrayInputStream\",{$reqBytes})\n"
					+ "$provider.setInputStream($bais)\n"
					+ "$context := $vm.getContext($provider)\n"
					+ "$reqParser := $context.getBean(\"requestParser\")\n"
					+ "$provider.setParser($reqParser)\n"
					+ "$shell := $context.getBean(\"shell\")\n"
					+ "$shell.run()\n" + "end";
			reqProvider.setInputStream(new ByteArrayInputStream(reqs
					.getBytes("UTF-8")));
			ApplicationContext context = vm.getContext(reqProvider);
			RequestParser<String> reqParser = (RequestParser<String>) context
					.getBean(ContextKey.REQUEST_PARSER);
			reqProvider.setParser(reqParser);
			Shell shell = (Shell) context.getBean(ContextKey.SHELL);
			shell.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
