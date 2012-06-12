package org.opennaas.extensions.protocols.tl1.message;

/**
 * This class is the java representation of an Acknoledgement message. The ACK Codes are located in the
 * ACKCode Interface.
 * @author  Mathieu Lemay 
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1AckMsg extends TL1OutputMsg 
{
	/** The In Progress status that means that it is being treated and an answer will be sent back */
	public static final String IN_PROGRESS = "IP";

	/** The Printout Follows status means the same thing as In Progress */
	public static final String PRINTOUT_FOLLOWS = "PF";

	/** The All Right status means that everything is ok */
	public static final String ALL_RIGHT = "OK";

	/** The No Acknowlegement status means that the command was not acknowledged by the switch */
	public static final String NO_ACKNOWLEDGEMENT = "NA";

	/** The No Good status means that the command is bad */
	public static final String NO_GOOD = "NG";

	/** This means that the switch is busy and that the command must be sent later */
	public static final String RETRY_LATER = "RL";

	/** ACK Codes
	 */
	private String ackCode;

	/** Command Tag
	 */
	private String ctag;

	/** Creates a new instance of TL1AckMsg */
	public TL1AckMsg() {
	}

	/** Returns the ACK code 
	 * @return ACK code
	 */
	public String getAckCode() {
		return ackCode;
	}

	/** Returns the CTAG code 
	 * @return CTAG code
	 */
	public String getCTAG() {
		return ctag;
	}

	/** Returns the string representation of the message
	 * @return Raw TL1 String
	 */
	public String toString() {
		return ackCode;
	}

	/** Sets the Ackknowlegement Code 
	 * @param ack The acknowledgement code
	 **/
	public void setAckCode(String ack) {
		ackCode = ack;
	}

	/** Sets the CTAG 
		 * @param tag The CTAG value
		 **/
	public void setCTAG(String tag) {
		ctag = tag;
	}
	
	/**
	 * Verify that the Ack code for this ACK message is valid. If the
	 * transport received a non standard message such as a welcome message where the
	 * is no message code, it will get caught in this method and thrown out by the calling 
	 * method.
	 * 
	 * @return true if the ack code is valid, false otherwise.
	 */
	public boolean isValidAckCode() {
	    if(ackCode.equals(IN_PROGRESS))
	        return true;
	    else if(ackCode.equals(PRINTOUT_FOLLOWS))
	        return true;
	    else if(ackCode.equals(ALL_RIGHT))
	        return true;
	    else if(ackCode.equals(NO_ACKNOWLEDGEMENT))
	        return true;
	    else if(ackCode.equals(NO_GOOD))
	        return true;
	    else if(ackCode.equals(RETRY_LATER))
	        return true;
	    else
	        return false;
	}
}

