package com.kyben.translatecim;

import org.junit.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.kohsuke.args4j.CmdLineException;
import java.io.*;

/**
 * Unit test for TranslateCIM.
 */
public class TestCommandLine extends TestCase {

	private PrintStream originalSysOut, originalSysErr;
	private ByteArrayOutputStream sysOut, sysErr;

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TestCommandLine( String testName ) {
		super( testName );
	}

//	@Before
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
		return new TestSuite( TestCommandLine.class );
	}

	@Test
	public void testNotEnoughCommandLineArguments() {
		String inputFileName = "";
		String stgFileName = "";
		String outputDirectoryName = "";
		String[] testArgs = {
				"/Users/siemsen/TranslateCIM/src/test/data/cim/cimv2201Experimental-MOFs/cim_schema_2.20.1.mof",
				"/Users/siemsen/TranslateCIM/TranslateCIM-java.stg",
				//  "/tmp/org/dmtf/cim"   // for this test, comment this out to cause a CmdLineException
		};
		TranslateCIM tc = new TranslateCIM();
		String fileNames[];
		try {
			fileNames = tc.parseCommandLine(testArgs);
			System.setOut(originalSysOut);
			System.setErr(originalSysErr);
			fail("Should have thrown a CmdLineException, didn't throw any exception at all");
		} catch (CmdLineException ce) {
			System.setOut(originalSysOut);
			System.setErr(originalSysErr);
			String expectedString = "Need 3 comand-line arguments, got 2";
			if (sysErr.toString().contains(expectedString)) {
				assertTrue(true);
			} else {
				fail("Expected a CmdLineException with sysErr containing \"" + expectedString + "\", got a CmdLineException with \"" + ce.getMessage() + "\"");
			}
		} catch (Exception e) {
			System.setOut(originalSysOut);
			System.setErr(originalSysErr);
			fail("Expected a CmdLineException, got an Exception: " + e.getMessage());
		}
	}

	@Test
	public void testIllegalCommandLineArgument() {
		String inputFileName = "";
		String stgFileName = "";
		String outputDirectoryName = "";
		String[] testArgs = {
				"-nnn",
				"/Users/siemsen/TranslateCIM/src/test/data/cim/cimv2201Experimental-MOFs/cim_schema_2.20.1.mof",
				"/Users/siemsen/TranslateCIM/TranslateCIM-java.stg",
				 System.getProperty("java.io.tmpdir")
		};
		TranslateCIM tc = new TranslateCIM();
		String fileNames[];
		try {
			fileNames = tc.parseCommandLine(testArgs);
			System.setOut(originalSysOut);
			System.setErr(originalSysErr);
			fail("Should have thrown a CmdLineException, didn't throw any exception at all");
		} catch (CmdLineException ce) {
			System.setOut(originalSysOut);
			System.setErr(originalSysErr);
			String expectedString = "is not a valid option";
			if (sysErr.toString().contains(expectedString)) {
				assertTrue(true);
			} else {
				fail("Expected a CmdLineException with sysErr containing \"" + expectedString + "\", got a CmdLineException with \"" + ce.getMessage() + "\"");
			}
		} catch (Exception e) {
			System.setOut(originalSysOut);
			System.setErr(originalSysErr);
			fail("Expected a CmdLineException, got an Exception: " + e.getMessage());
		}
	}
}
