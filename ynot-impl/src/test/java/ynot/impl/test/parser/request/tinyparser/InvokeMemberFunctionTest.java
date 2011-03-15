package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.impl.parser.request.tinyparser.InvokeMemberFunction;


/**
 * Mini parser for function.
 * Example: $var.function()
 * @author equesada
 */
public class InvokeMemberFunctionTest {

	/**
	 * The mini parser to test.
	 */
	private InvokeMemberFunction imf;
	
	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {

		imf = new InvokeMemberFunction();
	}
	
	/**
	 * To test the parse method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testParse() {
		List<Request> reqs = imf.parse(" $var.method() ");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs.get(0).getWordToUse(), "invoke");
		assertEquals(reqs.get(0).getGivenParameters()[0], "@$var@");
		assertEquals(reqs.get(0).getGivenParameters()[1], "method");
		assertTrue(((List<Object>) 
				(reqs.get(0).getGivenParameters()[2])).size() == 0);

	}
}
