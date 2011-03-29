package ynot.lang.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ynot.core.entity.Shell;
import ynot.lang.Structure;

/**
 * To test the Structure class.
 * 
 * @author equesada
 */
public class StructureTest {

	/**
	 * The structure to test.
	 */
	private Structure structure;

	/**
	 * To set up the structure.
	 */
	@Before
	public final void setUp() {
		structure = new Structure();
	}

	/**
	 * To test the functionMethod method.
	 */
	 @Test
	public final void functionMethodTest() {
		Shell shell = new Shell();
		List<Object> argumentNames = new ArrayList<Object>();
		argumentNames.add("a");
		argumentNames.add("b");
		shell.goIn("functionMethod");
		structure.functionMethod(shell, "test", argumentNames);
		shell.goOut();
		assertEquals(shell.getVariable("label:function_test_2"), 1);
	}

}
