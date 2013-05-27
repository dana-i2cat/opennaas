package com.kyben.translatecim;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.Test;

/**
 * Unit test for TranslateCIM.
 */
public class TestBasics extends TestCase {

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public TestBasics(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static TestSuite suite() {
		return new TestSuite(TestBasics.class);
	}

	@Test
	public void testBasics() {
		try {
			TranslateCIM.processArgs(// "-noenums",
					"src/test/resources/testBasicsWrapper.mof",
					"TranslateCIM-java.stg",
					System.getProperty("java.io.tmpdir"));
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Test of \"Basics\" file failed: " + e.getMessage());
		}
		assert (true);
	}
}
