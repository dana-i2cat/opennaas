package org.opennaas.extensions.router.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Julio Carlos Barrera
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VRRPProtocolEndpoint extends ProtocolEndpoint implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4139025716656776213L;

	private int					priority;

	public VRRPProtocolEndpoint() {
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}