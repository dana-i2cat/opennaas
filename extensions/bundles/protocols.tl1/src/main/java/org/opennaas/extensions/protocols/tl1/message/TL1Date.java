package org.opennaas.extensions.protocols.tl1.message;

import java.io.Serializable;
import java.util.StringTokenizer;
/**
 * This objet stores the Date Information contained in the TL1 Autonomous and Response Messages' header.
 * @author  Mathieu Lemay 
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1Date implements Serializable {
	private static final long serialVersionUID = 4460403856236866398L;
	/** Creates a new instance of TL1Date */
    private int year;
    /** Creates a new instance of TL1Date */
    private int month;
    /** Creates a new instance of TL1Date */
    private int date;
    /** Constructor that parses TL1 Date Information
     * @param datestr TL1 Date String
     */    
    public TL1Date(String datestr) throws TL1ParserException{
        StringTokenizer parser=new StringTokenizer(datestr,"-");
        try{
        year=Integer.parseInt(parser.nextToken());
        month=Integer.parseInt(parser.nextToken());
        date=Integer.parseInt(parser.nextToken());
        }
        catch (NumberFormatException e){
        throw (new TL1ParserException());
        }
    }  
    /** Returns the year contained in the header
     * @return Year
     */
    public int getYear(){
    	return year;
    	}
    /** Returns the Month contained in the header
     * @return Month
     */
    public int getMonth(){
    	return month;
    	}
    /** Returns the Date contained in the header
     * @return Date
     */ 
    public int getDate(){
    	return date;
    }
    
    public boolean equals(TL1Date other){
    if (other.getDate()==date && other.getMonth()==month && other.getYear()==year)
    	return true;
    else
    	return false;
    }
}

