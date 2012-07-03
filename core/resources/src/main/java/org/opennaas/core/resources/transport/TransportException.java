package org.opennaas.core.resources.transport;

import org.opennaas.core.resources.ResourceException;

public class TransportException extends ResourceException{

	private static final long serialVersionUID = 1L;
	
	public TransportException(){
        super();
    }
	
	public TransportException(String message){
		super(message);
	}
    
    public TransportException(Exception e) {
    	super(e);
    }
    
    public TransportException(String msg, Exception e) {
    	super(msg, e);
    }

}
