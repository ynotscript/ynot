package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.impl.parser.request.tinyparser.CallName;


/**
 * Mini parser for simple call.
 * Example: *toto()
 * @author equesada
 */
public class CallNameTest {

	/**
	 * The mini parser to test.
	 */
	private CallName simpleCallNameMiniParser;

	/**
	 * Set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		simpleCallNameMiniParser = new CallName();
	}

	/**
	 * Test of the parse method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testParse() {
		List<Request> reqs = simpleCallNameMiniParser
		    .parse(" *call ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "call");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs.get(0).getGivenParameters().length, 2);
		assertEquals((reqs.get(0).getGivenParameters()[0]), "call");
		assertTrue(((List<Object>) reqs.get(0).getGivenParameters()[1])
		    .size() == 0);

		List<Request> reqs2 = simpleCallNameMiniParser
		    .parse(" *run ( ) ");
		assertTrue(reqs2.size() == 1);
		assertEquals(reqs2.get(0).getWordToUse(), "call");
		assertEquals(reqs2.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs2.get(0).getGivenParameters().length, 2);
		assertEquals((reqs2.get(0).getGivenParameters()[0]), "run");
		assertTrue(((List<Object>) reqs2.get(0).getGivenParameters()[1])
		    .size() == 0);
	}
}
