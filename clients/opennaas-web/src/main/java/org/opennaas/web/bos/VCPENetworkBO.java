/**
 * 
 */
package org.opennaas.web.bos;

import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.web.entities.VCPENetwork;

/**
 * @author Jordi
 */
public class VCPENetworkBO extends GenericBO {

	private static final String	RESOURCE_TYPE	= "vcpenet";
	private static final String	RESOURCE_NAME	= "vCPENet-1";

	/**
	 * Call a rest url to create a VCPE Network resource
	 * 
	 * @param params
	 */
	public String create(Object params) {
		String url = getURL("resources/create");
		return (String) opennaasRest.post((url), getResourceDescriptor(params), String.class);
	}

	/**
	 * Call a rest url to delete a VCPE Network resource
	 * 
	 * @param vcpeNetworkName
	 */
	public void delete(String vcpeNetworkId) {
		String url = getURL("resources/delete/" + vcpeNetworkId);
		opennaasRest.post(url);
	}

	/**
	 * Call a rest url to start a VCPE Network resource
	 * 
	 * @param vcpeNetworkName
	 */
	public void start(String vcpeNetworkId) {
		String url = getURL("resources/start/" + vcpeNetworkId);
		opennaasRest.post(url);
	}

	/**
	 * Call a rest url to stop a VCPE Network resource
	 * 
	 * @param vcpeNetworkName
	 */
	public void stop(String vcpeNetworkId) {
		String url = getURL("resources/stop/" + vcpeNetworkId);
		opennaasRest.post(url);
	}

	/**
	 * @param vcpeNetworkName
	 * @return
	 */
	public VCPENetwork getVCPENetwork(String vcpeNetworkId) {
		String url = getURL("resources/getResourceById/" + vcpeNetworkId);
		return opennaasRest.post(url).getEntity(VCPENetwork.class);
	}

	/**
	 * @param params
	 * @return
	 */
	private ResourceDescriptor getResourceDescriptor(Object params) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Information information = new Information();
		information.setType(RESOURCE_TYPE);
		information.setType(RESOURCE_NAME);
		resourceDescriptor.setInformation(information);
		return resourceDescriptor;
	}

}
