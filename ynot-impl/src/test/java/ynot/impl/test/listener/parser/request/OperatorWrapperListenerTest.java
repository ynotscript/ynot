package ynot.impl.test.listener.parser.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import ynot.core.listener.parser.request.RequestParserListener;
import ynot.impl.listener.parser.request.OperatorWrapperListener;

/**
 * Class to test the listener wrapping the mathematical operators.
 * 
 * @author equesada
 */
public class OperatorWrapperListenerTest {

    /**
     * The listener to test.
     */
    private RequestParserListener<String> owl;

    /**
     * To set up the listener.
     * 
     * @throws Exception
     *             if something is wrong.
     */
    @Before
    public final void setUp() throws Exception {
        owl = new OperatorWrapperListener();
    }

    /**
     * To test the preNotice method.
     */
    @Test
    public final void testPreNotice() {
        String objToParse = "1+3";
        String test = owl.preNotice(objToParse);
        assertEquals(test, "1@+@3");
    }

}
