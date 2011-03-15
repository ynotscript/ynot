package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.parser.request.tinyparser.InvokeMemberFunctionWithVariablesAndParameters;


/**
 * Mini parser for function with parameters and returned variables.
 * Example: $a := $var.function("abc")
 * @author equesada
 */
public class InvokeMemberFunctionWithVariablesAndParametersTest {
	
	/**
	 * Three value.
	 */
	private static final int THREE = 3;
	/**
	 * The mini parser to test.
	 */
	private InvokeMemberFunctionWithVariablesAndParameters imfwvap;

	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		imfwvap = new InvokeMemberFunctionWithVariablesAndParameters();
	}

	/**
	 * To test the parse method.
	 * @throws UnparsableRequestException if something is wrong.
	 */
	@Test
	public final void testParse() throws UnparsableRequestException {
		List<Request> reqs = imfwvap
		    .parse(" $d , $e := $c.test ( \"azerty\" , 2 , {1,2,\"o\"}) ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "invoke");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertTrue(reqs.get(0).getGivenParameters().length == THREE);
		assertTrue(reqs.get(0).getVariableNames().length == 2);
		assertEquals(reqs.get(0).getVariableNames()[0], "d");
		assertEquals(reqs.get(0).getGivenParameters()[0], "@$c@");
		assertEquals(reqs.get(0).getGivenParameters()[1], "test");
	}
}
