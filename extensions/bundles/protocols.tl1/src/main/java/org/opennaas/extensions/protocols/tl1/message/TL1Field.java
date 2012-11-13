package org.opennaas.extensions.protocols.tl1.message;

import java.io.Serializable;
import java.util.StringTokenizer;
/** This class corresponds to a parsed TL1 Field
 * @author Mathieu Lemay
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1Field implements Serializable{
	
	private static final long serialVersionUID = 5267091533470904387L;

	/** Name of the Field */
    private String name;
    
    /** This is the Value of the field */
    private String value;
   
    /** Creates a new instance of TL1Field
     * @param field Field to be parsed in it's Name/Value format
     */
    public TL1Field(String field) {
        if(field.charAt(0)=='"' && field.charAt(field.length()-1)=='"')
           field=field.substring(1,field.length()-1); //Remove quotes
    
        StringTokenizer parser=new StringTokenizer(field,"=");
        name=parser.nextToken();
   		 if(name.charAt(0)=='"' && name.charAt(name.length()-1)=='"')
   		    name=name.substring(1,name.length()-1); //Remove quotes
        
        if(parser.hasMoreTokens()){
        	value=parser.nextToken();
    		 if(value.charAt(0)=='"' && value.charAt(value.length()-1)=='"')
    		    value=value.substring(1,value.length()-1); //Remove quotes
        }
    }
    public TL1Field(){
        
    }
    /** Retrives the name of the Field
     * @return Name of the Field
     */    
    public String getName(){
        return name;
    }
    
    /** Retreives the value of the field specified by name in arguments
     * @return Value of the Field
     */    
    public String getValue(){
        return value;
    }
    /** Sets the value for the current field specified 
     * @param val Value of the field
     * */ 
    public void setValue(String val){
    	value=val;
    }
	/** Sets the name for the current field specified 
		 * @param localname Name of the field
		 * */ 
    public void setName(String localname){
    	name=localname;
    }
    /** Converts the TL1Field back to a string
     * @return String of the field
     */    
    public String toString(){
        if(value==null)
            return name;
        else
            return name+"="+value;
    }
}

