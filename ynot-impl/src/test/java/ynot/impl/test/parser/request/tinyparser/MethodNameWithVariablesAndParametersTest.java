package ynot.impl.test.parser.request.tinyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Request;
import ynot.core.exception.parser.UnparsableRequestException;
import ynot.impl.parser.request.tinyparser.MethodNameWithVariablesAndParameters;

/**
 * Mini parser for method with parameters and returned variables. Example: $a :=
 * toto("abc")
 * 
 * @author equesada
 */
public class MethodNameWithVariablesAndParametersTest {

    /**
     * Number of parameters when tests.
     */
    private static final int NB_PARAMS = 3;

    /**
     * Number of parameters when tests.
     */
    private static final int NB_VARS = 2;
    /**
     * The mini parser to test.
     */
    private MethodNameWithVariablesAndParameters mnwvap;

    /**
     * To set up the mini parser.
     * 
     * @throws Exception
     *             if something is wrong.
     */
    @Before
    public final void setUp() throws Exception {
        mnwvap = new MethodNameWithVariablesAndParameters();
    }

    /**
     * Test the parse method.
     * 
     * @throws UnparsableRequestException
     *             if something is wrong.
     */
    @Test
    public final void testParse() throws UnparsableRequestException {
        List<Request> reqs = mnwvap
                .parse(" $d , $e := p2\\test ( \"azerty\" , 2 , {1,2,\"o\"}) ");
        assertTrue(reqs.size() == 1);
        assertEquals(reqs.get(0).getWordToUse(), "test");
        assertEquals(reqs.get(0).getDefinitionProviderName(), "p2");
        assertTrue(reqs.get(0).getGivenParameters().length == NB_PARAMS);
        assertTrue(reqs.get(0).getVariableNames().length == NB_VARS);
        assertEquals(reqs.get(0).getVariableNames()[0], "d");

        List<Request> reqs2 = mnwvap
                .parse("$method := new(\"GetMethod\",{\"http://www.google.com/\"})");
        assertTrue(reqs2.size() == 1);
    }
}
