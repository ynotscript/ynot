package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.parser.request.tinyparser.MethodNameWithParameters;


/**
 * Mini parser for method with parameters.
 * Example: toto("abc")
 * @author equesada
 */
public class MethodNameWithParametersTest {
	
	/**
	 * three value.
	 */
	private static final int THREE = 3;
	/**
	 * Mini parser to test.
	 */
	private MethodNameWithParameters methodNameWithParametersMiniParser;

	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		methodNameWithParametersMiniParser = new MethodNameWithParameters();
	}

	/**
	 * To test the parse method.
	 * @throws UnparsableRequestException if something is wrong.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public final void testParse() throws UnparsableRequestException {
		List<Request> reqs = methodNameWithParametersMiniParser
		    .parse(" p1\\test({1,3,\"b\"},2) ");
		assertTrue(reqs.size() == 1);
		assertEquals(reqs.get(0).getWordToUse(), "test");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "p1");
		assertTrue(reqs.get(0).getGivenParameters().length == 2);
		assertTrue(reqs.get(0).getGivenParameters()[0] instanceof List);
		assertEquals(reqs.get(0).getGivenParameters()[1], 2);

		List<Request> reqs2 = methodNameWithParametersMiniParser
		    .parse(" call( \"azerty\" , 2 , {1,2,\"o\"}) ");
		assertTrue(reqs2.size() == 1);
		assertEquals(reqs2.get(0).getWordToUse(), "call");
		assertTrue(reqs2.get(0).getGivenParameters().length == THREE);
		assertEquals(reqs2.get(0).getGivenParameters()[0], "azerty");
		assertEquals(reqs2.get(0).getGivenParameters()[1], 2);
		assertEquals(((List) reqs2.get(0).getGivenParameters()[2])
		    .size(), THREE);
		assertEquals(((List) reqs2.get(0).getGivenParameters()[2])
		    .get(2), "o");
	}
}
