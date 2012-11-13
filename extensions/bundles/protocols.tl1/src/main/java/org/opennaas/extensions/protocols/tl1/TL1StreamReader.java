package org.opennaas.extensions.protocols.tl1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.opennaas.extensions.protocols.tl1.message.TL1OutputMsg;
import org.opennaas.extensions.protocols.tl1.message.TL1OutputParser;
import org.opennaas.extensions.protocols.tl1.message.TL1ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TL1StreamReader extends Thread{
	/** Logger */
    static private Logger logger = LoggerFactory.getLogger(TL1StreamReader.class);
    
    /** The queue where parsed response message are sent **/
    private BlockingQueue<TL1OutputMsg> syncResponseQueue;
    
    /** Int value for Carriage Return */
	public static final int CR = 13;

	/** Int value for Line Feed */
	public static final int LF = 10;

	/** Int value for valid character start */
	public static final int VALIDSTART = 31;

	/** Int value for valid character stop */
	public static final int VALIDSTOP = 127;

	/** Input Reader from TCP Port */
	private BufferedReader inRead;

	/** String Buffer to input Data */
	private StringBuilder buffer = new StringBuilder();

	/** Die variable will kill the reader thread when set to true */
	public boolean die = false;

	/** Characters to look for that flag the end of a message */
	private char[] msgdelimiters;
	
	/** The number of characters to look back in the stringbuffer to try and match the prompt */
	private int promptOffset = 50;
	
	/**
	 * The network element's promt string to use to detect the end of a message. This is used in
	 * conjunction with the msgdelimiters to find the end of a message
	 */
	private String[] prompts = null;
	
	/**
	 * The tL1 session that this stream reader reports to
	 */
	private TL1ProtocolSession tl1Session = null;
	
	/**
	 * Constructor that Creates a TL1 Stream reader from an Input Stream
	 * @param in
	 * @param msgdeli the message delimiters
	 * @param syncResponseQueue the response queue where response messages and acks will be sent
	 * @param eventAdmin used to send notifications about autonomous messages
	 * @param topic the topic of the notifications
	 */
	public TL1StreamReader(InputStream in, char[] msgdeli, BlockingQueue<TL1OutputMsg> syncResponseQueue, 
			TL1ProtocolSession tl1Session){
		inRead = new BufferedReader(new InputStreamReader(in));
		msgdelimiters = msgdeli;
		this.syncResponseQueue = syncResponseQueue;
		this.tl1Session = tl1Session;
	}

	/**
	 * Constructor that Creates a TL1 Stream reader from an Input Stream
	 * @param in
	 * @param msgdeli the message delimiters
	 * @param prompts
	 * @param promptOffset
	 * @param syncResponseQueue the response queue where response messages and acks will be sent
	 * @param eventAdmin used to send notifications about autonomous messages
	 * @param topic the topic of the notifications
	 */
	public TL1StreamReader(InputStream in, char[] msgdeli, String[] prompts, int promptOffset, 
			BlockingQueue<TL1OutputMsg> syncResponseQueue, TL1ProtocolSession tl1Session){
		inRead = new BufferedReader(new InputStreamReader(in));
		msgdelimiters = msgdeli;
		this.prompts = prompts;
		this.promptOffset = promptOffset;
		this.syncResponseQueue = syncResponseQueue;
		this.tl1Session = tl1Session;
	}
	
	/**
	 * Gets the different valid characters when the thread starts and puts them in a buffer
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try {
			int c;
			while (((c = inRead.read()) != -1) && (!die)) {
				if ((c > VALIDSTART && c < VALIDSTOP) || c == LF || c == CR) {

					buffer.append((char) c);

					for (int i = 0; i < msgdelimiters.length; i++) {
						if ((char) c == msgdelimiters[i]) {
							if (buffer.toString().indexOf("REPT EVT SESSION") == -1) {
								// look back for prompt if it has been set
								if (prompts != null) {
									int startIndex = 0;
									// Make sure the start index is not a negative integer
									if (buffer.length() < promptOffset) {
										startIndex = 0;
									}else {
										startIndex = buffer.length() - promptOffset;
									}

									// loop over the list of possible prompts to look for a match
									for (int j = 0; j < prompts.length; j++) {
										if (buffer.toString().substring(startIndex).indexOf(
												prompts[j]) != -1) {
											logger.debug("Message received, I'm going to process it");
											processMessage((String) buffer.toString());
											buffer = new StringBuilder();
											break;
										}
									}
								}else {
									logger.debug("Message received, I'm going to process it");
									processMessage((String) buffer.toString());
									buffer = new StringBuilder();
								}
							}
							break;
						}
					}
				}
			}
			
			if (streamIsClosed(c)){
				logger.debug("Connection Lost!");
				tl1Session.restartSession();
			}
			
		}catch (IOException e) {
			logger.debug("Connection Lost!" + e.getMessage());
			tl1Session.restartSession();
		}
	}
	
	private boolean streamIsClosed(int c){
		if (c == -1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Parse the TL1 message and either put it in the response queue (ACKs and Response messages)
	 * or send a notification (Autonomous messages)
	 * @param message
	 */
	private void processMessage(String message){
		TL1OutputMsg tl1Message = null;
		
		//Parse the plain text message
		try {
			tl1Message = TL1OutputParser.parse(message);
        }catch (TL1ParserException e) {
            logger.error("\n***********\n" +
            		"Error parsing message:\n" + e.getMessage() +
            		"\n***********\n");
            return;
        }
        
        //Process the message based on its type
        if (tl1Message != null){
        	switch(tl1Message.getType()){
        	case TL1OutputMsg.PROMPT_TYPE:
        		logger.debug("Received PROMPT, ignore it");
        		break;
        	case TL1OutputMsg.AUTO_TYPE:
        		logger.debug("Received TL1 Autonomous message, sending notification");
        		tl1Session.notify(tl1Message);
        		break;
        	case TL1OutputMsg.ACK_TYPE:
        		logger.debug("Received TL1 ACK message, adding it to the queue");
        		syncResponseQueue.add(tl1Message);
        		break;
        	default:
        		logger.debug("Received TL1 response message, adding it to the queue");
        		syncResponseQueue.add(tl1Message);
        		break;
        	}
        }
	}
}
