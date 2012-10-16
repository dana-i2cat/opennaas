/**
 * 
 */
package org.opennaas.web.bos;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.web.entities.VCPENetwork;
import org.opennaas.web.services.ResourceService;
import org.opennaas.web.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jordi
 */
public class VCPENetworkBO {

	private static final Logger	LOGGER	= Logger.getLogger(VCPENetworkBO.class);

	@Autowired
	private ResourceService		resourceService;

	/**
	 * Create a VCPE Network. After start the resource
	 * 
	 * @param vcpeNetwork
	 */
	public String create(VCPENetwork vcpeNetwork) {
		LOGGER.debug("create a VCPENetwork: " + vcpeNetwork);
		String vcpeNetworkId = resourceService.createResource(getResourceDescriptor(vcpeNetwork));
		LOGGER.debug("start the VCPENetwork with id: " + vcpeNetworkId);
		resourceService.startResource(vcpeNetworkId);
		return vcpeNetworkId;
	}

	/**
	 * Delete a VCPE Network. First stop the resource
	 * 
	 * @param vcpeNetworkId
	 */
	public void delete(String vcpeNetworkId) {
		LOGGER.debug("stop a VCPENetwork with id: " + vcpeNetworkId);
		resourceService.stopResource(vcpeNetworkId);
		LOGGER.debug("delete a VCPENetwork with id: " + vcpeNetworkId);
		resourceService.deleteResource(vcpeNetworkId);
	}

	/**
	 * Get a VCPE Network with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @return VCPENetwork
	 */
	public VCPENetwork getById(String vcpeNetworkId) {
		LOGGER.debug("get a VCPENetwork with id: " + vcpeNetworkId);
		// TODO Call to OpenNaaS
		// Resource resource = resourceService.getResourceById(vcpeNetworkId);
		// return resourceToVCPENetwork(resource);
		VCPENetwork vcpeNetwork = new VCPENetwork();
		vcpeNetwork.setName("VCPENetwork-1");
		return vcpeNetwork;
	}

	/**
	 * Get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 */
	public List<VCPENetwork> getAll() {
		LOGGER.debug("get all VCPENetwork");
		List<VCPENetwork> vcpeNetworks = new ArrayList<VCPENetwork>();
		VCPENetwork vcpeNetwork1 = new VCPENetwork();
		VCPENetwork vcpeNetwork2 = new VCPENetwork();
		vcpeNetworks.add(vcpeNetwork1);
		vcpeNetworks.add(vcpeNetwork2);
		vcpeNetwork1.setName("VCPENetwork-1");
		vcpeNetwork1.setId("1");
		vcpeNetwork2.setName("VCPENetwork-2");
		vcpeNetwork2.setId("2");
		return vcpeNetworks;
		// TODO Call to OpenNaaS
		// return resourceService.getAllResources();
	}

