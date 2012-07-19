/**
 * 
 */
package org.opennaas.extensions.router.model.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.LogicalPort;

/**
 * @author Jordi
 */
@XmlRootElement
public class SetEncapsulationLabelRequest {

	private LogicalPort	iface;
	private String		encapsulationLabel;

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
	 * @return the encapsulationLabel
	 */
	public String getEncapsulationLabel() {
		return encapsulationLabel;
	}

	/**
	 * @param encapsulationLabel
	 *            the encapsulationLabel to set
	 */
	public void setEncapsulationLabel(String encapsulationLabel) {
		this.encapsulationLabel = encapsulationLabel;
	}

}
