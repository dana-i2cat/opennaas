package org.opennaas.extensions.protocols.tl1.message;

import java.io.Serializable;

/**
 * This class reprensent an implementation of an Autonomous Msg this kind of messages are sent
 * without used intervention to report changes or alarm.
 * @author  Mathieu Lemay 
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1AutonomousMsg extends TL1OutputMsg implements Serializable{
	
	private static final long serialVersionUID = 4863792971544722933L;
	
	public static final String CRITICAL="*C";
	public static final String MAJOR="**";
	public static final String MINOR="*";
	public static final String NONALARM="A";
		
	/** Alarm Code for the Autonomous Message */
	protected String alarmCode;
	/** ATAG for the Autonomous Message */
	protected String atag;
	/** Modifier 1 and Modifier 2 */
	protected String md1;
	/** Modifier 1 and Modifier 2 */
	protected String md2;
	/** Verb of the Autonomous Message */
	protected String verb;

	/** Creates a new instance of TL1AutonomousMsg */
	public TL1AutonomousMsg() {
		md1 = null;
		md2 = null;
	}
	/** Returns the alarm code
	 * @return Alarm Code
	 */
	public String getAlarmCode() {
		return alarmCode;
	}
	/** Returns the ATAG
	 * @return ATAG
	 */
	public String getATAG() {
		return atag;
	}
	/** Returns Modifier 1 concerned by the Autonomous msg
	 * @return Modifier 1
	 */
	public String getMD1() {
		return md1;
	}
	/** Returns Modifier 2 concerned by the Autonomous msg
	 * @return Modifier 2
	 */
	public String getMD2() {
		return md2;
	}
	/** Returns Verb concerned by the Autonomous msg
	 * @return Verb
	 */
	public String getVerb() {
		return verb;
	}
	/** Sets the alarm code
		 * @param acode Alarm Code
		 */
	public void setAlarmCode(String acode) {
		alarmCode = acode;
	}
	/** Sets the Autonomous TAG
	 * @param tag The Autonomous tag
	 * */
	public void setATAG(String tag) {
		atag = tag;
	}
	/** Sets the Modifier 1
	 * @param mod1 The Modifier 1 
	 * */
	public void setMD1(String mod1) {
		md1 = mod1;
	}
	/** Sets the Modifier 2
	 * @param mod2 The Modifier 2  
	 * */
	public void setMD2(String mod2) {
		md2 = mod2;
	}

	/** Sets the Verb 
	 * @param localverb The autonmous message verb  
	 * */
	public void setVerb(String localverb) {
		verb = localverb;
	}

	/** Converts to a String
	 * @return Raw String
	 */
	public String toString() {
		return super.toString();
	}
}

