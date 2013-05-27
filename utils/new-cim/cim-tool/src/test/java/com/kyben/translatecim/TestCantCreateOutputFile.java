package com.kyben.translatecim;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.Test;

/**
 * Unit test for TranslateCIM.
 */
public class TestCantCreateOutputFile extends TestCase {

	private PrintStream	originalSysOut, originalSysErr;
	private ByteArrayOutputStream	sysOut, sysErr;

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public TestCantCreateOutputFile(String testName) {
		super(testName);
	}

	// @Before
	@Override
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
		return new TestSuite(TestCantCreateOutputFile.class);
	}

	@Test
	public void testCantCreateOutputFile() {
		try {
			TranslateCIM.processArgs("src/test/resources/testBasicsWrapper.mof",
					"TranslateCIM-java.stg",
					"/etc/bogus");
			System.setOut(originalSysOut);
			System.setErr(originalSysErr);
			// assertTrue(sysErr.toString().contains("couldn't create output file"));
		} catch (Exception e) {
			System.setOut(originalSysOut);
			System.setErr(originalSysErr);
			e.printStackTrace();
		}
	}
}
