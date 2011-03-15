package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.parser.request.tinyparser.InvokeMemberFunctionWithVariables;

/**
 * Mini parser for function with returned variables. Example: $a :=
 * $var.function()
 * 
 * @author equesada
 */
public class InvokeMemberFunctionWithVariablesTest {

	/**
	 * The mini parser to test.
	 */
	private InvokeMemberFunctionWithVariables imfwv;

	/**
	 * To set up the mini parser.
	 * 
	 * @throws Exception
	 *             if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		imfwv = new InvokeMemberFunctionWithVariables();
	}

	/**
	 * To test the parse method.
	 * 
	 * @throws UnparsableRequestException
	 *             if something is wrong.
	 */
	@Test
	public final void testParse() throws UnparsableRequestException {
		List<Request> reqs = imfwv.parse(" $a, $b := $c.call() ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "invoke");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs.get(0).getGivenParameters()[0], "@$c@");
		assertEquals(reqs.get(0).getGivenParameters()[1], "call");
		assertTrue(reqs.get(0).getVariableNames().length == 2);
		assertEquals(reqs.get(0).getVariableNames()[0], "a");
		assertEquals(reqs.get(0).getVariableNames()[1], "b");
	}
}
