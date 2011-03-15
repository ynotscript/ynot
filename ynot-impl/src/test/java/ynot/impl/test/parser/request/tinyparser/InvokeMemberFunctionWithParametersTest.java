package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.impl.parser.request.tinyparser.InvokeMemberFunctionWithParameters;


/**
 * Mini parser for function with parameters.
 * Example: $var.function("abc")
 * @author equesada
 */
public class InvokeMemberFunctionWithParametersTest {

	/**
	 * The mini parser to test.
	 */
	private InvokeMemberFunctionWithParameters imfwp;
	
	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {

		imfwp = new InvokeMemberFunctionWithParameters();
	}
	
	/**
	 * To test the parse method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testParse() {
		List<Request> reqs = imfwp.parse(" $var.method(\"toto\",1) ");
		assertEquals(reqs.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs.get(0).getWordToUse(), "invoke");
		assertEquals(reqs.get(0).getGivenParameters()[0], "@$var@");
		assertEquals(reqs.get(0).getGivenParameters()[1], "method");
		assertTrue(((List<Object>) 
				(reqs.get(0).getGivenParameters()[2])).size() == 2);

	}
}
