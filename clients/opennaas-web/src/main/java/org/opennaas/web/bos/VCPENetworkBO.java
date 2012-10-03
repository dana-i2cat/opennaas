/**
 * 
 */
package org.opennaas.web.bos;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.web.entities.LogicalRouter;
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
		String vcpeNetworkId = (String) opennaasRest
				.post((url), getResourceDescriptor(params), String.class);
		url = getURL("resources/start/" + vcpeNetworkId);
		opennaasRest.post(url);
		return vcpeNetworkId;
	}

	/**
	 * Call a rest url to delete a VCPE Network resource
	 * 
	 * @param vcpeNetworkName
	 */
	public void delete(String vcpeNetworkId) {
		String url = getURL("resources/stop/" + vcpeNetworkId);
		opennaasRest.post(url);
		url = getURL("resources/delete/" + vcpeNetworkId);
		opennaasRest.post(url);
	}

	/**
	 * Call a rest url to get a VCPE Network with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkName
	 * @return VCPENetwork
	 */
	public VCPENetwork getVCPENetwork(String vcpeNetworkId) {
		// TODO Need to call OpenNaaS
		// String url = getURL("resources/getResourceById/" + vcpeNetworkId);
		// return opennaasRest.post(url).getEntity(VCPENetwork.class);
		return getAllVCPENetwork().get(0);
	}

	/**
	 * Call a rest url to get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 */
	public List<VCPENetwork> getAllVCPENetwork() {
		// TODO Need to call OpenNaaS
		List<VCPENetwork> list = new ArrayList<VCPENetwork>();
		VCPENetwork vcpeNetwork1 = new VCPENetwork();
		VCPENetwork vcpeNetwork2 = new VCPENetwork();

		vcpeNetwork1.setId("1");
		vcpeNetwork1.setName("VCPENetwork-1");

		LogicalRouter logicalRouter1 = new LogicalRouter();
		logicalRouter1.setName("LR1-VCPE1");

		LogicalRouter logicalRouter2 = new LogicalRouter();
		logicalRouter2.setName("LR2-VCPE1");

		vcpeNetwork1.setLogicalRouter1(logicalRouter1);
		vcpeNetwork1.setLogicalRouter2(logicalRouter2);

		vcpeNetwork2.setId("2");
		vcpeNetwork2.setName("VCPENetwork-2");

		logicalRouter1 = new LogicalRouter();
		logicalRouter1.setName("LR1-VCPE2");

		logicalRouter2 = new LogicalRouter();
		logicalRouter2.setName("LR2-VCPE2");

		vcpeNetwork2.setLogicalRouter1(logicalRouter1);
		vcpeNetwork2.setLogicalRouter2(logicalRouter2);

		list.add(vcpeNetwork1);
		list.add(vcpeNetwork2);

		return list;
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
