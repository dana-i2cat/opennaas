package com.kyben.translatecim;

import org.junit.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.*;

/**
 * Unit test for TranslateCIM.
 */
public class TestArrayTypeOnNonArray extends TestCase {

    private PrintStream originalSysOut, originalSysErr;
    private ByteArrayOutputStream sysOut, sysErr;

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TestArrayTypeOnNonArray(String testName) {
		super(testName);
	}

//  @Before
    public void setUp() {
        originalSysOut = System.out;
        originalSysErr = System.err;
        sysOut = new ByteArrayOutputStream();
        sysErr = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
        System.setErr(new PrintStream(sysErr));
    }

	/**
	 * @return the suite of tests being tested
	 */
	public static TestSuite suite() {
		return new TestSuite( TestArrayTypeOnNonArray.class );
	}

	@Test
	public void testArrayTypeOnNonArray() {
		try {
			TranslateCIM.processArgs("src/test/resources/testArrayTypeOnNonArrayWrapper.mof",
								 	 "TranslateCIM-java.stg",
								 	 System.getProperty("java.io.tmpdir"));
			System.setOut(originalSysOut);
            System.setErr(originalSysErr);
			assertTrue(sysErr.toString().contains("has the ArrayType qualifier, but isn't an array"));
		} catch (Exception e) {
            System.setOut(originalSysOut);
            System.setErr(originalSysErr);
			e.printStackTrace();
		}
	}
}
