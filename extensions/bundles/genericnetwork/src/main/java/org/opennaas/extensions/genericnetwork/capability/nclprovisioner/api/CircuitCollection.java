package org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement(name = "circuits", namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class CircuitCollection implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8144269409692480588L;

	@XmlElement(name = "circuit")
	private Collection<Circuit>	circuits;

	public Collection<Circuit> getCircuits() {
		return circuits;
	}

	public void setCircuits(Collection<Circuit> circuits) {
		this.circuits = circuits;
	}

}
