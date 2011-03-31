package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.listener.parser.request.OperatorWrapperListener;
import ynot.impl.parser.request.tinyparser.Operation;

/**
 * Mini parser for operation. Example: $a = "toto"
 * 
 * @author equesada
 */
public class OperationTest {

    /**
     * The mini parser to test.
     */
    private Operation operationMiniParser;
    
    /**
     * The used listener for operator.
     */
    private OperatorWrapperListener operatorListener;

    /**
     * To set up the mini parser.
     * 
     * @throws Exception
     *             if something is wrong.
     */
    @Before
    public final void setUp() throws Exception {
        operationMiniParser = new Operation();
        operatorListener = new OperatorWrapperListener();
    }

    /**
     * Test the parse method.
     * 
     * @throws UnparsableRequestException
     *             if something is wrong.
     */
    @Test
    public final void testParse() throws UnparsableRequestException {
        String objToParse = " $a = \"toto\" ";
        objToParse = operatorListener.preNotice(objToParse);
        List<Request> reqs = operationMiniParser.parse(objToParse);
        assertTrue(reqs.size() == 1);
        assertEquals(reqs.get(0).getWordToUse(), "=");
        assertEquals(reqs.get(0).getDefinitionProviderName(), null);
        assertTrue(reqs.get(0).getGivenParameters().length == 2);
        assertEquals(reqs.get(0).getGivenParameters()[0], "@$a@");
        assertEquals(reqs.get(0).getGivenParameters()[1], "toto");
        
        String objToParse2 = "\"historic -c\" = $req";
        objToParse2 = operatorListener.preNotice(objToParse2);
        List<Request> reqs2 = operationMiniParser.parse(objToParse2);
        assertTrue(reqs2.size() == 1);
        assertEquals(reqs2.get(0).getWordToUse(), "=");
        assertEquals(reqs2.get(0).getDefinitionProviderName(), null);
        assertTrue(reqs2.get(0).getGivenParameters().length == 2);
        assertEquals(reqs2.get(0).getGivenParameters()[0], "historic -c");
        assertEquals(reqs2.get(0).getGivenParameters()[1], "@$req@");
    }

}
