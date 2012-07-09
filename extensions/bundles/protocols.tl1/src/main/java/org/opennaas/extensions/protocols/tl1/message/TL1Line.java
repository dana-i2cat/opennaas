package org.opennaas.extensions.protocols.tl1.message;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * TL1 Line from Payload response
 * 
 * @author Mathieu Lemay
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1Line implements Serializable{

	private static final long serialVersionUID = -8622712773579862180L;
	/** TL1 Sections in this Line */

    private TL1Section[] sections;
    public TL1Line(){
        
    }
    /**
     * Creates a new instance of TL1Line
     * 
     * @param line
     *            Raw TL1 Line Data String to parse
     */
    public TL1Line(String line) {
        if (line.charAt(0) == '"' && line.charAt(line.length() - 1) == '"')
            line = line.substring(1, line.length() - 1); //Remove quotes
        StringTokenizer parser = new StringTokenizer(line, ":");
        int numSections = parser.countTokens();
        sections = new TL1Section[numSections];
        for (int i = 0; i < numSections; i++)
            sections[i] = new TL1Section(parser.nextToken());
    }

    /**
     * Returns an array of the TL1 sections from that line
     * 
     * @return Array of TL1Section
     */
    public TL1Section[] getSections() {
        return sections;
    }

    /**
     * Gets a specific section on the line
     * 
     * @param index
     *            index of the section to get.
     * @return Section in TL1Section format
     */
    public TL1Section getSection(int index) {
        return sections[index];
    }
    
    /** 
     * Convert a Tl1Line to a String
     */ 
    public String toString() {
        StringBuffer s = new StringBuffer();
        for(int i=0; i<sections.length; i++) {
            s.append(sections[i].toString());
            if(i < sections.length - 1)
                s.append(":");
        }        
        return s.toString();
    }

    public void setSections(TL1Section[] sections)
    {
        this.sections = sections;
    }
}

