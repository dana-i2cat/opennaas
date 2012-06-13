package com.kyben.translatecim;

import org.junit.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.*;

/**
 * Unit test for TranslateCIM.
 */
public class TestValuesDontMatchValueMap extends TestCase {

    private PrintStream originalSysOut, originalSysErr;
    private ByteArrayOutputStream sysOut, sysErr;

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TestValuesDontMatchValueMap(String testName) {
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
		return new TestSuite( TestValuesDontMatchValueMap.class );
	}

	@Test
	public void testValuesDontMatchValueMap() {
		try {
			TranslateCIM.processArgs("src/test/resources/testValuesDontMatchValueMapWrapper.mof",
									 "TranslateCIM-java.stg",
									 System.getProperty("java.io.tmpdir"));
			System.setOut(originalSysOut);
            System.setErr(originalSysErr);
			assertTrue(sysErr.toString().contains(" property "));
			assertTrue(sysErr.toString().contains(" has Values and ValueMaps qualifiers with differing numbers of elements"));
		} catch (Exception e) {
            System.setOut(originalSysOut);
            System.setErr(originalSysErr);
			e.printStackTrace();
		}
	}
}
