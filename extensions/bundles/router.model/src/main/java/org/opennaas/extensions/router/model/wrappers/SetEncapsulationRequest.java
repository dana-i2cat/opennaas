/**
 * 
 */
package org.opennaas.extensions.router.model.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;

/**
 * @author Jordi
 */
@XmlRootElement
public class SetEncapsulationRequest {

	private LogicalPort		iface;
	private ProtocolIFType	encapsulation;

	/**
	 * @return the iface
	 */
	public LogicalPort getIface() {
		return iface;
	}

	/**
	 * @param iface
	 *            the iface to set
	 */
	public void setIface(LogicalPort iface) {
		this.iface = iface;
	}

	/**
	 * @return the encapsulation
	 */
	public ProtocolIFType getEncapsulation() {
		return encapsulation;
	}

	/**
	 * @param encapsulation
	 *            the encapsulation to set
	 */
	public void setEncapsulation(ProtocolIFType encapsulation) {
		this.encapsulation = encapsulation;
	}

}
