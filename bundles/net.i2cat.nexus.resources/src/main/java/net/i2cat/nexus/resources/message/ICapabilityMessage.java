package net.i2cat.nexus.resources.message;

import java.io.Serializable;

/**
 * Base Message Interface
 * @author Scott Campbell
 *
 */
public interface ICapabilityMessage extends Serializable {
	public String getMessage();
	public void setMessage(String message);
	public String getMessageID();
	public void setMessageID(String id);
	public String getRequestor();
	public void setRequestor(String requestor);
}
