package org.opennaas.extensions.protocols.tl1.message;

/**
 * Thrown exception when a parsing error occurs.
 * @author  Mathieu Lemay 
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1ParserException extends Exception{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** Creates a new instance of TL1ParserException */
    public TL1ParserException() {
        super();
    }
    
    public TL1ParserException(String s)
    {
        super(s);
    }
}
