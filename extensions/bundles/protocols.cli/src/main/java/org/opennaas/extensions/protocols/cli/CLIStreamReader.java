package org.opennaas.extensions.protocols.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author edu
 *
 */
public class CLIStreamReader {
	
	/** Logger */
    static private Logger logger = LoggerFactory.getLogger(CLIStreamReader.class);
    
	/** The prompts used to detect the end of the message **/
    private List<String> prompts;
    
    /** Contains a pair of <intermediate_prompt, string to be send>, to make the remote 
     * agent continue sending the response message **/
    private Hashtable<String, String> intermediatePrompts;
    
    /** Contains some of the strings that appear when the response message is an error message **/
    private List<String> errorMessagePatterns;
    
    /** The stream to receive characters of the CLI response message **/
    private BufferedReader cliStream;
    
    /** Int value for Carriage Return */
	public static final int CR = 13;

	/** Int value for Line Feed */
	public static final int LF = 10;

	/** Int value for valid character start */
	public static final int VALIDSTART = 31;

	/** Int value for valid character stop */
	public static final int VALIDSTOP = 127;
	
	/** String Buffer to input Data */
	private StringBuilder buffer = null;
	
	/** PrintWriter to write to the stream transport */
	private PrintWriter outPrint;
    
    /**
     * Creates a CLIStreamReader that will listen for the characters received in the stream, assemble response messages
     * and parse them
     * @param stream
     * @param oStream
     * @param prompts
     * @param intermediatePrompts
     * @param errorMessagePatterns
     */
    public CLIStreamReader(InputStream stream, OutputStream oStream, List<String> prompts, 
    		Hashtable<String, String> intermediatePrompts, List<String> errorMessagePatterns){
    	this.prompts = prompts;
    	this.intermediatePrompts = intermediatePrompts;
    	this.errorMessagePatterns = errorMessagePatterns;
    	cliStream = new BufferedReader(new InputStreamReader(stream));
    	outPrint=new PrintWriter(oStream,true);
    	buffer = new StringBuilder();
    }
    
    /**
     * Gets a response message from the input stream. If the request message is echoed, it will eliminate it from 
     * the response
     * @param request
     * @return
     */
    public CLIResponseMessage getResponse(String request) throws IOException{
    	try {
			int c;
			String currentResponse = null;
			List<String> stringsToRemove = new ArrayList<String>();
			stringsToRemove.add(request);
			CLIResponseMessage response = null;
			
			boolean end = false;
			while (((c = cliStream.read()) != -1) && !end) {
				if ((c > VALIDSTART && c < VALIDSTOP) || c == LF || c == CR) {
					//Append the character to the buffer of received characters
					buffer.append((char) c);
					currentResponse = buffer.toString();
					
					//Check for prompts
					for (int i = 0; i < prompts.size(); i++) {
						if (currentResponse.indexOf(prompts.get(i))!= -1){
							//Prompt detected, end of the message, parse it!
							logger.debug("Message received, let's process it: "+currentResponse);
							stringsToRemove.add(prompts.get(i));
							//process message
							response = CLIResponseMessage.parse(currentResponse, stringsToRemove, errorMessagePatterns);
							buffer = new StringBuilder();
							//quit the loops
							return response;
						}
					}
					
					//Check for intermediate prompts
					Enumeration<String> iterator = intermediatePrompts.keys();
					while(iterator.hasMoreElements()){
						String currentKey = iterator.nextElement();
						if (currentResponse.endsWith(currentKey)){
							//Intermediate prompt detected
							logger.debug("Intermediate prompt detected, sending continue character:"+intermediatePrompts.get(currentKey));
							outPrint.println(intermediatePrompts.get(currentKey).toCharArray());
							stringsToRemove.add(currentKey);
							//quit the loop
							break;
						}
					}
				}
			}
			
			//return the response
			return response;
		}catch (IOException e) {
			logger.debug("Connection Lost!" + e.getMessage());
			throw e;
		}
    }
}