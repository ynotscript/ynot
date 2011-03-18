package ynot.impl.test.listener.provider.command;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import ynot.impl.listener.provider.command.UnlockManager;

/**
 * Class to test the UnlockManager listener.
 * 
 * @author equesada
 */
public class UnlockManagerTest {
    
    /**
     * The listener to test.
     */
    private UnlockManager um;
    
    /**
     * To set up the listener.
     * 
     * @throws Exception
     *             if something is wrong.
     */
    @Before
    public final void setUp() throws Exception {
        um = new UnlockManager();
    }
    
    /**
     * To test the preNotice method.
     */
    @Test
    public final void testPreNotice() {
        try {
            um.preNotice(null);            
        } catch (Exception e) {
           fail("the null is not managed");
        }
    }
}
