package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.impl.parser.request.tinyparser.MethodNameWithVariables;


/**
 * Mini parser for method with returned variables.
 * Example: $a := toto()
 * @author equesada
 */
public class MethodNameWithVariablesTest {

	/**
	 * The mini parser to test.
	 */
	private MethodNameWithVariables methodNameWithVariablesMiniParser;

	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		methodNameWithVariablesMiniParser = new MethodNameWithVariables();
	}

	/**
	 * Test the parse method.
	 */
	@Test
	public final void testParse() {
		List<Request> reqs = methodNameWithVariablesMiniParser
		    .parse(" $a, $b := p1\\call() ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "call");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "p1");
		assertTrue(reqs.get(0).getVariableNames().length == 2);
		assertEquals(reqs.get(0).getVariableNames()[0], "a");
		assertEquals(reqs.get(0).getVariableNames()[1], "b");
	}

}
