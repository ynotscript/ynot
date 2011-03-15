package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.parser.request.tinyparser.CallNameWithParameters;


/**
 * Mini parser for call with parameters.
 * Example: *function("abc")
 * @author equesada
 */
public class CallNameWithParametersTest {
	
	/**
	 * Three value.
	 */
	private static final int THREE = 3;
	
	/**
	 * The mini parser to test.
	 */
	private CallNameWithParameters cnwp;

	/**
	 * To set up the mini parser.
	 * @throws Exception if something's wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		cnwp = new CallNameWithParameters();
	}

	/**
	 * To test the parse method.
	 * @throws UnparsableRequestException if something's wrong.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testParse() throws UnparsableRequestException {
		List<Request> reqs = cnwp
		    .parse(" *test({1,3,\"b\"},2,3) ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "call");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertTrue(reqs.get(0).getGivenParameters().length == 2);
		assertEquals(reqs.get(0).getGivenParameters()[0], "test");
		assertTrue(((List<Object>) reqs.get(0).getGivenParameters()[1])
		    .get(0) instanceof List);
		assertTrue(((List<Object>) reqs.get(0).getGivenParameters()[1])
		    .size() == THREE);
	}
}
