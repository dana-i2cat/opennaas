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
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.web.entities.LogicalRouter;
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
		String vcpeNetworkId = resourceService.create(getResourceDescriptor(vcpeNetwork));
		LOGGER.debug("start the VCPENetwork with id: " + vcpeNetworkId);
		resourceService.start(vcpeNetworkId);
		return vcpeNetworkId;
	}

	/**
	 * Delete a VCPE Network. First stop the resource
	 * 
	 * @param vcpeNetworkId
	 */
	public void delete(String vcpeNetworkId) {
		LOGGER.debug("stop a VCPENetwork with id: " + vcpeNetworkId);
		resourceService.stop(vcpeNetworkId);
		LOGGER.debug("delete a VCPENetwork with id: " + vcpeNetworkId);
		resourceService.delete(vcpeNetworkId);
	}

	/**
	 * Get a VCPE Network with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @return VCPENetwork
	 */
	public VCPENetwork getById(String vcpeNetworkId) {
		LOGGER.debug("get a VCPENetwork with id: " + vcpeNetworkId);
		return resourceService.getById(vcpeNetworkId);
	}

	/**
	 * Get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 */
	public List<VCPENetwork> getAll() {
		LOGGER.debug("get all VCPENetwork");
		return resourceService.getAll();
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
		// OpenNaaS VCPENetwork Model
		descriptor.setvCPEModel(getVCPENetworkModel(vcpeNetwork));
		return descriptor;
	}

	public static void main(String[] args) {
		VCPENetwork vcpeNetwork = new VCPENetwork();
		LogicalRouter logicalRouter1 = new LogicalRouter();
		vcpeNetwork.setLogicalRouter1(logicalRouter1);
		LogicalRouter logicalRouter2 = new LogicalRouter();
		vcpeNetwork.setLogicalRouter2(logicalRouter2);

		org.opennaas.web.entities.Interface interface1 = new org.opennaas.web.entities.Interface();
		interface1.setName("fe-0/3/3");
		interface1.setPort("1");
		interface1.setVlan(1);
		interface1.setIpAddress("192.1.1.128");
		org.opennaas.web.entities.Interface interface2 = new org.opennaas.web.entities.Interface();
		interface2.setName("fe-0/3/0");
		interface2.setPort("13");
		interface2.setVlan(13);
		interface2.setIpAddress("192.1.1.1");
		org.opennaas.web.entities.Interface interface3 = new org.opennaas.web.entities.Interface();
		interface3.setName("ge-0/2/0");
		interface3.setPort("80");
		interface3.setVlan(80);
		interface3.setIpAddress("192.1.1.256");
		List<org.opennaas.web.entities.Interface> interfaces = new ArrayList<org.opennaas.web.entities.Interface>();
		interfaces.add(interface1);
		interfaces.add(interface2);
		interfaces.add(interface3);
		logicalRouter1.setInterfaces(interfaces);

		interface1 = new org.opennaas.web.entities.Interface();
		interface1.setName("ge-2/0/1");
		interface1.setPort("81");
		interface1.setVlan(81);
		interface1.setIpAddress("160.1.1.128");
		interface2 = new org.opennaas.web.entities.Interface();
		interface2.setName("ge-2/0/0");
		interface2.setPort("12");
		interface2.setVlan(12);
		interface2.setIpAddress("160.1.1.1");
		interface3 = new org.opennaas.web.entities.Interface();
		interface3.setName("ge-2/0/0");
		interface3.setPort("13");
		interface3.setVlan(16);
		interface3.setIpAddress("160.1.1.256");
		interfaces = new ArrayList<org.opennaas.web.entities.Interface>();
		interfaces.add(interface1);
		interfaces.add(interface2);
		interfaces.add(interface3);
		logicalRouter2.setInterfaces(interfaces);
		System.out.println(getVCPENetworkModel(vcpeNetwork));
	}

	/**
	 * @param vcpeNetwork
	 * @return
	 */
	private static String getVCPENetworkModel(VCPENetwork vcpeNetwork) {
		// vcpe1
		Router vcpe1 = new Router();
		vcpe1.setTemplateName(VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName("router:vCPE1");

		// Inter Interface
		Interface inter1 = new Interface();
		inter1.setTemplateName(VCPETemplate.INTER1_INTERFACE_LOCAL);
		inter1.setName(vcpeNetwork.getLogicalRouter1().getInterfaces().get(0).getName() +
				"." + vcpeNetwork.getLogicalRouter1().getInterfaces().get(0).getPort());
		inter1.setVlanId(vcpeNetwork.getLogicalRouter1().getInterfaces().get(0).getVlan());
		inter1.setIpAddress(vcpeNetwork.getLogicalRouter1().getInterfaces().get(0).getIpAddress());

		// Down Interface
		Interface down1 = new Interface();
		down1.setTemplateName(VCPETemplate.DOWN1_INTERFACE_LOCAL);
		down1.setName(vcpeNetwork.getLogicalRouter1().getInterfaces().get(1).getName() +
				"." + vcpeNetwork.getLogicalRouter1().getInterfaces().get(1).getPort());
		down1.setVlanId(vcpeNetwork.getLogicalRouter1().getInterfaces().get(1).getVlan());
		down1.setIpAddress(vcpeNetwork.getLogicalRouter1().getInterfaces().get(1).getIpAddress());

		// Up Interface
		Interface up1 = new Interface();
		up1.setTemplateName(VCPETemplate.UP1_INTERFACE_LOCAL);
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
		vcpe2.setTemplateName(VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName("router:vCPE2");

		// Inter Interface
		Interface inter2 = new Interface();
		inter2.setTemplateName(VCPETemplate.INTER2_INTERFACE_LOCAL);
		inter2.setName(vcpeNetwork.getLogicalRouter2().getInterfaces().get(0).getName() +
				"." + vcpeNetwork.getLogicalRouter2().getInterfaces().get(0).getPort());
		inter2.setVlanId(vcpeNetwork.getLogicalRouter2().getInterfaces().get(0).getVlan());
		inter2.setIpAddress(vcpeNetwork.getLogicalRouter2().getInterfaces().get(0).getIpAddress());

		// Down Interface
		Interface down2 = new Interface();
		down2.setTemplateName(VCPETemplate.DOWN2_INTERFACE_LOCAL);
		down2.setName(vcpeNetwork.getLogicalRouter2().getInterfaces().get(1).getName() +
				"." + vcpeNetwork.getLogicalRouter2().getInterfaces().get(1).getPort());
		down2.setVlanId(vcpeNetwork.getLogicalRouter2().getInterfaces().get(1).getVlan());
		down2.setIpAddress(vcpeNetwork.getLogicalRouter2().getInterfaces().get(1).getIpAddress());

		// Up Interface
		Interface up2 = new Interface();
		up2.setTemplateName(VCPETemplate.UP2_INTERFACE_LOCAL);
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
