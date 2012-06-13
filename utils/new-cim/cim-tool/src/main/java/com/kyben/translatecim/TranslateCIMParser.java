// $ANTLR 3.1.3 Mar 17, 2009 19:23:44 com\\kyben\\translatecim\\TranslateCIM.g 2012-05-23 17:28:22

package com.kyben.translatecim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;

public class TranslateCIMParser extends Parser {

	// Reserved words
	private static final String[]	RESERVED_JAVA_WORDS		= { "static", "volatile", "default" };

	public static final String[]	tokenNames				= new String[] {
															"<invalid>", "<EOR>", "<DOWN>", "<UP>", "PragmaInclude", "PragmaLocale", "QUALIFIER", "COLON", "SEMICOLON", "Identifier", "EQUALS", "COMMA", "SCOPE", "LPAREN", "RPAREN", "ANY", "CLASS", "METHOD", "PARAMETER", "PROPERTY", "REFERENCE", "FLAVOR", "ENABLEOVERRIDE", "DISABLEOVERRIDE", "TOSUBCLASS", "RESTRICTED", "TRANSLATABLE", "LCURLY", "RCURLY", "AS", "DOLLAR", "REF", "LBRACK", "RBRACK", "BOOLEAN", "CHAR16", "DATETIME", "REAL32", "REAL64", "SINT16", "SINT32", "SINT64", "SINT8", "STRING", "UINT16", "UINT32", "UINT64", "UINT8", "IntegralConstant", "DoubleQuotedString", "CharacterConstant", "TRUE", "FALSE", "NULL", "BACKSLASH", "DOUBLEQUOTE", "MINUS", "PLUS", "PRAGMAINCLUDE", "PRAGMALOCALE", "SINGLEQUOTE", "SOURCETYPE", "WhiteSpace", "InlineComment", "MultiLineComment", "EscapeSequence", "StupidEscapeSequence", "HexEscape", "HexDigit", "DecimalConstant", "BinaryConstant", "OctalConstant", "HexConstant", "Exponent", "FloatingPointConstant"
															};
	public static final int			DOLLAR					= 30;
	public static final int			OctalConstant			= 71;
	public static final int			SINT16					= 39;
	public static final int			CLASS					= 16;
	public static final int			LBRACK					= 32;
	public static final int			PRAGMAINCLUDE			= 58;
	public static final int			UINT8					= 47;
	public static final int			HexConstant				= 72;
	public static final int			InlineComment			= 63;
	public static final int			FLAVOR					= 21;
	public static final int			DecimalConstant			= 69;
	public static final int			StupidEscapeSequence	= 66;
	public static final int			HexEscape				= 67;
	public static final int			Exponent				= 73;
	public static final int			EQUALS					= 10;
	public static final int			DOUBLEQUOTE				= 55;
	public static final int			EOF						= -1;
	public static final int			HexDigit				= 68;
	public static final int			Identifier				= 9;
	public static final int			LPAREN					= 13;
	public static final int			AS						= 29;
	public static final int			SOURCETYPE				= 61;
	public static final int			DoubleQuotedString		= 49;
	public static final int			RPAREN					= 14;
	public static final int			BOOLEAN					= 34;
	public static final int			SINT64					= 41;
	public static final int			IntegralConstant		= 48;
	public static final int			SCOPE					= 12;
	public static final int			COMMA					= 11;
	public static final int			SINGLEQUOTE				= 60;
	public static final int			PARAMETER				= 18;
	public static final int			UINT16					= 44;
	public static final int			PRAGMALOCALE			= 59;
	public static final int			PLUS					= 57;
	public static final int			WhiteSpace				= 62;
	public static final int			SINT32					= 40;
	public static final int			CharacterConstant		= 50;
	public static final int			MultiLineComment		= 64;
	public static final int			BinaryConstant			= 70;
	public static final int			RBRACK					= 33;
	public static final int			TRANSLATABLE			= 26;
	public static final int			REAL64					= 38;
	public static final int			QUALIFIER				= 6;
	public static final int			DATETIME				= 36;
	public static final int			NULL					= 53;
	public static final int			REFERENCE				= 20;
	public static final int			DISABLEOVERRIDE			= 23;
	public static final int			RESTRICTED				= 25;
	public static final int			LCURLY					= 27;
	public static final int			SEMICOLON				= 8;
	public static final int			PragmaInclude			= 4;
	public static final int			MINUS					= 56;
	public static final int			ENABLEOVERRIDE			= 22;
	public static final int			TRUE					= 51;
	public static final int			UINT64					= 46;
	public static final int			REF						= 31;
	public static final int			COLON					= 7;
	public static final int			SINT8					= 42;
	public static final int			ANY						= 15;
	public static final int			FloatingPointConstant	= 74;
	public static final int			PROPERTY				= 19;
	public static final int			TOSUBCLASS				= 24;
	public static final int			REAL32					= 37;
	public static final int			RCURLY					= 28;
	public static final int			PragmaLocale			= 5;
	public static final int			UINT32					= 45;
	public static final int			FALSE					= 52;
	public static final int			EscapeSequence			= 65;
	public static final int			METHOD					= 17;
	public static final int			CHAR16					= 35;
	public static final int			STRING					= 43;
	public static final int			BACKSLASH				= 54;

	// delegates
	// delegators

