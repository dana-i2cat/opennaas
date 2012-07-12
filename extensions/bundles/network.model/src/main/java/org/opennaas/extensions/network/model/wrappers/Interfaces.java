/**
 * 
 */
package org.opennaas.extensions.network.model.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.network.model.topology.Interface;

/**
 * Class necessary to invoke REST request
 * 
 * @author Jordi
 * 
 */
@XmlRootElement
public class Interfaces {

	private Interface	interface1;

	private Interface	interface2;

	/**
	 * @return the interface1
	 */
	public Interface getInterface1() {
		return interface1;
	}

	/**
	 * @param interface1
	 *            the interface1 to set
	 */
	public void setInterface1(Interface interface1) {
		this.interface1 = interface1;
	}

	/**
	 * @return the interface2
	 */
	public Interface getInterface2() {
		return interface2;
	}

	/**
	 * @param interface2
	 *            the interface2 to set
	 */
	public void setInterface2(Interface interface2) {
		this.interface2 = interface2;
	}

}
