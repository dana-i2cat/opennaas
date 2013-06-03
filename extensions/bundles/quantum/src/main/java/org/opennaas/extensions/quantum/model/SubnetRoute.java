package org.opennaas.extensions.quantum.model;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class SubnetRoute extends Route {

	private String	subnet_id;

	public String getSubnet_id() {
		return subnet_id;
	}

	public void setSubnet_id(String subnet_id) {
		this.subnet_id = subnet_id;
	}
}
