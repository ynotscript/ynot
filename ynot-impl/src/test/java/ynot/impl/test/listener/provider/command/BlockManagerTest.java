package ynot.impl.test.listener.provider.command;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import ynot.impl.listener.provider.command.BlockManager;

/**
 * Class to test the BlockManager listener.
 * 
 * @author equesada
 */
public class BlockManagerTest {
    
    /**
     * The listener to test.
     */
    private BlockManager bm;
    
    /**
     * To set up the listener.
     * 
     * @throws Exception
     *             if something is wrong.
     */
    @Before
    public final void setUp() throws Exception {
        bm = new BlockManager();
    }
    
    /**
     * To test the preNotice method.
     */
    @Test
    public final void testPreNotice() {
        try {
           bm.preNotice(null);            
        } catch (Exception e) {
           fail("the null is not managed");
        }
    }
}
