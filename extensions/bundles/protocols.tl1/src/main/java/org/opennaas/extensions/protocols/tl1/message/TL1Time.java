package org.opennaas.extensions.protocols.tl1.message;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * This objet stores the Time Information contained in the TL1 Autonomous and Response Messages' header.
 * @author  Mathieu Lemay 
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1Time implements Serializable{
	
	private static final long serialVersionUID = 5225689273012367541L;
	/** Time information for TL1 Command */
    private int hour;
	/** Time information for TL1 Command */
    private int minutes;
	/** Time information for TL1 Command */
    private int seconds;
    /** Creates a new instance of TL1Time
     * @param date Time in raw string format
     */
    public TL1Time(String date) {
        StringTokenizer parser=new StringTokenizer(date,":");
        hour=(Integer.parseInt(parser.nextToken()));
        minutes=(Integer.parseInt(parser.nextToken()));
        seconds=(Integer.parseInt(parser.nextToken()));
    }
    /** Returns the hour in the message header
     * @return Hour
     */
    public int getHour(){return hour;}
    /** Returns the minutes in the message header
     * @return Minutes
     */
    public int getMinutes(){return minutes;}
    /** Returns the seconds in the message header
     * @return Seconds
     */
    public int getSeconds(){return seconds;}
}

