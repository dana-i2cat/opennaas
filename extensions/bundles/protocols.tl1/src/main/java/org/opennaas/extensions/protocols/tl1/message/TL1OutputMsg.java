package org.opennaas.extensions.protocols.tl1.message;

import java.io.Serializable;

/**
 * This class is the basic class for any TL1 output message. It provides basic
 * information for getting/setting the right information while parsing.
 * 
 * @author Mathieu Lemay
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1OutputMsg implements Serializable
{
	private static final long serialVersionUID = 7305422008112750261L;

	/** Constant to identify that the type of OutputMsg is a Response Message */
	public static final int RESP_TYPE = 0;

	/** Constant to identify that the type of OutputMsg is an Autonomous Message */
	public static final int AUTO_TYPE = 1;

	/**
	 * Constant to identify that the type of OutputMsg is an Acknowledgement
	 * Message
	 */
	public static final int ACK_TYPE = 2;

	/** Constant to identify the prompt * */
	public static final int PROMPT_TYPE = 3;

	/** Raw OutPut Message */
	private String rawMessage;

	/** System ID */
	private String sid;

	/** Termination Code */
	private char termCode;

	/** Time Information */
	private TL1Time timeInfo;

	/** Date Information */
	private TL1Date dateInfo;

	/** Message Payload */
	protected TL1Line[] payload;

	/** Type of the OutputMessage */
	private int type;

	/** Creates a new instance of TL1OutputMsg */
	public TL1OutputMsg() {
	}

	/**
	 * Returns the message header date info
	 * 
	 * @return Date Information in TL1Date format
	 */

	public TL1Date getDate() {
		return dateInfo;
	}

	/**
	 * Returns RAW Text Message
	 * 
	 * @return Raw Output Message
	 */
	public String getRaw() {
		return rawMessage;
	}

	public void setRaw(String raw) {
		rawMessage = raw;
	}

	/**
	 * Returns Source ID
	 * 
	 * @return System Identifier
	 */
	public String getSID() {
		return sid;
	}

	/**
	 * Returns the Message Time
	 * 
	 * @return Time Information in TL1Time format
	 */
	public TL1Time getTime() {
		return timeInfo;
	}

	/**
	 * Return the TypeCode
	 * 
	 * @return Message Type 0= Response 1= Autonomous 2= Acknoledgement
	 */
	public int getType() {
		return type;
	}
	
	public void setType(int type){
		this.type = type;
	}

	/**
	 * Return the payload TL1 Lines array
	 * 
	 * @return TL1Line Array
	 */
	public TL1Line[] getPayload() {
		return payload;
	}

	/**
	 * Get a specific payload line.
	 * 
	 * @return TL1Line fetched
	 * @param index
	 *            index of the line to get
	 */
	public TL1Line getPayload(int index) {
		return payload[index];
	}

	/**
	 * Sets the Date Information
	 * 
	 * @param date
	 *            Date Information in TL1Date format
	 */

	public void setDate(TL1Date date) {
		dateInfo = date;
	}

	/**
	 * Set the SID Information
	 * 
	 * @param localsid
	 *            System ID
	 */
	public void setSID(String localsid) {
		sid = localsid;
	}

	/**
	 * Set the Time
	 * 
	 * @param time
	 *            Time Information in TL1Time format
	 */
	public void setTime(TL1Time time) {
		timeInfo = time;
	}

	/**
	 * set the Payload data
	 * 
	 * @param lines
	 *            Array of TL1Line payload lines
	 */
	public void setPayload(TL1Line[] lines) {
		payload = lines;
	}

	/**
	 * Returns the message payload
	 * 
	 * @return String payload
	 */
	public String toString() {
		String s = new String();
		if (payload != null) {
			for (int i = 0; i < payload.length; i++) {
				s = s + payload[i].toString() + "\n";
			}
		}
		return s;
	}

	public void setTermCode(char termcode) {
		termCode = termcode;
	}

	/**
	 * @return Returns the termCode.
	 */
	public char getTermCode() {
		return termCode;
	}
}
