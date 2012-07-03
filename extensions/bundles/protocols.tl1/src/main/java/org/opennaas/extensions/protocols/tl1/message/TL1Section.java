package org.opennaas.extensions.protocols.tl1.message;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * TL1 Section of payload data
 * 
 * @author Mathieu Lemay
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */

public class TL1Section implements Serializable{
	
	private static final long serialVersionUID = 6545988016642150536L;
	/** Field TL1Fields in this Section */
    private TL1Field[] fields;

    /**
     * Creates a new instance of TL1Section
     * 
     * @param section
     *            raw Section data to parse
     */
    public TL1Section(){
        
    }
    
    public TL1Section(String section) {
        if (section.charAt(0) == '"' && section.charAt(section.length() - 1) == '"')
            section = section.substring(1, section.length() - 1); //Remove
                                                                  // quotes
        StringTokenizer parser = new StringTokenizer(section, ",");
        fields = new TL1Field[parser.countTokens()];
        int numToken = parser.countTokens();
        for (int i = 0; i < numToken; i++)
            fields[i] = new TL1Field(parser.nextToken());
    }
    public void add(TL1Field field){
    	
    }
    /**
     * get the differents field from that section in an array form
     * 
     * @return TL1Field array of data
     */
    public TL1Field[] getFields() {
        return fields;
    }

    /**
     * Get a specific field
     * 
     * @param index
     *            index of the field to get
     * @return TL1Field to get
     */
    public TL1Field getField(int index) {
        return fields[index];
    }
    
    /**
     * Convert a TL1Section to a String
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        for(int i=0; i<fields.length; i++) {
            s.append(fields[i].toString());
            if(i < fields.length -1)
               s.append(",");
        }       
        return s.toString();        
    }

    public void setFields(TL1Field[] fields)
    {
        this.fields = fields;
    }
}