package net.i2cat.mantychore.core.resources;

import net.i2cat.mantychore.core.resources.descriptor.Information;

/**
 * Base Resource Exception
 * @author Scott Campbell
 *
 */
public class ResourceException extends Exception
{
    private static final long serialVersionUID = -5669367817669690129L;
    
    /** Resource Information */
    Information resourceInformation = null;
    
    public ResourceException() {
    	super();
    }
    
    public ResourceException(String msg){
        super(msg);
    }
    
    public ResourceException(Exception e) {
    	super(e);
    }
    
    public ResourceException(String msg, Exception e) {
    	super(msg, e);
    }
    
    public ResourceException(String msg, Information information) {
    	super(msg);
    	this.resourceInformation = information;
    }

	public Information getResourceInformation() {
		return resourceInformation;
	}

	public void setResourceInformation(Information resourceInformation) {
		this.resourceInformation = resourceInformation;
	}
}
