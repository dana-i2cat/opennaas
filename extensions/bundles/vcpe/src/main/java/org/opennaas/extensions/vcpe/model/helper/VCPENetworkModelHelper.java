package org.opennaas.extensions.vcpe.model.helper;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;

public class VCPENetworkModelHelper {

	public static VCPENetworkElement getElementByNameInTemplate(VCPENetworkModel model, String nameInTemplate) {
		return getElementByNameInTemplate(model.getElements(), nameInTemplate);
	}

	public static VCPENetworkElement getElementByNameInTemplate(List<VCPENetworkElement> elements, String nameInTemplate) {
		if (nameInTemplate == null)
			return null;

		for (VCPENetworkElement element : elements) {
			if (nameInTemplate.equals(element.getNameInTemplate()))
				return element;
		}
		return null;
	}

	public static List<Link> getLinks(List<VCPENetworkElement> elements) {
		List<Link> links = new ArrayList<Link>();
		for (VCPENetworkElement element : elements) {
			if (element instanceof Link) {
				links.add((Link) element);
			}
		}
		return links;
	}

	public static VCPENetworkModel generateSampleModel() {

		List<VCPENetworkElement> phy = generatePhysicalSampleModel();

		List<VCPENetworkElement> logical = generateLogicalSampleModel();

		List<VCPENetworkElement> all = new ArrayList<VCPENetworkElement>();
		all.addAll(phy);
		all.addAll(logical);

		VCPENetworkModel model = new VCPENetworkModel();
		model.setElements(all);

		model.setClientIpAddressRange("192.0.2.0/24");

		return model;
	}

