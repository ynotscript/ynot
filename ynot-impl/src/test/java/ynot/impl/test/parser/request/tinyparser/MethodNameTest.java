package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.impl.parser.request.tinyparser.MethodName;

/**
 * Mini parser for simple method. Example: toto()
 * @author equesada.
 */
public class MethodNameTest {

	/**
	 * Mini parser to test.
	 */
	private MethodName simpleMethodMiniParser;

	/**
	 * Set up the mini parser.
	 * 
	 * @throws Exception
	 *             if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		simpleMethodMiniParser = new MethodName();
	}

	/**
	 * Test of the parse method.
	 */
	@Test
	public final void testParse() {
		List<Request> reqs = simpleMethodMiniParser.parse(" call ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "call");

		List<Request> reqs2 = simpleMethodMiniParser.parse(" run ( ) ");
		assertTrue(reqs2.size() == 1);
		assertEquals(reqs2.get(0).getWordToUse(), "run");
		assertFalse("stop".equals(reqs2.get(0).getWordToUse()));

		List<Request> reqs3 = simpleMethodMiniParser.parse(" test\\run ( ) ");
		assertTrue(reqs3.size() == 1);
		assertEquals(reqs3.get(0).getWordToUse(), "run");
		assertEquals(reqs3.get(0).getDefinitionProviderName(), "test");
	}
}
