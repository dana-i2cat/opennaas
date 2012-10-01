/**
 * 
 */
package org.opennaas.web.bos;

import org.opennaas.web.entities.VCPENetwork;

/**
 * @author Jordi
 */
public class VCPENetworkBO extends GenericBO {

	private static final String	RESOURCE_NAME	= "";
	private static final String	VCPE_NAME		= "";
	private static final String	CAPABILITY_NAME	= "";

	/**
	 * Call a rest url to create a VCPE Network
	 * 
	 * @param params
	 */
	public void createVCPENetwork(Object params) {
		String url = getURL(RESOURCE_NAME + "/" + VCPE_NAME + "/" + CAPABILITY_NAME + "/" + "createVCPENetwork");
		opennaasRest.post((url), params);
	}

	/**
	 * Call a rest url to delete a VCPE Network
	 * 
	 * @param vcpeNetworkName
	 */
	public void deleteVCPENetwork(String vcpeNetworkName) {
		String url = getURL(RESOURCE_NAME + "/" + VCPE_NAME + "/" + CAPABILITY_NAME + "/" + "deleteVCPENetwork");
		opennaasRest.post((url), vcpeNetworkName);
	}

	/**
	 * @param vcpeNetworkName
	 * @return
	 */
	public VCPENetwork getVCPENetworkByName(String vcpeNetworkName) {
		String url = getURL(RESOURCE_NAME + "/" + VCPE_NAME + "/" + CAPABILITY_NAME + "/" + "getVCPENetwork");
		return opennaasRest.post((url), vcpeNetworkName).getEntity(VCPENetwork.class);
	}

}