	public TranslateCIMParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}

	public TranslateCIMParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);

	}

	protected StringTemplateGroup	templateLib	=
														new StringTemplateGroup("TranslateCIMParserTemplates", AngleBracketTemplateLexer.class);

	public void setTemplateLib(StringTemplateGroup templateLib) {
		this.templateLib = templateLib;
	}

	public StringTemplateGroup getTemplateLib() {
		return templateLib;
	}

	/**
	 * allows convenient multi-value initialization: "new STAttrMap().put(...).put(...)"
	 */
	public static class STAttrMap extends HashMap {
		public STAttrMap put(String attrName, Object value) {
			super.put(attrName, value);
			return this;
		}

		public STAttrMap put(String attrName, int value) {
			super.put(attrName, new Integer(value));
			return this;
		}
	}

	@Override
	public String[] getTokenNames() {
		return TranslateCIMParser.tokenNames;
	}

	@Override
	public String getGrammarFileName() {
		return "com\\kyben\\translatecim\\TranslateCIM.g";
	}

	HashMap<String, qualifierDefinition>	qualifierDefinitions	= new HashMap<String, qualifierDefinition>();
	HashMap<String, String>					classSubDirectories		= new HashMap<String, String>();
	HashSet<String>							accessedSubDirs;
	HashSet<String>							outputDirectoryNames	= new HashSet<String>();
	String									TCVersion				= "0.9.1";
	String									cimMajorVersion			= null;
	String									cimMinorVersion			= null;
	String									incSubDirectory			= null;
	boolean									noDeprecated;
	boolean									noEnums;
	StringTemplateGroup						templates				= null;
	String									outputDirectory			= null;										// from the command line, test
	String									currentInputFileName	= "";											// this gets a new value with each
																													// "pragma include" statement
	String									currentISO8601DateTime	= null;

	HashSet<String>							javaKeywords			= new HashSet<String>();
	HashSet<String>							javaClasses				= new HashSet<String>();

	public class qualifierDefinition {
		String				name;
		String				type;
		boolean				isArray;
		ArrayList<String>	scopes;
		ArrayList<String>	flavors;

		qualifierDefinition(String qName,
				String qType,
				boolean qIsArray,
				ArrayList<String> qScopes,
				ArrayList<String> qFlavors) {
			this.name = qName;
			this.type = qType;
			this.isArray = qIsArray;
			this.scopes = qScopes;
			this.flavors = qFlavors;
		}

		@Override
		public String toString() {
			String str =
					"name: \"" + name + "\"\n" +
							"type: \"" + type + "\"\n" +
							"isArray: \"" + isArray + "\"\n" +
							"scopes: \"" + scopes + "\"\n" +
							"flavors: \"" + flavors + "\"\n";
			return str;
		}
	}

	//
	// The parser calls this method whenever a CIM class is used in a property
	// declaration, method declaration, or method REF. This method emits an error if
	// the CIM class is undefined. Otherwise, it updates the list of subdirectories
	// that need to be imported in order to resolve the reference to the class.
	//
	void checkClassName(Token classNameToken) throws RecognitionException {
		String classNam = classNameToken.getText();
		if (classSubDirectories.containsKey(classNam)) {
			String subDir = classSubDirectories.get(classNam);
			if (!subDir.equals(incSubDirectory)) {
				accessedSubDirs.add(subDir.toLowerCase());
			}
		} else {
			RecognitionException r = new RecognitionException(input);
			throw new UndefinedClassException(
					currentInputFileName + "(" + r.line + ":" + r.charPositionInLine + ")" +
							": class " + classNam + " not defined");
		}
	}

	String	htmlNewLineString		= "<br><br>";
	int		htmlNewLineStringLength	= htmlNewLineString.length();

	String htmlize(String str) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		StringBuilder retstr = new StringBuilder(len + 20);
		for (char estr : str.toCharArray()) {
			switch (estr) {
				case '&':
					retstr.append("&amp;");
					break;
				case '<':
					retstr.append("&lt;");
					break;
				case '>':
					retstr.append("&gt;");
					break;
				case '\n':
					retstr.append(htmlNewLineString);
					break;
				default:
					retstr.append(estr);
			}
		}
		return retstr.toString();
	}

	// javadocize does:
	// 1. break into separate lines (on line length and when you see <br><br>)
	// 2. wrap @see on around every reference to a CIM class (not implemented yet)
	// 3. escape "@" when it appears as the first character on a line (not impremented yet)
	ArrayList<String> javadocize(String str) {
		if (str == null) {
			return null;
		}
		ArrayList<String> retArrayList = new ArrayList<String>();
		String[] result = str.split("\\s");
		StringBuilder currentLine = new StringBuilder();
		for (int x = 0; x < result.length; x++) {
			String newWord = result[x];
			while (newWord.contains(htmlNewLineString)) {
				int lineEnd = newWord.indexOf(htmlNewLineString);
				currentLine.append(newWord.substring(0, lineEnd + htmlNewLineStringLength));
				retArrayList.add(currentLine.toString());
				currentLine.setLength(0);
				newWord = newWord.substring(lineEnd + htmlNewLineStringLength);
			}
			if ((currentLine.length() + newWord.length()) > 75) {
				retArrayList.add(currentLine.toString());
				currentLine.setLength(0);
			}
			currentLine.append(" " + newWord);
		}
		retArrayList.add(currentLine.toString());
		return retArrayList;
	}

	String canonicalizeValue(String inValue) {
		String retValue = inValue;
		retValue = retValue.replace(" ", "_");
		retValue = retValue.replace("#", "_");
		retValue = retValue.replace("&", "_and_");
		retValue = retValue.replace("(", "_");
		retValue = retValue.replace(")", "_");
		retValue = retValue.replace("+", "_plus_");
		retValue = retValue.replace("-", "_");
		retValue = retValue.replace(".", "_");
		retValue = retValue.replace(",", "_");
		retValue = retValue.replace("/", "_");
		retValue = retValue.replace("\\", "_");
		retValue = retValue.replace("'", "_");
		retValue = retValue.replace(":", "_");
		retValue = retValue.replace("<", "_");
		retValue = retValue.replace("=", "_");
		retValue = retValue.replace(">", "_");
		retValue = retValue.replace("|", "_");
		return retValue;
	}

	//
	// Given a string and a hashset of unique strings, make sure the string doesn't
	// appear in the hashset. If it does, generate a new, unique string that
	// doesn't appear in the hashset and return it. Store the new string in the
	// hashset so it won't be allowed later.
	//
	String makeUnique(String valueName, HashSet<String> uniqueValueNames) {
		String originalValueName = valueName;
		int j = 1;
		while (uniqueValueNames.contains(valueName)) {
			valueName = originalValueName + "_" + Integer.toString(j);
			j++;
		}
		uniqueValueNames.add(valueName);
		return valueName;
	}

	// Debugging methods.
	public static void printKeys(Map m) {
		System.out.println("printkeys: Size = " + m.size() + ", ");
		System.out.println("printkeys: Keys: ");
		System.out.println(m.keySet());
	}

	public static void printValues(Map m) {
		System.out.println("printkeys: Values: ");
		System.out.println(m.values());
	}

	public static class mofSpecification_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "mofSpecification"
	// com\\kyben\\translatecim\\TranslateCIM.g:306:1: mofSpecification[String majVersion,\n String minVersion,\n String stgFileName,\n String
	// outputDirectoryName,\n boolean noDeprecatedIn,\n boolean noEnumsIn] : ( mofProduction )+ ;
	public final TranslateCIMParser.mofSpecification_return mofSpecification(String majVersion, String minVersion, String stgFileName,
			String outputDirectoryName, boolean noDeprecatedIn, boolean noEnumsIn) throws RecognitionException {
		TranslateCIMParser.mofSpecification_return retval = new TranslateCIMParser.mofSpecification_return();
		retval.start = input.LT(1);

		System.out.println("Starting parser phase, reading token stream, creating java files in " + outputDirectoryName);
		cimMajorVersion = majVersion;
		cimMinorVersion = minVersion;
		outputDirectory = outputDirectoryName;
		noDeprecated = noDeprecatedIn;
		noEnums = noEnumsIn;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		currentISO8601DateTime = sdf.format(Calendar.getInstance().getTime());
		javaKeywords.add("boolean");
		javaClasses.add("System");

		// Load the template group file (named something.stg) into the "templates" variable
		try {
			FileReader groupFileR = new FileReader(stgFileName);
			templates = new StringTemplateGroup(groupFileR);
			groupFileR.close();
		} catch (IOException e) {
			throw new CantReadStgFileException(": couldn't read string template group file " + e.getMessage());
		}
		setTemplateLib(templates);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:334:5: ( ( mofProduction )+ )
			// com\\kyben\\translatecim\\TranslateCIM.g:334:7: ( mofProduction )+
			{
				// com\\kyben\\translatecim\\TranslateCIM.g:334:7: ( mofProduction )+
				int cnt1 = 0;
				loop1: do {
					int alt1 = 2;
					int LA1_0 = input.LA(1);

					if (((LA1_0 >= PragmaInclude && LA1_0 <= QUALIFIER) || LA1_0 == LBRACK)) {
						alt1 = 1;
					}

					switch (alt1) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:334:8: mofProduction
						{
							pushFollow(FOLLOW_mofProduction_in_mofSpecification133);
							mofProduction();

							state._fsp--;

						}
							break;

						default:
							if (cnt1 >= 1)
								break loop1;
							EarlyExitException eee =
									new EarlyExitException(1, input);
							throw eee;
					}
					cnt1++;
				} while (true);

				// Create and write the build file.
				// String buildFileName = outputDirectory + File.separator + "compile.sh";
				// try {
				// BufferedWriter buildFile = new BufferedWriter(new FileWriter(buildFileName));
				// buildFile.write("#!/bin/bash\n");
				// for (String oDirName : outputDirectoryNames) {
				// // System.out.println("mofSpecification: oDirName = \"" + oDirName + "\""); // dbg
				// buildFile.write("javac -cp /tmp " + oDirName + File.separator + "*.java\n");
				// }
				// buildFile.close();
				// } catch (IOException e) {
				// throw new CantCreateBuildFileException("couldn't create build file " + e.getMessage());
				// }

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "mofSpecification"

	public static class mofProduction_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "mofProduction"
	// com\\kyben\\translatecim\\TranslateCIM.g:353:1: mofProduction : ( compilerDirective | qualifierDeclaration | classDeclaration );
	public final TranslateCIMParser.mofProduction_return mofProduction() throws RecognitionException {
		TranslateCIMParser.mofProduction_return retval = new TranslateCIMParser.mofProduction_return();
		retval.start = input.LT(1);

		TranslateCIMParser.classDeclaration_return classDeclaration1 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:357:5: ( compilerDirective | qualifierDeclaration | classDeclaration )
			int alt2 = 3;
			switch (input.LA(1)) {
				case PragmaInclude:
				case PragmaLocale: {
					alt2 = 1;
				}
					break;
				case QUALIFIER: {
					alt2 = 2;
				}
					break;
				case LBRACK: {
					alt2 = 3;
				}
					break;
				default:
					NoViableAltException nvae =
							new NoViableAltException("", 2, 0, input);

					throw nvae;
			}

			switch (alt2) {
				case 1:
				// com\\kyben\\translatecim\\TranslateCIM.g:357:7: compilerDirective
				{
					pushFollow(FOLLOW_compilerDirective_in_mofProduction162);
					compilerDirective();

					state._fsp--;

				}
					break;
				case 2:
				// com\\kyben\\translatecim\\TranslateCIM.g:358:7: qualifierDeclaration
				{
					pushFollow(FOLLOW_qualifierDeclaration_in_mofProduction170);
					qualifierDeclaration();

					state._fsp--;

				}
					break;
				case 3:
				// com\\kyben\\translatecim\\TranslateCIM.g:359:7: classDeclaration
				{
					pushFollow(FOLLOW_classDeclaration_in_mofProduction178);
					classDeclaration1 = classDeclaration();

					state._fsp--;

					// System.out.println("mofProduction: called"); // dbg
					if ((classDeclaration1 != null ? classDeclaration1.className : null) != null) { // omitted (deprecated) classes are null
						classSubDirectories.put((classDeclaration1 != null ? classDeclaration1.className : null), incSubDirectory);
						// Create the output subdirectory if it doesn't exist.
						String outputSubDirectoryName = outputDirectory.toLowerCase() + File.separator + incSubDirectory;
						File outputSubDirectory = new File(outputSubDirectoryName.toLowerCase());
						if (!outputSubDirectory.isDirectory()) {
							outputSubDirectory.mkdirs();
						}
						outputDirectoryNames.add(outputSubDirectoryName); // save the subdirectory name so we can create a "build file" later
						// Create and write the output file.
						String outputFileName = outputSubDirectoryName + File.separator + (classDeclaration1 != null ? classDeclaration1.className : null) + ".java";
						try {
							BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputFileName));
							// System.out.println("writing file \"" + outputFileName + "\"");
							outputFile.write((classDeclaration1 != null ? classDeclaration1.st : null).toString());
							outputFile.close();
						} catch (IOException e) {
							RecognitionException r = new RecognitionException(input);
							throw new CantCreateOutputFileException(
									currentInputFileName + "(" + r.line + ":" + r.charPositionInLine + ")" +
											": couldn't create output file " + e.getMessage());
						}
					}

				}
					break;

			}
			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "mofProduction"

	public static class compilerDirective_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "compilerDirective"
	// com\\kyben\\translatecim\\TranslateCIM.g:389:1: compilerDirective : ( PragmaInclude | PragmaLocale );
	public final TranslateCIMParser.compilerDirective_return compilerDirective() throws RecognitionException {
		TranslateCIMParser.compilerDirective_return retval = new TranslateCIMParser.compilerDirective_return();
		retval.start = input.LT(1);

		Token PragmaInclude2 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:393:5: ( PragmaInclude | PragmaLocale )
			int alt3 = 2;
			int LA3_0 = input.LA(1);

			if ((LA3_0 == PragmaInclude)) {
				alt3 = 1;
			}
			else if ((LA3_0 == PragmaLocale)) {
				alt3 = 2;
			}
			else {
				NoViableAltException nvae =
						new NoViableAltException("", 3, 0, input);

				throw nvae;
			}
			switch (alt3) {
				case 1:
				// com\\kyben\\translatecim\\TranslateCIM.g:393:7: PragmaInclude
				{
					PragmaInclude2 = (Token) match(input, PragmaInclude, FOLLOW_PragmaInclude_in_compilerDirective208);

					currentInputFileName = (PragmaInclude2 != null ? PragmaInclude2.getText() : null);
					// System.out.println("new current filename = " + currentInputFileName); // dbg

				}
					break;
				case 2:
				// com\\kyben\\translatecim\\TranslateCIM.g:398:7: PragmaLocale
				{
					match(input, PragmaLocale, FOLLOW_PragmaLocale_in_compilerDirective222);

				}
					break;

			}
			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "compilerDirective"

	public static class qualifierDeclaration_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifierDeclaration"
	// com\\kyben\\translatecim\\TranslateCIM.g:402:1: qualifierDeclaration : QUALIFIER qualifierName COLON qualifierType qualifierScopeList (qFl=
	// qualifierFlavorList )? SEMICOLON ;
	public final TranslateCIMParser.qualifierDeclaration_return qualifierDeclaration() throws RecognitionException {
		TranslateCIMParser.qualifierDeclaration_return retval = new TranslateCIMParser.qualifierDeclaration_return();
		retval.start = input.LT(1);

		TranslateCIMParser.qualifierFlavorList_return qFl = null;

		TranslateCIMParser.qualifierName_return qualifierName3 = null;

		TranslateCIMParser.qualifierType_return qualifierType4 = null;

		TranslateCIMParser.qualifierScopeList_return qualifierScopeList5 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:406:5: ( QUALIFIER qualifierName COLON qualifierType qualifierScopeList (qFl=
			// qualifierFlavorList )? SEMICOLON )
			// com\\kyben\\translatecim\\TranslateCIM.g:406:7: QUALIFIER qualifierName COLON qualifierType qualifierScopeList (qFl=
			// qualifierFlavorList )? SEMICOLON
			{
				match(input, QUALIFIER, FOLLOW_QUALIFIER_in_qualifierDeclaration243);
				pushFollow(FOLLOW_qualifierName_in_qualifierDeclaration245);
				qualifierName3 = qualifierName();

				state._fsp--;

				match(input, COLON, FOLLOW_COLON_in_qualifierDeclaration247);
				pushFollow(FOLLOW_qualifierType_in_qualifierDeclaration249);
				qualifierType4 = qualifierType();

				state._fsp--;

				pushFollow(FOLLOW_qualifierScopeList_in_qualifierDeclaration251);
				qualifierScopeList5 = qualifierScopeList();

				state._fsp--;

				// com\\kyben\\translatecim\\TranslateCIM.g:406:70: (qFl= qualifierFlavorList )?
				int alt4 = 2;
				int LA4_0 = input.LA(1);

				if ((LA4_0 == COMMA)) {
					alt4 = 1;
				}
				switch (alt4) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:406:72: qFl= qualifierFlavorList
					{
						pushFollow(FOLLOW_qualifierFlavorList_in_qualifierDeclaration257);
						qFl = qualifierFlavorList();

						state._fsp--;

					}
						break;

				}

				match(input, SEMICOLON, FOLLOW_SEMICOLON_in_qualifierDeclaration262);

				ArrayList<String> fl = (qFl == null) ? new ArrayList<String>() : qFl.fList;
				// System.out.println("qualifierDefinition: called, name = " +
				// (qualifierName3!=null?input.toString(qualifierName3.start,qualifierName3.stop):null)); // dbg
				qualifierDefinitions.put((qualifierName3 != null ? input.toString(qualifierName3.start, qualifierName3.stop) : null).toLowerCase(),
						new qualifierDefinition((qualifierName3 != null ? input.toString(qualifierName3.start, qualifierName3.stop) : null),
								(qualifierType4 != null ? qualifierType4.type : null),
								(qualifierType4 != null ? qualifierType4.isArray : false),
								(qualifierScopeList5 != null ? qualifierScopeList5.sList : null),
								fl));

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "qualifierDeclaration"

	public static class qualifierName_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifierName"
	// com\\kyben\\translatecim\\TranslateCIM.g:420:1: qualifierName : Identifier ;
	public final TranslateCIMParser.qualifierName_return qualifierName() throws RecognitionException {
		TranslateCIMParser.qualifierName_return retval = new TranslateCIMParser.qualifierName_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:421:5: ( Identifier )
			// com\\kyben\\translatecim\\TranslateCIM.g:421:7: Identifier
			{
				match(input, Identifier, FOLLOW_Identifier_in_qualifierName286);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "qualifierName"

	public static class qualifierType_return extends ParserRuleReturnScope {
		public String			type;
		public boolean			isArray;
		public String			defaultVal;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifierType"
	// com\\kyben\\translatecim\\TranslateCIM.g:425:1: qualifierType returns [String type, boolean isArray, String defaultVal] : dataType ( array )? (
	// EQUALS qi= qualifierInitializer )? ;
	public final TranslateCIMParser.qualifierType_return qualifierType() throws RecognitionException {
		TranslateCIMParser.qualifierType_return retval = new TranslateCIMParser.qualifierType_return();
		retval.start = input.LT(1);

		TranslateCIMParser.qualifierInitializer_return qi = null;

		TranslateCIMParser.dataType_return dataType6 = null;

		boolean qualifierIsArray = false;
		String defValue = "";
		// System.out.println("qualifierType: called");

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:431:5: ( dataType ( array )? ( EQUALS qi= qualifierInitializer )? )
			// com\\kyben\\translatecim\\TranslateCIM.g:431:7: dataType ( array )? ( EQUALS qi= qualifierInitializer )?
			{
				pushFollow(FOLLOW_dataType_in_qualifierType317);
				dataType6 = dataType();

				state._fsp--;

				// com\\kyben\\translatecim\\TranslateCIM.g:431:16: ( array )?
				int alt5 = 2;
				int LA5_0 = input.LA(1);

				if ((LA5_0 == LBRACK)) {
					alt5 = 1;
				}
				switch (alt5) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:431:17: array
					{
						pushFollow(FOLLOW_array_in_qualifierType320);
						array();

						state._fsp--;

						qualifierIsArray = true;

					}
						break;

				}

				// com\\kyben\\translatecim\\TranslateCIM.g:431:49: ( EQUALS qi= qualifierInitializer )?
				int alt6 = 2;
				int LA6_0 = input.LA(1);

				if ((LA6_0 == EQUALS)) {
					alt6 = 1;
				}
				switch (alt6) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:431:51: EQUALS qi= qualifierInitializer
					{
						match(input, EQUALS, FOLLOW_EQUALS_in_qualifierType327);
						pushFollow(FOLLOW_qualifierInitializer_in_qualifierType331);
						qi = qualifierInitializer();

						state._fsp--;

					}
						break;

				}

				retval.type = (dataType6 != null ? input.toString(dataType6.start, dataType6.stop) : null);
				retval.isArray = qualifierIsArray;
				// System.out.println("qualifierType: defValue = " + defValue);
				if (qi != null) {
					retval.defaultVal = (qi != null ? input.toString(qi.start, qi.stop) : null); // this is never used!!!!!!!!!!!!!!!!!!!!!
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "qualifierType"

	public static class qualifierScopeList_return extends ParserRuleReturnScope {
		public ArrayList<String>	sList;
		public StringTemplate		st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifierScopeList"
	// com\\kyben\\translatecim\\TranslateCIM.g:443:1: qualifierScopeList returns [ArrayList<String> sList] : COMMA SCOPE LPAREN m1= metaElement (
	// COMMA m2= metaElement )* RPAREN ;
	public final TranslateCIMParser.qualifierScopeList_return qualifierScopeList() throws RecognitionException {
		TranslateCIMParser.qualifierScopeList_return retval = new TranslateCIMParser.qualifierScopeList_return();
		retval.start = input.LT(1);

		TranslateCIMParser.metaElement_return m1 = null;

		TranslateCIMParser.metaElement_return m2 = null;

		retval.sList = new ArrayList<String>();
		// System.out.println("qualifierScopeList: called");

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:448:5: ( COMMA SCOPE LPAREN m1= metaElement ( COMMA m2= metaElement )* RPAREN )
			// com\\kyben\\translatecim\\TranslateCIM.g:448:7: COMMA SCOPE LPAREN m1= metaElement ( COMMA m2= metaElement )* RPAREN
			{
				match(input, COMMA, FOLLOW_COMMA_in_qualifierScopeList371);
				match(input, SCOPE, FOLLOW_SCOPE_in_qualifierScopeList373);
				match(input, LPAREN, FOLLOW_LPAREN_in_qualifierScopeList375);
				pushFollow(FOLLOW_metaElement_in_qualifierScopeList379);
				m1 = metaElement();

				state._fsp--;

				retval.sList.add((m1 != null ? input.toString(m1.start, m1.stop) : null));
				// com\\kyben\\translatecim\\TranslateCIM.g:449:19: ( COMMA m2= metaElement )*
				loop7: do {
					int alt7 = 2;
					int LA7_0 = input.LA(1);

					if ((LA7_0 == COMMA)) {
						alt7 = 1;
					}

					switch (alt7) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:449:20: COMMA m2= metaElement
						{
							match(input, COMMA, FOLLOW_COMMA_in_qualifierScopeList402);
							pushFollow(FOLLOW_metaElement_in_qualifierScopeList406);
							m2 = metaElement();

							state._fsp--;

							retval.sList.add((m2 != null ? input.toString(m2.start, m2.stop) : null));

						}
							break;

						default:
							break loop7;
					}
				} while (true);

				match(input, RPAREN, FOLLOW_RPAREN_in_qualifierScopeList412);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "qualifierScopeList"

	public static class metaElement_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "metaElement"
	// com\\kyben\\translatecim\\TranslateCIM.g:453:1: metaElement : ( ANY | CLASS | METHOD | PARAMETER | PROPERTY | REFERENCE | Identifier ) ;
	public final TranslateCIMParser.metaElement_return metaElement() throws RecognitionException {
		TranslateCIMParser.metaElement_return retval = new TranslateCIMParser.metaElement_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:454:5: ( ( ANY | CLASS | METHOD | PARAMETER | PROPERTY | REFERENCE | Identifier ) )
			// com\\kyben\\translatecim\\TranslateCIM.g:454:8: ( ANY | CLASS | METHOD | PARAMETER | PROPERTY | REFERENCE | Identifier )
			{
				if (input.LA(1) == Identifier || (input.LA(1) >= ANY && input.LA(1) <= REFERENCE)) {
					input.consume();
					state.errorRecovery = false;
				}
				else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					throw mse;
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "metaElement"

	public static class qualifierFlavorList_return extends ParserRuleReturnScope {
		public ArrayList		fList;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifierFlavorList"
	// com\\kyben\\translatecim\\TranslateCIM.g:465:1: qualifierFlavorList returns [ArrayList fList] : COMMA FLAVOR LPAREN f1= flavor ( COMMA f2=
	// flavor )* RPAREN ;
	public final TranslateCIMParser.qualifierFlavorList_return qualifierFlavorList() throws RecognitionException {
		TranslateCIMParser.qualifierFlavorList_return retval = new TranslateCIMParser.qualifierFlavorList_return();
		retval.start = input.LT(1);

		TranslateCIMParser.flavor_return f1 = null;

		TranslateCIMParser.flavor_return f2 = null;

		retval.fList = new ArrayList();
		// System.out.println("qualifierFlavorList: called");

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:470:5: ( COMMA FLAVOR LPAREN f1= flavor ( COMMA f2= flavor )* RPAREN )
			// com\\kyben\\translatecim\\TranslateCIM.g:470:7: COMMA FLAVOR LPAREN f1= flavor ( COMMA f2= flavor )* RPAREN
			{
				match(input, COMMA, FOLLOW_COMMA_in_qualifierFlavorList546);
				match(input, FLAVOR, FOLLOW_FLAVOR_in_qualifierFlavorList548);
				match(input, LPAREN, FOLLOW_LPAREN_in_qualifierFlavorList550);
				pushFollow(FOLLOW_flavor_in_qualifierFlavorList554);
				f1 = flavor();

				state._fsp--;

				retval.fList.add((f1 != null ? input.toString(f1.start, f1.stop) : null));
				// com\\kyben\\translatecim\\TranslateCIM.g:471:20: ( COMMA f2= flavor )*
				loop8: do {
					int alt8 = 2;
					int LA8_0 = input.LA(1);

					if ((LA8_0 == COMMA)) {
						alt8 = 1;
					}

					switch (alt8) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:471:21: COMMA f2= flavor
						{
							match(input, COMMA, FOLLOW_COMMA_in_qualifierFlavorList578);
							pushFollow(FOLLOW_flavor_in_qualifierFlavorList582);
							f2 = flavor();

							state._fsp--;

							retval.fList.add((f2 != null ? input.toString(f2.start, f2.stop) : null));

						}
							break;

						default:
							break loop8;
					}
				} while (true);

				match(input, RPAREN, FOLLOW_RPAREN_in_qualifierFlavorList589);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "qualifierFlavorList"

	public static class flavor_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "flavor"
	// com\\kyben\\translatecim\\TranslateCIM.g:475:1: flavor : ( ENABLEOVERRIDE | DISABLEOVERRIDE | TOSUBCLASS | RESTRICTED | TRANSLATABLE );
	public final TranslateCIMParser.flavor_return flavor() throws RecognitionException {
		TranslateCIMParser.flavor_return retval = new TranslateCIMParser.flavor_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:476:5: ( ENABLEOVERRIDE | DISABLEOVERRIDE | TOSUBCLASS | RESTRICTED | TRANSLATABLE )
			// com\\kyben\\translatecim\\TranslateCIM.g:
			{
				if ((input.LA(1) >= ENABLEOVERRIDE && input.LA(1) <= TRANSLATABLE)) {
					input.consume();
					state.errorRecovery = false;
				}
				else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					throw mse;
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "flavor"

	protected static class classDeclaration_scope {
		ArrayList	features;
	}

	protected Stack	classDeclaration_stack	= new Stack();

	public static class classDeclaration_return extends ParserRuleReturnScope {
		public String			className;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "classDeclaration"
	// com\\kyben\\translatecim\\TranslateCIM.g:484:1: classDeclaration returns [ String className ] : classDeclarationHeader classDeclarationTrailer
	// ->
	// classFileTemplate(TranslateCIMVersion=TCVersioncurrentDateTime=currentISO8601DateTimemajorCimVersion=cimMajorVersionminorCimVersion=cimMinorVersionsubPackage=incSubDirectorydeprecated=$classDeclarationHeader.qualifierHash.get(\"Deprecated\")classComment=javadocize(htmlize((String)$classDeclarationHeader.qualifierHash.get(\"Description\")))abstract=$classDeclarationHeader.qualifierHash.containsKey(\"Abstract\")cimClassName=$classDeclarationHeader.classNamemofSuperClass=superClassNamefeatures=$classDeclaration::featuresimports=importStatements);
	public final TranslateCIMParser.classDeclaration_return classDeclaration() throws RecognitionException {
		classDeclaration_stack.push(new classDeclaration_scope());
		TranslateCIMParser.classDeclaration_return retval = new TranslateCIMParser.classDeclaration_return();
		retval.start = input.LT(1);

		TranslateCIMParser.classDeclarationHeader_return classDeclarationHeader7 = null;

		TranslateCIMParser.classDeclarationTrailer_return classDeclarationTrailer8 = null;

		// System.out.println("classDeclaration: initializing"); // dbg
		((classDeclaration_scope) classDeclaration_stack.peek()).features = new ArrayList();

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:492:5: ( classDeclarationHeader classDeclarationTrailer ->
			// classFileTemplate(TranslateCIMVersion=TCVersioncurrentDateTime=currentISO8601DateTimemajorCimVersion=cimMajorVersionminorCimVersion=cimMinorVersionsubPackage=incSubDirectorydeprecated=$classDeclarationHeader.qualifierHash.get(\"Deprecated\")classComment=javadocize(htmlize((String)$classDeclarationHeader.qualifierHash.get(\"Description\")))abstract=$classDeclarationHeader.qualifierHash.containsKey(\"Abstract\")cimClassName=$classDeclarationHeader.classNamemofSuperClass=superClassNamefeatures=$classDeclaration::featuresimports=importStatements))
			// com\\kyben\\translatecim\\TranslateCIM.g:492:7: classDeclarationHeader classDeclarationTrailer
			{
				pushFollow(FOLLOW_classDeclarationHeader_in_classDeclaration686);
				classDeclarationHeader7 = classDeclarationHeader();

				state._fsp--;

				pushFollow(FOLLOW_classDeclarationTrailer_in_classDeclaration688);
				classDeclarationTrailer8 = classDeclarationTrailer();

				state._fsp--;

				if ((classDeclarationHeader7 != null ? classDeclarationHeader7.className : null) == null) { // this means
																											// "if it's deprecated and we're skipping deprecated classes"
					return retval;
				}
				String superClassName = null;
				if ((classDeclarationTrailer8 != null ? classDeclarationTrailer8.superClassToken : null) != null) {
					checkClassName((classDeclarationTrailer8 != null ? classDeclarationTrailer8.superClassToken : null));
					superClassName = (classDeclarationTrailer8 != null ? classDeclarationTrailer8.superClassToken : null).getText();
				}
				retval.className = (classDeclarationHeader7 != null ? classDeclarationHeader7.className : null);
				ArrayList<String> importStatements = new ArrayList<String>();
				for (String it : accessedSubDirs) {
					StringTemplate iStatement = templateLib.getInstanceOf("importTemplate", new STAttrMap().put("subDirectory", it));
					importStatements.add(iStatement.toString());
				}
				// System.out.println("classDeclaration: finishing, abstract = " +
				// (classDeclarationHeader7!=null?classDeclarationHeader7.qualifierHash:null).containsKey("Abstract"));
				// System.out.println("classDeclaration: finishing, className = " +
				// (classDeclarationHeader7!=null?classDeclarationHeader7.className:null));

				// TEMPLATE REWRITE
				// 511:5: ->
				// classFileTemplate(TranslateCIMVersion=TCVersioncurrentDateTime=currentISO8601DateTimemajorCimVersion=cimMajorVersionminorCimVersion=cimMinorVersionsubPackage=incSubDirectorydeprecated=$classDeclarationHeader.qualifierHash.get(\"Deprecated\")classComment=javadocize(htmlize((String)$classDeclarationHeader.qualifierHash.get(\"Description\")))abstract=$classDeclarationHeader.qualifierHash.containsKey(\"Abstract\")cimClassName=$classDeclarationHeader.classNamemofSuperClass=superClassNamefeatures=$classDeclaration::featuresimports=importStatements)
				{
					retval.st = templateLib
							.getInstanceOf(
									"classFileTemplate",
									new STAttrMap()
											.put("TranslateCIMVersion", TCVersion)
											.put("currentDateTime", currentISO8601DateTime)
											.put("majorCimVersion", cimMajorVersion)
											.put("minorCimVersion", cimMinorVersion)
											.put("subPackage", incSubDirectory)
											.put("deprecated",
													(classDeclarationHeader7 != null ? classDeclarationHeader7.qualifierHash : null)
															.get("Deprecated"))
											.put("classComment",
													javadocize(htmlize((String) (classDeclarationHeader7 != null ? classDeclarationHeader7.qualifierHash : null)
															.get("Description"))))
											.put("abstract",
													(classDeclarationHeader7 != null ? classDeclarationHeader7.qualifierHash : null)
															.containsKey("Abstract"))
											.put("cimClassName", (classDeclarationHeader7 != null ? classDeclarationHeader7.className : null))
											.put("mofSuperClass", superClassName)
											.put("features", ((classDeclaration_scope) classDeclaration_stack.peek()).features)
											.put("imports", importStatements));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
			classDeclaration_stack.pop();
		}
		return retval;
	}

	// $ANTLR end "classDeclaration"

	public static class classDeclarationHeader_return extends ParserRuleReturnScope {
		public HashMap			qualifierHash;
		public String			className;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "classDeclarationHeader"
	// com\\kyben\\translatecim\\TranslateCIM.g:527:1: classDeclarationHeader returns [ HashMap qualifierHash, String className] : cq= qualifierList
	// CLASS className ;
	public final TranslateCIMParser.classDeclarationHeader_return classDeclarationHeader() throws RecognitionException {
		TranslateCIMParser.classDeclarationHeader_return retval = new TranslateCIMParser.classDeclarationHeader_return();
		retval.start = input.LT(1);

		TranslateCIMParser.qualifierList_return cq = null;

		TranslateCIMParser.className_return className9 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:528:5: (cq= qualifierList CLASS className )
			// com\\kyben\\translatecim\\TranslateCIM.g:528:7: cq= qualifierList CLASS className
			{
				pushFollow(FOLLOW_qualifierList_in_classDeclarationHeader1182);
				cq = qualifierList();

				state._fsp--;

				match(input, CLASS, FOLLOW_CLASS_in_classDeclarationHeader1184);
				pushFollow(FOLLOW_className_in_classDeclarationHeader1186);
				className9 = className();

				state._fsp--;

				// System.out.println("classDeclarationHeader: called, className = \"" +
				// (className9!=null?input.toString(className9.start,className9.stop):null) + "\"");
				// printKeys((cq!=null?cq.qualifierHash:null));
				// printValues((cq!=null?cq.qualifierHash:null));
				if ((cq != null ? cq.qualifierHash : null).containsKey("Deprecated") && noDeprecated) {
					return retval; // return with className = null
				}
				retval.className = (className9 != null ? input.toString(className9.start, className9.stop) : null);
				retval.qualifierHash = (cq != null ? cq.qualifierHash : null);
				// Get the subdirectory name and check that the output file name matches the class name.
				File incFile = new File(currentInputFileName);
				incSubDirectory = incFile.getParent();
				incSubDirectory = incSubDirectory != null ? incSubDirectory.toLowerCase() : incSubDirectory;
				// System.out.println("classDeclarationHeader: called, incSubDirectory = \"" + incSubDirectory + "\"");
				String incBaseName = incFile.getName();
				// System.out.println("classDeclarationHeader: incBaseName = \"" + incBaseName + "\"");
				if (!incBaseName.startsWith((className9 != null ? input.toString(className9.start, className9.stop) : null))) {
					RecognitionException r = new RecognitionException(input);
					throw new ClassNameDoesntMatchFileNameException(
							currentInputFileName + "(" + r.line + ":" + r.charPositionInLine + ")" +
									": class name " + retval.className + " doesn't match file name");
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "classDeclarationHeader"

	public static class classDeclarationTrailer_return extends ParserRuleReturnScope {
		public Token			superClassToken;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "classDeclarationTrailer"
	// com\\kyben\\translatecim\\TranslateCIM.g:554:1: classDeclarationTrailer returns [ Token superClassToken ] : ( alias )? (scl= superClass )?
	// LCURLY ( classFeature )* RCURLY SEMICOLON ;
	public final TranslateCIMParser.classDeclarationTrailer_return classDeclarationTrailer() throws RecognitionException {
		TranslateCIMParser.classDeclarationTrailer_return retval = new TranslateCIMParser.classDeclarationTrailer_return();
		retval.start = input.LT(1);

		TranslateCIMParser.superClass_return scl = null;

		// System.out.println("classDeclarationTrailer: initializing");
		accessedSubDirs = new HashSet<String>(); // replace any old one with a new one !

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:559:5: ( ( alias )? (scl= superClass )? LCURLY ( classFeature )* RCURLY SEMICOLON )
			// com\\kyben\\translatecim\\TranslateCIM.g:559:7: ( alias )? (scl= superClass )? LCURLY ( classFeature )* RCURLY SEMICOLON
			{
				// com\\kyben\\translatecim\\TranslateCIM.g:559:7: ( alias )?
				int alt9 = 2;
				int LA9_0 = input.LA(1);

				if ((LA9_0 == AS)) {
					alt9 = 1;
				}
				switch (alt9) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:559:7: alias
					{
						pushFollow(FOLLOW_alias_in_classDeclarationTrailer1223);
						alias();

						state._fsp--;

					}
						break;

				}

				// com\\kyben\\translatecim\\TranslateCIM.g:559:14: (scl= superClass )?
				int alt10 = 2;
				int LA10_0 = input.LA(1);

				if ((LA10_0 == COLON)) {
					alt10 = 1;
				}
				switch (alt10) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:559:16: scl= superClass
					{
						pushFollow(FOLLOW_superClass_in_classDeclarationTrailer1230);
						scl = superClass();

						state._fsp--;

					}
						break;

				}

				match(input, LCURLY, FOLLOW_LCURLY_in_classDeclarationTrailer1235);
				// com\\kyben\\translatecim\\TranslateCIM.g:559:41: ( classFeature )*
				loop11: do {
					int alt11 = 2;
					int LA11_0 = input.LA(1);

					if ((LA11_0 == LBRACK)) {
						alt11 = 1;
					}

					switch (alt11) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:559:41: classFeature
						{
							pushFollow(FOLLOW_classFeature_in_classDeclarationTrailer1237);
							classFeature();

							state._fsp--;

						}
							break;

						default:
							break loop11;
					}
				} while (true);

				match(input, RCURLY, FOLLOW_RCURLY_in_classDeclarationTrailer1240);
				match(input, SEMICOLON, FOLLOW_SEMICOLON_in_classDeclarationTrailer1242);

				if (scl != null) {
					retval.superClassToken = (scl != null ? scl.token : null);
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "classDeclarationTrailer"

	public static class className_return extends ParserRuleReturnScope {
		public Token			token;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "className"
	// com\\kyben\\translatecim\\TranslateCIM.g:568:1: className returns [ Token token ] : Identifier ;
	public final TranslateCIMParser.className_return className() throws RecognitionException {
		TranslateCIMParser.className_return retval = new TranslateCIMParser.className_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:569:5: ( Identifier )
			// com\\kyben\\translatecim\\TranslateCIM.g:569:7: Identifier
			{
				match(input, Identifier, FOLLOW_Identifier_in_className1270);

				retval.token = input.LT(-1);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "className"

	public static class alias_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "alias"
	// com\\kyben\\translatecim\\TranslateCIM.g:576:1: alias : AS aliasIdentifier ;
	public final TranslateCIMParser.alias_return alias() throws RecognitionException {
		TranslateCIMParser.alias_return retval = new TranslateCIMParser.alias_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:577:5: ( AS aliasIdentifier )
			// com\\kyben\\translatecim\\TranslateCIM.g:577:7: AS aliasIdentifier
			{
				match(input, AS, FOLLOW_AS_in_alias1294);
				pushFollow(FOLLOW_aliasIdentifier_in_alias1296);
				aliasIdentifier();

				state._fsp--;

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "alias"

	public static class aliasIdentifier_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "aliasIdentifier"
	// com\\kyben\\translatecim\\TranslateCIM.g:581:1: aliasIdentifier : DOLLAR Identifier ;
	public final TranslateCIMParser.aliasIdentifier_return aliasIdentifier() throws RecognitionException {
		TranslateCIMParser.aliasIdentifier_return retval = new TranslateCIMParser.aliasIdentifier_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:582:5: ( DOLLAR Identifier )
			// com\\kyben\\translatecim\\TranslateCIM.g:582:7: DOLLAR Identifier
			{
				match(input, DOLLAR, FOLLOW_DOLLAR_in_aliasIdentifier1314);
				match(input, Identifier, FOLLOW_Identifier_in_aliasIdentifier1316);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "aliasIdentifier"

	public static class superClass_return extends ParserRuleReturnScope {
		public Token			token;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "superClass"
	// com\\kyben\\translatecim\\TranslateCIM.g:586:1: superClass returns [ Token token ] : COLON className ;
	public final TranslateCIMParser.superClass_return superClass() throws RecognitionException {
		TranslateCIMParser.superClass_return retval = new TranslateCIMParser.superClass_return();
		retval.start = input.LT(1);

		TranslateCIMParser.className_return className10 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:587:5: ( COLON className )
			// com\\kyben\\translatecim\\TranslateCIM.g:587:7: COLON className
			{
				match(input, COLON, FOLLOW_COLON_in_superClass1338);
				pushFollow(FOLLOW_className_in_superClass1340);
				className10 = className();

				state._fsp--;

				retval.token = (className10 != null ? className10.token : null);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "superClass"

	public static class classFeature_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "classFeature"
	// com\\kyben\\translatecim\\TranslateCIM.g:594:1: classFeature : classFeaturePiece ;
	public final TranslateCIMParser.classFeature_return classFeature() throws RecognitionException {
		TranslateCIMParser.classFeature_return retval = new TranslateCIMParser.classFeature_return();
		retval.start = input.LT(1);

		TranslateCIMParser.classFeaturePiece_return classFeaturePiece11 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:598:5: ( classFeaturePiece )
			// com\\kyben\\translatecim\\TranslateCIM.g:598:7: classFeaturePiece
			{
				pushFollow(FOLLOW_classFeaturePiece_in_classFeature1367);
				classFeaturePiece11 = classFeaturePiece();

				state._fsp--;

				// System.out.println("classFeature: called");
				if ((classFeaturePiece11 != null ? classFeaturePiece11.st : null) != null) {
					((classDeclaration_scope) classDeclaration_stack.peek()).features
							.add((classFeaturePiece11 != null ? classFeaturePiece11.st : null));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "classFeature"

	protected static class classFeaturePiece_scope {
		HashMap	featureQualifierHash;
	}

	protected Stack	classFeaturePiece_stack	= new Stack();

	public static class classFeaturePiece_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "classFeaturePiece"
	// com\\kyben\\translatecim\\TranslateCIM.g:608:1: classFeaturePiece : qualifierList featureBodyDeclaration ->
	// classFeatureTemplate(comment=javadocize(htmlize((String)$qualifierList.qualifierHash.get(\"Description\")))deprecated=$qualifierList.qualifierHash.get(\"Deprecated\")featureBody=$featureBodyDeclaration.strng);
	public final TranslateCIMParser.classFeaturePiece_return classFeaturePiece() throws RecognitionException {
		classFeaturePiece_stack.push(new classFeaturePiece_scope());
		TranslateCIMParser.classFeaturePiece_return retval = new TranslateCIMParser.classFeaturePiece_return();
		retval.start = input.LT(1);

		TranslateCIMParser.qualifierList_return qualifierList12 = null;

		TranslateCIMParser.featureBodyDeclaration_return featureBodyDeclaration13 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:615:5: ( qualifierList featureBodyDeclaration ->
			// classFeatureTemplate(comment=javadocize(htmlize((String)$qualifierList.qualifierHash.get(\"Description\")))deprecated=$qualifierList.qualifierHash.get(\"Deprecated\")featureBody=$featureBodyDeclaration.strng))
			// com\\kyben\\translatecim\\TranslateCIM.g:615:7: qualifierList featureBodyDeclaration
			{
				pushFollow(FOLLOW_qualifierList_in_classFeaturePiece1402);
				qualifierList12 = qualifierList();

				state._fsp--;

				((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash = (qualifierList12 != null ? qualifierList12.qualifierHash : null);
				pushFollow(FOLLOW_featureBodyDeclaration_in_classFeaturePiece1406);
				featureBodyDeclaration13 = featureBodyDeclaration();

				state._fsp--;

				// TEMPLATE REWRITE
				// 616:5: ->
				// classFeatureTemplate(comment=javadocize(htmlize((String)$qualifierList.qualifierHash.get(\"Description\")))deprecated=$qualifierList.qualifierHash.get(\"Deprecated\")featureBody=$featureBodyDeclaration.strng)
				{
					retval.st = templateLib.getInstanceOf(
							"classFeatureTemplate",
							new STAttrMap()
									.put("comment",
											javadocize(htmlize((String) (qualifierList12 != null ? qualifierList12.qualifierHash : null)
													.get("Description"))))
									.put("deprecated", (qualifierList12 != null ? qualifierList12.qualifierHash : null).get("Deprecated"))
									.put("featureBody", (featureBodyDeclaration13 != null ? featureBodyDeclaration13.strng : null)));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
			classFeaturePiece_stack.pop();
		}
		return retval;
	}

	// $ANTLR end "classFeaturePiece"

	public static class featureBodyDeclaration_return extends ParserRuleReturnScope {
		public String			strng;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "featureBodyDeclaration"
	// com\\kyben\\translatecim\\TranslateCIM.g:622:1: featureBodyDeclaration returns [ String strng ] : ( dataPropertyDeclaration |
	// referencePropertyDeclaration | methodDeclaration );
	public final TranslateCIMParser.featureBodyDeclaration_return featureBodyDeclaration() throws RecognitionException {
		TranslateCIMParser.featureBodyDeclaration_return retval = new TranslateCIMParser.featureBodyDeclaration_return();
		retval.start = input.LT(1);

		TranslateCIMParser.dataPropertyDeclaration_return dataPropertyDeclaration14 = null;

		TranslateCIMParser.referencePropertyDeclaration_return referencePropertyDeclaration15 = null;

		TranslateCIMParser.methodDeclaration_return methodDeclaration16 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:626:5: ( dataPropertyDeclaration | referencePropertyDeclaration | methodDeclaration )
			int alt12 = 3;
			alt12 = dfa12.predict(input);
			switch (alt12) {
				case 1:
				// com\\kyben\\translatecim\\TranslateCIM.g:626:7: dataPropertyDeclaration
				{
					pushFollow(FOLLOW_dataPropertyDeclaration_in_featureBodyDeclaration1521);
					dataPropertyDeclaration14 = dataPropertyDeclaration();

					state._fsp--;

					if ((dataPropertyDeclaration14 != null ? dataPropertyDeclaration14.st : null) != null) {
						retval.strng = (dataPropertyDeclaration14 != null ? dataPropertyDeclaration14.st : null).toString();
					}

				}
					break;
				case 2:
				// com\\kyben\\translatecim\\TranslateCIM.g:632:7: referencePropertyDeclaration
				{
					pushFollow(FOLLOW_referencePropertyDeclaration_in_featureBodyDeclaration1535);
					referencePropertyDeclaration15 = referencePropertyDeclaration();

					state._fsp--;

					if ((referencePropertyDeclaration15 != null ? referencePropertyDeclaration15.st : null) != null) {
						retval.strng = (referencePropertyDeclaration15 != null ? referencePropertyDeclaration15.st : null).toString();
					}

				}
					break;
				case 3:
				// com\\kyben\\translatecim\\TranslateCIM.g:638:7: methodDeclaration
				{
					pushFollow(FOLLOW_methodDeclaration_in_featureBodyDeclaration1549);
					methodDeclaration16 = methodDeclaration();

					state._fsp--;

					if ((methodDeclaration16 != null ? methodDeclaration16.st : null) != null) {
						retval.strng = (methodDeclaration16 != null ? methodDeclaration16.st : null).toString();
					}

				}
					break;

			}
			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "featureBodyDeclaration"

	protected static class dataPropertyDeclaration_scope {
		ArrayList<String>	constantNames;
	}

	protected Stack	dataPropertyDeclaration_stack	= new Stack();

	public static class dataPropertyDeclaration_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "dataPropertyDeclaration"
	// com\\kyben\\translatecim\\TranslateCIM.g:647:1: dataPropertyDeclaration : dataType nonReservedName (arr= array )? ( EQUALS dV= initializer )?
	// SEMICOLON ->
	// classDataProperty(enumerable=enumerabledeprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$dataType.stisArray=arr!=nullname=$nonReservedName.nrStringinitializerFrag=initializerFragment.toString()valuesLineList=classPropertyValuesLineList.toString()valuesComments=classPropertyValuesComments);
	public final TranslateCIMParser.dataPropertyDeclaration_return dataPropertyDeclaration() throws RecognitionException {
		dataPropertyDeclaration_stack.push(new dataPropertyDeclaration_scope());
		TranslateCIMParser.dataPropertyDeclaration_return retval = new TranslateCIMParser.dataPropertyDeclaration_return();
		retval.start = input.LT(1);

		TranslateCIMParser.array_return arr = null;

		TranslateCIMParser.initializer_return dV = null;

		TranslateCIMParser.nonReservedName_return nonReservedName17 = null;

		TranslateCIMParser.dataType_return dataType18 = null;

		// System.out.println("dataPropertyDeclaration: initializing"); // dbg
		boolean isArray = false;
		String classPropertyText = null;
		((dataPropertyDeclaration_scope) dataPropertyDeclaration_stack.peek()).constantNames = new ArrayList<String>();

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:657:5: ( dataType nonReservedName (arr= array )? ( EQUALS dV= initializer )? SEMICOLON ->
			// classDataProperty(enumerable=enumerabledeprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$dataType.stisArray=arr!=nullname=$nonReservedName.nrStringinitializerFrag=initializerFragment.toString()valuesLineList=classPropertyValuesLineList.toString()valuesComments=classPropertyValuesComments))
			// com\\kyben\\translatecim\\TranslateCIM.g:657:7: dataType nonReservedName (arr= array )? ( EQUALS dV= initializer )? SEMICOLON
			{
				pushFollow(FOLLOW_dataType_in_dataPropertyDeclaration1590);
				dataType18 = dataType();

				state._fsp--;

				pushFollow(FOLLOW_nonReservedName_in_dataPropertyDeclaration1592);

				boolean enumerable = true;
				if (noEnums ||
						!((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash.containsKey("Values") ||
						(!(dataType18 != null ? input.toString(dataType18.start, dataType18.stop) : null).equals("uint8") &&
								!(dataType18 != null ? input.toString(dataType18.start, dataType18.stop) : null).equals("sint8") &&
								!(dataType18 != null ? input.toString(dataType18.start, dataType18.stop) : null).equals("uint16") &&
								!(dataType18 != null ? input.toString(dataType18.start, dataType18.stop) : null).equals("sint16") &&
								!(dataType18 != null ? input.toString(dataType18.start, dataType18.stop) : null).equals("uint32") &&
								!(dataType18 != null ? input.toString(dataType18.start, dataType18.stop) : null).equals("sint32") &&
						!(dataType18 != null ? input.toString(dataType18.start, dataType18.stop) : null).equals("string"))) {
					enumerable = false;
				}

				nonReservedName17 = nonReservedName();

				state._fsp--;

				// com\\kyben\\translatecim\\TranslateCIM.g:657:32: (arr= array )?
				int alt13 = 2;
				int LA13_0 = input.LA(1);

				if ((LA13_0 == LBRACK)) {
					alt13 = 1;
				}
				switch (alt13) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:657:34: arr= array
					{
						pushFollow(FOLLOW_array_in_dataPropertyDeclaration1598);
						arr = array();

						state._fsp--;

					}
						break;

				}

				// com\\kyben\\translatecim\\TranslateCIM.g:657:47: ( EQUALS dV= initializer )?
				int alt14 = 2;
				int LA14_0 = input.LA(1);

				if ((LA14_0 == EQUALS)) {
					alt14 = 1;
				}
				switch (alt14) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:657:48: EQUALS dV= initializer
					{
						match(input, EQUALS, FOLLOW_EQUALS_in_dataPropertyDeclaration1604);
						pushFollow(FOLLOW_initializer_in_dataPropertyDeclaration1608);
						dV = initializer();

						state._fsp--;

					}
						break;

				}

				match(input, SEMICOLON, FOLLOW_SEMICOLON_in_dataPropertyDeclaration1613);

				// System.out.println("dataPropertyDeclaration: property = \"" + (nonReservedName17!=null?nonReservedName17.nrString:null) + "\""); //
				// dbg
				if (((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash.containsKey("Deprecated") && noDeprecated) {
					return retval; // return without setting dataPropertyDeclaration.st
				}

				ArrayList<String> classPropertyValuesLines = new ArrayList<String>();
				ArrayList<String> classPropertyValuesComments = new ArrayList<String>();
				StringTemplate classPropertyValuesLineList = templates.getInstanceOf("classPropertyValuesLineList");
				ArrayList<String> namedInitializers = new ArrayList<String>();
				if (((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash.containsKey("Values")) {
					if (!((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash.containsKey("ValueMap")) {
						RecognitionException r = new RecognitionException(input);
						throw new ValuesButNoValueMapException(
								currentInputFileName + "(" + r.line + ":" + r.charPositionInLine + ")" +
										": property " + (nonReservedName17 != null ? nonReservedName17.nrString : null) + " has a Values qualifier but no ValueMap qualifier");
					}

					ArrayList<String> valueMapList = (ArrayList) ((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash
							.get("ValueMap");
					ArrayList<String> valuesList = (ArrayList) ((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash
							.get("Values");
					if (valueMapList.size() != valuesList.size()) {
						RecognitionException r = new RecognitionException(input);
						throw new ValuesDontMatchValueMapException(
								currentInputFileName + "(" + r.line + ":" + r.charPositionInLine + ")" +
										": property " + (nonReservedName17 != null ? nonReservedName17.nrString : null) + " has Values and ValueMaps qualifiers with differing numbers of elements");
					}

					HashSet<String> uniqueValueNames = new HashSet<String>();
					for (int i = 0; i < valuesList.size(); i++) {
						String valueName = makeUnique(canonicalizeValue(valuesList.get(i)), uniqueValueNames);
						String valueMap = valueMapList.get(i);
						if (valueName.endsWith("_Reserved") ||
								valueMap.contains("..")) {
							StringTemplate classPropertyValuesComment = templates.getInstanceOf("classPropertyValuesComment");
							classPropertyValuesComment.setAttribute("enumerable", enumerable);
							classPropertyValuesComment.setAttribute("constantName", valueName);
							classPropertyValuesComment.setAttribute("constantValue", valueMap);
							classPropertyValuesComments.add(classPropertyValuesComment.toString());
						} else {
							if (enumerable) {
								if (javaKeywords.contains(valueName)) {
									valueName = "cim_" + valueName;
								} else {
									char first = valueName.charAt(0);
									if ((first >= '0') && (first <= '9')) {
										valueName = "cim_" + valueName;
									}
								}
							} else {
								valueName = (nonReservedName17 != null ? nonReservedName17.nrString : null).toUpperCase() + '_' + valueName;
							}
							if ((dataType18 != null ? dataType18.st : null).toString().equals("String")) {
								valueMap = "\"" + valueMap + "\"";
							}
							StringTemplate classPropertyValuesLine = templates.getInstanceOf("classPropertyValuesLine");
							classPropertyValuesLine.setAttribute("enumerable", enumerable);
							classPropertyValuesLine.setAttribute("type", (dataType18 != null ? dataType18.st : null));
							classPropertyValuesLine.setAttribute("constantName", valueName);
							classPropertyValuesLine.setAttribute("constantValue", valueMap);
							classPropertyValuesLines.add(classPropertyValuesLine.toString());
						}
					}

					classPropertyValuesLineList.setAttribute("enumerable", enumerable);
					classPropertyValuesLineList.setAttribute("valuesLines", classPropertyValuesLines);

					if (dV != null) {
						for (int z = 0; z < dV.initializerList.size(); z++) {
							String initialValue = dV.initializerList.get(z);
							if (((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash.containsKey("Values") && enumerable) {
								// lookup the name of the value here.
								for (int i = 0; i < valueMapList.size(); i++) {
									if (valueMapList.get(i).equals(initialValue)) {
										namedInitializers.add(canonicalizeValue(valuesList.get(i)));
										break;
									}
								}
							}
						}
					}
				} else {
					if (dV != null) {
						namedInitializers.add(dV.initializerList.get(0));
					}
				}

				// turn namedInitializers into an initializerFragment
				StringTemplate initializerFragment = templates.getInstanceOf("initializerFragment");
				initializerFragment.setAttribute("enumerable", enumerable);
				initializerFragment.setAttribute("isArray", (arr != null));
				initializerFragment.setAttribute("name", (nonReservedName17 != null ? nonReservedName17.nrString : null));
				initializerFragment.setAttribute("initializerPieces", namedInitializers);

				if (((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash.containsKey("ArrayType")) {
					if (arr == null) {
						RecognitionException r = new RecognitionException(input);
						throw new ArrayTypeOnNonArrayException(
								currentInputFileName + "(" + r.line + ":" + r.charPositionInLine + ")" +
										": property " + (nonReservedName17 != null ? nonReservedName17.nrString : null) + " has the ArrayType qualifier, but isn't an array");
					}
				}

				// TEMPLATE REWRITE
				// 773:5: ->
				// classDataProperty(enumerable=enumerabledeprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$dataType.stisArray=arr!=nullname=$nonReservedName.nrStringinitializerFrag=initializerFragment.toString()valuesLineList=classPropertyValuesLineList.toString()valuesComments=classPropertyValuesComments)
				{
					retval.st = templateLib
							.getInstanceOf(
									"classDataProperty",
									new STAttrMap()
											.put("enumerable", enumerable)
											.put("deprecated",
													((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash
															.containsKey("Deprecated")).put("type", (dataType18 != null ? dataType18.st : null))
											.put("isArray", arr != null).put("name", (nonReservedName17 != null ? nonReservedName17.nrString : null))
											.put("initializerFrag", initializerFragment.toString())
											.put("valuesLineList", classPropertyValuesLineList.toString())
											.put("valuesComments", classPropertyValuesComments));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
			dataPropertyDeclaration_stack.pop();
		}
		return retval;
	}

	// $ANTLR end "dataPropertyDeclaration"

	public static class referencePropertyDeclaration_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "referencePropertyDeclaration"
	// com\\kyben\\translatecim\\TranslateCIM.g:784:1: referencePropertyDeclaration : className REF nonReservedName (arr= array )? SEMICOLON ->
	// classReferencePropertyTemplate(deprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$className.textisArray=arr!=nullname=$nonReservedName.nrString);
	public final TranslateCIMParser.referencePropertyDeclaration_return referencePropertyDeclaration() throws RecognitionException {
		TranslateCIMParser.referencePropertyDeclaration_return retval = new TranslateCIMParser.referencePropertyDeclaration_return();
		retval.start = input.LT(1);

		TranslateCIMParser.array_return arr = null;

		TranslateCIMParser.className_return className19 = null;

		TranslateCIMParser.nonReservedName_return nonReservedName20 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:785:5: ( className REF nonReservedName (arr= array )? SEMICOLON ->
			// classReferencePropertyTemplate(deprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$className.textisArray=arr!=nullname=$nonReservedName.nrString))
			// com\\kyben\\translatecim\\TranslateCIM.g:785:7: className REF nonReservedName (arr= array )? SEMICOLON
			{
				pushFollow(FOLLOW_className_in_referencePropertyDeclaration1938);
				className19 = className();

				state._fsp--;

				match(input, REF, FOLLOW_REF_in_referencePropertyDeclaration1940);
				pushFollow(FOLLOW_nonReservedName_in_referencePropertyDeclaration1942);
				nonReservedName20 = nonReservedName();

				state._fsp--;

				// com\\kyben\\translatecim\\TranslateCIM.g:785:37: (arr= array )?
				int alt15 = 2;
				int LA15_0 = input.LA(1);

				if ((LA15_0 == LBRACK)) {
					alt15 = 1;
				}
				switch (alt15) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:785:39: arr= array
					{
						pushFollow(FOLLOW_array_in_referencePropertyDeclaration1948);
						arr = array();

						state._fsp--;

					}
						break;

				}

				match(input, SEMICOLON, FOLLOW_SEMICOLON_in_referencePropertyDeclaration1953);

				if (((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash.containsKey("Deprecated") && noDeprecated) {
					return retval; // return, skipping the setting of referencePropertyDeclaration.st
				}
				checkClassName((className19 != null ? className19.token : null));

				// TEMPLATE REWRITE
				// 792:5: ->
				// classReferencePropertyTemplate(deprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$className.textisArray=arr!=nullname=$nonReservedName.nrString)
				{
					retval.st = templateLib
							.getInstanceOf(
									"classReferencePropertyTemplate",
									new STAttrMap()
											.put("deprecated",
													((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash
															.containsKey("Deprecated"))
											.put("type", (className19 != null ? input.toString(className19.start, className19.stop) : null))
											.put("isArray", arr != null).put("name", (nonReservedName20 != null ? nonReservedName20.nrString : null)));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "referencePropertyDeclaration"

	protected static class methodDeclaration_scope {
		boolean	thisMethodIsDeprecated;
	}

	protected Stack	methodDeclaration_stack	= new Stack();

	public static class methodDeclaration_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "methodDeclaration"
	// com\\kyben\\translatecim\\TranslateCIM.g:799:1: methodDeclaration : dataType methodName LPAREN ( methodParameterList )? RPAREN SEMICOLON ->
	// classMethodTemplate(deprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$dataType.stname=$methodName.textparameters=mParmListbogusReturnValue=$dataType.defaultVal);
	public final TranslateCIMParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
		methodDeclaration_stack.push(new methodDeclaration_scope());
		TranslateCIMParser.methodDeclaration_return retval = new TranslateCIMParser.methodDeclaration_return();
		retval.start = input.LT(1);

		TranslateCIMParser.methodParameterList_return methodParameterList21 = null;

		TranslateCIMParser.dataType_return dataType22 = null;

		TranslateCIMParser.methodName_return methodName23 = null;

		boolean mpexists = false;
		StringTemplate mParmList = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:807:5: ( dataType methodName LPAREN ( methodParameterList )? RPAREN SEMICOLON ->
			// classMethodTemplate(deprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$dataType.stname=$methodName.textparameters=mParmListbogusReturnValue=$dataType.defaultVal))
			// com\\kyben\\translatecim\\TranslateCIM.g:807:7: dataType methodName LPAREN ( methodParameterList )? RPAREN SEMICOLON
			{
				pushFollow(FOLLOW_dataType_in_methodDeclaration2163);
				dataType22 = dataType();

				state._fsp--;

				pushFollow(FOLLOW_methodName_in_methodDeclaration2165);
				methodName23 = methodName();

				state._fsp--;

				match(input, LPAREN, FOLLOW_LPAREN_in_methodDeclaration2167);
				// com\\kyben\\translatecim\\TranslateCIM.g:807:34: ( methodParameterList )?
				int alt16 = 2;
				int LA16_0 = input.LA(1);

				if ((LA16_0 == Identifier || LA16_0 == LBRACK || (LA16_0 >= BOOLEAN && LA16_0 <= UINT8))) {
					alt16 = 1;
				}
				switch (alt16) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:807:35: methodParameterList
					{
						pushFollow(FOLLOW_methodParameterList_in_methodDeclaration2170);
						methodParameterList21 = methodParameterList();

						state._fsp--;

						mpexists = true;

					}
						break;

				}

				match(input, RPAREN, FOLLOW_RPAREN_in_methodDeclaration2175);
				match(input, SEMICOLON, FOLLOW_SEMICOLON_in_methodDeclaration2177);

				if (((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash.containsKey("Deprecated") && noDeprecated) {
					return retval; // return, skipping the setting of methodDeclaration.st
				}
				if (mpexists) {
					mParmList = (methodParameterList21 != null ? methodParameterList21.st : null);
				}

				// TEMPLATE REWRITE
				// 816:5: ->
				// classMethodTemplate(deprecated=$classFeaturePiece::featureQualifierHash.containsKey(\"Deprecated\")type=$dataType.stname=$methodName.textparameters=mParmListbogusReturnValue=$dataType.defaultVal)
				{
					retval.st = templateLib
							.getInstanceOf(
									"classMethodTemplate",
									new STAttrMap()
											.put("deprecated",
													((classFeaturePiece_scope) classFeaturePiece_stack.peek()).featureQualifierHash
															.containsKey("Deprecated")).put("type", (dataType22 != null ? dataType22.st : null))
											.put("name", (methodName23 != null ? input.toString(methodName23.start, methodName23.stop) : null))
											.put("parameters", mParmList)
											.put("bogusReturnValue", (dataType22 != null ? dataType22.defaultVal : null)));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
			methodDeclaration_stack.pop();
		}
		return retval;
	}

	// $ANTLR end "methodDeclaration"

	public static class methodName_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "methodName"
	// com\\kyben\\translatecim\\TranslateCIM.g:824:1: methodName : Identifier ;
	public final TranslateCIMParser.methodName_return methodName() throws RecognitionException {
		TranslateCIMParser.methodName_return retval = new TranslateCIMParser.methodName_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:825:5: ( Identifier )
			// com\\kyben\\translatecim\\TranslateCIM.g:825:7: Identifier
			{
				match(input, Identifier, FOLLOW_Identifier_in_methodName2396);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}

		return retval;
	}

	// $ANTLR end "methodName"

	protected static class methodParameterList_scope {
		List	mparms;
	}

	protected Stack	methodParameterList_stack	= new Stack();

	public static class methodParameterList_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "methodParameterList"
	// com\\kyben\\translatecim\\TranslateCIM.g:829:1: methodParameterList : methodParameter ( COMMA methodParameter )* ->
	// methodParameterListTemplate(parms=$methodParameterList::mparms);
	public final TranslateCIMParser.methodParameterList_return methodParameterList() throws RecognitionException {
		methodParameterList_stack.push(new methodParameterList_scope());
		TranslateCIMParser.methodParameterList_return retval = new TranslateCIMParser.methodParameterList_return();
		retval.start = input.LT(1);

		((methodParameterList_scope) methodParameterList_stack.peek()).mparms = new ArrayList();

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:836:5: ( methodParameter ( COMMA methodParameter )* ->
			// methodParameterListTemplate(parms=$methodParameterList::mparms))
			// com\\kyben\\translatecim\\TranslateCIM.g:836:7: methodParameter ( COMMA methodParameter )*
			{
				pushFollow(FOLLOW_methodParameter_in_methodParameterList2431);
				methodParameter();

				state._fsp--;

				// com\\kyben\\translatecim\\TranslateCIM.g:836:23: ( COMMA methodParameter )*
				loop17: do {
					int alt17 = 2;
					int LA17_0 = input.LA(1);

					if ((LA17_0 == COMMA)) {
						alt17 = 1;
					}

					switch (alt17) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:836:24: COMMA methodParameter
						{
							match(input, COMMA, FOLLOW_COMMA_in_methodParameterList2434);
							pushFollow(FOLLOW_methodParameter_in_methodParameterList2436);
							methodParameter();

							state._fsp--;

						}
							break;

						default:
							break loop17;
					}
				} while (true);

				// TEMPLATE REWRITE
				// 837:5: -> methodParameterListTemplate(parms=$methodParameterList::mparms)
				{
					retval.st = templateLib.getInstanceOf("methodParameterListTemplate",
							new STAttrMap().put("parms", ((methodParameterList_scope) methodParameterList_stack.peek()).mparms));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
			methodParameterList_stack.pop();
		}
		return retval;
	}

	// $ANTLR end "methodParameterList"

	public static class methodParameter_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "methodParameter"
	// com\\kyben\\translatecim\\TranslateCIM.g:841:1: methodParameter : ( methodParm | methodRef );
	public final TranslateCIMParser.methodParameter_return methodParameter() throws RecognitionException {
		TranslateCIMParser.methodParameter_return retval = new TranslateCIMParser.methodParameter_return();
		retval.start = input.LT(1);

		TranslateCIMParser.methodParm_return methodParm24 = null;

		TranslateCIMParser.methodRef_return methodRef25 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:842:5: ( methodParm | methodRef )
			int alt18 = 2;
			alt18 = dfa18.predict(input);
			switch (alt18) {
				case 1:
				// com\\kyben\\translatecim\\TranslateCIM.g:842:7: methodParm
				{
					pushFollow(FOLLOW_methodParm_in_methodParameter2469);
					methodParm24 = methodParm();

					state._fsp--;

					((methodParameterList_scope) methodParameterList_stack.peek()).mparms.add((methodParm24 != null ? methodParm24.st : null));

				}
					break;
				case 2:
				// com\\kyben\\translatecim\\TranslateCIM.g:846:7: methodRef
				{
					pushFollow(FOLLOW_methodRef_in_methodParameter2483);
					methodRef25 = methodRef();

					state._fsp--;

					((methodParameterList_scope) methodParameterList_stack.peek()).mparms.add((methodRef25 != null ? methodRef25.st : null));

				}
					break;

			}
			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "methodParameter"

	public static class methodParm_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "methodParm"
	// com\\kyben\\translatecim\\TranslateCIM.g:853:1: methodParm : (mq= qualifierList )? dataType nonReservedName (arr= array )? ->
	// methodParameterTemplate(comment=javadocize(htmlize((String)$mq.qualifierHash.get(\"Description\")))type=$dataType.stisArray=arr!=nullname=$nonReservedName.nrString);
	public final TranslateCIMParser.methodParm_return methodParm() throws RecognitionException {
		TranslateCIMParser.methodParm_return retval = new TranslateCIMParser.methodParm_return();
		retval.start = input.LT(1);

		TranslateCIMParser.qualifierList_return mq = null;

		TranslateCIMParser.array_return arr = null;

		TranslateCIMParser.dataType_return dataType26 = null;

		TranslateCIMParser.nonReservedName_return nonReservedName27 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:854:5: ( (mq= qualifierList )? dataType nonReservedName (arr= array )? ->
			// methodParameterTemplate(comment=javadocize(htmlize((String)$mq.qualifierHash.get(\"Description\")))type=$dataType.stisArray=arr!=nullname=$nonReservedName.nrString))
			// com\\kyben\\translatecim\\TranslateCIM.g:854:7: (mq= qualifierList )? dataType nonReservedName (arr= array )?
			{
				// com\\kyben\\translatecim\\TranslateCIM.g:854:9: (mq= qualifierList )?
				int alt19 = 2;
				int LA19_0 = input.LA(1);

				if ((LA19_0 == LBRACK)) {
					alt19 = 1;
				}
				switch (alt19) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:854:9: mq= qualifierList
					{
						pushFollow(FOLLOW_qualifierList_in_methodParm2509);
						mq = qualifierList();

						state._fsp--;

					}
						break;

				}

				pushFollow(FOLLOW_dataType_in_methodParm2512);
				dataType26 = dataType();

				state._fsp--;

				pushFollow(FOLLOW_nonReservedName_in_methodParm2514);
				nonReservedName27 = nonReservedName();

				state._fsp--;

				// com\\kyben\\translatecim\\TranslateCIM.g:854:50: (arr= array )?
				int alt20 = 2;
				int LA20_0 = input.LA(1);

				if ((LA20_0 == LBRACK)) {
					alt20 = 1;
				}
				switch (alt20) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:854:52: arr= array
					{
						pushFollow(FOLLOW_array_in_methodParm2520);
						arr = array();

						state._fsp--;

					}
						break;

				}

				// TEMPLATE REWRITE
				// 855:5: ->
				// methodParameterTemplate(comment=javadocize(htmlize((String)$mq.qualifierHash.get(\"Description\")))type=$dataType.stisArray=arr!=nullname=$nonReservedName.nrString)
				{
					retval.st = templateLib.getInstanceOf(
							"methodParameterTemplate",
							new STAttrMap().put("comment", javadocize(htmlize((String) (mq != null ? mq.qualifierHash : null).get("Description"))))
									.put("type", (dataType26 != null ? dataType26.st : null)).put("isArray", arr != null)
									.put("name", (nonReservedName27 != null ? nonReservedName27.nrString : null)));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "methodParm"

	public static class methodRef_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "methodRef"
	// com\\kyben\\translatecim\\TranslateCIM.g:862:1: methodRef : (mq= qualifierList )? className REF nonReservedName (arr= array )? ->
	// methodReferenceTemplate(comment=$mq.qualifierHash.get(\"Description\")className=$className.textisArray=arr!=nullreferenceName=$nonReservedName.nrString);
	public final TranslateCIMParser.methodRef_return methodRef() throws RecognitionException {
		TranslateCIMParser.methodRef_return retval = new TranslateCIMParser.methodRef_return();
		retval.start = input.LT(1);

		TranslateCIMParser.qualifierList_return mq = null;

		TranslateCIMParser.array_return arr = null;

		TranslateCIMParser.className_return className28 = null;

		TranslateCIMParser.nonReservedName_return nonReservedName29 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:863:5: ( (mq= qualifierList )? className REF nonReservedName (arr= array )? ->
			// methodReferenceTemplate(comment=$mq.qualifierHash.get(\"Description\")className=$className.textisArray=arr!=nullreferenceName=$nonReservedName.nrString))
			// com\\kyben\\translatecim\\TranslateCIM.g:863:7: (mq= qualifierList )? className REF nonReservedName (arr= array )?
			{
				// com\\kyben\\translatecim\\TranslateCIM.g:863:9: (mq= qualifierList )?
				int alt21 = 2;
				int LA21_0 = input.LA(1);

				if ((LA21_0 == LBRACK)) {
					alt21 = 1;
				}
				switch (alt21) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:863:9: mq= qualifierList
					{
						pushFollow(FOLLOW_qualifierList_in_methodRef2686);
						mq = qualifierList();

						state._fsp--;

					}
						break;

				}

				pushFollow(FOLLOW_className_in_methodRef2689);
				className28 = className();

				state._fsp--;

				match(input, REF, FOLLOW_REF_in_methodRef2691);
				pushFollow(FOLLOW_nonReservedName_in_methodRef2693);
				nonReservedName29 = nonReservedName();

				state._fsp--;

				// com\\kyben\\translatecim\\TranslateCIM.g:863:55: (arr= array )?
				int alt22 = 2;
				int LA22_0 = input.LA(1);

				if ((LA22_0 == LBRACK)) {
					alt22 = 1;
				}
				switch (alt22) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:863:57: arr= array
					{
						pushFollow(FOLLOW_array_in_methodRef2699);
						arr = array();

						state._fsp--;

					}
						break;

				}

				if (((methodDeclaration_scope) methodDeclaration_stack.peek()).thisMethodIsDeprecated) {
					return retval;
				}
				checkClassName((className28 != null ? className28.token : null));

				// TEMPLATE REWRITE
				// 870:5: ->
				// methodReferenceTemplate(comment=$mq.qualifierHash.get(\"Description\")className=$className.textisArray=arr!=nullreferenceName=$nonReservedName.nrString)
				{
					retval.st = templateLib.getInstanceOf(
							"methodReferenceTemplate",
							new STAttrMap().put("comment", (mq != null ? mq.qualifierHash : null).get("Description"))
									.put("className", (className28 != null ? input.toString(className28.start, className28.stop) : null))
									.put("isArray", arr != null)
									.put("referenceName", (nonReservedName29 != null ? nonReservedName29.nrString : null)));
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "methodRef"

	public static class nonReservedName_return extends ParserRuleReturnScope {
		public String			nrString;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "nonReservedName"
	// com\\kyben\\translatecim\\TranslateCIM.g:877:1: nonReservedName returns [ String nrString ] : Identifier ;
	public final TranslateCIMParser.nonReservedName_return nonReservedName() throws RecognitionException {
		TranslateCIMParser.nonReservedName_return retval = new TranslateCIMParser.nonReservedName_return();
		retval.start = input.LT(1);

		Token Identifier30 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:878:5: ( Identifier )
			// com\\kyben\\translatecim\\TranslateCIM.g:878:7: Identifier
			{
				Identifier30 = (Token) match(input, Identifier, FOLLOW_Identifier_in_nonReservedName2875);

				if (javaClasses.contains((Identifier30 != null ? Identifier30.getText() : null))) {
					retval.nrString = "cim_" + (Identifier30 != null ? Identifier30.getText() : null);
				} else {
					retval.nrString = (Identifier30 != null ? Identifier30.getText() : null);
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}

		if (retval.nrString != null) {
			retval.nrString = retval.nrString.substring(0, 1).toLowerCase() + retval.nrString.substring(1);
			if (Arrays.asList(RESERVED_JAVA_WORDS).contains(retval.nrString)) {
				retval.nrString = "_" + retval.nrString;
			}
		}
		return retval;
	}

	// $ANTLR end "nonReservedName"

	protected static class qualifierList_scope {
		HashMap	qh;
	}

	protected Stack	qualifierList_stack	= new Stack();

	public static class qualifierList_return extends ParserRuleReturnScope {
		public HashMap			qualifierHash;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifierList"
	// com\\kyben\\translatecim\\TranslateCIM.g:889:1: qualifierList returns [ HashMap qualifierHash ] : LBRACK qualifier ( COMMA qualifier )* RBRACK
	// ;
	public final TranslateCIMParser.qualifierList_return qualifierList() throws RecognitionException {
		qualifierList_stack.push(new qualifierList_scope());
		TranslateCIMParser.qualifierList_return retval = new TranslateCIMParser.qualifierList_return();
		retval.start = input.LT(1);

		((qualifierList_scope) qualifierList_stack.peek()).qh = new HashMap();
		// System.out.println("qualifierList: initializing");

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:897:9: ( LBRACK qualifier ( COMMA qualifier )* RBRACK )
			// com\\kyben\\translatecim\\TranslateCIM.g:897:11: LBRACK qualifier ( COMMA qualifier )* RBRACK
			{
				match(input, LBRACK, FOLLOW_LBRACK_in_qualifierList2924);
				pushFollow(FOLLOW_qualifier_in_qualifierList2926);
				qualifier();

				state._fsp--;

				// com\\kyben\\translatecim\\TranslateCIM.g:897:28: ( COMMA qualifier )*
				loop23: do {
					int alt23 = 2;
					int LA23_0 = input.LA(1);

					if ((LA23_0 == COMMA)) {
						alt23 = 1;
					}

					switch (alt23) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:897:29: COMMA qualifier
						{
							match(input, COMMA, FOLLOW_COMMA_in_qualifierList2929);
							pushFollow(FOLLOW_qualifier_in_qualifierList2931);
							qualifier();

							state._fsp--;

						}
							break;

						default:
							break loop23;
					}
				} while (true);

				match(input, RBRACK, FOLLOW_RBRACK_in_qualifierList2935);

				// System.out.println("qualifierList: returning with qualifierHash set");
				// printKeys(((qualifierList_scope)qualifierList_stack.peek()).qh); // dbg
				// System.out.println("qualifierList: yo.");
				retval.qualifierHash = ((qualifierList_scope) qualifierList_stack.peek()).qh;

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
			qualifierList_stack.pop();
		}
		return retval;
	}

	// $ANTLR end "qualifierList"

	public static class qualifier_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifier"
	// com\\kyben\\translatecim\\TranslateCIM.g:907:1: qualifier : Identifier (qp= qualifierParameter )? ( flavorList )? ;
	public final TranslateCIMParser.qualifier_return qualifier() throws RecognitionException {
		TranslateCIMParser.qualifier_return retval = new TranslateCIMParser.qualifier_return();
		retval.start = input.LT(1);

		Token Identifier31 = null;
		TranslateCIMParser.qualifierParameter_return qp = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:908:5: ( Identifier (qp= qualifierParameter )? ( flavorList )? )
			// com\\kyben\\translatecim\\TranslateCIM.g:908:7: Identifier (qp= qualifierParameter )? ( flavorList )?
			{
				Identifier31 = (Token) match(input, Identifier, FOLLOW_Identifier_in_qualifier2959);
				// com\\kyben\\translatecim\\TranslateCIM.g:908:20: (qp= qualifierParameter )?
				int alt24 = 2;
				int LA24_0 = input.LA(1);

				if ((LA24_0 == LPAREN || LA24_0 == LCURLY)) {
					alt24 = 1;
				}
				switch (alt24) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:908:20: qp= qualifierParameter
					{
						pushFollow(FOLLOW_qualifierParameter_in_qualifier2963);
						qp = qualifierParameter();

						state._fsp--;

					}
						break;

				}

				// com\\kyben\\translatecim\\TranslateCIM.g:908:41: ( flavorList )?
				int alt25 = 2;
				int LA25_0 = input.LA(1);

				if ((LA25_0 == COLON)) {
					alt25 = 1;
				}
				switch (alt25) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:908:41: flavorList
					{
						pushFollow(FOLLOW_flavorList_in_qualifier2966);
						flavorList();

						state._fsp--;

					}
						break;

				}

				// System.out.println("qualifier: looking up " + (Identifier31!=null?Identifier31.getText():null).toLowerCase() +
				// " in qualifierDefinitions");
				qualifierDefinition qqDef = qualifierDefinitions.get((Identifier31 != null ? Identifier31.getText() : null).toLowerCase());
				if (qqDef == null) {
					RecognitionException r = new RecognitionException(input);
					throw new UndefinedQualifierException(
							currentInputFileName + "(" + r.line + ":" + r.charPositionInLine + ")" +
									": qualifier " + (Identifier31 != null ? Identifier31.getText() : null) + " not defined");
				}
				((qualifierList_scope) qualifierList_stack.peek()).qh.put(qqDef.name, (qp == null) ? "" : qp.qualifierVal);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "qualifier"

	public static class qualifierParameter_return extends ParserRuleReturnScope {
		public Object			qualifierVal;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifierParameter"
	// com\\kyben\\translatecim\\TranslateCIM.g:923:1: qualifierParameter returns [ Object qualifierVal ] : ( ( LPAREN ( constantValue |
	// stringConstant ) RPAREN ) | arrayInitializer );
	public final TranslateCIMParser.qualifierParameter_return qualifierParameter() throws RecognitionException {
		TranslateCIMParser.qualifierParameter_return retval = new TranslateCIMParser.qualifierParameter_return();
		retval.start = input.LT(1);

		TranslateCIMParser.stringConstant_return stringConstant32 = null;

		TranslateCIMParser.arrayInitializer_return arrayInitializer33 = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:924:5: ( ( LPAREN ( constantValue | stringConstant ) RPAREN ) | arrayInitializer )
			int alt27 = 2;
			int LA27_0 = input.LA(1);

			if ((LA27_0 == LPAREN)) {
				alt27 = 1;
			}
			else if ((LA27_0 == LCURLY)) {
				alt27 = 2;
			}
			else {
				NoViableAltException nvae =
						new NoViableAltException("", 27, 0, input);

				throw nvae;
			}
			switch (alt27) {
				case 1:
				// com\\kyben\\translatecim\\TranslateCIM.g:924:7: ( LPAREN ( constantValue | stringConstant ) RPAREN )
				{
					// com\\kyben\\translatecim\\TranslateCIM.g:924:7: ( LPAREN ( constantValue | stringConstant ) RPAREN )
					// com\\kyben\\translatecim\\TranslateCIM.g:924:9: LPAREN ( constantValue | stringConstant ) RPAREN
					{
						match(input, LPAREN, FOLLOW_LPAREN_in_qualifierParameter2997);
						// com\\kyben\\translatecim\\TranslateCIM.g:924:16: ( constantValue | stringConstant )
						int alt26 = 2;
						int LA26_0 = input.LA(1);

						if ((LA26_0 == IntegralConstant || (LA26_0 >= CharacterConstant && LA26_0 <= NULL))) {
							alt26 = 1;
						}
						else if ((LA26_0 == DoubleQuotedString)) {
							alt26 = 2;
						}
						else {
							NoViableAltException nvae =
									new NoViableAltException("", 26, 0, input);

							throw nvae;
						}
						switch (alt26) {
							case 1:
							// com\\kyben\\translatecim\\TranslateCIM.g:924:18: constantValue
							{
								pushFollow(FOLLOW_constantValue_in_qualifierParameter3001);
								constantValue();

								state._fsp--;

							}
								break;
							case 2:
							// com\\kyben\\translatecim\\TranslateCIM.g:924:34: stringConstant
							{
								pushFollow(FOLLOW_stringConstant_in_qualifierParameter3005);
								stringConstant32 = stringConstant();

								state._fsp--;

								retval.qualifierVal = (stringConstant32 != null ? stringConstant32.string : null);

							}
								break;

						}

						match(input, RPAREN, FOLLOW_RPAREN_in_qualifierParameter3011);

					}

				}
					break;
				case 2:
				// com\\kyben\\translatecim\\TranslateCIM.g:925:7: arrayInitializer
				{
					pushFollow(FOLLOW_arrayInitializer_in_qualifierParameter3021);
					arrayInitializer33 = arrayInitializer();

					state._fsp--;

					retval.qualifierVal = (arrayInitializer33 != null ? arrayInitializer33.initList : null);

				}
					break;

			}
			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "qualifierParameter"

	public static class flavorList_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "flavorList"
	// com\\kyben\\translatecim\\TranslateCIM.g:932:1: flavorList : COLON ( flavor )+ ;
	public final TranslateCIMParser.flavorList_return flavorList() throws RecognitionException {
		TranslateCIMParser.flavorList_return retval = new TranslateCIMParser.flavorList_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:933:5: ( COLON ( flavor )+ )
			// com\\kyben\\translatecim\\TranslateCIM.g:933:7: COLON ( flavor )+
			{
				match(input, COLON, FOLLOW_COLON_in_flavorList3045);
				// com\\kyben\\translatecim\\TranslateCIM.g:933:13: ( flavor )+
				int cnt28 = 0;
				loop28: do {
					int alt28 = 2;
					int LA28_0 = input.LA(1);

					if (((LA28_0 >= ENABLEOVERRIDE && LA28_0 <= TRANSLATABLE))) {
						alt28 = 1;
					}

					switch (alt28) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:933:13: flavor
						{
							pushFollow(FOLLOW_flavor_in_flavorList3047);
							flavor();

							state._fsp--;

						}
							break;

						default:
							if (cnt28 >= 1)
								break loop28;
							EarlyExitException eee =
									new EarlyExitException(28, input);
							throw eee;
					}
					cnt28++;
				} while (true);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "flavorList"

	public static class dataType_return extends ParserRuleReturnScope {
		public StringTemplate	defaultVal;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "dataType"
	// com\\kyben\\translatecim\\TranslateCIM.g:937:1: dataType returns [ StringTemplate defaultVal ] : ( BOOLEAN -> booleanTemplate() | CHAR16 ->
	// char16Template() | DATETIME -> datetimeTemplate() | REAL32 -> real32Template() | REAL64 -> real64Template() | SINT16 -> sint16Template() |
	// SINT32 -> sint32Template() | SINT64 -> sint64Template() | SINT8 -> sint8Template() | STRING -> strTemplate() | UINT16 -> uint16Template() |
	// UINT32 -> uint32Template() | UINT64 -> uint64Template() | UINT8 -> uint8Template());
	public final TranslateCIMParser.dataType_return dataType() throws RecognitionException {
		TranslateCIMParser.dataType_return retval = new TranslateCIMParser.dataType_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:941:5: ( BOOLEAN -> booleanTemplate() | CHAR16 -> char16Template() | DATETIME ->
			// datetimeTemplate() | REAL32 -> real32Template() | REAL64 -> real64Template() | SINT16 -> sint16Template() | SINT32 -> sint32Template()
			// | SINT64 -> sint64Template() | SINT8 -> sint8Template() | STRING -> strTemplate() | UINT16 -> uint16Template() | UINT32 ->
			// uint32Template() | UINT64 -> uint64Template() | UINT8 -> uint8Template())
			int alt29 = 14;
			switch (input.LA(1)) {
				case BOOLEAN: {
					alt29 = 1;
				}
					break;
				case CHAR16: {
					alt29 = 2;
				}
					break;
				case DATETIME: {
					alt29 = 3;
				}
					break;
				case REAL32: {
					alt29 = 4;
				}
					break;
				case REAL64: {
					alt29 = 5;
				}
					break;
				case SINT16: {
					alt29 = 6;
				}
					break;
				case SINT32: {
					alt29 = 7;
				}
					break;
				case SINT64: {
					alt29 = 8;
				}
					break;
				case SINT8: {
					alt29 = 9;
				}
					break;
				case STRING: {
					alt29 = 10;
				}
					break;
				case UINT16: {
					alt29 = 11;
				}
					break;
				case UINT32: {
					alt29 = 12;
				}
					break;
				case UINT64: {
					alt29 = 13;
				}
					break;
				case UINT8: {
					alt29 = 14;
				}
					break;
				default:
					NoViableAltException nvae =
							new NoViableAltException("", 29, 0, input);

					throw nvae;
			}

			switch (alt29) {
				case 1:
				// com\\kyben\\translatecim\\TranslateCIM.g:941:7: BOOLEAN
				{
					match(input, BOOLEAN, FOLLOW_BOOLEAN_in_dataType3073);
					retval.defaultVal = templateLib.getInstanceOf("booleanDefaultValueTemplate");

					// TEMPLATE REWRITE
					// 941:93: -> booleanTemplate()
					{
						retval.st = templateLib.getInstanceOf("booleanTemplate");
					}

				}
					break;
				case 2:
				// com\\kyben\\translatecim\\TranslateCIM.g:942:7: CHAR16
				{
					match(input, CHAR16, FOLLOW_CHAR16_in_dataType3090);
					retval.defaultVal = templateLib.getInstanceOf("char16DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 942:93: -> char16Template()
					{
						retval.st = templateLib.getInstanceOf("char16Template");
					}

				}
					break;
				case 3:
				// com\\kyben\\translatecim\\TranslateCIM.g:943:7: DATETIME
				{
					match(input, DATETIME, FOLLOW_DATETIME_in_dataType3108);
					retval.defaultVal = templateLib.getInstanceOf("datetimeDefaultValueTemplate");

					// TEMPLATE REWRITE
					// 943:93: -> datetimeTemplate()
					{
						retval.st = templateLib.getInstanceOf("datetimeTemplate");
					}

				}
					break;
				case 4:
				// com\\kyben\\translatecim\\TranslateCIM.g:944:7: REAL32
				{
					match(input, REAL32, FOLLOW_REAL32_in_dataType3124);
					retval.defaultVal = templateLib.getInstanceOf("real32DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 944:93: -> real32Template()
					{
						retval.st = templateLib.getInstanceOf("real32Template");
					}

				}
					break;
				case 5:
				// com\\kyben\\translatecim\\TranslateCIM.g:945:7: REAL64
				{
					match(input, REAL64, FOLLOW_REAL64_in_dataType3142);
					retval.defaultVal = templateLib.getInstanceOf("real64DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 945:93: -> real64Template()
					{
						retval.st = templateLib.getInstanceOf("real64Template");
					}

				}
					break;
				case 6:
				// com\\kyben\\translatecim\\TranslateCIM.g:946:7: SINT16
				{
					match(input, SINT16, FOLLOW_SINT16_in_dataType3160);
					retval.defaultVal = templateLib.getInstanceOf("sint16DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 946:93: -> sint16Template()
					{
						retval.st = templateLib.getInstanceOf("sint16Template");
					}

				}
					break;
				case 7:
				// com\\kyben\\translatecim\\TranslateCIM.g:947:7: SINT32
				{
					match(input, SINT32, FOLLOW_SINT32_in_dataType3178);
					retval.defaultVal = templateLib.getInstanceOf("sint32DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 947:93: -> sint32Template()
					{
						retval.st = templateLib.getInstanceOf("sint32Template");
					}

				}
					break;
				case 8:
				// com\\kyben\\translatecim\\TranslateCIM.g:948:7: SINT64
				{
					match(input, SINT64, FOLLOW_SINT64_in_dataType3196);
					retval.defaultVal = templateLib.getInstanceOf("sint64DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 948:93: -> sint64Template()
					{
						retval.st = templateLib.getInstanceOf("sint64Template");
					}

				}
					break;
				case 9:
				// com\\kyben\\translatecim\\TranslateCIM.g:949:7: SINT8
				{
					match(input, SINT8, FOLLOW_SINT8_in_dataType3214);
					retval.defaultVal = templateLib.getInstanceOf("sint8DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 949:93: -> sint8Template()
					{
						retval.st = templateLib.getInstanceOf("sint8Template");
					}

				}
					break;
				case 10:
				// com\\kyben\\translatecim\\TranslateCIM.g:950:7: STRING
				{
					match(input, STRING, FOLLOW_STRING_in_dataType3233);
					retval.defaultVal = templateLib.getInstanceOf("strDefaultValueTemplate");

					// TEMPLATE REWRITE
					// 950:93: -> strTemplate()
					{
						retval.st = templateLib.getInstanceOf("strTemplate");
					}

				}
					break;
				case 11:
				// com\\kyben\\translatecim\\TranslateCIM.g:951:7: UINT16
				{
					match(input, UINT16, FOLLOW_UINT16_in_dataType3251);
					retval.defaultVal = templateLib.getInstanceOf("uint16DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 951:93: -> uint16Template()
					{
						retval.st = templateLib.getInstanceOf("uint16Template");
					}

				}
					break;
				case 12:
				// com\\kyben\\translatecim\\TranslateCIM.g:952:7: UINT32
				{
					match(input, UINT32, FOLLOW_UINT32_in_dataType3269);
					retval.defaultVal = templateLib.getInstanceOf("uint32DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 952:93: -> uint32Template()
					{
						retval.st = templateLib.getInstanceOf("uint32Template");
					}

				}
					break;
				case 13:
				// com\\kyben\\translatecim\\TranslateCIM.g:953:7: UINT64
				{
					match(input, UINT64, FOLLOW_UINT64_in_dataType3287);
					retval.defaultVal = templateLib.getInstanceOf("uint64DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 953:93: -> uint64Template()
					{
						retval.st = templateLib.getInstanceOf("uint64Template");
					}

				}
					break;
				case 14:
				// com\\kyben\\translatecim\\TranslateCIM.g:954:7: UINT8
				{
					match(input, UINT8, FOLLOW_UINT8_in_dataType3305);
					retval.defaultVal = templateLib.getInstanceOf("uint8DefaultValueTemplate");

					// TEMPLATE REWRITE
					// 954:93: -> uint8Template()
					{
						retval.st = templateLib.getInstanceOf("uint8Template");
					}

				}
					break;

			}
			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "dataType"

	public static class array_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "array"
	// com\\kyben\\translatecim\\TranslateCIM.g:958:1: array : LBRACK ( IntegralConstant )? RBRACK ;
	public final TranslateCIMParser.array_return array() throws RecognitionException {
		TranslateCIMParser.array_return retval = new TranslateCIMParser.array_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:959:5: ( LBRACK ( IntegralConstant )? RBRACK )
			// com\\kyben\\translatecim\\TranslateCIM.g:959:7: LBRACK ( IntegralConstant )? RBRACK
			{
				match(input, LBRACK, FOLLOW_LBRACK_in_array3334);
				// com\\kyben\\translatecim\\TranslateCIM.g:959:14: ( IntegralConstant )?
				int alt30 = 2;
				int LA30_0 = input.LA(1);

				if ((LA30_0 == IntegralConstant)) {
					alt30 = 1;
				}
				switch (alt30) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:959:14: IntegralConstant
					{
						match(input, IntegralConstant, FOLLOW_IntegralConstant_in_array3336);

					}
						break;

				}

				match(input, RBRACK, FOLLOW_RBRACK_in_array3339);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "array"

	public static class qualifierInitializer_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "qualifierInitializer"
	// com\\kyben\\translatecim\\TranslateCIM.g:963:1: qualifierInitializer : ( constantValue | DoubleQuotedString );
	public final TranslateCIMParser.qualifierInitializer_return qualifierInitializer() throws RecognitionException {
		TranslateCIMParser.qualifierInitializer_return retval = new TranslateCIMParser.qualifierInitializer_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:964:5: ( constantValue | DoubleQuotedString )
			int alt31 = 2;
			int LA31_0 = input.LA(1);

			if ((LA31_0 == IntegralConstant || (LA31_0 >= CharacterConstant && LA31_0 <= NULL))) {
				alt31 = 1;
			}
			else if ((LA31_0 == DoubleQuotedString)) {
				alt31 = 2;
			}
			else {
				NoViableAltException nvae =
						new NoViableAltException("", 31, 0, input);

				throw nvae;
			}
			switch (alt31) {
				case 1:
				// com\\kyben\\translatecim\\TranslateCIM.g:964:7: constantValue
				{
					pushFollow(FOLLOW_constantValue_in_qualifierInitializer3365);
					constantValue();

					state._fsp--;

				}
					break;
				case 2:
				// com\\kyben\\translatecim\\TranslateCIM.g:965:7: DoubleQuotedString
				{
					match(input, DoubleQuotedString, FOLLOW_DoubleQuotedString_in_qualifierInitializer3373);

				}
					break;

			}
			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "qualifierInitializer"

	public static class initializer_return extends ParserRuleReturnScope {
		public ArrayList<String>	initializerList;
		public StringTemplate		st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "initializer"
	// com\\kyben\\translatecim\\TranslateCIM.g:969:1: initializer returns [ ArrayList<String> initializerList ] : ( constantValue |
	// DoubleQuotedString | arrayInitializer | cimReferenceInitializer= referenceInitializer );
	public final TranslateCIMParser.initializer_return initializer() throws RecognitionException {
		TranslateCIMParser.initializer_return retval = new TranslateCIMParser.initializer_return();
		retval.start = input.LT(1);

		Token DoubleQuotedString35 = null;
		TranslateCIMParser.referenceInitializer_return cimReferenceInitializer = null;

		TranslateCIMParser.constantValue_return constantValue34 = null;

		TranslateCIMParser.arrayInitializer_return arrayInitializer36 = null;

		retval.initializerList = new ArrayList<String>();
		// System.out.println("initializer: called");

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:974:5: ( constantValue | DoubleQuotedString | arrayInitializer | cimReferenceInitializer=
			// referenceInitializer )
			int alt32 = 4;
			switch (input.LA(1)) {
				case IntegralConstant:
				case CharacterConstant:
				case TRUE:
				case FALSE:
				case NULL: {
					alt32 = 1;
				}
					break;
				case DoubleQuotedString: {
					alt32 = 2;
				}
					break;
				case LCURLY: {
					alt32 = 3;
				}
					break;
				case DOLLAR: {
					alt32 = 4;
				}
					break;
				default:
					NoViableAltException nvae =
							new NoViableAltException("", 32, 0, input);

					throw nvae;
			}

			switch (alt32) {
				case 1:
				// com\\kyben\\translatecim\\TranslateCIM.g:974:7: constantValue
				{
					pushFollow(FOLLOW_constantValue_in_initializer3404);
					constantValue34 = constantValue();

					state._fsp--;

					retval.initializerList.add((constantValue34 != null ? input.toString(constantValue34.start, constantValue34.stop) : null));

				}
					break;
				case 2:
				// com\\kyben\\translatecim\\TranslateCIM.g:978:7: DoubleQuotedString
				{
					DoubleQuotedString35 = (Token) match(input, DoubleQuotedString, FOLLOW_DoubleQuotedString_in_initializer3418);

					retval.initializerList.add((DoubleQuotedString35 != null ? DoubleQuotedString35.getText() : null));

				}
					break;
				case 3:
				// com\\kyben\\translatecim\\TranslateCIM.g:982:7: arrayInitializer
				{
					pushFollow(FOLLOW_arrayInitializer_in_initializer3432);
					arrayInitializer36 = arrayInitializer();

					state._fsp--;

					retval.initializerList = (arrayInitializer36 != null ? arrayInitializer36.initList : null);

				}
					break;
				case 4:
				// com\\kyben\\translatecim\\TranslateCIM.g:986:7: cimReferenceInitializer= referenceInitializer
				{
					pushFollow(FOLLOW_referenceInitializer_in_initializer3450);
					cimReferenceInitializer = referenceInitializer();

					state._fsp--;

					retval.initializerList.add((cimReferenceInitializer != null ? input.toString(cimReferenceInitializer.start,
							cimReferenceInitializer.stop) : null));

				}
					break;

			}
			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "initializer"

	public static class arrayInitializer_return extends ParserRuleReturnScope {
		public ArrayList<String>	initList;
		public StringTemplate		st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "arrayInitializer"
	// com\\kyben\\translatecim\\TranslateCIM.g:993:1: arrayInitializer returns [ ArrayList<String> initList] : LCURLY (c1= constantValue | s1=
	// stringConstant ) ( COMMA (c2= constantValue | s2= stringConstant ) )* RCURLY ;
	public final TranslateCIMParser.arrayInitializer_return arrayInitializer() throws RecognitionException {
		TranslateCIMParser.arrayInitializer_return retval = new TranslateCIMParser.arrayInitializer_return();
		retval.start = input.LT(1);

		TranslateCIMParser.constantValue_return c1 = null;

		TranslateCIMParser.stringConstant_return s1 = null;

		TranslateCIMParser.constantValue_return c2 = null;

		TranslateCIMParser.stringConstant_return s2 = null;

		retval.initList = new ArrayList<String>();

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:997:5: ( LCURLY (c1= constantValue | s1= stringConstant ) ( COMMA (c2= constantValue | s2=
			// stringConstant ) )* RCURLY )
			// com\\kyben\\translatecim\\TranslateCIM.g:997:7: LCURLY (c1= constantValue | s1= stringConstant ) ( COMMA (c2= constantValue | s2=
			// stringConstant ) )* RCURLY
			{
				match(input, LCURLY, FOLLOW_LCURLY_in_arrayInitializer3487);
				// com\\kyben\\translatecim\\TranslateCIM.g:997:15: (c1= constantValue | s1= stringConstant )
				int alt33 = 2;
				int LA33_0 = input.LA(1);

				if ((LA33_0 == IntegralConstant || (LA33_0 >= CharacterConstant && LA33_0 <= NULL))) {
					alt33 = 1;
				}
				else if ((LA33_0 == DoubleQuotedString)) {
					alt33 = 2;
				}
				else {
					NoViableAltException nvae =
							new NoViableAltException("", 33, 0, input);

					throw nvae;
				}
				switch (alt33) {
					case 1:
					// com\\kyben\\translatecim\\TranslateCIM.g:997:17: c1= constantValue
					{
						pushFollow(FOLLOW_constantValue_in_arrayInitializer3494);
						c1 = constantValue();

						state._fsp--;

						retval.initList.add((c1 != null ? input.toString(c1.start, c1.stop) : null));

					}
						break;
					case 2:
					// com\\kyben\\translatecim\\TranslateCIM.g:997:63: s1= stringConstant
					{
						pushFollow(FOLLOW_stringConstant_in_arrayInitializer3502);
						s1 = stringConstant();

						state._fsp--;

						retval.initList.add((s1 != null ? s1.string : null));

					}
						break;

				}

				// com\\kyben\\translatecim\\TranslateCIM.g:998:7: ( COMMA (c2= constantValue | s2= stringConstant ) )*
				loop35: do {
					int alt35 = 2;
					int LA35_0 = input.LA(1);

					if ((LA35_0 == COMMA)) {
						alt35 = 1;
					}

					switch (alt35) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:998:9: COMMA (c2= constantValue | s2= stringConstant )
						{
							match(input, COMMA, FOLLOW_COMMA_in_arrayInitializer3516);
							// com\\kyben\\translatecim\\TranslateCIM.g:998:15: (c2= constantValue | s2= stringConstant )
							int alt34 = 2;
							int LA34_0 = input.LA(1);

							if ((LA34_0 == IntegralConstant || (LA34_0 >= CharacterConstant && LA34_0 <= NULL))) {
								alt34 = 1;
							}
							else if ((LA34_0 == DoubleQuotedString)) {
								alt34 = 2;
							}
							else {
								NoViableAltException nvae =
										new NoViableAltException("", 34, 0, input);

								throw nvae;
							}
							switch (alt34) {
								case 1:
								// com\\kyben\\translatecim\\TranslateCIM.g:998:17: c2= constantValue
								{
									pushFollow(FOLLOW_constantValue_in_arrayInitializer3522);
									c2 = constantValue();

									state._fsp--;

									retval.initList.add((c2 != null ? input.toString(c2.start, c2.stop) : null));

								}
									break;
								case 2:
								// com\\kyben\\translatecim\\TranslateCIM.g:998:63: s2= stringConstant
								{
									pushFollow(FOLLOW_stringConstant_in_arrayInitializer3530);
									s2 = stringConstant();

									state._fsp--;

									retval.initList.add((s2 != null ? s2.string : null));

								}
									break;

							}

						}
							break;

						default:
							break loop35;
					}
				} while (true);

				match(input, RCURLY, FOLLOW_RCURLY_in_arrayInitializer3539);

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "arrayInitializer"

	public static class constantValue_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "constantValue"
	// com\\kyben\\translatecim\\TranslateCIM.g:1002:1: constantValue : ( IntegralConstant | CharacterConstant | TRUE | FALSE | NULL ) ;
	public final TranslateCIMParser.constantValue_return constantValue() throws RecognitionException {
		TranslateCIMParser.constantValue_return retval = new TranslateCIMParser.constantValue_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:1006:5: ( ( IntegralConstant | CharacterConstant | TRUE | FALSE | NULL ) )
			// com\\kyben\\translatecim\\TranslateCIM.g:1006:7: ( IntegralConstant | CharacterConstant | TRUE | FALSE | NULL )
			{
				if (input.LA(1) == IntegralConstant || (input.LA(1) >= CharacterConstant && input.LA(1) <= NULL)) {
					input.consume();
					state.errorRecovery = false;
				}
				else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					throw mse;
				}

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "constantValue"

	public static class referenceInitializer_return extends ParserRuleReturnScope {
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "referenceInitializer"
	// com\\kyben\\translatecim\\TranslateCIM.g:1015:1: referenceInitializer : aliasIdentifier ;
	public final TranslateCIMParser.referenceInitializer_return referenceInitializer() throws RecognitionException {
		TranslateCIMParser.referenceInitializer_return retval = new TranslateCIMParser.referenceInitializer_return();
		retval.start = input.LT(1);

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:1016:5: ( aliasIdentifier )
			// com\\kyben\\translatecim\\TranslateCIM.g:1016:7: aliasIdentifier
			{
				pushFollow(FOLLOW_aliasIdentifier_in_referenceInitializer3636);
				aliasIdentifier();

				state._fsp--;

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "referenceInitializer"

	public static class stringConstant_return extends ParserRuleReturnScope {
		public String			string;
		public StringTemplate	st;

		@Override
		public Object getTemplate() {
			return st;
		}

		@Override
		public String toString() {
			return st == null ? null : st.toString();
		}
	};

	// $ANTLR start "stringConstant"
	// com\\kyben\\translatecim\\TranslateCIM.g:1020:1: stringConstant returns [String string] : (strs+= DoubleQuotedString )+ ;
	public final TranslateCIMParser.stringConstant_return stringConstant() throws RecognitionException {
		TranslateCIMParser.stringConstant_return retval = new TranslateCIMParser.stringConstant_return();
		retval.start = input.LT(1);

		Token strs = null;
		List list_strs = null;

		try {
			// com\\kyben\\translatecim\\TranslateCIM.g:1021:5: ( (strs+= DoubleQuotedString )+ )
			// com\\kyben\\translatecim\\TranslateCIM.g:1021:7: (strs+= DoubleQuotedString )+
			{
				// com\\kyben\\translatecim\\TranslateCIM.g:1021:11: (strs+= DoubleQuotedString )+
				int cnt36 = 0;
				loop36: do {
					int alt36 = 2;
					int LA36_0 = input.LA(1);

					if ((LA36_0 == DoubleQuotedString)) {
						alt36 = 1;
					}

					switch (alt36) {
						case 1:
						// com\\kyben\\translatecim\\TranslateCIM.g:1021:11: strs+= DoubleQuotedString
						{
							strs = (Token) match(input, DoubleQuotedString, FOLLOW_DoubleQuotedString_in_stringConstant3660);
							if (list_strs == null)
								list_strs = new ArrayList();
							list_strs.add(strs);

						}
							break;

						default:
							if (cnt36 >= 1)
								break loop36;
							EarlyExitException eee =
									new EarlyExitException(36, input);
							throw eee;
					}
					cnt36++;
				} while (true);

				StringBuilder retstr = new StringBuilder(list_strs.size() * 60); // for speed, estimate the initial size
				ListIterator lstrs = list_strs.listIterator();
				while (lstrs.hasNext()) {
					String dqString = ((Token) lstrs.next()).getText();
					String bareString = dqString.substring(1, dqString.length() - 1); // strip off the leading and trailing double quotes
					bareString = bareString.replaceAll("\\\\'", "'");
					bareString = bareString.replaceAll("\\\\\"", "\"");
					bareString = bareString.replaceAll("\\\\n", "\n");
					retstr.append(bareString);
				}
				retval.string = retstr.toString();

			}

			retval.stop = input.LT(-1);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end "stringConstant"

	// Delegated rules

	protected DFA12			dfa12				= new DFA12(this);
	protected DFA18			dfa18				= new DFA18(this);
	static final String		DFA12_eotS			=
														"\23\uffff";
	static final String		DFA12_eofS			=
														"\23\uffff";
	static final String		DFA12_minS			=
														"\17\11\1\uffff\1\10\2\uffff";
	static final String		DFA12_maxS			=
														"\1\57\16\11\1\uffff\1\40\2\uffff";
	static final String		DFA12_acceptS		=
														"\17\uffff\1\2\1\uffff\1\1\1\3";
	static final String		DFA12_specialS		=
														"\23\uffff}>";
	static final String[]	DFA12_transitionS	= {
												"\1\17\30\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1" +
														"\13\1\14\1\15\1\16",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"\1\20",
												"",
												"\1\21\1\uffff\1\21\2\uffff\1\22\22\uffff\1\21",
												"",
												""
												};

	static final short[]	DFA12_eot			= DFA.unpackEncodedString(DFA12_eotS);
	static final short[]	DFA12_eof			= DFA.unpackEncodedString(DFA12_eofS);
	static final char[]		DFA12_min			= DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
	static final char[]		DFA12_max			= DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
	static final short[]	DFA12_accept		= DFA.unpackEncodedString(DFA12_acceptS);
	static final short[]	DFA12_special		= DFA.unpackEncodedString(DFA12_specialS);
	static final short[][]	DFA12_transition;

	static {
		int numStates = DFA12_transitionS.length;
		DFA12_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
		}
	}

	class DFA12 extends DFA {

		public DFA12(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 12;
			this.eot = DFA12_eot;
			this.eof = DFA12_eof;
			this.min = DFA12_min;
			this.max = DFA12_max;
			this.accept = DFA12_accept;
			this.special = DFA12_special;
			this.transition = DFA12_transition;
		}

		@Override
		public String getDescription() {
			return "622:1: featureBodyDeclaration returns [ String strng ] : ( dataPropertyDeclaration | referencePropertyDeclaration | methodDeclaration );";
		}
	}

	static final String		DFA18_eotS			=
														"\42\uffff";
	static final String		DFA18_eofS			=
														"\42\uffff";
	static final String		DFA18_minS			=
														"\2\11\2\uffff\1\7\2\60\1\26\2\11\2\16\3\13\2\7\1\60\1\7\2\60\1" +
																"\26\2\13\2\16\3\13\1\7\1\60\1\7\2\13";
	static final String		DFA18_maxS			=
														"\1\57\1\11\2\uffff\1\41\2\65\1\32\1\11\1\57\1\16\1\61\1\34\1\61" +
																"\3\41\1\65\1\41\2\65\1\32\1\34\1\61\1\16\1\61\1\34\1\61\2\41\1\65" +
																"\1\41\1\34\1\61";
	static final String		DFA18_acceptS		=
														"\2\uffff\1\1\1\2\36\uffff";
	static final String		DFA18_specialS		=
														"\42\uffff}>";
	static final String[]	DFA18_transitionS	= {
												"\1\3\26\uffff\1\1\1\uffff\16\2",
												"\1\4",
												"",
												"",
												"\1\7\3\uffff\1\10\1\uffff\1\5\15\uffff\1\6\5\uffff\1\11",
												"\1\12\1\13\4\12",
												"\1\14\1\15\4\14",
												"\5\16",
												"\1\17",
												"\1\3\30\uffff\16\2",
												"\1\20",
												"\1\20\42\uffff\1\13",
												"\1\21\20\uffff\1\22",
												"\1\21\20\uffff\1\22\24\uffff\1\15",
												"\1\10\12\uffff\5\16\6\uffff\1\11",
												"\1\25\3\uffff\1\10\1\uffff\1\23\15\uffff\1\24\5\uffff\1\11",
												"\1\7\3\uffff\1\10\25\uffff\1\11",
												"\1\26\1\27\4\26",
												"\1\7\3\uffff\1\10\25\uffff\1\11",
												"\1\30\1\31\4\30",
												"\1\32\1\33\4\32",
												"\5\34",
												"\1\21\20\uffff\1\22",
												"\1\21\20\uffff\1\22\24\uffff\1\27",
												"\1\35",
												"\1\35\42\uffff\1\31",
												"\1\36\20\uffff\1\37",
												"\1\36\20\uffff\1\37\24\uffff\1\33",
												"\1\10\12\uffff\5\34\6\uffff\1\11",
												"\1\25\3\uffff\1\10\25\uffff\1\11",
												"\1\40\1\41\4\40",
												"\1\25\3\uffff\1\10\25\uffff\1\11",
												"\1\36\20\uffff\1\37",
												"\1\36\20\uffff\1\37\24\uffff\1\41"
												};

	static final short[]	DFA18_eot			= DFA.unpackEncodedString(DFA18_eotS);
	static final short[]	DFA18_eof			= DFA.unpackEncodedString(DFA18_eofS);
	static final char[]		DFA18_min			= DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
	static final char[]		DFA18_max			= DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
	static final short[]	DFA18_accept		= DFA.unpackEncodedString(DFA18_acceptS);
	static final short[]	DFA18_special		= DFA.unpackEncodedString(DFA18_specialS);
	static final short[][]	DFA18_transition;

	static {
		int numStates = DFA18_transitionS.length;
		DFA18_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
		}
	}

	class DFA18 extends DFA {

		public DFA18(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 18;
			this.eot = DFA18_eot;
			this.eof = DFA18_eof;
			this.min = DFA18_min;
			this.max = DFA18_max;
			this.accept = DFA18_accept;
			this.special = DFA18_special;
			this.transition = DFA18_transition;
		}

		@Override
		public String getDescription() {
			return "841:1: methodParameter : ( methodParm | methodRef );";
		}
	}

	public static final BitSet	FOLLOW_mofProduction_in_mofSpecification133							= new BitSet(new long[] { 0x0000000100000072L });
	public static final BitSet	FOLLOW_compilerDirective_in_mofProduction162						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_qualifierDeclaration_in_mofProduction170						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_classDeclaration_in_mofProduction178							= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_PragmaInclude_in_compilerDirective208						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_PragmaLocale_in_compilerDirective222							= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_QUALIFIER_in_qualifierDeclaration243							= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_qualifierName_in_qualifierDeclaration245						= new BitSet(new long[] { 0x0000000000000080L });
	public static final BitSet	FOLLOW_COLON_in_qualifierDeclaration247								= new BitSet(new long[] { 0x0000FFFC00000000L });
	public static final BitSet	FOLLOW_qualifierType_in_qualifierDeclaration249						= new BitSet(new long[] { 0x0000000000000800L });
	public static final BitSet	FOLLOW_qualifierScopeList_in_qualifierDeclaration251				= new BitSet(new long[] { 0x0000000000000900L });
	public static final BitSet	FOLLOW_qualifierFlavorList_in_qualifierDeclaration257				= new BitSet(new long[] { 0x0000000000000100L });
	public static final BitSet	FOLLOW_SEMICOLON_in_qualifierDeclaration262							= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_Identifier_in_qualifierName286								= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_dataType_in_qualifierType317									= new BitSet(new long[] { 0x0000000100000402L });
	public static final BitSet	FOLLOW_array_in_qualifierType320									= new BitSet(new long[] { 0x0000000000000402L });
	public static final BitSet	FOLLOW_EQUALS_in_qualifierType327									= new BitSet(new long[] { 0x003F000000000000L });
	public static final BitSet	FOLLOW_qualifierInitializer_in_qualifierType331						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_COMMA_in_qualifierScopeList371								= new BitSet(new long[] { 0x0000000000001000L });
	public static final BitSet	FOLLOW_SCOPE_in_qualifierScopeList373								= new BitSet(new long[] { 0x0000000000002000L });
	public static final BitSet	FOLLOW_LPAREN_in_qualifierScopeList375								= new BitSet(new long[] { 0x00000000001F8200L });
	public static final BitSet	FOLLOW_metaElement_in_qualifierScopeList379							= new BitSet(new long[] { 0x0000000000004800L });
	public static final BitSet	FOLLOW_COMMA_in_qualifierScopeList402								= new BitSet(new long[] { 0x00000000001F8200L });
	public static final BitSet	FOLLOW_metaElement_in_qualifierScopeList406							= new BitSet(new long[] { 0x0000000000004800L });
	public static final BitSet	FOLLOW_RPAREN_in_qualifierScopeList412								= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_set_in_metaElement431										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_COMMA_in_qualifierFlavorList546								= new BitSet(new long[] { 0x0000000000200000L });
	public static final BitSet	FOLLOW_FLAVOR_in_qualifierFlavorList548								= new BitSet(new long[] { 0x0000000000002000L });
	public static final BitSet	FOLLOW_LPAREN_in_qualifierFlavorList550								= new BitSet(new long[] { 0x0000000007C00000L });
	public static final BitSet	FOLLOW_flavor_in_qualifierFlavorList554								= new BitSet(new long[] { 0x0000000000004800L });
	public static final BitSet	FOLLOW_COMMA_in_qualifierFlavorList578								= new BitSet(new long[] { 0x0000000007C00000L });
	public static final BitSet	FOLLOW_flavor_in_qualifierFlavorList582								= new BitSet(new long[] { 0x0000000000004800L });
	public static final BitSet	FOLLOW_RPAREN_in_qualifierFlavorList589								= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_set_in_flavor0												= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_classDeclarationHeader_in_classDeclaration686				= new BitSet(new long[] { 0x0000000028000080L });
	public static final BitSet	FOLLOW_classDeclarationTrailer_in_classDeclaration688				= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_qualifierList_in_classDeclarationHeader1182					= new BitSet(new long[] { 0x0000000000010000L });
	public static final BitSet	FOLLOW_CLASS_in_classDeclarationHeader1184							= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_className_in_classDeclarationHeader1186						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_alias_in_classDeclarationTrailer1223							= new BitSet(new long[] { 0x0000000008000080L });
	public static final BitSet	FOLLOW_superClass_in_classDeclarationTrailer1230					= new BitSet(new long[] { 0x0000000008000000L });
	public static final BitSet	FOLLOW_LCURLY_in_classDeclarationTrailer1235						= new BitSet(new long[] { 0x0000FFFD10000270L });
	public static final BitSet	FOLLOW_classFeature_in_classDeclarationTrailer1237					= new BitSet(new long[] { 0x0000FFFD10000270L });
	public static final BitSet	FOLLOW_RCURLY_in_classDeclarationTrailer1240						= new BitSet(new long[] { 0x0000000000000100L });
	public static final BitSet	FOLLOW_SEMICOLON_in_classDeclarationTrailer1242						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_Identifier_in_className1270									= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_AS_in_alias1294												= new BitSet(new long[] { 0x0000000040000000L });
	public static final BitSet	FOLLOW_aliasIdentifier_in_alias1296									= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_DOLLAR_in_aliasIdentifier1314								= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_Identifier_in_aliasIdentifier1316							= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_COLON_in_superClass1338										= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_className_in_superClass1340									= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_classFeaturePiece_in_classFeature1367						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_qualifierList_in_classFeaturePiece1402						= new BitSet(new long[] { 0x0000FFFD00000270L });
	public static final BitSet	FOLLOW_featureBodyDeclaration_in_classFeaturePiece1406				= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_dataPropertyDeclaration_in_featureBodyDeclaration1521		= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_referencePropertyDeclaration_in_featureBodyDeclaration1535	= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_methodDeclaration_in_featureBodyDeclaration1549				= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_dataType_in_dataPropertyDeclaration1590						= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_nonReservedName_in_dataPropertyDeclaration1592				= new BitSet(new long[] { 0x0000000100000500L });
	public static final BitSet	FOLLOW_array_in_dataPropertyDeclaration1598							= new BitSet(new long[] { 0x0000000000000500L });
	public static final BitSet	FOLLOW_EQUALS_in_dataPropertyDeclaration1604						= new BitSet(new long[] { 0x003F000048000000L });
	public static final BitSet	FOLLOW_initializer_in_dataPropertyDeclaration1608					= new BitSet(new long[] { 0x0000000000000100L });
	public static final BitSet	FOLLOW_SEMICOLON_in_dataPropertyDeclaration1613						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_className_in_referencePropertyDeclaration1938				= new BitSet(new long[] { 0x0000000080000000L });
	public static final BitSet	FOLLOW_REF_in_referencePropertyDeclaration1940						= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_nonReservedName_in_referencePropertyDeclaration1942			= new BitSet(new long[] { 0x0000000100000100L });
	public static final BitSet	FOLLOW_array_in_referencePropertyDeclaration1948					= new BitSet(new long[] { 0x0000000000000100L });
	public static final BitSet	FOLLOW_SEMICOLON_in_referencePropertyDeclaration1953				= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_dataType_in_methodDeclaration2163							= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_methodName_in_methodDeclaration2165							= new BitSet(new long[] { 0x0000000000002000L });
	public static final BitSet	FOLLOW_LPAREN_in_methodDeclaration2167								= new BitSet(new long[] { 0x0000FFFD00004270L });
	public static final BitSet	FOLLOW_methodParameterList_in_methodDeclaration2170					= new BitSet(new long[] { 0x0000000000004000L });
	public static final BitSet	FOLLOW_RPAREN_in_methodDeclaration2175								= new BitSet(new long[] { 0x0000000000000100L });
	public static final BitSet	FOLLOW_SEMICOLON_in_methodDeclaration2177							= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_Identifier_in_methodName2396									= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_methodParameter_in_methodParameterList2431					= new BitSet(new long[] { 0x0000000000000802L });
	public static final BitSet	FOLLOW_COMMA_in_methodParameterList2434								= new BitSet(new long[] { 0x0000FFFD00000270L });
	public static final BitSet	FOLLOW_methodParameter_in_methodParameterList2436					= new BitSet(new long[] { 0x0000000000000802L });
	public static final BitSet	FOLLOW_methodParm_in_methodParameter2469							= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_methodRef_in_methodParameter2483								= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_qualifierList_in_methodParm2509								= new BitSet(new long[] { 0x0000FFFC00000000L });
	public static final BitSet	FOLLOW_dataType_in_methodParm2512									= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_nonReservedName_in_methodParm2514							= new BitSet(new long[] { 0x0000000100000002L });
	public static final BitSet	FOLLOW_array_in_methodParm2520										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_qualifierList_in_methodRef2686								= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_className_in_methodRef2689									= new BitSet(new long[] { 0x0000000080000000L });
	public static final BitSet	FOLLOW_REF_in_methodRef2691											= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_nonReservedName_in_methodRef2693								= new BitSet(new long[] { 0x0000000100000002L });
	public static final BitSet	FOLLOW_array_in_methodRef2699										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_Identifier_in_nonReservedName2875							= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_LBRACK_in_qualifierList2924									= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_qualifier_in_qualifierList2926								= new BitSet(new long[] { 0x0000000200000800L });
	public static final BitSet	FOLLOW_COMMA_in_qualifierList2929									= new BitSet(new long[] { 0x0000000000000200L });
	public static final BitSet	FOLLOW_qualifier_in_qualifierList2931								= new BitSet(new long[] { 0x0000000200000800L });
	public static final BitSet	FOLLOW_RBRACK_in_qualifierList2935									= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_Identifier_in_qualifier2959									= new BitSet(new long[] { 0x0000000008002082L });
	public static final BitSet	FOLLOW_qualifierParameter_in_qualifier2963							= new BitSet(new long[] { 0x0000000000000082L });
	public static final BitSet	FOLLOW_flavorList_in_qualifier2966									= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_LPAREN_in_qualifierParameter2997								= new BitSet(new long[] { 0x003F000000000000L });
	public static final BitSet	FOLLOW_constantValue_in_qualifierParameter3001						= new BitSet(new long[] { 0x0000000000004000L });
	public static final BitSet	FOLLOW_stringConstant_in_qualifierParameter3005						= new BitSet(new long[] { 0x0000000000004000L });
	public static final BitSet	FOLLOW_RPAREN_in_qualifierParameter3011								= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_arrayInitializer_in_qualifierParameter3021					= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_COLON_in_flavorList3045										= new BitSet(new long[] { 0x0000000007C00000L });
	public static final BitSet	FOLLOW_flavor_in_flavorList3047										= new BitSet(new long[] { 0x0000000007C00002L });
	public static final BitSet	FOLLOW_BOOLEAN_in_dataType3073										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_CHAR16_in_dataType3090										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_DATETIME_in_dataType3108										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_REAL32_in_dataType3124										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_REAL64_in_dataType3142										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_SINT16_in_dataType3160										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_SINT32_in_dataType3178										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_SINT64_in_dataType3196										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_SINT8_in_dataType3214										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_STRING_in_dataType3233										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_UINT16_in_dataType3251										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_UINT32_in_dataType3269										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_UINT64_in_dataType3287										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_UINT8_in_dataType3305										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_LBRACK_in_array3334											= new BitSet(new long[] { 0x0001000200000000L });
	public static final BitSet	FOLLOW_IntegralConstant_in_array3336								= new BitSet(new long[] { 0x0000000200000000L });
	public static final BitSet	FOLLOW_RBRACK_in_array3339											= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_constantValue_in_qualifierInitializer3365					= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_DoubleQuotedString_in_qualifierInitializer3373				= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_constantValue_in_initializer3404								= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_DoubleQuotedString_in_initializer3418						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_arrayInitializer_in_initializer3432							= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_referenceInitializer_in_initializer3450						= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_LCURLY_in_arrayInitializer3487								= new BitSet(new long[] { 0x003F000000000000L });
	public static final BitSet	FOLLOW_constantValue_in_arrayInitializer3494						= new BitSet(new long[] { 0x0000000010000800L });
	public static final BitSet	FOLLOW_stringConstant_in_arrayInitializer3502						= new BitSet(new long[] { 0x0000000010000800L });
	public static final BitSet	FOLLOW_COMMA_in_arrayInitializer3516								= new BitSet(new long[] { 0x003F000000000000L });
	public static final BitSet	FOLLOW_constantValue_in_arrayInitializer3522						= new BitSet(new long[] { 0x0000000010000800L });
	public static final BitSet	FOLLOW_stringConstant_in_arrayInitializer3530						= new BitSet(new long[] { 0x0000000010000800L });
	public static final BitSet	FOLLOW_RCURLY_in_arrayInitializer3539								= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_set_in_constantValue3560										= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_aliasIdentifier_in_referenceInitializer3636					= new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet	FOLLOW_DoubleQuotedString_in_stringConstant3660						= new BitSet(new long[] { 0x0002000000000002L });

}