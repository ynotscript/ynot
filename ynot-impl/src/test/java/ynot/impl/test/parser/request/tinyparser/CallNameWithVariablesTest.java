package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.impl.parser.request.tinyparser.CallNameWithVariables;


/**
 * Mini parser for call with returned variables.
 * Example: $a := *function()
 * @author equesada
 */
public class CallNameWithVariablesTest {
	
	/**
	 * The mini parser to test.
	 */
	private CallNameWithVariables cnwv;

	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		cnwv = new CallNameWithVariables();
	}

	/**
	 * To test the parse method.
	 */
	@Test
	public final void testParse() {
		List<Request> reqs = cnwv
		    .parse(" $a, $b := *test() ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "call");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertEquals((reqs.get(0).getGivenParameters()[0]), "test");
		assertTrue(reqs.get(0).getVariableNames().length == 2);
		assertEquals(reqs.get(0).getVariableNames()[0], "a");
		assertEquals(reqs.get(0).getVariableNames()[1], "b");
	}
}
