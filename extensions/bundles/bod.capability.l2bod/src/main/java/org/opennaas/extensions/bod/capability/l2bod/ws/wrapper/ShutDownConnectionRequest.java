package org.opennaas.extensions.bod.capability.l2bod.ws.wrapper;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.network.model.topology.Interface;

@XmlRootElement
public class ShutDownConnectionRequest {

	private List<Interface>	listInterfaces;

	/**
	 * @return the listInterfaces
	 */
	public List<Interface> getListInterfaces() {
		return listInterfaces;
	}

	/**
	 * @param listInterfaces
	 *            the listInterfaces to set
	 */
	public void setListInterfaces(List<Interface> listInterfaces) {
		this.listInterfaces = listInterfaces;
	}

}
