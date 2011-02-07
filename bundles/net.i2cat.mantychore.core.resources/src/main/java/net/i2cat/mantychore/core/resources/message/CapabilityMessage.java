package net.i2cat.mantychore.core.resources.message;

import java.util.UUID;

/**
 * Base Capability Message Implementation
 * @author Scott Campbell
 *
 */
public class CapabilityMessage implements ICapabilityMessage {

	private static final long serialVersionUID = 8301588000958655937L;
	private String message;
	private UUID uuid = null;
	private String requestor = null;
	
	public CapabilityMessage(){
		this.uuid = UUID.randomUUID();
	}
	
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageID() {
		return uuid.toString();
	}
	
	public void setMessageID(String id) {
		this.uuid = UUID.fromString(id);
	}
	
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	
	@Override
	public String toString() {
		return "CapabilityMessage [message=" + message + ", uuid=" + uuid + ", requestor=" + requestor + "]";
	}
}