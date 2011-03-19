package ynot.impl.test.parser.argument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Resource;
import ynot.core.exception.parser.UnparsableArgumentException;
import ynot.impl.parser.argument.SimpleArgumentParser;

/**
 * Class to test the SimpleArgumentParser parser.
 * 
 * @author equesada
 */
public class SimpleArgumentParserTest {

	/**
	 * Value 100.
	 */
	private static final int VALUE_100 = 100;
	
	/**
	 * Value 1.1.
	 */
	private static final double VALUE_1_DOT_1 = 1.1;
	
	/**
	 * The parser to test.
	 */
	private SimpleArgumentParser parser;

	/**
	 * To set up the parser.
	 * 
	 * @throws Exception
	 *             if something is wrong.
	 */
	@Before
	public final void setUp() throws Exception {
		parser = new SimpleArgumentParser();
	}

	/**
	 * To test the parse method.
	 * 
	 * @throws UnparsableArgumentException
	 *             if something is wrong.
	 */
	@Test
	public final void testParse() throws UnparsableArgumentException {
		assertEquals(1, parser.parse("1"));
		assertEquals("toto", parser.parse("\"toto\""));
		assertEquals(true, parser.parse("true"));
		assertEquals(false, parser.parse("false"));
		assertEquals(null, parser.parse("null"));
		assertEquals("@$a@", parser.parse("$a"));
		Object resA = parser.parse("?a?");
		assertTrue(resA instanceof Resource);
		assertEquals(((Resource) resA).getProviderName(), null);
		assertEquals(((Resource) resA).getResourceName(), "a");
		Object resB = parser.parse("?a\\b?");
		assertTrue(resB instanceof Resource);
		assertEquals(((Resource) resB).getProviderName(), "a");
		assertEquals(((Resource) resB).getResourceName(), "b");
		assertTrue(parser.parse("{1,2,3}") instanceof List);
		assertTrue(parser.parse("1..3") instanceof List);
		assertTrue(parser.parse("{\"http://www.google.com/\"}") instanceof List);
		assertEquals(new Double(VALUE_1_DOT_1), parser.parse("1.1"));
		assertEquals(new Float(VALUE_1_DOT_1), parser.parse("fx1.1"));
		assertEquals(VALUE_100, parser.parse("2x1100100"));
		assertEquals(VALUE_100, parser.parse("8x144"));
		assertEquals(VALUE_100, parser.parse("16x64"));
		try {
			parser.parse("aaaa");
		} catch (UnparsableArgumentException e) {
			return;
		}
		fail("aaaa is not parsable normally");
	}

}