	/**
	 * @param resource
	 * @return
	 */
	private VCPENetwork resourceToVCPENetwork(Resource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param params
	 * @return
	 */
	private VCPENetworkDescriptor getResourceDescriptor(VCPENetwork vcpeNetwork) {
		VCPENetworkDescriptor descriptor = new VCPENetworkDescriptor();
		Information information = new Information();
		information.setType(Constants.RESOURCE_VCPENET_TYPE);
		information.setName(vcpeNetwork.getName());
		descriptor.setInformation(information);

		List<CapabilityDescriptor> capabs = new ArrayList<CapabilityDescriptor>();
		capabs.add(generateBuilderCapabilityDescriptor());
		descriptor.setCapabilityDescriptors(capabs);

		// OpenNaaS VCPENetwork Model
		descriptor.setvCPEModel(getVCPENetworkModel(vcpeNetwork));
		return descriptor;
	}

	private static CapabilityDescriptor generateBuilderCapabilityDescriptor() {
		CapabilityDescriptor desc = new CapabilityDescriptor();
		Information info = new Information();
		info.setType("vcpenet_builder");
		desc.setCapabilityInformation(info);
		return desc;
	}

	/**
	 * @param vcpeNetwork
	 * @return
	 */
	private static String getVCPENetworkModel(VCPENetwork vcpeNetwork) {
		// vcpe1
		Router vcpe1 = new Router();
		vcpe1.setNameInTemplate(VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName("router:vCPE1");

		// Inter Interface
		Interface inter1 = new Interface();
		inter1.setNameInTemplate(VCPETemplate.INTER1_INTERFACE_LOCAL);
		inter1.setName(vcpeNetwork.getLogicalRouter1().getInterfaces().get(0).getName() +
				"." + vcpeNetwork.getLogicalRouter1().getInterfaces().get(0).getPort());
		inter1.setVlanId(vcpeNetwork.getLogicalRouter1().getInterfaces().get(0).getVlan());
		inter1.setIpAddress(vcpeNetwork.getLogicalRouter1().getInterfaces().get(0).getIpAddress());

		// Down Interface
		Interface down1 = new Interface();
		down1.setNameInTemplate(VCPETemplate.DOWN1_INTERFACE_LOCAL);
		down1.setName(vcpeNetwork.getLogicalRouter1().getInterfaces().get(1).getName() +
				"." + vcpeNetwork.getLogicalRouter1().getInterfaces().get(1).getPort());
		down1.setVlanId(vcpeNetwork.getLogicalRouter1().getInterfaces().get(1).getVlan());
		down1.setIpAddress(vcpeNetwork.getLogicalRouter1().getInterfaces().get(1).getIpAddress());

		// Up Interface
		Interface up1 = new Interface();
		up1.setNameInTemplate(VCPETemplate.UP1_INTERFACE_LOCAL);
		up1.setName(vcpeNetwork.getLogicalRouter1().getInterfaces().get(2).getName() +
				"." + vcpeNetwork.getLogicalRouter1().getInterfaces().get(2).getPort());
		up1.setVlanId(vcpeNetwork.getLogicalRouter1().getInterfaces().get(2).getVlan());
		up1.setIpAddress(vcpeNetwork.getLogicalRouter1().getInterfaces().get(2).getIpAddress());

		List<Interface> vcpe1Interfaces = new ArrayList<Interface>();
		vcpe1Interfaces.add(inter1);
		vcpe1Interfaces.add(down1);
		vcpe1Interfaces.add(up1);
		vcpe1.setInterfaces(vcpe1Interfaces);

		// vcpe2
		Router vcpe2 = new Router();
		vcpe2.setNameInTemplate(VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName("router:vCPE2");

		// Inter Interface
		Interface inter2 = new Interface();
		inter2.setNameInTemplate(VCPETemplate.INTER2_INTERFACE_LOCAL);
		inter2.setName(vcpeNetwork.getLogicalRouter2().getInterfaces().get(0).getName() +
				"." + vcpeNetwork.getLogicalRouter2().getInterfaces().get(0).getPort());
		inter2.setVlanId(vcpeNetwork.getLogicalRouter2().getInterfaces().get(0).getVlan());
		inter2.setIpAddress(vcpeNetwork.getLogicalRouter2().getInterfaces().get(0).getIpAddress());

		// Down Interface
		Interface down2 = new Interface();
		down2.setNameInTemplate(VCPETemplate.DOWN2_INTERFACE_LOCAL);
		down2.setName(vcpeNetwork.getLogicalRouter2().getInterfaces().get(1).getName() +
				"." + vcpeNetwork.getLogicalRouter2().getInterfaces().get(1).getPort());
		down2.setVlanId(vcpeNetwork.getLogicalRouter2().getInterfaces().get(1).getVlan());
		down2.setIpAddress(vcpeNetwork.getLogicalRouter2().getInterfaces().get(1).getIpAddress());

		// Up Interface
		Interface up2 = new Interface();
		up2.setNameInTemplate(VCPETemplate.UP2_INTERFACE_LOCAL);
		up2.setName(vcpeNetwork.getLogicalRouter2().getInterfaces().get(2).getName() +
				"." + vcpeNetwork.getLogicalRouter2().getInterfaces().get(2).getPort());
		up2.setVlanId(vcpeNetwork.getLogicalRouter2().getInterfaces().get(2).getVlan());
		up2.setIpAddress(vcpeNetwork.getLogicalRouter2().getInterfaces().get(2).getIpAddress());

		List<Interface> vcpe2Interfaces = new ArrayList<Interface>();
		vcpe2Interfaces.add(inter2);
		vcpe2Interfaces.add(down2);
		vcpe2Interfaces.add(up2);
		vcpe2.setInterfaces(vcpe2Interfaces);

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		elements.add(vcpe1);
		elements.add(vcpe2);
		elements.addAll(vcpe1.getInterfaces());
		elements.addAll(vcpe2.getInterfaces());

		VCPENetworkModel vcpeNetworkModel = new VCPENetworkModel();
		vcpeNetworkModel.setElements(elements);

		return convertToXml(vcpeNetworkModel, VCPENetworkModel.class);
	}

	public static String convertToXml(Object source, Class<?>... type) {
		String result;
		StringWriter sw = new StringWriter();
		try {
			JAXBContext carContext = JAXBContext.newInstance(type);
			Marshaller carMarshaller = carContext.createMarshaller();
			carMarshaller.marshal(source, sw);
			result = sw.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

}
