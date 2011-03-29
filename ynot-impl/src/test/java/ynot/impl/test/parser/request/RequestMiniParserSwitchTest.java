package ynot.impl.test.parser.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.parser.request.RequestParserHandler;

/**
 * MiniRequestParserSwitcher.
 * 
 * @author equesada
 */
public class RequestMiniParserSwitchTest {

	/**
	 * The switcher to test.
	 */
	private RequestParserHandler miniRequestParserSwitcher;

	/**
	 * To set up the switcher.
	 * 
	 * @throws Exception
	 *             if something's wrong.
	 */
	@Before
	public final void setUp() throws Exception {	
		miniRequestParserSwitcher = new RequestParserHandler();
	}

	/**
	 * To test the parse method.
	 * 
	 * @throws UnparsableRequestException
	 *             if something's wrong.
	 */
	@Test
	public final void testParse() throws UnparsableRequestException {

		List<Request> reqs = miniRequestParserSwitcher
				.parse("toto({\"tutu\",2} , \"tata\")#># $a := lolo");
		assertTrue(reqs.size() == 2);
		assertEquals(reqs.get(0).getWordToUse(), "toto");
		assertEquals(reqs.get(1).getWordToUse(), "lolo");
		assertEquals(reqs.get(1).getVariableNames()[0], "a");
		
		List<Request> reqs2 = miniRequestParserSwitcher.parse("$req := null");
		assertTrue(reqs2.size() == 1);
		assertEquals(reqs2.get(0).getWordToUse(), "assign");
		assertEquals(reqs2.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs2.get(0).getGivenParameters().length, 1);
		assertEquals((reqs2.get(0).getGivenParameters()[0]), null);
		assertEquals((reqs2.get(0).getVariableNames()[0]), "req");
		
		List<Request> reqs3 = miniRequestParserSwitcher.parse("$req:=null");
		assertTrue(reqs3.size() == 1);
		assertEquals(reqs3.get(0).getWordToUse(), "assign");
		assertEquals(reqs3.get(0).getDefinitionProviderName(), "ynot");
		assertEquals(reqs3.get(0).getGivenParameters().length, 1);
		assertEquals((reqs3.get(0).getGivenParameters()[0]), null);
		assertEquals((reqs3.get(0).getVariableNames()[0]), "req");
	}
}
