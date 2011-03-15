package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.impl.parser.request.tinyparser.Empty;


/**
 * Mini parser for empty command.
 * Example: # this is a comment
 * @author equesada
 */
public class EmptyTest {

	/**
	 * The mini parser to test.
	 */
	private Empty emptyMiniParser;

	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {

		emptyMiniParser = new Empty();
	}

	/**
	 * To test the parse method.
	 */
	@Test
	public final void testParse() {
		List<Request> reqs = emptyMiniParser
		    .parse(" # this line is a comment");
		assertTrue(reqs.size() == 1);
		assertFalse(reqs.get(0).isActive());
	}
}
