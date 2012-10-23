/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

/**
 * @author Jordi
 */
public class BasicTemplate implements ITemplate {

	/**
	 * Generate the model
	 * 
	 * @return VCPENetworkModel
	 */
	@Override
	public VCPENetworkModel buildModel(VCPENetworkModel initialModel) {
		VCPENetworkModel model = new VCPENetworkModel();
		model.setClientIpAddressRange(initialModel.getClientIpAddressRange());
		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		model.setElements(elements);
		// Generate the physical model
		List<VCPENetworkElement> physicalElements = generatePhysicalElements();
		// Generate the logical model
		List<VCPENetworkElement> logicalElements = generateLogicalElements(initialModel);
		// Add all elements
		elements.addAll(physicalElements);
		elements.addAll(logicalElements);
		return model;
	}

	/**
	 * @param initialModel
	 * @return
	 */
	private List<VCPENetworkElement> generateLogicalElements(VCPENetworkModel initialModel) {
		// ----------------------------- VCPE-router1 -----------------------------
		Router vcpe1 = (Router) VCPENetworkModelHelper
				.getElementByNameInTemplate(initialModel, VCPETemplate.VCPE1_ROUTER);
		// Interfaces VCPE-router1
		Interface inter1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.INTER1_INTERFACE_LOCAL);
		Interface down1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.DOWN1_INTERFACE_LOCAL);
		Interface up1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.UP1_INTERFACE_LOCAL);
		// Other interfaces VCPE-router1
		Interface inter1other = getInterface("autobahnID:000001.1", VCPETemplate.INTER1_INTERFACE_AUTOBAHN, 1, null);
		Interface down1other = getInterface("autobahnID:000001.2", VCPETemplate.DOWN1_INTERFACE_AUTOBAHN, 2, null);
		Interface up1other = getInterface("lt-0/1/2.3", VCPETemplate.UP1_INTERFACE_PEER, 0, "192.168.0.6/30");

		// ----------------------------- VCPE-router2 -----------------------------
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.VCPE2_ROUTER);
		// Interfaces VCPE-router2
		Interface inter2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.INTER2_INTERFACE_LOCAL);
		Interface down2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.DOWN2_INTERFACE_LOCAL);
		Interface up2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.UP2_INTERFACE_LOCAL);
		// Other interfaces VCPE-router2
		Interface inter2other = getInterface("autobahnID:000002.1", VCPETemplate.INTER2_INTERFACE_AUTOBAHN, 1, null);
		Interface down2other = getInterface("autobahnID:000002.2", VCPETemplate.DOWN2_INTERFACE_AUTOBAHN, 2, null);
		Interface up2other = getInterface("lt-0/1/2.4", VCPETemplate.UP2_INTERFACE_PEER, 1, "192.168.0.10/30");

		// Client interfaces
		Interface client1other = getInterface("autobahnID:000003.1", VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN, 2, null);
		Interface client2other = getInterface("autobahnID:000003.2", VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN, 2, null);

		// ----------------------------- Links ------------------------------------
		// Inter links
		Link linkInter1local = getLink(null, VCPETemplate.INTER1_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, inter1, inter1other);
		Link linkInter1other = getLink("autobahnID:request:0000001", VCPETemplate.INTER_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, inter1other,
				inter2other);
		Link linkInter2local = getLink(null, VCPETemplate.INTER2_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, inter2, inter2other);
		// Down links
		Link linkDown1local = getLink(null, VCPETemplate.DOWN1_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, down1, down1other);
		Link linkDown1other = getLink("autobahnID:request:0000002", VCPETemplate.DOWN1_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, down1other,
				client1other);
		Link linkDown2local = getLink(null, VCPETemplate.DOWN2_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, down2, down2other);
		Link linkDown2other = getLink("autobahnID:request:0000003", VCPETemplate.DOWN2_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, down2other,
				client2other);
		// Up links
		Link linkUp1 = getLink(null, VCPETemplate.UP1_LINK, VCPETemplate.LINK_TYPE_LT, up1, up1other);
		Link linkUp2 = getLink(null, VCPETemplate.UP2_LINK, VCPETemplate.LINK_TYPE_LT, up2, up2other);
		// Virtual links
		Link inter = getLink(null, VCPETemplate.INTER_LINK, VCPETemplate.LINK_TYPE_VIRTUAL, inter1, inter2);
		List<Link> subLinks = new ArrayList<Link>();
		subLinks.add(linkInter1local);
		subLinks.add(linkInter1other);
		subLinks.add(linkInter2local);
		inter.setImplementedBy(subLinks);
		Link linkdown1 = getLink(null, VCPETemplate.DOWN1_LINK, VCPETemplate.LINK_TYPE_VIRTUAL, down1, client1other);
		subLinks = new ArrayList<Link>();
		subLinks.add(linkDown1local);
		subLinks.add(linkDown1other);
		linkdown1.setImplementedBy(subLinks);
		Link linkdown2 = getLink(null, VCPETemplate.DOWN2_LINK, VCPETemplate.LINK_TYPE_VIRTUAL, down2, client2other);
		subLinks.add(linkDown2local);
		subLinks.add(linkDown2other);
		linkdown2.setImplementedBy(subLinks);

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		elements.add(vcpe1);
		elements.addAll(vcpe1.getInterfaces());
		elements.add(inter1other);
		elements.add(down1other);
		elements.add(up1other);
		elements.add(vcpe2);
		elements.addAll(vcpe2.getInterfaces());
		elements.add(inter2other);
		elements.add(down2other);
		elements.add(up2other);
		elements.add(client1other);
		elements.add(client2other);
		elements.add(inter);
		elements.addAll(inter.getImplementedBy());
		elements.add(linkdown1);
		elements.addAll(linkdown1.getImplementedBy());
		elements.add(linkdown2);
		elements.addAll(linkdown2.getImplementedBy());
		elements.add(linkUp1);
		elements.add(linkUp2);

		return elements;
	}

	private static List<VCPENetworkElement> generatePhysicalElements() {
		Router r1 = new Router();
		r1.setNameInTemplate(VCPETemplate.CPE1_PHY_ROUTER);
		r1.setName("lola");

		Interface inter1 = new Interface();
		inter1.setNameInTemplate(VCPETemplate.INTER1_PHY_INTERFACE_LOCAL);
		inter1.setName("fe-0/3/2");

		Interface inter1other = new Interface();
		inter1other.setNameInTemplate(VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);
		inter1other.setName("autobahnID:000001");

		Interface down1 = new Interface();
		down1.setNameInTemplate(VCPETemplate.DOWN1_PHY_INTERFACE_LOCAL);
		down1.setName("ge-0/2/0");

		Interface down1other = new Interface();
		down1other.setNameInTemplate(VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);
		down1other.setName("autobahnID:000001");

		Interface up1 = new Interface();
		up1.setNameInTemplate(VCPETemplate.UP1_PHY_INTERFACE_LOCAL);
		up1.setName("lt-1/2/0");

		Interface client1 = new Interface();
		client1.setNameInTemplate(VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		client1.setName("autobahnID:000003");

		List<Interface> r1Interfaces = new ArrayList<Interface>();
		r1Interfaces.add(inter1);
		r1Interfaces.add(down1);
		r1Interfaces.add(up1);
		r1.setInterfaces(r1Interfaces);

		Router r2 = new Router();
		r2.setNameInTemplate(VCPETemplate.CPE2_PHY_ROUTER);
		r2.setName("lola");

		Interface inter2 = new Interface();
		inter2.setNameInTemplate(VCPETemplate.INTER2_PHY_INTERFACE_LOCAL);
		inter2.setName("fe-0/3/3");

		Interface inter2other = new Interface();
		inter2other.setNameInTemplate(VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);
		inter2other.setName("autobahnID:000002");

		Interface down2 = new Interface();
		down2.setNameInTemplate(VCPETemplate.DOWN2_PHY_INTERFACE_LOCAL);
		down2.setName("ge-0/2/0");

		Interface down2other = new Interface();
		down2other.setNameInTemplate(VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);
		down2other.setName("autobahnID:000002");

		Interface up2 = new Interface();
		up2.setNameInTemplate(VCPETemplate.UP2_PHY_INTERFACE_LOCAL);
		up2.setName("lt-1/2/0");

		Interface client2 = new Interface();
		client2.setNameInTemplate(VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
		client2.setName("autobahnID:000003");

		List<Interface> r2Interfaces = new ArrayList<Interface>();
		r2Interfaces.add(inter2);
		r2Interfaces.add(down2);
		r2Interfaces.add(up2);
		r2.setInterfaces(r2Interfaces);

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		elements.add(r1);
		elements.add(inter1);
		elements.add(inter1other);
		elements.add(down1);
		elements.add(down1other);
		elements.add(up1);
		elements.add(client1);
		elements.add(r2);
		elements.add(inter2);
		elements.add(inter2other);
		elements.add(down2);
		elements.add(down2other);
		elements.add(up2);
		elements.add(client2);

		return elements;
	}

	/**
	 * @param name
	 * @param nameInTemplate
	 * @param vlan
	 * @param ipAddress
	 * @return the interface
	 */
	private Interface getInterface(String name, String nameInTemplate, long vlan, String ipAddress) {
		Interface iface = new Interface();
		iface.setName(name);
		iface.setNameInTemplate(nameInTemplate);
		iface.setIpAddress(ipAddress);
		iface.setVlanId(vlan);
		return iface;
	}

	/**
	 * @param id
	 * @param nameInTemplate
	 * @param type
	 * @param source
	 * @param sink
	 * @return
	 */
	private Link getLink(String id, String nameInTemplate, String type, Interface source, Interface sink) {
		Link link = new Link();
		link.setId(id);
		link.setNameInTemplate(nameInTemplate);
		link.setType(type);
		link.setSource(source);
		link.setSink(sink);
		return link;
	}

}
