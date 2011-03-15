package ynot.impl.test.parser.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
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
	 * To get the base path of the project.
	 * 
	 * @return the base path.
	 */
	public static String getBasePath() {
		String base = "./";
		URL path = RequestMiniParserSwitchTest.class.getProtectionDomain()
				.getCodeSource().getLocation();
		if (path.toString().endsWith(".jar")) {
			base = path.toString().replace("file:/", "");
			base = base.substring(0, base.lastIndexOf("/") + 1);
			if (!base.matches("^[a-zA-Z]:.*$")) {
				base = "/" + base;
			} else {
				base = base.replaceAll("/", "\\\\");
				base = base.replaceAll("%20", " ");
			}

		}
		return base;
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
	}
}
