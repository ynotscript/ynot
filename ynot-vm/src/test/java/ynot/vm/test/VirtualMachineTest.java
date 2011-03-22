package ynot.vm.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import ynot.impl.provider.request.InputStreamRequestProvider;
import ynot.vm.VirtualMachine;

/**
 * Class to test the VirtualMachine.
 * 
 * @author equesada
 */
@Ignore
public class VirtualMachineTest {

    /**
     * A simple test.
     * 
     * @throws IOException if something is wrong
     */
    @Test
    public final void testSimple() throws IOException {
        // TODO: fix
        VirtualMachine vm = new VirtualMachine();
        InputStreamRequestProvider reqProvider = new InputStreamRequestProvider("test");
        String reqs = "echo(\"lo\")\nreadLine()";
        reqProvider.setInputStream(new ByteArrayInputStream(reqs.getBytes("UTF-8")));
        vm.getShell(reqProvider);
    }

}