	private static List<VCPENetworkElement> generateLogicalSampleModel() {

		// vcpe1
		Router vcpe1 = new Router();
		vcpe1.setNameInTemplate(VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName("vCPE1");

		Interface inter1 = new Interface();
		inter1.setNameInTemplate(VCPETemplate.INTER1_INTERFACE_LOCAL);
		inter1.setName("fe-0/3/2.1");
		inter1.setVlanId(1);
		inter1.setIpAddress("192.168.0.1/30");

		Interface inter1other = new Interface();
		inter1other.setNameInTemplate(VCPETemplate.INTER1_INTERFACE_AUTOBAHN);
		inter1other.setName("autobahnID:000001.1");
		inter1other.setVlanId(1);

		Interface down1 = new Interface();
		down1.setNameInTemplate(VCPETemplate.DOWN1_INTERFACE_LOCAL);
		down1.setName("ge-0/2/0.1");
		down1.setVlanId(1);
		down1.setIpAddress("192.0.2.2/25");

		Interface down1other = new Interface();
		down1other.setNameInTemplate(VCPETemplate.DOWN1_INTERFACE_AUTOBAHN);
		down1other.setName("autobahnID:000001.2");
		down1other.setVlanId(2);

		Interface up1 = new Interface();
		up1.setNameInTemplate(VCPETemplate.UP1_INTERFACE_LOCAL);
		up1.setName("lt-0/1/2.1");
		up1.setIpAddress("192.168.0.5/30");

		Interface up1other = new Interface();
		up1other.setNameInTemplate(VCPETemplate.UP1_INTERFACE_PEER);
		up1other.setName("lt-0/1/2.3"); // in physical router
		up1other.setIpAddress("192.168.0.6/30");

		List<Interface> vcpe1Interfaces = new ArrayList<Interface>();
		vcpe1Interfaces.add(inter1);
		vcpe1Interfaces.add(down1);
		vcpe1Interfaces.add(up1);
		vcpe1.setInterfaces(vcpe1Interfaces);

		// vcpe2
		Router vcpe2 = new Router();
		vcpe2.setNameInTemplate(VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName("vCPE2");

		Interface inter2 = new Interface();
		inter2.setNameInTemplate(VCPETemplate.INTER2_INTERFACE_LOCAL);
		inter2.setName("fe-0/3/3.1");
		inter2.setVlanId(1);
		inter2.setIpAddress("192.168.0.2/30");

		Interface inter2other = new Interface();
		inter2other.setNameInTemplate(VCPETemplate.INTER2_INTERFACE_AUTOBAHN);
		inter2other.setName("autobahnID:000002.1");
		inter2other.setVlanId(1);

		Interface down2 = new Interface();
		down2.setNameInTemplate(VCPETemplate.DOWN2_INTERFACE_LOCAL);
		down2.setName("ge-0/2/0.2");
		down2.setVlanId(2);
		down2.setIpAddress("192.0.2.3/25");

		Interface down2other = new Interface();
		down2other.setNameInTemplate(VCPETemplate.DOWN2_INTERFACE_AUTOBAHN);
		down2other.setName("autobahnID:000002.2");
		down2other.setVlanId(2);

		Interface up2 = new Interface();
		up2.setNameInTemplate(VCPETemplate.UP2_INTERFACE_LOCAL);
		up2.setName("lt-0/1/2.2"); // in physical router
		up2.setIpAddress("192.168.0.9/30");

		Interface up2other = new Interface();
		up2other.setNameInTemplate(VCPETemplate.UP2_INTERFACE_PEER);
		up2other.setName("lt-0/1/2.4");
		up2other.setIpAddress("192.168.0.10/30");

		List<Interface> vcpe2Interfaces = new ArrayList<Interface>();
		vcpe2Interfaces.add(inter2);
		vcpe2Interfaces.add(down2);
		vcpe2Interfaces.add(up2);
		vcpe2.setInterfaces(vcpe2Interfaces);

		// client interfaces
		Interface client1other = new Interface();
		client1other.setNameInTemplate(VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		client1other.setName("autobahnID:000003.1");
		client1other.setVlanId(2);

		Interface client2other = new Interface();
		client2other.setNameInTemplate(VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);
		client2other.setName("autobahnID:000003.2");
		client2other.setVlanId(2);

		// links

		// inter link
		Link linkInter1local = new Link();
		linkInter1local.setNameInTemplate(VCPETemplate.INTER1_LINK_LOCAL);
		linkInter1local.setType(VCPETemplate.LINK_TYPE_ETH);
		linkInter1local.setSource(inter1);
		linkInter1local.setSink(inter1other);

		Link linkInter1other = new Link();
		linkInter1other.setNameInTemplate(VCPETemplate.INTER_LINK_AUTOBAHN);
		linkInter1other.setType(VCPETemplate.LINK_TYPE_AUTOBAHN);
		linkInter1other.setId("autobahnID:request:0000001");
		linkInter1other.setSource(inter1other);
		linkInter1other.setSink(inter2other);

		Link linkInter2local = new Link();
		linkInter2local.setNameInTemplate(VCPETemplate.INTER2_LINK_LOCAL);
		linkInter2local.setType(VCPETemplate.LINK_TYPE_ETH);
		linkInter2local.setSource(inter2);
		linkInter2local.setSink(inter2other);

		// down1 link
		Link linkDown1ocal = new Link();
		linkDown1ocal.setNameInTemplate(VCPETemplate.DOWN1_LINK_LOCAL);
		linkDown1ocal.setType(VCPETemplate.LINK_TYPE_ETH);
		linkDown1ocal.setSource(down1);
		linkDown1ocal.setSink(down1other);

		Link linkDown1other = new Link();
		linkDown1other.setNameInTemplate(VCPETemplate.DOWN1_LINK_AUTOBAHN);
		linkDown1other.setType(VCPETemplate.LINK_TYPE_AUTOBAHN);
		linkDown1other.setId("autobahnID:request:0000002");
		linkDown1other.setSource(down1other);
		linkDown1other.setSink(client1other);

		// down2 link
		Link linkDown2ocal = new Link();
		linkDown2ocal.setNameInTemplate(VCPETemplate.DOWN2_LINK_LOCAL);
		linkDown2ocal.setType(VCPETemplate.LINK_TYPE_ETH);
		linkDown2ocal.setSource(down2);
		linkDown2ocal.setSink(down2other);

		Link linkDown2other = new Link();
		linkDown2other.setNameInTemplate(VCPETemplate.DOWN2_LINK_AUTOBAHN);
		linkDown2other.setType(VCPETemplate.LINK_TYPE_AUTOBAHN);
		linkDown2other.setId("autobahnID:request:0000003");
		linkDown2other.setSource(down2other);
		linkDown2other.setSink(client2other);

		// up1 link
		Link linkUp1 = new Link();
		linkUp1.setNameInTemplate(VCPETemplate.UP1_LINK);
		linkUp1.setType(VCPETemplate.LINK_TYPE_LT);
		linkUp1.setSource(up1);
		linkUp1.setSink(up1other);

		// up2 link
		Link linkUp2 = new Link();
		linkUp2.setNameInTemplate(VCPETemplate.UP2_LINK);
		linkUp2.setType(VCPETemplate.LINK_TYPE_LT);
		linkUp2.setSource(up2);
		linkUp2.setSink(up2other);

		// virtual links
		Link inter = new Link();
		inter.setNameInTemplate(VCPETemplate.INTER_LINK);
		inter.setSource(inter1);
		inter.setSink(inter2);
		inter.setType(VCPETemplate.LINK_TYPE_VIRTUAL);
		List<Link> subLinks = new ArrayList<Link>();
		subLinks.add(linkInter1local);
		subLinks.add(linkInter1other);
		subLinks.add(linkInter2local);
		inter.setImplementedBy(subLinks);

		Link linkdown1 = new Link();
		linkdown1.setNameInTemplate(VCPETemplate.DOWN1_LINK);
		linkdown1.setSource(down1);
		linkdown1.setSink(client1other);
		linkdown1.setType(VCPETemplate.LINK_TYPE_VIRTUAL);
		subLinks = new ArrayList<Link>();
		subLinks.add(linkDown1ocal);
		subLinks.add(linkDown1other);
		linkdown1.setImplementedBy(subLinks);

		Link linkdown2 = new Link();
		linkdown2.setNameInTemplate(VCPETemplate.DOWN2_LINK);
		linkdown2.setSource(down2);
		linkdown2.setSink(client2other);
		linkdown2.setType(VCPETemplate.LINK_TYPE_VIRTUAL);
		subLinks = new ArrayList<Link>();
		subLinks.add(linkDown2ocal);
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

	private static List<VCPENetworkElement> generatePhysicalSampleModel() {

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

}
