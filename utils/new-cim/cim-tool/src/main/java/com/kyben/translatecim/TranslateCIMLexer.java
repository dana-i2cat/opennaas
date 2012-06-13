// $ANTLR 3.1.3 Mar 17, 2009 19:23:44 com\\kyben\\translatecim\\TranslateCIM.g 2012-05-23 17:28:23

    package com.kyben.translatecim;

    import java.io.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class TranslateCIMLexer extends Lexer {
    public static final int DOLLAR=30;
    public static final int OctalConstant=71;
    public static final int SINT16=39;
    public static final int CLASS=16;
    public static final int LBRACK=32;
    public static final int PRAGMAINCLUDE=58;
    public static final int UINT8=47;
    public static final int InlineComment=63;
    public static final int HexConstant=72;
    public static final int FLAVOR=21;
    public static final int DecimalConstant=69;
    public static final int StupidEscapeSequence=66;
    public static final int HexEscape=67;
    public static final int Exponent=73;
    public static final int EQUALS=10;
    public static final int DOUBLEQUOTE=55;
    public static final int EOF=-1;
    public static final int HexDigit=68;
    public static final int Identifier=9;
    public static final int LPAREN=13;
    public static final int AS=29;
    public static final int SOURCETYPE=61;
    public static final int DoubleQuotedString=49;
    public static final int RPAREN=14;
    public static final int BOOLEAN=34;
    public static final int SINT64=41;
    public static final int IntegralConstant=48;
    public static final int SCOPE=12;
    public static final int COMMA=11;
    public static final int SINGLEQUOTE=60;
    public static final int PARAMETER=18;
    public static final int UINT16=44;
    public static final int PRAGMALOCALE=59;
    public static final int PLUS=57;
    public static final int WhiteSpace=62;
    public static final int SINT32=40;
    public static final int CharacterConstant=50;
    public static final int MultiLineComment=64;
    public static final int BinaryConstant=70;
    public static final int RBRACK=33;
    public static final int TRANSLATABLE=26;
    public static final int REAL64=38;
    public static final int QUALIFIER=6;
    public static final int DATETIME=36;
    public static final int NULL=53;
    public static final int RESTRICTED=25;
    public static final int DISABLEOVERRIDE=23;
    public static final int REFERENCE=20;
    public static final int LCURLY=27;
    public static final int PragmaInclude=4;
    public static final int SEMICOLON=8;
    public static final int ENABLEOVERRIDE=22;
    public static final int MINUS=56;
    public static final int TRUE=51;
    public static final int UINT64=46;
    public static final int REF=31;
    public static final int COLON=7;
    public static final int SINT8=42;
    public static final int ANY=15;
    public static final int FloatingPointConstant=74;
    public static final int PROPERTY=19;
    public static final int REAL32=37;
    public static final int TOSUBCLASS=24;
    public static final int RCURLY=28;
    public static final int PragmaLocale=5;
    public static final int UINT32=45;
    public static final int FALSE=52;
    public static final int CHAR16=35;
    public static final int METHOD=17;
    public static final int EscapeSequence=65;
    public static final int STRING=43;
    public static final int BACKSLASH=54;

        // This stolen straight from the ANTLR Wiki page titled "How do I implement include files?"
        class SaveStruct {
            SaveStruct(CharStream input) {
                this.input = input;
                this.marker = input.mark();
            }
            public CharStream input;
            public int marker;
        }

        Stack<SaveStruct> includes = new Stack<SaveStruct>();

        // We should override this method for handling EOF of included file
         public Token nextToken(){
           Token token = super.nextToken();

           if(token==Token.EOF_TOKEN && !includes.empty()){
            // We've got EOF and have non empty stack.
             SaveStruct ss = includes.pop();
             setCharStream(ss.input);
             input.rewind(ss.marker);
             //this should be used instead of super [like below] to handle exits from nested includes
             //it matters, when the 'include' token is the last in previous stream (using super, lexer 'crashes' returning EOF token)
             token = this.nextToken();
           }

          // Skip first token after switching on another input.
          // You need to use this rather than super as there may be nested include files
           if(((CommonToken)token).getStartIndex() < 0)
             token = this.nextToken();

           return token;
         }



    // delegates
    // delegators

    public TranslateCIMLexer() {;} 
    public TranslateCIMLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public TranslateCIMLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "com\\kyben\\translatecim\\TranslateCIM.g"; }

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1042:17: ( 'any' | 'Any' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='a') ) {
                alt1=1;
            }
            else if ( (LA1_0=='A') ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1042:19: 'any'
                    {
                    match("any"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1042:27: 'Any'
                    {
                    match("Any"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ANY"

    // $ANTLR start "AS"
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1043:17: ( 'as' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1043:19: 'as'
            {
            match("as"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AS"

    // $ANTLR start "BACKSLASH"
    public final void mBACKSLASH() throws RecognitionException {
        try {
            int _type = BACKSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1044:17: ( '\\\\' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1044:19: '\\\\'
            {
            match('\\'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BACKSLASH"

    // $ANTLR start "BOOLEAN"
    public final void mBOOLEAN() throws RecognitionException {
        try {
            int _type = BOOLEAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1045:17: ( 'boolean' | 'Boolean' )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='b') ) {
                alt2=1;
            }
            else if ( (LA2_0=='B') ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1045:19: 'boolean'
                    {
                    match("boolean"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1045:31: 'Boolean'
                    {
                    match("Boolean"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOLEAN"

    // $ANTLR start "CHAR16"
    public final void mCHAR16() throws RecognitionException {
        try {
            int _type = CHAR16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1046:17: ( 'char16' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1046:19: 'char16'
            {
            match("char16"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR16"

    // $ANTLR start "CLASS"
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1047:17: ( 'class' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1047:19: 'class'
            {
            match("class"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLASS"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1048:17: ( ':' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1048:19: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1049:17: ( ',' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1049:19: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "DATETIME"
    public final void mDATETIME() throws RecognitionException {
        try {
            int _type = DATETIME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1050:17: ( 'datetime' | 'dateTime' )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='d') ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1=='a') ) {
                    int LA3_2 = input.LA(3);

                    if ( (LA3_2=='t') ) {
                        int LA3_3 = input.LA(4);

                        if ( (LA3_3=='e') ) {
                            int LA3_4 = input.LA(5);

                            if ( (LA3_4=='t') ) {
                                alt3=1;
                            }
                            else if ( (LA3_4=='T') ) {
                                alt3=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 3, 4, input);

                                throw nvae;
                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 3, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 3, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1050:19: 'datetime'
                    {
                    match("datetime"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1050:32: 'dateTime'
                    {
                    match("dateTime"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DATETIME"

    // $ANTLR start "DISABLEOVERRIDE"
    public final void mDISABLEOVERRIDE() throws RecognitionException {
        try {
            int _type = DISABLEOVERRIDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1051:17: ( 'DisableOverride' | 'disableoverride' )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='D') ) {
                alt4=1;
            }
            else if ( (LA4_0=='d') ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1051:19: 'DisableOverride'
                    {
                    match("DisableOverride"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1051:39: 'disableoverride'
                    {
                    match("disableoverride"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DISABLEOVERRIDE"

    // $ANTLR start "DOLLAR"
    public final void mDOLLAR() throws RecognitionException {
        try {
            int _type = DOLLAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1052:17: ( '$' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1052:19: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOLLAR"

    // $ANTLR start "DOUBLEQUOTE"
    public final void mDOUBLEQUOTE() throws RecognitionException {
        try {
            int _type = DOUBLEQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1053:17: ( '\"' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1053:19: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLEQUOTE"

    // $ANTLR start "ENABLEOVERRIDE"
    public final void mENABLEOVERRIDE() throws RecognitionException {
        try {
            int _type = ENABLEOVERRIDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1054:17: ( 'EnableOverride' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1054:19: 'EnableOverride'
            {
            match("EnableOverride"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENABLEOVERRIDE"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1055:17: ( '=' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1055:19: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1056:17: ( 'False' | 'false' | 'FALSE' )
            int alt5=3;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='F') ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1=='a') ) {
                    alt5=1;
                }
                else if ( (LA5_1=='A') ) {
                    alt5=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA5_0=='f') ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1056:19: 'False'
                    {
                    match("False"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1056:29: 'false'
                    {
                    match("false"); 


                    }
                    break;
                case 3 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1056:39: 'FALSE'
                    {
                    match("FALSE"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "FLAVOR"
    public final void mFLAVOR() throws RecognitionException {
        try {
            int _type = FLAVOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1057:17: ( 'Flavor' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1057:19: 'Flavor'
            {
            match("Flavor"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLAVOR"

    // $ANTLR start "LBRACK"
    public final void mLBRACK() throws RecognitionException {
        try {
            int _type = LBRACK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1058:17: ( '[' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1058:19: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACK"

    // $ANTLR start "LCURLY"
    public final void mLCURLY() throws RecognitionException {
        try {
            int _type = LCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1059:17: ( '\\{' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1059:19: '\\{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LCURLY"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1060:17: ( '(' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1060:19: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "METHOD"
    public final void mMETHOD() throws RecognitionException {
        try {
            int _type = METHOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1061:17: ( 'Method' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1061:19: 'Method'
            {
            match("Method"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "METHOD"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1062:17: ( '-' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1062:19: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1063:17: ( 'null' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1063:19: 'null'
            {
            match("null"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "PARAMETER"
    public final void mPARAMETER() throws RecognitionException {
        try {
            int _type = PARAMETER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1064:17: ( 'Parameter' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1064:19: 'Parameter'
            {
            match("Parameter"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PARAMETER"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1065:17: ( '+' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1065:19: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "PRAGMAINCLUDE"
    public final void mPRAGMAINCLUDE() throws RecognitionException {
        try {
            int _type = PRAGMAINCLUDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1066:17: ( '#pragma include' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1066:19: '#pragma include'
            {
            match("#pragma include"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PRAGMAINCLUDE"

    // $ANTLR start "PRAGMALOCALE"
    public final void mPRAGMALOCALE() throws RecognitionException {
        try {
            int _type = PRAGMALOCALE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1067:17: ( '#pragma locale' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1067:19: '#pragma locale'
            {
            match("#pragma locale"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PRAGMALOCALE"

    // $ANTLR start "PROPERTY"
    public final void mPROPERTY() throws RecognitionException {
        try {
            int _type = PROPERTY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1068:17: ( 'Property' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1068:19: 'Property'
            {
            match("Property"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PROPERTY"

    // $ANTLR start "QUALIFIER"
    public final void mQUALIFIER() throws RecognitionException {
        try {
            int _type = QUALIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1069:17: ( 'Qualifier' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1069:19: 'Qualifier'
            {
            match("Qualifier"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUALIFIER"

    // $ANTLR start "RBRACK"
    public final void mRBRACK() throws RecognitionException {
        try {
            int _type = RBRACK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1070:17: ( ']' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1070:19: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACK"

    // $ANTLR start "RCURLY"
    public final void mRCURLY() throws RecognitionException {
        try {
            int _type = RCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1071:17: ( '\\}' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1071:19: '\\}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RCURLY"

    // $ANTLR start "REAL32"
    public final void mREAL32() throws RecognitionException {
        try {
            int _type = REAL32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1072:17: ( 'real32' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1072:19: 'real32'
            {
            match("real32"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REAL32"

    // $ANTLR start "REAL64"
    public final void mREAL64() throws RecognitionException {
        try {
            int _type = REAL64;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1073:17: ( 'real64' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1073:19: 'real64'
            {
            match("real64"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REAL64"

    // $ANTLR start "REF"
    public final void mREF() throws RecognitionException {
        try {
            int _type = REF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1074:17: ( 'REF' | 'ref' )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='R') ) {
                alt6=1;
            }
            else if ( (LA6_0=='r') ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1074:19: 'REF'
                    {
                    match("REF"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1074:27: 'ref'
                    {
                    match("ref"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REF"

    // $ANTLR start "REFERENCE"
    public final void mREFERENCE() throws RecognitionException {
        try {
            int _type = REFERENCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1075:17: ( 'Reference' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1075:19: 'Reference'
            {
            match("Reference"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REFERENCE"

    // $ANTLR start "RESTRICTED"
    public final void mRESTRICTED() throws RecognitionException {
        try {
            int _type = RESTRICTED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1076:17: ( 'Restricted' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1076:19: 'Restricted'
            {
            match("Restricted"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RESTRICTED"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1077:17: ( ')' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1077:19: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "SCOPE"
    public final void mSCOPE() throws RecognitionException {
        try {
            int _type = SCOPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1078:17: ( 'Scope' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1078:19: 'Scope'
            {
            match("Scope"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SCOPE"

    // $ANTLR start "SEMICOLON"
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1079:17: ( ';' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1079:19: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMICOLON"

    // $ANTLR start "SINGLEQUOTE"
    public final void mSINGLEQUOTE() throws RecognitionException {
        try {
            int _type = SINGLEQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1080:17: ( '\\'' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1080:19: '\\''
            {
            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SINGLEQUOTE"

    // $ANTLR start "SINT8"
    public final void mSINT8() throws RecognitionException {
        try {
            int _type = SINT8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1081:17: ( 'sint8' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1081:19: 'sint8'
            {
            match("sint8"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SINT8"

    // $ANTLR start "SINT16"
    public final void mSINT16() throws RecognitionException {
        try {
            int _type = SINT16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1082:17: ( 'sint16' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1082:19: 'sint16'
            {
            match("sint16"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SINT16"

    // $ANTLR start "SINT32"
    public final void mSINT32() throws RecognitionException {
        try {
            int _type = SINT32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1083:17: ( 'sint32' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1083:19: 'sint32'
            {
            match("sint32"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SINT32"

    // $ANTLR start "SINT64"
    public final void mSINT64() throws RecognitionException {
        try {
            int _type = SINT64;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1084:17: ( 'sint64' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1084:19: 'sint64'
            {
            match("sint64"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SINT64"

    // $ANTLR start "SOURCETYPE"
    public final void mSOURCETYPE() throws RecognitionException {
        try {
            int _type = SOURCETYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1085:17: ( 'Sourcetype' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1085:19: 'Sourcetype'
            {
            match("Sourcetype"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SOURCETYPE"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1086:17: ( 'string' | 'String' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='s') ) {
                alt7=1;
            }
            else if ( (LA7_0=='S') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1086:19: 'string'
                    {
                    match("string"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1086:30: 'String'
                    {
                    match("String"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "TOSUBCLASS"
    public final void mTOSUBCLASS() throws RecognitionException {
        try {
            int _type = TOSUBCLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1087:17: ( 'tosubclass' | 'ToSubclass' )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='t') ) {
                alt8=1;
            }
            else if ( (LA8_0=='T') ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1087:19: 'tosubclass'
                    {
                    match("tosubclass"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1087:34: 'ToSubclass'
                    {
                    match("ToSubclass"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TOSUBCLASS"

    // $ANTLR start "TRANSLATABLE"
    public final void mTRANSLATABLE() throws RecognitionException {
        try {
            int _type = TRANSLATABLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1088:17: ( 'Translatable' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1088:19: 'Translatable'
            {
            match("Translatable"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRANSLATABLE"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1089:17: ( 'True' | 'true' | 'TRUE' )
            int alt9=3;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='T') ) {
                int LA9_1 = input.LA(2);

                if ( (LA9_1=='r') ) {
                    alt9=1;
                }
                else if ( (LA9_1=='R') ) {
                    alt9=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA9_0=='t') ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1089:19: 'True'
                    {
                    match("True"); 


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1089:28: 'true'
                    {
                    match("true"); 


                    }
                    break;
                case 3 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1089:37: 'TRUE'
                    {
                    match("TRUE"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "UINT8"
    public final void mUINT8() throws RecognitionException {
        try {
            int _type = UINT8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1090:17: ( 'uint8' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1090:19: 'uint8'
            {
            match("uint8"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UINT8"

    // $ANTLR start "UINT16"
    public final void mUINT16() throws RecognitionException {
        try {
            int _type = UINT16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1091:17: ( 'uint16' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1091:19: 'uint16'
            {
            match("uint16"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UINT16"

    // $ANTLR start "UINT32"
    public final void mUINT32() throws RecognitionException {
        try {
            int _type = UINT32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1092:17: ( 'uint32' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1092:19: 'uint32'
            {
            match("uint32"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UINT32"

    // $ANTLR start "UINT64"
    public final void mUINT64() throws RecognitionException {
        try {
            int _type = UINT64;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1093:17: ( 'uint64' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1093:19: 'uint64'
            {
            match("uint64"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UINT64"

    // $ANTLR start "PragmaInclude"
    public final void mPragmaInclude() throws RecognitionException {
        try {
            int _type = PragmaInclude;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            Token f=null;

            // com\\kyben\\translatecim\\TranslateCIM.g:1096:5: ( PRAGMAINCLUDE WhiteSpace LPAREN f= DoubleQuotedString RPAREN )
            // com\\kyben\\translatecim\\TranslateCIM.g:1096:7: PRAGMAINCLUDE WhiteSpace LPAREN f= DoubleQuotedString RPAREN
            {
            mPRAGMAINCLUDE(); 
            mWhiteSpace(); 
            mLPAREN(); 
            int fStart2378 = getCharIndex();
            mDoubleQuotedString(); 
            f = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, fStart2378, getCharIndex()-1);
            mRPAREN(); 

                    String quotedIncludeFileName = f.getText();
                    String newIncludeFileName = quotedIncludeFileName.substring(1, quotedIncludeFileName.length()-1);

                    // In order to open the new include file, we have to get the current file's directory
                    String currentInputFileName = ((ANTLRFileStream)input).getSourceName();
                    File currentInputFile = new File(currentInputFileName);
                    String currentInputSubDirectory = currentInputFile.getParent();
                    String fullCurFileName = "";
                    try {
                        fullCurFileName = currentInputFile.getCanonicalPath();
                    } catch (IOException e) {
                        System.out.println("Couldn't get path" + e.getMessage());
                        System.exit(1);
                    }
                    currentInputFile = new File(fullCurFileName);
                    currentInputSubDirectory = currentInputFile.getParent();

                    String fullNewIncludeFileName = currentInputSubDirectory + '/' + newIncludeFileName;
                    
            //        System.out.println("PragmaInclude (lexer!): pushing into " + fullNewIncludeFileName);  // dbg

                    // This is stolen from the ANTLR Wiki page titled "How do I implement include files?",
                    // except that I added better error handling.
                    SaveStruct ss = new SaveStruct(input);
                    includes.push(ss);
                    // switch to the new input stream
                    try {
                        setCharStream(new ANTLRFileStream(fullNewIncludeFileName));
                    } catch(IOException e) {
                        RecognitionException r = new RecognitionException(input);
                        throw new CantReadIncludeFileException(
                               currentInputFile.getName() + "(" + r.line + ":" + r.charPositionInLine + ")" +
                               ": couldn't read include file " + e.getMessage());
                    }
                    // We have to call emit here because the call to setCharStream cleared the token stream,
                    // which means the parser won't see any tokens from the include statement itself.  The
                    // parser needs to see the include statement to learn the name of the input file, in
                    // order to create output files in the proper subdirectories.
                    emit(new CommonToken(PragmaInclude,newIncludeFileName));
                

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PragmaInclude"

    // $ANTLR start "PragmaLocale"
    public final void mPragmaLocale() throws RecognitionException {
        try {
            int _type = PragmaLocale;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            Token DoubleQuotedString1=null;

            // com\\kyben\\translatecim\\TranslateCIM.g:1141:5: ( PRAGMALOCALE WhiteSpace LPAREN DoubleQuotedString RPAREN )
            // com\\kyben\\translatecim\\TranslateCIM.g:1141:7: PRAGMALOCALE WhiteSpace LPAREN DoubleQuotedString RPAREN
            {
            mPRAGMALOCALE(); 
            mWhiteSpace(); 
            mLPAREN(); 
            int DoubleQuotedString1Start2410 = getCharIndex();
            mDoubleQuotedString(); 
            DoubleQuotedString1 = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, DoubleQuotedString1Start2410, getCharIndex()-1);
            mRPAREN(); 

                    System.out.println("PragmaLocale: ignoring pragma locale " + (DoubleQuotedString1!=null?DoubleQuotedString1.getText():null));
                

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PragmaLocale"

    // $ANTLR start "Identifier"
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1148:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // com\\kyben\\translatecim\\TranslateCIM.g:1148:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // com\\kyben\\translatecim\\TranslateCIM.g:1148:37: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>='0' && LA10_0<='9')||(LA10_0>='A' && LA10_0<='Z')||LA10_0=='_'||(LA10_0>='a' && LA10_0<='z')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Identifier"

    // $ANTLR start "WhiteSpace"
    public final void mWhiteSpace() throws RecognitionException {
        try {
            int _type = WhiteSpace;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1152:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // com\\kyben\\translatecim\\TranslateCIM.g:1152:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // com\\kyben\\translatecim\\TranslateCIM.g:1152:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>='\t' && LA11_0<='\n')||LA11_0=='\r'||LA11_0==' ') ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WhiteSpace"

    // $ANTLR start "InlineComment"
    public final void mInlineComment() throws RecognitionException {
        try {
            int _type = InlineComment;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1156:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1156:7: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 

            // com\\kyben\\translatecim\\TranslateCIM.g:1156:12: (~ ( '\\n' | '\\r' ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='\u0000' && LA12_0<='\t')||(LA12_0>='\u000B' && LA12_0<='\f')||(LA12_0>='\u000E' && LA12_0<='\uFFFF')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:1156:12: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            // com\\kyben\\translatecim\\TranslateCIM.g:1156:26: ( '\\r' )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='\r') ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1156:26: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "InlineComment"

    // $ANTLR start "MultiLineComment"
    public final void mMultiLineComment() throws RecognitionException {
        try {
            int _type = MultiLineComment;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1160:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // com\\kyben\\translatecim\\TranslateCIM.g:1160:7: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // com\\kyben\\translatecim\\TranslateCIM.g:1160:12: ( options {greedy=false; } : . )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0=='*') ) {
                    int LA14_1 = input.LA(2);

                    if ( (LA14_1=='/') ) {
                        alt14=2;
                    }
                    else if ( ((LA14_1>='\u0000' && LA14_1<='.')||(LA14_1>='0' && LA14_1<='\uFFFF')) ) {
                        alt14=1;
                    }


                }
                else if ( ((LA14_0>='\u0000' && LA14_0<=')')||(LA14_0>='+' && LA14_0<='\uFFFF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:1160:40: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            match("*/"); 

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MultiLineComment"

    // $ANTLR start "CharacterConstant"
    public final void mCharacterConstant() throws RecognitionException {
        try {
            int _type = CharacterConstant;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1164:5: ( SINGLEQUOTE ( EscapeSequence | ~ ( '\\\\' | '\\'' ) ) SINGLEQUOTE )
            // com\\kyben\\translatecim\\TranslateCIM.g:1164:7: SINGLEQUOTE ( EscapeSequence | ~ ( '\\\\' | '\\'' ) ) SINGLEQUOTE
            {
            mSINGLEQUOTE(); 
            // com\\kyben\\translatecim\\TranslateCIM.g:1164:19: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0=='\\') ) {
                alt15=1;
            }
            else if ( ((LA15_0>='\u0000' && LA15_0<='&')||(LA15_0>='(' && LA15_0<='[')||(LA15_0>=']' && LA15_0<='\uFFFF')) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1164:21: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1164:38: ~ ( '\\\\' | '\\'' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            mSINGLEQUOTE(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CharacterConstant"

    // $ANTLR start "DoubleQuotedString"
    public final void mDoubleQuotedString() throws RecognitionException {
        try {
            int _type = DoubleQuotedString;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1168:5: ( DOUBLEQUOTE ( EscapeSequence | StupidEscapeSequence | ~ ( '\\\\' | '\"' ) )* DOUBLEQUOTE )
            // com\\kyben\\translatecim\\TranslateCIM.g:1168:7: DOUBLEQUOTE ( EscapeSequence | StupidEscapeSequence | ~ ( '\\\\' | '\"' ) )* DOUBLEQUOTE
            {
            mDOUBLEQUOTE(); 
            // com\\kyben\\translatecim\\TranslateCIM.g:1168:19: ( EscapeSequence | StupidEscapeSequence | ~ ( '\\\\' | '\"' ) )*
            loop16:
            do {
                int alt16=4;
                int LA16_0 = input.LA(1);

                if ( (LA16_0=='\\') ) {
                    int LA16_2 = input.LA(2);

                    if ( (LA16_2=='\"'||LA16_2=='\''||LA16_2=='\\'||LA16_2=='b'||LA16_2=='f'||LA16_2=='n'||LA16_2=='r'||LA16_2=='t'||LA16_2=='x') ) {
                        alt16=1;
                    }
                    else if ( (LA16_2=='C'||LA16_2=='P') ) {
                        alt16=2;
                    }


                }
                else if ( ((LA16_0>='\u0000' && LA16_0<='!')||(LA16_0>='#' && LA16_0<='[')||(LA16_0>=']' && LA16_0<='\uFFFF')) ) {
                    alt16=3;
                }


                switch (alt16) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:1168:21: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:1168:38: StupidEscapeSequence
            	    {
            	    mStupidEscapeSequence(); 

            	    }
            	    break;
            	case 3 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:1168:61: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            mDOUBLEQUOTE(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DoubleQuotedString"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1173:5: ( BACKSLASH ( 'b' | 't' | 'n' | 'f' | 'r' | DOUBLEQUOTE | SINGLEQUOTE | BACKSLASH ) | HexEscape )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0=='\\') ) {
                int LA17_1 = input.LA(2);

                if ( (LA17_1=='x') ) {
                    alt17=2;
                }
                else if ( (LA17_1=='\"'||LA17_1=='\''||LA17_1=='\\'||LA17_1=='b'||LA17_1=='f'||LA17_1=='n'||LA17_1=='r'||LA17_1=='t') ) {
                    alt17=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1173:7: BACKSLASH ( 'b' | 't' | 'n' | 'f' | 'r' | DOUBLEQUOTE | SINGLEQUOTE | BACKSLASH )
                    {
                    mBACKSLASH(); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1174:7: HexEscape
                    {
                    mHexEscape(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "StupidEscapeSequence"
    public final void mStupidEscapeSequence() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1187:5: ( BACKSLASH ( 'C' | 'P' ) )
            // com\\kyben\\translatecim\\TranslateCIM.g:1187:7: BACKSLASH ( 'C' | 'P' )
            {
            mBACKSLASH(); 
            if ( input.LA(1)=='C'||input.LA(1)=='P' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "StupidEscapeSequence"

    // $ANTLR start "HexEscape"
    public final void mHexEscape() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1192:5: ( BACKSLASH 'x' HexDigit | BACKSLASH 'x' HexDigit HexDigit | BACKSLASH 'x' HexDigit HexDigit HexDigit | BACKSLASH 'x' HexDigit HexDigit HexDigit HexDigit )
            int alt18=4;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1192:7: BACKSLASH 'x' HexDigit
                    {
                    mBACKSLASH(); 
                    match('x'); 
                    mHexDigit(); 

                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1193:7: BACKSLASH 'x' HexDigit HexDigit
                    {
                    mBACKSLASH(); 
                    match('x'); 
                    mHexDigit(); 
                    mHexDigit(); 

                    }
                    break;
                case 3 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1194:7: BACKSLASH 'x' HexDigit HexDigit HexDigit
                    {
                    mBACKSLASH(); 
                    match('x'); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 

                    }
                    break;
                case 4 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1195:7: BACKSLASH 'x' HexDigit HexDigit HexDigit HexDigit
                    {
                    mBACKSLASH(); 
                    match('x'); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "HexEscape"

    // $ANTLR start "IntegralConstant"
    public final void mIntegralConstant() throws RecognitionException {
        try {
            int _type = IntegralConstant;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1199:5: ( ( MINUS | PLUS )? DecimalConstant | BinaryConstant | OctalConstant | HexConstant )
            int alt20=4;
            alt20 = dfa20.predict(input);
            switch (alt20) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1199:7: ( MINUS | PLUS )? DecimalConstant
                    {
                    // com\\kyben\\translatecim\\TranslateCIM.g:1199:7: ( MINUS | PLUS )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0=='+'||LA19_0=='-') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // com\\kyben\\translatecim\\TranslateCIM.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    mDecimalConstant(); 

                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1200:7: BinaryConstant
                    {
                    mBinaryConstant(); 

                    }
                    break;
                case 3 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1201:7: OctalConstant
                    {
                    mOctalConstant(); 

                    }
                    break;
                case 4 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1202:7: HexConstant
                    {
                    mHexConstant(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IntegralConstant"

    // $ANTLR start "DecimalConstant"
    public final void mDecimalConstant() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1207:5: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) )
            // com\\kyben\\translatecim\\TranslateCIM.g:1207:7: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            {
            // com\\kyben\\translatecim\\TranslateCIM.g:1207:7: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='0') ) {
                alt22=1;
            }
            else if ( ((LA22_0>='1' && LA22_0<='9')) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1207:8: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1207:14: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // com\\kyben\\translatecim\\TranslateCIM.g:1207:23: ( '0' .. '9' )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // com\\kyben\\translatecim\\TranslateCIM.g:1207:23: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);


                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "DecimalConstant"

    // $ANTLR start "BinaryConstant"
    public final void mBinaryConstant() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1211:5: ( ( '0' | '1' )+ ( 'b' | 'B' ) )
            // com\\kyben\\translatecim\\TranslateCIM.g:1211:7: ( '0' | '1' )+ ( 'b' | 'B' )
            {
            // com\\kyben\\translatecim\\TranslateCIM.g:1211:7: ( '0' | '1' )+
            int cnt23=0;
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>='0' && LA23_0<='1')) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='1') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);

            if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "BinaryConstant"

    // $ANTLR start "OctalConstant"
    public final void mOctalConstant() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1216:5: ( '0' ( '0' .. '7' )+ )
            // com\\kyben\\translatecim\\TranslateCIM.g:1216:7: '0' ( '0' .. '7' )+
            {
            match('0'); 
            // com\\kyben\\translatecim\\TranslateCIM.g:1216:11: ( '0' .. '7' )+
            int cnt24=0;
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>='0' && LA24_0<='7')) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:1216:12: '0' .. '7'
            	    {
            	    matchRange('0','7'); 

            	    }
            	    break;

            	default :
            	    if ( cnt24 >= 1 ) break loop24;
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        throw eee;
                }
                cnt24++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "OctalConstant"

    // $ANTLR start "HexConstant"
    public final void mHexConstant() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1220:5: ( '0' ( 'x' | 'X' ) ( HexDigit )+ )
            // com\\kyben\\translatecim\\TranslateCIM.g:1220:7: '0' ( 'x' | 'X' ) ( HexDigit )+
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // com\\kyben\\translatecim\\TranslateCIM.g:1220:21: ( HexDigit )+
            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>='0' && LA25_0<='9')||(LA25_0>='A' && LA25_0<='F')||(LA25_0>='a' && LA25_0<='f')) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:1220:21: HexDigit
            	    {
            	    mHexDigit(); 

            	    }
            	    break;

            	default :
            	    if ( cnt25 >= 1 ) break loop25;
                        EarlyExitException eee =
                            new EarlyExitException(25, input);
                        throw eee;
                }
                cnt25++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "HexConstant"

    // $ANTLR start "HexDigit"
    public final void mHexDigit() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1224:5: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // com\\kyben\\translatecim\\TranslateCIM.g:1224:7: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HexDigit"

    // $ANTLR start "FloatingPointConstant"
    public final void mFloatingPointConstant() throws RecognitionException {
        try {
            int _type = FloatingPointConstant;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\kyben\\translatecim\\TranslateCIM.g:1227:5: ( ( MINUS | PLUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent )
            int alt33=3;
            alt33 = dfa33.predict(input);
            switch (alt33) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1227:7: ( MINUS | PLUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )?
                    {
                    // com\\kyben\\translatecim\\TranslateCIM.g:1227:7: ( MINUS | PLUS )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0=='+'||LA26_0=='-') ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // com\\kyben\\translatecim\\TranslateCIM.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    // com\\kyben\\translatecim\\TranslateCIM.g:1227:23: ( '0' .. '9' )+
                    int cnt27=0;
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( ((LA27_0>='0' && LA27_0<='9')) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // com\\kyben\\translatecim\\TranslateCIM.g:1227:24: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt27 >= 1 ) break loop27;
                                EarlyExitException eee =
                                    new EarlyExitException(27, input);
                                throw eee;
                        }
                        cnt27++;
                    } while (true);

                    match('.'); 
                    // com\\kyben\\translatecim\\TranslateCIM.g:1227:39: ( '0' .. '9' )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( ((LA28_0>='0' && LA28_0<='9')) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // com\\kyben\\translatecim\\TranslateCIM.g:1227:40: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);

                    // com\\kyben\\translatecim\\TranslateCIM.g:1227:51: ( Exponent )?
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0=='E'||LA29_0=='e') ) {
                        alt29=1;
                    }
                    switch (alt29) {
                        case 1 :
                            // com\\kyben\\translatecim\\TranslateCIM.g:1227:51: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1228:7: '.' ( '0' .. '9' )+ ( Exponent )?
                    {
                    match('.'); 
                    // com\\kyben\\translatecim\\TranslateCIM.g:1228:11: ( '0' .. '9' )+
                    int cnt30=0;
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( ((LA30_0>='0' && LA30_0<='9')) ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // com\\kyben\\translatecim\\TranslateCIM.g:1228:12: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt30 >= 1 ) break loop30;
                                EarlyExitException eee =
                                    new EarlyExitException(30, input);
                                throw eee;
                        }
                        cnt30++;
                    } while (true);

                    // com\\kyben\\translatecim\\TranslateCIM.g:1228:23: ( Exponent )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0=='E'||LA31_0=='e') ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // com\\kyben\\translatecim\\TranslateCIM.g:1228:23: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:1229:7: ( '0' .. '9' )+ Exponent
                    {
                    // com\\kyben\\translatecim\\TranslateCIM.g:1229:7: ( '0' .. '9' )+
                    int cnt32=0;
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( ((LA32_0>='0' && LA32_0<='9')) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // com\\kyben\\translatecim\\TranslateCIM.g:1229:8: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt32 >= 1 ) break loop32;
                                EarlyExitException eee =
                                    new EarlyExitException(32, input);
                                throw eee;
                        }
                        cnt32++;
                    } while (true);

                    mExponent(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FloatingPointConstant"

    // $ANTLR start "Exponent"
    public final void mExponent() throws RecognitionException {
        try {
            // com\\kyben\\translatecim\\TranslateCIM.g:1233:10: ( ( 'e' | 'E' ) ( MINUS | PLUS )? ( '0' .. '9' )+ )
            // com\\kyben\\translatecim\\TranslateCIM.g:1233:12: ( 'e' | 'E' ) ( MINUS | PLUS )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // com\\kyben\\translatecim\\TranslateCIM.g:1233:22: ( MINUS | PLUS )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0=='+'||LA34_0=='-') ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // com\\kyben\\translatecim\\TranslateCIM.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // com\\kyben\\translatecim\\TranslateCIM.g:1233:38: ( '0' .. '9' )+
            int cnt35=0;
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( ((LA35_0>='0' && LA35_0<='9')) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // com\\kyben\\translatecim\\TranslateCIM.g:1233:39: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt35 >= 1 ) break loop35;
                        EarlyExitException eee =
                            new EarlyExitException(35, input);
                        throw eee;
                }
                cnt35++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "Exponent"

    public void mTokens() throws RecognitionException {
        // com\\kyben\\translatecim\\TranslateCIM.g:1:8: ( ANY | AS | BACKSLASH | BOOLEAN | CHAR16 | CLASS | COLON | COMMA | DATETIME | DISABLEOVERRIDE | DOLLAR | DOUBLEQUOTE | ENABLEOVERRIDE | EQUALS | FALSE | FLAVOR | LBRACK | LCURLY | LPAREN | METHOD | MINUS | NULL | PARAMETER | PLUS | PRAGMAINCLUDE | PRAGMALOCALE | PROPERTY | QUALIFIER | RBRACK | RCURLY | REAL32 | REAL64 | REF | REFERENCE | RESTRICTED | RPAREN | SCOPE | SEMICOLON | SINGLEQUOTE | SINT8 | SINT16 | SINT32 | SINT64 | SOURCETYPE | STRING | TOSUBCLASS | TRANSLATABLE | TRUE | UINT8 | UINT16 | UINT32 | UINT64 | PragmaInclude | PragmaLocale | Identifier | WhiteSpace | InlineComment | MultiLineComment | CharacterConstant | DoubleQuotedString | IntegralConstant | FloatingPointConstant )
        int alt36=62;
        alt36 = dfa36.predict(input);
        switch (alt36) {
            case 1 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:10: ANY
                {
                mANY(); 

                }
                break;
            case 2 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:14: AS
                {
                mAS(); 

                }
                break;
            case 3 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:17: BACKSLASH
                {
                mBACKSLASH(); 

                }
                break;
            case 4 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:27: BOOLEAN
                {
                mBOOLEAN(); 

                }
                break;
            case 5 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:35: CHAR16
                {
                mCHAR16(); 

                }
                break;
            case 6 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:42: CLASS
                {
                mCLASS(); 

                }
                break;
            case 7 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:48: COLON
                {
                mCOLON(); 

                }
                break;
            case 8 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:54: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 9 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:60: DATETIME
                {
                mDATETIME(); 

                }
                break;
            case 10 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:69: DISABLEOVERRIDE
                {
                mDISABLEOVERRIDE(); 

                }
                break;
            case 11 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:85: DOLLAR
                {
                mDOLLAR(); 

                }
                break;
            case 12 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:92: DOUBLEQUOTE
                {
                mDOUBLEQUOTE(); 

                }
                break;
            case 13 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:104: ENABLEOVERRIDE
                {
                mENABLEOVERRIDE(); 

                }
                break;
            case 14 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:119: EQUALS
                {
                mEQUALS(); 

                }
                break;
            case 15 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:126: FALSE
                {
                mFALSE(); 

                }
                break;
            case 16 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:132: FLAVOR
                {
                mFLAVOR(); 

                }
                break;
            case 17 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:139: LBRACK
                {
                mLBRACK(); 

                }
                break;
            case 18 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:146: LCURLY
                {
                mLCURLY(); 

                }
                break;
            case 19 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:153: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 20 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:160: METHOD
                {
                mMETHOD(); 

                }
                break;
            case 21 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:167: MINUS
                {
                mMINUS(); 

                }
                break;
            case 22 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:173: NULL
                {
                mNULL(); 

                }
                break;
            case 23 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:178: PARAMETER
                {
                mPARAMETER(); 

                }
                break;
            case 24 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:188: PLUS
                {
                mPLUS(); 

                }
                break;
            case 25 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:193: PRAGMAINCLUDE
                {
                mPRAGMAINCLUDE(); 

                }
                break;
            case 26 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:207: PRAGMALOCALE
                {
                mPRAGMALOCALE(); 

                }
                break;
            case 27 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:220: PROPERTY
                {
                mPROPERTY(); 

                }
                break;
            case 28 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:229: QUALIFIER
                {
                mQUALIFIER(); 

                }
                break;
            case 29 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:239: RBRACK
                {
                mRBRACK(); 

                }
                break;
            case 30 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:246: RCURLY
                {
                mRCURLY(); 

                }
                break;
            case 31 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:253: REAL32
                {
                mREAL32(); 

                }
                break;
            case 32 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:260: REAL64
                {
                mREAL64(); 

                }
                break;
            case 33 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:267: REF
                {
                mREF(); 

                }
                break;
            case 34 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:271: REFERENCE
                {
                mREFERENCE(); 

                }
                break;
            case 35 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:281: RESTRICTED
                {
                mRESTRICTED(); 

                }
                break;
            case 36 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:292: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 37 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:299: SCOPE
                {
                mSCOPE(); 

                }
                break;
            case 38 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:305: SEMICOLON
                {
                mSEMICOLON(); 

                }
                break;
            case 39 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:315: SINGLEQUOTE
                {
                mSINGLEQUOTE(); 

                }
                break;
            case 40 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:327: SINT8
                {
                mSINT8(); 

                }
                break;
            case 41 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:333: SINT16
                {
                mSINT16(); 

                }
                break;
            case 42 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:340: SINT32
                {
                mSINT32(); 

                }
                break;
            case 43 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:347: SINT64
                {
                mSINT64(); 

                }
                break;
            case 44 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:354: SOURCETYPE
                {
                mSOURCETYPE(); 

                }
                break;
            case 45 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:365: STRING
                {
                mSTRING(); 

                }
                break;
            case 46 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:372: TOSUBCLASS
                {
                mTOSUBCLASS(); 

                }
                break;
            case 47 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:383: TRANSLATABLE
                {
                mTRANSLATABLE(); 

                }
                break;
            case 48 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:396: TRUE
                {
                mTRUE(); 

                }
                break;
            case 49 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:401: UINT8
                {
                mUINT8(); 

                }
                break;
            case 50 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:407: UINT16
                {
                mUINT16(); 

                }
                break;
            case 51 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:414: UINT32
                {
                mUINT32(); 

                }
                break;
            case 52 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:421: UINT64
                {
                mUINT64(); 

                }
                break;
            case 53 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:428: PragmaInclude
                {
                mPragmaInclude(); 

                }
                break;
            case 54 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:442: PragmaLocale
                {
                mPragmaLocale(); 

                }
                break;
            case 55 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:455: Identifier
                {
                mIdentifier(); 

                }
                break;
            case 56 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:466: WhiteSpace
                {
                mWhiteSpace(); 

                }
                break;
            case 57 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:477: InlineComment
                {
                mInlineComment(); 

                }
                break;
            case 58 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:491: MultiLineComment
                {
                mMultiLineComment(); 

                }
                break;
            case 59 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:508: CharacterConstant
                {
                mCharacterConstant(); 

                }
                break;
            case 60 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:526: DoubleQuotedString
                {
                mDoubleQuotedString(); 

                }
                break;
            case 61 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:545: IntegralConstant
                {
                mIntegralConstant(); 

                }
                break;
            case 62 :
                // com\\kyben\\translatecim\\TranslateCIM.g:1:562: FloatingPointConstant
                {
                mFloatingPointConstant(); 

                }
                break;

        }

    }


    protected DFA18 dfa18 = new DFA18(this);
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA36 dfa36 = new DFA36(this);
    static final String DFA18_eotS =
        "\3\uffff\1\4\1\uffff\1\6\1\uffff\1\10\2\uffff";
    static final String DFA18_eofS =
        "\12\uffff";
    static final String DFA18_minS =
        "\1\134\1\170\2\60\1\uffff\1\60\1\uffff\1\60\2\uffff";
    static final String DFA18_maxS =
        "\1\134\1\170\2\146\1\uffff\1\146\1\uffff\1\146\2\uffff";
    static final String DFA18_acceptS =
        "\4\uffff\1\1\1\uffff\1\2\1\uffff\1\3\1\4";
    static final String DFA18_specialS =
        "\12\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\1",
            "\1\2",
            "\12\3\7\uffff\6\3\32\uffff\6\3",
            "\12\5\7\uffff\6\5\32\uffff\6\5",
            "",
            "\12\7\7\uffff\6\7\32\uffff\6\7",
            "",
            "\12\11\7\uffff\6\11\32\uffff\6\11",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
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
        public String getDescription() {
            return "1190:1: fragment HexEscape : ( BACKSLASH 'x' HexDigit | BACKSLASH 'x' HexDigit HexDigit | BACKSLASH 'x' HexDigit HexDigit HexDigit | BACKSLASH 'x' HexDigit HexDigit HexDigit HexDigit );";
        }
    }
    static final String DFA20_eotS =
        "\2\uffff\2\1\1\uffff\1\7\2\uffff\1\1";
    static final String DFA20_eofS =
        "\11\uffff";
    static final String DFA20_minS =
        "\1\53\1\uffff\2\60\1\uffff\1\60\2\uffff\1\60";
    static final String DFA20_maxS =
        "\1\71\1\uffff\1\170\1\142\1\uffff\1\142\2\uffff\1\142";
    static final String DFA20_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\1\uffff\1\2\1\3\1\uffff";
    static final String DFA20_specialS =
        "\11\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\1\1\uffff\1\1\2\uffff\1\2\1\3\10\1",
            "",
            "\2\5\6\7\12\uffff\1\6\25\uffff\1\4\11\uffff\1\6\25\uffff\1"+
            "\4",
            "\2\10\20\uffff\1\6\37\uffff\1\6",
            "",
            "\2\5\20\uffff\1\6\37\uffff\1\6",
            "",
            "",
            "\2\10\20\uffff\1\6\37\uffff\1\6"
    };

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "1198:1: IntegralConstant : ( ( MINUS | PLUS )? DecimalConstant | BinaryConstant | OctalConstant | HexConstant );";
        }
    }
    static final String DFA33_eotS =
        "\5\uffff";
    static final String DFA33_eofS =
        "\5\uffff";
    static final String DFA33_minS =
        "\1\53\1\uffff\1\56\2\uffff";
    static final String DFA33_maxS =
        "\1\71\1\uffff\1\145\2\uffff";
    static final String DFA33_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\1\3";
    static final String DFA33_specialS =
        "\5\uffff}>";
    static final String[] DFA33_transitionS = {
            "\1\1\1\uffff\1\1\1\3\1\uffff\12\2",
            "",
            "\1\1\1\uffff\12\2\13\uffff\1\4\37\uffff\1\4",
            "",
            ""
    };

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "1226:1: FloatingPointConstant : ( ( MINUS | PLUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent );";
        }
    }
    static final String DFA36_eotS =
        "\1\uffff\2\47\1\uffff\3\47\2\uffff\2\47\1\uffff\1\70\1\47\1\uffff"+
        "\2\47\3\uffff\1\47\1\101\2\47\1\106\1\uffff\1\47\2\uffff\2\47\1"+
        "\uffff\1\47\1\uffff\1\117\4\47\3\uffff\3\133\1\uffff\1\47\1\141"+
        "\10\47\2\uffff\6\47\1\133\1\uffff\1\133\3\47\2\uffff\7\47\2\uffff"+
        "\10\47\3\uffff\4\133\1\u0087\1\uffff\1\u0087\15\47\1\133\3\47\1"+
        "\uffff\2\47\2\u009b\16\47\1\uffff\15\47\1\u00b8\2\47\1\uffff\2\47"+
        "\1\uffff\10\47\1\u00ca\2\47\2\u00ca\4\47\1\u00d4\5\47\2\u00da\1"+
        "\47\1\u00da\1\47\1\uffff\2\47\1\uffff\5\47\1\u00e5\2\47\1\u00e8"+
        "\5\47\1\uffff\2\47\1\u00f0\5\47\1\u00f6\1\uffff\5\47\1\uffff\1\u00fc"+
        "\1\u00fd\2\47\1\uffff\1\47\1\u0102\1\u0103\2\47\1\uffff\1\47\1\u0107"+
        "\1\uffff\1\u0108\1\u0109\1\u010a\1\u0107\3\47\1\uffff\1\u010e\1"+
        "\u010f\1\u0110\2\u0111\1\uffff\5\47\2\uffff\2\47\1\uffff\1\47\2"+
        "\uffff\3\47\4\uffff\3\47\4\uffff\2\u0121\4\47\1\u0126\1\uffff\7"+
        "\47\1\uffff\3\47\1\u0133\3\uffff\1\u0136\1\u0137\10\47\5\uffff\1"+
        "\u0142\1\u0143\2\u0144\4\47\5\uffff\4\47\2\uffff\1\u0151\3\47\3"+
        "\uffff\2\47\1\u0159\1\uffff\1\u015b\2\u015d\1\uffff\1\u015e\5\uffff";
    static final String DFA36_eofS =
        "\u0160\uffff";
    static final String DFA36_minS =
        "\1\11\2\156\1\uffff\2\157\1\150\2\uffff\1\141\1\151\1\uffff\1\0"+
        "\1\156\1\uffff\1\101\1\141\3\uffff\1\145\1\60\1\165\1\141\1\60\1"+
        "\160\1\165\2\uffff\1\145\1\105\1\uffff\1\143\1\uffff\1\0\1\151\1"+
        "\157\1\122\1\151\2\uffff\1\52\3\56\1\uffff\1\171\1\60\1\171\2\157"+
        "\2\141\1\164\2\163\2\uffff\1\141\1\154\1\114\1\141\1\154\1\164\1"+
        "\56\1\uffff\1\56\1\154\1\162\1\157\1\uffff\1\162\2\141\1\106\1\146"+
        "\1\157\1\165\1\162\2\uffff\1\156\1\162\1\163\1\165\1\123\1\141\1"+
        "\125\1\156\3\uffff\4\56\1\60\1\uffff\1\60\2\154\1\162\1\163\1\145"+
        "\2\141\1\142\1\163\1\123\1\166\1\163\1\150\1\56\1\154\1\141\1\160"+
        "\1\141\2\154\2\60\1\145\1\164\1\160\1\162\1\151\1\164\1\151\1\165"+
        "\1\145\1\165\1\156\1\145\1\105\1\164\1\uffff\2\145\1\61\1\163\1"+
        "\124\2\142\1\154\1\145\1\105\1\157\1\145\1\157\1\60\1\155\1\145"+
        "\1\147\1\151\1\63\1\uffff\2\162\1\145\1\143\1\156\1\61\1\156\1\142"+
        "\1\60\1\142\1\163\2\60\1\61\2\141\1\66\1\60\2\151\2\154\1\145\2"+
        "\60\1\162\1\60\1\144\1\uffff\1\145\1\162\1\155\1\146\1\62\1\64\1"+
        "\145\1\151\1\60\1\145\1\147\1\60\1\66\1\62\1\64\1\147\1\143\1\uffff"+
        "\1\143\1\154\1\60\1\66\1\62\1\64\2\156\1\60\1\uffff\2\155\2\145"+
        "\1\117\1\uffff\2\60\2\164\1\141\1\151\2\60\1\156\1\143\1\uffff\1"+
        "\164\1\60\1\uffff\4\60\2\154\1\141\1\uffff\5\60\1\uffff\2\145\1"+
        "\157\1\117\1\166\2\uffff\1\145\1\171\1\40\1\145\2\uffff\1\143\1"+
        "\164\1\171\4\uffff\2\141\1\164\4\uffff\2\60\2\166\1\145\1\162\1"+
        "\60\1\151\1\162\2\145\1\160\2\163\1\141\1\uffff\2\145\1\162\1\60"+
        "\1\uffff\1\156\1\157\2\60\1\144\1\145\2\163\1\142\3\162\1\uffff"+
        "\2\143\2\uffff\4\60\1\154\2\162\1\151\1\154\1\141\3\uffff\1\145"+
        "\2\151\1\144\1\165\1\154\1\60\2\144\1\145\1\144\1\145\1\uffff\2"+
        "\145\1\60\1\145\1\11\2\60\1\uffff\1\11\5\uffff";
    static final String DFA36_maxS =
        "\1\175\1\163\1\156\1\uffff\2\157\1\154\2\uffff\2\151\1\uffff\1"+
        "\uffff\1\156\1\uffff\1\154\1\141\3\uffff\1\145\1\71\1\165\1\162"+
        "\1\71\1\160\1\165\2\uffff\2\145\1\uffff\1\164\1\uffff\1\uffff\1"+
        "\164\2\162\1\151\2\uffff\1\57\3\145\1\uffff\1\171\1\172\1\171\2"+
        "\157\2\141\1\164\2\163\2\uffff\1\141\1\154\1\114\1\141\1\154\1\164"+
        "\1\71\1\uffff\1\71\1\154\1\162\1\157\1\uffff\1\162\1\141\1\146\1"+
        "\106\1\163\1\157\1\165\1\162\2\uffff\1\156\1\162\1\163\1\165\1\123"+
        "\1\165\1\125\1\156\3\uffff\4\145\1\172\1\uffff\1\172\2\154\1\162"+
        "\1\163\1\145\2\141\1\142\1\163\1\123\1\166\1\163\1\150\1\71\1\154"+
        "\1\141\1\160\1\141\2\154\2\172\1\145\1\164\1\160\1\162\1\151\1\164"+
        "\1\151\1\165\1\145\1\165\1\156\1\145\1\105\1\164\1\uffff\2\145\1"+
        "\61\1\163\1\164\2\142\1\154\1\145\1\105\1\157\1\145\1\157\1\172"+
        "\1\155\1\145\1\147\1\151\1\66\1\uffff\2\162\1\145\1\143\1\156\1"+
        "\70\1\156\1\142\1\172\1\142\1\163\2\172\1\70\2\141\1\66\1\172\2"+
        "\151\2\154\1\145\2\172\1\162\1\172\1\144\1\uffff\1\145\1\162\1\155"+
        "\1\146\1\62\1\64\1\145\1\151\1\172\1\145\1\147\1\172\1\66\1\62\1"+
        "\64\1\147\1\143\1\uffff\1\143\1\154\1\172\1\66\1\62\1\64\2\156\1"+
        "\172\1\uffff\2\155\2\145\1\117\1\uffff\2\172\2\164\1\141\1\151\2"+
        "\172\1\156\1\143\1\uffff\1\164\1\172\1\uffff\4\172\2\154\1\141\1"+
        "\uffff\5\172\1\uffff\2\145\1\157\1\117\1\166\2\uffff\1\145\1\171"+
        "\1\40\1\145\2\uffff\1\143\1\164\1\171\4\uffff\2\141\1\164\4\uffff"+
        "\2\172\2\166\1\145\1\162\1\172\1\154\1\162\2\145\1\160\2\163\1\141"+
        "\1\uffff\2\145\1\162\1\172\1\uffff\1\156\1\157\2\172\1\144\1\145"+
        "\2\163\1\142\3\162\1\uffff\2\143\2\uffff\4\172\1\154\2\162\1\151"+
        "\1\154\1\141\3\uffff\1\145\2\151\1\144\1\165\1\154\1\172\2\144\1"+
        "\145\1\144\1\145\1\uffff\2\145\1\172\1\145\1\40\2\172\1\uffff\1"+
        "\40\5\uffff";
    static final String DFA36_acceptS =
        "\3\uffff\1\3\3\uffff\1\7\1\10\2\uffff\1\13\2\uffff\1\16\2\uffff"+
        "\1\21\1\22\1\23\7\uffff\1\35\1\36\2\uffff\1\44\1\uffff\1\46\5\uffff"+
        "\1\67\1\70\4\uffff\1\76\12\uffff\1\14\1\74\7\uffff\1\25\4\uffff"+
        "\1\30\10\uffff\1\47\1\73\10\uffff\1\71\1\72\1\75\5\uffff\1\2\45"+
        "\uffff\1\1\23\uffff\1\41\34\uffff\1\26\21\uffff\1\60\11\uffff\1"+
        "\6\5\uffff\1\17\12\uffff\1\45\2\uffff\1\50\7\uffff\1\61\5\uffff"+
        "\1\5\5\uffff\1\20\1\24\4\uffff\1\37\1\40\3\uffff\1\55\1\51\1\52"+
        "\1\53\3\uffff\1\62\1\63\1\64\1\4\17\uffff\1\11\4\uffff\1\33\14\uffff"+
        "\1\27\2\uffff\1\34\1\42\12\uffff\1\43\1\54\1\56\14\uffff\1\57\7"+
        "\uffff\1\15\1\uffff\1\32\1\66\1\12\1\31\1\65";
    static final String DFA36_specialS =
        "\14\uffff\1\0\25\uffff\1\1\u013d\uffff}>";
    static final String[] DFA36_transitionS = {
            "\2\50\2\uffff\1\50\22\uffff\1\50\1\uffff\1\14\1\31\1\13\2\uffff"+
            "\1\42\1\23\1\37\1\uffff\1\30\1\10\1\25\1\55\1\51\1\52\1\53\10"+
            "\54\1\7\1\41\1\uffff\1\16\3\uffff\1\2\1\5\1\47\1\12\1\15\1\17"+
            "\6\47\1\24\2\47\1\27\1\32\1\36\1\40\1\45\6\47\1\21\1\3\1\33"+
            "\1\uffff\1\47\1\uffff\1\1\1\4\1\6\1\11\1\47\1\20\7\47\1\26\3"+
            "\47\1\35\1\43\1\44\1\46\5\47\1\22\1\uffff\1\34",
            "\1\56\4\uffff\1\57",
            "\1\60",
            "",
            "\1\61",
            "\1\62",
            "\1\63\3\uffff\1\64",
            "",
            "",
            "\1\65\7\uffff\1\66",
            "\1\67",
            "",
            "\0\71",
            "\1\72",
            "",
            "\1\74\37\uffff\1\73\12\uffff\1\75",
            "\1\76",
            "",
            "",
            "",
            "\1\77",
            "\1\100\11\102",
            "\1\103",
            "\1\104\20\uffff\1\105",
            "\1\100\11\102",
            "\1\107",
            "\1\110",
            "",
            "",
            "\1\111",
            "\1\112\37\uffff\1\113",
            "",
            "\1\114\13\uffff\1\115\4\uffff\1\116",
            "",
            "\47\120\1\uffff\uffd8\120",
            "\1\121\12\uffff\1\122",
            "\1\123\2\uffff\1\124",
            "\1\127\34\uffff\1\125\2\uffff\1\126",
            "\1\130",
            "",
            "",
            "\1\132\4\uffff\1\131",
            "\1\55\1\uffff\2\134\6\135\2\55\13\uffff\1\55\37\uffff\1\55",
            "\1\55\1\uffff\2\136\10\137\13\uffff\1\55\37\uffff\1\55",
            "\1\55\1\uffff\12\137\13\uffff\1\55\37\uffff\1\55",
            "",
            "\1\140",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "",
            "",
            "\1\152",
            "\1\153",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\157",
            "\1\55\1\uffff\12\55",
            "",
            "\1\55\1\uffff\12\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "",
            "\1\164",
            "\1\165",
            "\1\166\4\uffff\1\167",
            "\1\170",
            "\1\171\14\uffff\1\172",
            "\1\173",
            "\1\174",
            "\1\175",
            "",
            "",
            "\1\176",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\1\u0083\23\uffff\1\u0084",
            "\1\u0085",
            "\1\u0086",
            "",
            "",
            "",
            "\1\55\1\uffff\2\134\6\135\2\55\13\uffff\1\55\37\uffff\1\55",
            "\1\55\1\uffff\10\135\2\55\13\uffff\1\55\37\uffff\1\55",
            "\1\55\1\uffff\2\136\10\137\13\uffff\1\55\37\uffff\1\55",
            "\1\55\1\uffff\12\137\13\uffff\1\55\37\uffff\1\55",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\55\1\uffff\12\160",
            "\1\u0095",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u009c",
            "\1\u009d",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "",
            "\1\u00aa",
            "\1\u00ab",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00af\37\uffff\1\u00ae",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd\2\uffff\1\u00be",
            "",
            "\1\u00bf",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "\1\u00c3",
            "\1\u00c5\1\uffff\1\u00c6\2\uffff\1\u00c7\1\uffff\1\u00c4",
            "\1\u00c8",
            "\1\u00c9",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00cb",
            "\1\u00cc",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00ce\1\uffff\1\u00cf\2\uffff\1\u00d0\1\uffff\1\u00cd",
            "\1\u00d1",
            "\1\u00d2",
            "\1\u00d3",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00db",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00dc",
            "",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "\1\u00e0",
            "\1\u00e1",
            "\1\u00e2",
            "\1\u00e3",
            "\1\u00e4",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00e6",
            "\1\u00e7",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00e9",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "",
            "\1\u00ee",
            "\1\u00ef",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00f1",
            "\1\u00f2",
            "\1\u00f3",
            "\1\u00f4",
            "\1\u00f5",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "",
            "\1\u00f7",
            "\1\u00f8",
            "\1\u00f9",
            "\1\u00fa",
            "\1\u00fb",
            "",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u0104",
            "\1\u0105",
            "",
            "\1\u0106",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u010b",
            "\1\u010c",
            "\1\u010d",
            "",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "",
            "\1\u0112",
            "\1\u0113",
            "\1\u0114",
            "\1\u0115",
            "\1\u0116",
            "",
            "",
            "\1\u0117",
            "\1\u0118",
            "\1\u0119",
            "\1\u011a",
            "",
            "",
            "\1\u011b",
            "\1\u011c",
            "\1\u011d",
            "",
            "",
            "",
            "",
            "\1\u011e",
            "\1\u011f",
            "\1\u0120",
            "",
            "",
            "",
            "",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u0122",
            "\1\u0123",
            "\1\u0124",
            "\1\u0125",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u0127\2\uffff\1\u0128",
            "\1\u0129",
            "\1\u012a",
            "\1\u012b",
            "\1\u012c",
            "\1\u012d",
            "\1\u012e",
            "\1\u012f",
            "",
            "\1\u0130",
            "\1\u0131",
            "\1\u0132",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "",
            "\1\u0134",
            "\1\u0135",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u0138",
            "\1\u0139",
            "\1\u013a",
            "\1\u013b",
            "\1\u013c",
            "\1\u013d",
            "\1\u013e",
            "\1\u013f",
            "",
            "\1\u0140",
            "\1\u0141",
            "",
            "",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u0145",
            "\1\u0146",
            "\1\u0147",
            "\1\u0148",
            "\1\u0149",
            "\1\u014a",
            "",
            "",
            "",
            "\1\u014b",
            "\1\u014c",
            "\1\u014d",
            "\1\u014e",
            "\1\u014f",
            "\1\u0150",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u0152",
            "\1\u0153",
            "\1\u0154",
            "\1\u0155",
            "\1\u0156",
            "",
            "\1\u0157",
            "\1\u0158",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\1\u015a",
            "\2\u015c\2\uffff\1\u015c\22\uffff\1\u015c",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32\47",
            "",
            "\2\u015f\2\uffff\1\u015f\22\uffff\1\u015f",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA36_eot = DFA.unpackEncodedString(DFA36_eotS);
    static final short[] DFA36_eof = DFA.unpackEncodedString(DFA36_eofS);
    static final char[] DFA36_min = DFA.unpackEncodedStringToUnsignedChars(DFA36_minS);
    static final char[] DFA36_max = DFA.unpackEncodedStringToUnsignedChars(DFA36_maxS);
    static final short[] DFA36_accept = DFA.unpackEncodedString(DFA36_acceptS);
    static final short[] DFA36_special = DFA.unpackEncodedString(DFA36_specialS);
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA36_transition[i] = DFA.unpackEncodedString(DFA36_transitionS[i]);
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ANY | AS | BACKSLASH | BOOLEAN | CHAR16 | CLASS | COLON | COMMA | DATETIME | DISABLEOVERRIDE | DOLLAR | DOUBLEQUOTE | ENABLEOVERRIDE | EQUALS | FALSE | FLAVOR | LBRACK | LCURLY | LPAREN | METHOD | MINUS | NULL | PARAMETER | PLUS | PRAGMAINCLUDE | PRAGMALOCALE | PROPERTY | QUALIFIER | RBRACK | RCURLY | REAL32 | REAL64 | REF | REFERENCE | RESTRICTED | RPAREN | SCOPE | SEMICOLON | SINGLEQUOTE | SINT8 | SINT16 | SINT32 | SINT64 | SOURCETYPE | STRING | TOSUBCLASS | TRANSLATABLE | TRUE | UINT8 | UINT16 | UINT32 | UINT64 | PragmaInclude | PragmaLocale | Identifier | WhiteSpace | InlineComment | MultiLineComment | CharacterConstant | DoubleQuotedString | IntegralConstant | FloatingPointConstant );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA36_12 = input.LA(1);

                        s = -1;
                        if ( ((LA36_12>='\u0000' && LA36_12<='\uFFFF')) ) {s = 57;}

                        else s = 56;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA36_34 = input.LA(1);

                        s = -1;
                        if ( ((LA36_34>='\u0000' && LA36_34<='&')||(LA36_34>='(' && LA36_34<='\uFFFF')) ) {s = 80;}

                        else s = 79;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 36, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}