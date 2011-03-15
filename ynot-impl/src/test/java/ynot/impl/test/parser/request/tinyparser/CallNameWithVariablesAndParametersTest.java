package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.parser.request.tinyparser.CallNameWithVariablesAndParameters;


/**
 * Mini parser for call with parameters and returned variables.
 * Example: $a := *function("abc")
 * @author equesada
 */
public class CallNameWithVariablesAndParametersTest {
	
	/**
	 * The mini parser to test.
	 */
	private CallNameWithVariablesAndParameters cnwvap;

	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		cnwvap = new CallNameWithVariablesAndParameters();
	}

	/**
	 * To test the parse method.
	 * @throws UnparsableRequestException if something's wrong.
	 */
	@Test
	public final void testParse() throws UnparsableRequestException {
		List<Request> reqs = cnwvap
		    .parse(" $d , $e := *test ( \"azerty\" , 2 , {1,2,\"o\"}) ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "call");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertTrue(reqs.get(0).getGivenParameters().length == 2);
		assertTrue(reqs.get(0).getVariableNames().length == 2);
		assertEquals(reqs.get(0).getVariableNames()[0], "d");
	}
}
