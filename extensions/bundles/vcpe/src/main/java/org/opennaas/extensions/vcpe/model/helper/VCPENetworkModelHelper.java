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

	public static VCPENetworkModel generateSampleModel() {

		List<VCPENetworkElement> phy = generatePhysicalSampleModel();

		List<VCPENetworkElement> logical = generateLogicalSampleModel();

		List<VCPENetworkElement> all = new ArrayList<VCPENetworkElement>();
		all.addAll(phy);
		all.addAll(logical);

		VCPENetworkModel model = new VCPENetworkModel();
		model.setElements(all);

		return model;
	}

	private static List<VCPENetworkElement> generateLogicalSampleModel() {

		// vcpe1
		Router vcpe1 = new Router();
		vcpe1.setTemplateName(VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName("router:vCPE1");

		Interface inter1 = new Interface();
		inter1.setTemplateName(VCPETemplate.INTER1_INTERFACE_LOCAL);
		inter1.setName("fe-0/1/2.1");
		inter1.setVlanId(1);

		Interface inter1other = new Interface();
		inter1other.setTemplateName(VCPETemplate.INTER1_INTERFACE_AUTOBAHN);
		inter1other.setName("autobahnID:000001.1");
		inter1other.setVlanId(1);

		Interface down1 = new Interface();
		down1.setTemplateName(VCPETemplate.DOWN1_INTERFACE_LOCAL);
		down1.setName("fe-0/1/2.2");
		down1.setVlanId(2);

		Interface down1other = new Interface();
		down1other.setTemplateName(VCPETemplate.DOWN1_INTERFACE_AUTOBAHN);
		down1other.setName("autobahnID:000001.2");
		down1other.setVlanId(2);

		Interface up1 = new Interface();
		up1.setTemplateName(VCPETemplate.UP1_INTERFACE_LOCAL);
		up1.setName("lt-0/1/2.1");

		Interface up1other = new Interface();
		up1other.setTemplateName(VCPETemplate.UP1_INTERFACE_PEER);
		up1other.setName("lt-0/1/2.1"); // in physical router

		List<Interface> vcpe1Interfaces = new ArrayList<Interface>();
		vcpe1Interfaces.add(inter1);
		vcpe1Interfaces.add(down1);
		vcpe1Interfaces.add(up1);
		vcpe1.setInterfaces(vcpe1Interfaces);

		// vcpe2
		Router vcpe2 = new Router();
		vcpe2.setTemplateName(VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName("router:vCPE2");

		Interface inter2 = new Interface();
		inter2.setTemplateName(VCPETemplate.INTER2_INTERFACE_LOCAL);
		inter2.setName("fe-0/1/2.1");
		inter2.setVlanId(1);

		Interface inter2other = new Interface();
		inter2other.setTemplateName(VCPETemplate.INTER2_INTERFACE_AUTOBAHN);
		inter2other.setName("autobahnID:000002.1");
		inter2other.setVlanId(1);

		Interface down2 = new Interface();
		down2.setTemplateName(VCPETemplate.DOWN2_INTERFACE_LOCAL);
		down2.setName("fe-0/1/2.2");
		down2.setVlanId(2);

		Interface down2other = new Interface();
		down2other.setTemplateName(VCPETemplate.DOWN2_INTERFACE_AUTOBAHN);
		down2other.setName("autobahnID:000002.2");
		down2other.setVlanId(2);

		Interface up2 = new Interface();
		up2.setTemplateName(VCPETemplate.UP2_INTERFACE_LOCAL);
		up2.setName("lt-0/1/2.1"); // in physical router

		Interface up2other = new Interface();
		up2other.setTemplateName(VCPETemplate.UP2_INTERFACE_PEER);
		up2other.setName("lt-0/1/2.1");

		List<Interface> vcpe2Interfaces = new ArrayList<Interface>();
		vcpe2Interfaces.add(inter2);
		vcpe2Interfaces.add(down2);
		vcpe2Interfaces.add(up2);
		vcpe2.setInterfaces(vcpe2Interfaces);

		// client interfaces
		Interface client1other = new Interface();
		client1other.setTemplateName(VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		client1other.setName("autobahnID:000003.1");
		client1other.setVlanId(2);

		Interface client2other = new Interface();
		client2other.setTemplateName(VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);
		client2other.setName("autobahnID:000003.2");
		client2other.setVlanId(2);

		// links

		// inter link
		Link linkInter1local = new Link();
		linkInter1local.setTemplateName(VCPETemplate.INTER1_LINK_LOCAL);
		linkInter1local.setType("eth");
		linkInter1local.setSource(inter1);
		linkInter1local.setSink(inter1other);

		Link linkInter1other = new Link();
		linkInter1other.setTemplateName(VCPETemplate.INTER_LINK_AUTOBAHN);
		linkInter1other.setType("autobahn");
		linkInter1other.setId("autobahnID:request:0000001");
		linkInter1other.setSource(inter1other);
		linkInter1other.setSink(inter2other);

		Link linkInter2local = new Link();
		linkInter2local.setTemplateName(VCPETemplate.INTER2_LINK_LOCAL);
		linkInter2local.setType("eth");
		linkInter2local.setSource(inter2);
		linkInter2local.setSink(inter2other);

		// down1 link
		Link linkDown1ocal = new Link();
		linkDown1ocal.setTemplateName(VCPETemplate.DOWN1_LINK_LOCAL);
		linkDown1ocal.setType("eth");
		linkDown1ocal.setSource(down1);
		linkDown1ocal.setSink(down1other);

		Link linkDown1other = new Link();
		linkDown1other.setTemplateName(VCPETemplate.DOWN1_LINK_AUTOBAHN);
		linkDown1other.setType("autobahn");
		linkDown1other.setId("autobahnID:request:0000002");
		linkDown1other.setSource(down1other);
		linkDown1other.setSink(client1other);

		// down2 link
		Link linkDown2ocal = new Link();
		linkDown2ocal.setTemplateName(VCPETemplate.DOWN2_LINK_LOCAL);
		linkDown2ocal.setType("eth");
		linkDown2ocal.setSource(down2);
		linkDown2ocal.setSink(down2other);

		Link linkDown2other = new Link();
		linkDown2other.setTemplateName(VCPETemplate.DOWN2_LINK_AUTOBAHN);
		linkDown2other.setType("autobahn");
		linkDown2other.setId("autobahnID:request:0000003");
		linkDown2other.setSource(down2other);
		linkDown2other.setSink(client2other);

		// up1 link
		Link linkUp1 = new Link();
		linkUp1.setTemplateName(VCPETemplate.UP1_LINK);
		linkUp1.setType("lt");
		linkUp1.setSource(up1);
		linkUp1.setSink(up1other);

		// up2 link
		Link linkUp2 = new Link();
		linkUp2.setTemplateName(VCPETemplate.UP2_LINK);
		linkUp2.setType("lt");
		linkUp2.setSource(up2);
		linkUp2.setSink(up2other);

		// virtual links
		Link inter = new Link();
		inter.setTemplateName(VCPETemplate.INTER_LINK);
		inter.setSource(inter1);
		inter.setSink(inter2);
		List<Link> subLinks = new ArrayList<Link>();
		subLinks.add(linkInter1local);
		subLinks.add(linkInter1other);
		subLinks.add(linkInter2local);
		inter.setImplementedBy(subLinks);

		Link linkdown1 = new Link();
		linkdown1.setTemplateName(VCPETemplate.DOWN1_LINK);
		linkdown1.setSource(down1);
		linkdown1.setSink(client1other);
		subLinks = new ArrayList<Link>();
		subLinks.add(linkDown1ocal);
		subLinks.add(linkDown1other);
		linkdown1.setImplementedBy(subLinks);

		Link linkdown2 = new Link();
		linkdown2.setTemplateName(VCPETemplate.DOWN2_LINK);
		linkdown2.setSource(down2);
		linkdown2.setSink(client2other);
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
		r1.setTemplateName(VCPETemplate.CPE1_PHY_ROUTER);
		r1.setName("router:lola");

		Interface inter1 = new Interface();
		inter1.setTemplateName(VCPETemplate.INTER1_PHY_INTERFACE_LOCAL);
		inter1.setName("fe-0/1/2");

		Interface inter1other = new Interface();
		inter1other.setTemplateName(VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);
		inter1other.setName("autobahnID:000001");

		Interface down1 = new Interface();
		down1.setTemplateName(VCPETemplate.DOWN1_PHY_INTERFACE_LOCAL);
		down1.setName("fe-0/1/2");

		Interface down1other = new Interface();
		down1other.setTemplateName(VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);
		down1other.setName("autobahnID:000001");

		Interface up1 = new Interface();
		up1.setTemplateName(VCPETemplate.UP1_PHY_INTERFACE_LOCAL);
		up1.setName("lt-1/2/0");

		Interface client1 = new Interface();
		client1.setTemplateName(VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		client1.setName("autobahnID:000003");

		List<Interface> r1Interfaces = new ArrayList<Interface>();
		r1Interfaces.add(inter1);
		r1Interfaces.add(down1);
		r1Interfaces.add(up1);
		r1.setInterfaces(r1Interfaces);

		Router r2 = new Router();
		r2.setTemplateName(VCPETemplate.CPE2_PHY_ROUTER);
		r2.setName("router:myre");

		Interface inter2 = new Interface();
		inter2.setTemplateName(VCPETemplate.INTER2_PHY_INTERFACE_LOCAL);
		inter2.setName("fe-0/1/2");

		Interface inter2other = new Interface();
		inter2other.setTemplateName(VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);
		inter2other.setName("autobahnID:000002");

		Interface down2 = new Interface();
		down2.setTemplateName(VCPETemplate.DOWN2_PHY_INTERFACE_LOCAL);
		down2.setName("fe-0/1/2");

		Interface down2other = new Interface();
		down2other.setTemplateName(VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);
		down2other.setName("autobahnID:000002");

		Interface up2 = new Interface();
		up2.setTemplateName(VCPETemplate.UP2_PHY_INTERFACE_LOCAL);
		up2.setName("lt-1/2/0");

		Interface client2 = new Interface();
		client2.setTemplateName(VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
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
