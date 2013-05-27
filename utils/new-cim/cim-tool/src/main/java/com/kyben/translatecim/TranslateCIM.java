package com.kyben.translatecim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class TranslateCIM {
	@Option(name = "-noenums", usage = "don't emit enumerations")
	private static boolean	noEnums			= false;

	@Option(name = "-nodeprecated", usage = "don't emit deprecated classes, methods or fields")
	private static boolean	noDeprecated	= false;

	// receives command-line parameters other than options
	@Argument
	private List<String>	arguments		= new ArrayList<String>();

	/*
	 * Parse the command line and return 3 values in an array. The values are the arguments from the command line:
	 * 
	 * 0. the name of the input file (should end in .mof) 1. the name of the string template group file (should end in .stg) 2. the name of the output
	 * directory
	 */
	public String[] parseCommandLine(String[] args) throws IOException, CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		parser.setUsageWidth(80);
		try {
			parser.parseArgument(args);
			if (arguments.size() != 3)
				throw new CmdLineException("Need 3 comand-line arguments, got " + arguments.size());
		} catch (CmdLineException ce) {
			System.err.println(ce.getMessage());
			System.err.println();
			System.err.println("Usage:    java TranslateCIM [options...] inputFileName stgFileName outputDirectoryName");
			System.err.println(" Options are:");
			parser.printUsage(System.err); // print the list of available options
			System.err.println();
			System.err.println("  Example: java TranslateCIM -nodeprecated cimv2201.mof TranslateCIM-java.stg /tmp/org/dmtf/cim");
			throw ce;
		}
		String RetVal[] = new String[3];
		RetVal[0] = arguments.get(0); // input file name
		RetVal[1] = arguments.get(1); // StringTemplate group file name
		RetVal[2] = arguments.get(2); // output directory name
		return RetVal;
	}

	/*
	 * I broke this code out of the "main" function because the maven test framework doesn't support access to files via file names. You have to open
	 * test files as "resources" instead of files. So when testing, I open "resources", extract the true file names, and feed them to this routine.
	 * When not testing (when executing "main" below) I feed this routine the file names that were parsed off the command line. This is a little
	 * goofy, but I couldn't figure out a better aproach that lets me store test data files in a way that honors the maven testing framework.
	 */
	public static void processArgs(String inputFileName,
			String stgFileName,
			String outputDirectoryName) throws Exception {

		System.out.println("Warning: hardcoding the CIM major and minor version numbers");
		String cimMajorVersion = "2";
		String cimMinorVersion = "20.1";

		System.out.println("Starting lexer phase, reading CIM MOF files, creating a token stream");
		// Open an input file stream from the given file name
		CharStream input = new ANTLRFileStream(inputFileName);
		// Create a lexer that feeds from the input file stream
		TranslateCIMLexer lexer = null;
		lexer = new TranslateCIMLexer(input);
		// Create a stream of tokens fed by the lexer
		CommonTokenStream tokens = null;
		tokens = new CommonTokenStream(lexer);
		// Create a parser that feeds off the token stream
		TranslateCIMParser parser = new TranslateCIMParser(tokens);
		// Begin parsing at rule mofSpecification
		try {
			parser.mofSpecification(
					cimMajorVersion,
					cimMinorVersion,
					stgFileName,
					outputDirectoryName,
					noDeprecated,
					noEnums);
			// catch and print these so the user doesn't see an ugly stack trace
		} catch (ArrayTypeOnNonArrayException e) {
			System.err.println(e.getMessage());
		} catch (CantCreateBuildFileException e) {
			System.err.println(e.getMessage());
		} catch (CantCreateOutputFileException e) {
			System.err.println(e.getMessage());
		} catch (CantReadIncludeFileException e) {
			System.err.println(e.getMessage());
		} catch (CantReadStgFileException e) {
			System.err.println(e.getMessage());
		} catch (ClassNameDoesntMatchFileNameException e) {
			System.err.println(e.getMessage());
		} catch (UndefinedClassException e) {
			System.err.println(e.getMessage());
		} catch (UndefinedQualifierException e) {
			System.err.println(e.getMessage());
		} catch (ValuesButNoValueMapException e) {
			System.err.println(e.getMessage());
		} catch (ValuesDontMatchValueMapException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {

		TranslateCIM tc = new TranslateCIM();
		String fileNames[];
		try {
			fileNames = tc.parseCommandLine(args);
		} catch (CmdLineException ce) {
			return; // we've already written a message to System.err, catch it here just to avoid an ugly stack dump.
		}

		processArgs(
				fileNames[0], // input file name
				fileNames[1], // StringTemplate group file name
				fileNames[2]); // output directory name
	}
}
