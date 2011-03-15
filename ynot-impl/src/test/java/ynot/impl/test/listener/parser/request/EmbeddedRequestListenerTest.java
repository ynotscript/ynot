package ynot.impl.test.listener.parser.request;

import org.junit.Before;
import org.junit.Test;

import ynot.impl.listener.parser.request.EmbeddedRequestListener;

/**
 * Class to test the listener checking embedded requests.
 * @author equesada
 */
public class EmbeddedRequestListenerTest {
	
	/**
	 * The listener to test.
	 */
	private EmbeddedRequestListener erl;

	/**
	 * To set up the mini parser.
	 * @throws Exception if something is wrong. 
	 */
	@Before
	public final void setUp() throws Exception {
		erl = new EmbeddedRequestListener();
	}
	
	/**
	 * To test the preNotice method.
	 */
	@Test
	public final void testPreNotice() {
		String objToParse = "toto([tutu(\"abc\")],1,[tata([lala(2)])])";
		String ret = erl.preNotice(objToParse);
		System.out.println(ret);
	}
}
