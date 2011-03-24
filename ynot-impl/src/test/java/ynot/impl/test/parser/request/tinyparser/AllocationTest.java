package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.parser.request.tinyparser.Allocation;

/**
 * To test the Allocation tinyParser.
 * @author equesada
 */
public class AllocationTest {

	/**
	 * The mini parser to test.
	 */
	private Allocation allocation;

	/**
	 * Set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		allocation = new Allocation();
	}

	/**
	 * Test of the parse method.
	 * @throws UnparsableRequestException 
	 */
	@Test
	public final void testParse() throws UnparsableRequestException {
		List<Request> reqs = allocation.parse("$a := \"hello\"");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "assign");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs.get(0).getGivenParameters().length, 1);
		assertEquals((reqs.get(0).getGivenParameters()[0]), "hello");
		assertEquals((reqs.get(0).getVariableNames()[0]), "a");
		List<Request> reqs2 = allocation.parse("$req := null");
		assertTrue(reqs2.size() == 1);
		assertEquals(reqs2.get(0).getWordToUse(), "assign");
		assertEquals(reqs2.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs2.get(0).getGivenParameters().length, 1);
		assertEquals((reqs2.get(0).getGivenParameters()[0]), null);
		assertEquals((reqs2.get(0).getVariableNames()[0]), "req");
	}
}
