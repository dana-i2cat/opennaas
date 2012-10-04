package org.opennaas.extensions.vcpe.test;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.domain.NetworkDomain;
import org.opennaas.extensions.network.model.technology.ethernet.TaggedEthernetInterface;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.network.model.topology.Path;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.topology.virtual.VirtualDevice;
import org.opennaas.extensions.vcpe.model.topology.virtual.VirtualInterface;
import org.opennaas.extensions.vcpe.model.topology.virtual.VirtualLink;
import org.opennaas.extensions.vcpe.model.topology.virtual.VirtualNetworkDomain;

public class ModelTest {

	public void mappingSample() {

		VCPENetworkModel vCPENetModel = new VCPENetworkModel();
		vCPENetModel.setPhysicalTopology(generateSamplePhysicalTopology());
		vCPENetModel.setVirtualTopology(generateSampleVirtualTopology());
		vCPENetModel.setvCPENetworkTopology(generateSampleMapping(
				vCPENetModel.getVirtualTopology(),
				vCPENetModel.getPhysicalTopology()));

	}

	public void createGuidedRequestSample() {
		VCPENetworkModel vCPENetModel = new VCPENetworkModel();
		vCPENetModel.setVirtualTopology(generateSampleVirtualTopology());

		// user must guide the mapping, providing identifiers for
		// physical interfaces and vlans for each virtual interface

		for (Interface iface : NetworkModelHelper.getInterfaces(
				vCPENetModel.getVirtualTopology().getNetworkElements())) {

			if (iface instanceof VirtualInterface) { // ignore IP interfaces

				String physicalIfaceName = ""; // autobahn id or router:interfaceName
				long vlanID = 0; // vlanID for that interface (if any)
				String logicalIfaceName = ""; // OpenNaaS can generate it. No need to be user specified

				VirtualInterface virtualIface = (VirtualInterface) getInterfaceByName(
						vCPENetModel.getvCPENetworkTopology().getNetworkElements(), "IfaceName");
				Interface impl;
				if (vlanID > 0) {
					TaggedEthernetInterface clientIface = new TaggedEthernetInterface();
					clientIface.setVlanID(vlanID);
					impl = clientIface;
				} else {
					Interface clientIface = new Interface();
					impl = clientIface;
				}
				virtualIface.setImplementedBy(impl);

				Interface phy = new Interface();
				phy.setName(physicalIfaceName);
				impl.setServerInterface(phy);

			}
		}

	}

	public static NetworkModel generateSampleVirtualTopology() {
		List<NetworkElement> virtualTopology = new ArrayList<NetworkElement>();

		// client domain
		NetworkDomain clientDomain = new VirtualNetworkDomain();
		clientDomain.setName("clientAD");
		Interface clientIface1 = new VirtualInterface();
		clientIface1.setName("clientIface1");
		Interface clientIface2 = new VirtualInterface();
		clientIface2.setName("clientIface2");
		clientDomain.getHasInterface().add(clientIface1);
		clientDomain.getHasInterface().add(clientIface2);

		virtualTopology.add(clientDomain);
		virtualTopology.add(clientIface1);
		virtualTopology.add(clientIface2);

		// LRs
		Device lr1 = new VirtualDevice();
		lr1.setName("vCPE1");
		Interface lr1Iface1 = new VirtualInterface();
		lr1Iface1.setName("lr1DOWN");
		lr1Iface1.setDevice(lr1);
		Interface lr1Iface2 = new VirtualInterface();
		lr1Iface2.setName("lr1UP");
		lr1Iface2.setDevice(lr1);
		Interface lr1Iface3 = new VirtualInterface();
		lr1Iface3.setName("lr1INTER");
		lr1Iface3.setDevice(lr1);
		lr1.getInterfaces().add(lr1Iface1);
		lr1.getInterfaces().add(lr1Iface2);
		lr1.getInterfaces().add(lr1Iface3);

		Device lr2 = new VirtualDevice();
		lr2.setName("vCPE2");
		Interface lr2Iface1 = new VirtualInterface();
		lr2Iface1.setName("lr1DOWN");
		lr2Iface1.setDevice(lr2);
		Interface lr2Iface2 = new VirtualInterface();
		lr2Iface2.setName("lr1UP");
		lr2Iface2.setDevice(lr2);
		Interface lr2Iface3 = new VirtualInterface();
		lr2Iface3.setName("lr1INTER");
		lr2Iface3.setDevice(lr2);
		lr2.getInterfaces().add(lr2Iface1);
		lr2.getInterfaces().add(lr2Iface2);
		lr2.getInterfaces().add(lr2Iface3);

		virtualTopology.add(lr1);
		virtualTopology.add(lr1Iface1);
		virtualTopology.add(lr1Iface2);
		virtualTopology.add(lr1Iface3);
		virtualTopology.add(lr2);
		virtualTopology.add(lr2Iface1);
		virtualTopology.add(lr2Iface2);
		virtualTopology.add(lr2Iface3);

		// NOC domain
		NetworkDomain nocDomain = new VirtualNetworkDomain();
		clientDomain.setName("nocAD");
		Interface nocIface1 = new VirtualInterface();
		nocIface1.setName("nocIface1");
		Interface nocIface2 = new VirtualInterface();
		nocIface2.setName("nocIface2");
		nocDomain.getHasInterface().add(nocIface1);
		nocDomain.getHasInterface().add(nocIface2);

		virtualTopology.add(nocDomain);
		virtualTopology.add(nocIface1);
		virtualTopology.add(nocIface2);

		// Links
		Link up1 = new VirtualLink();
		up1.setName("up1");
		up1.setSource(lr1Iface2);
		up1.setSource(nocIface1);
		lr1Iface2.setLinkTo(up1);
		nocIface2.setLinkTo(up1);

		Link up2 = new VirtualLink();
		up1.setName("up2");
		up2.setSource(lr2Iface2);
		up2.setSource(nocIface2);
		lr1Iface2.setLinkTo(up2);
		nocIface2.setLinkTo(up2);

		Link inter = new VirtualLink();
		up1.setName("inter");
		inter.setSource(lr1Iface3);
		inter.setSink(lr2Iface3);
		lr1Iface3.setLinkTo(inter);
		lr2Iface3.setLinkTo(inter);

		Link down1 = new VirtualLink();
		up1.setName("down1");
		down1.setSource(lr1Iface1);
		down1.setSink(clientIface1);
		lr1Iface1.setLinkTo(down1);
		clientIface1.setLinkTo(down1);

		Link down2 = new VirtualLink();
		up1.setName("down2");
		down2.setSource(lr2Iface1);
		down2.setSink(clientIface2);
		lr2Iface1.setLinkTo(down2);
		clientIface2.setLinkTo(down2);

		// TODO CREATE IP INTERFACES IF REQUIRED
		// IP addresses are specified in the virtual topology
		// IP addresses have a virtual interface as a server interface

		virtualTopology.add(up1);
		virtualTopology.add(up2);
		virtualTopology.add(inter);
		virtualTopology.add(down1);
		virtualTopology.add(down2);

		NetworkModel virtualModel = new NetworkModel();
		virtualModel.setNetworkElements(virtualTopology);

		return virtualModel;
	}

	public static NetworkModel generateSamplePhysicalTopology() {

		List<NetworkElement> topology = new ArrayList<NetworkElement>();

		// PR1
		Device pr1 = new Device();
		pr1.setName("router:lola");
		Interface pr1Iface1 = new Interface();
		pr1Iface1.setName("router:lola:lt-0/1/2");
		pr1Iface1.setDevice(pr1);
		Interface pr1Iface2 = new Interface();
		pr1Iface2.setName("router:lola:ge-0/1/2");
		pr1Iface2.setDevice(pr1);
		pr1.getInterfaces().add(pr1Iface1);
		pr1.getInterfaces().add(pr1Iface2);

		topology.add(pr1);
		topology.add(pr1Iface1);
		topology.add(pr1Iface2);

		// PR2
		Device pr2 = new Device();
		pr2.setName("router:myre");
		Interface pr2Iface1 = new Interface();
		pr2Iface1.setName("router:myre:lt-0/1/2");
		pr2Iface1.setDevice(pr2);
		Interface pr2Iface2 = new Interface();
		pr2Iface2.setName("router:myre:ge-0/1/2");
		pr2Iface2.setDevice(pr2);
		pr2.getInterfaces().add(pr2Iface1);
		pr2.getInterfaces().add(pr2Iface2);

		topology.add(pr2);
		topology.add(pr2Iface1);
		topology.add(pr2Iface2);

		// BoD
		NetworkDomain bod = new NetworkDomain();
		bod.setName("Autobahn");
		Interface bodIface1 = new Interface();
		bodIface1.setName("autobahn:interface1ID");
		Interface bodIface2 = new Interface();
		bodIface2.setName("autobahn:interface2ID");
		Interface bodIface3 = new Interface();
		bodIface3.setName("autobahn:interface3ID");

		bod.getHasInterface().add(bodIface1);
		bod.getHasInterface().add(bodIface2);
		bod.getHasInterface().add(bodIface3);

		topology.add(bod);
		topology.add(bodIface1);
		topology.add(bodIface2);
		topology.add(bodIface3);

		// client
		NetworkDomain clientDomain = new NetworkDomain();
		clientDomain.setName("clientDomain");
		clientDomain.getHasInterface().add(bodIface3);

		topology.add(clientDomain);

		// Links
		Link link1 = new Link();
		link1.setSource(pr1Iface2);
		link1.setSink(bodIface1);
		pr1Iface2.setLinkTo(link1);
		bodIface1.setLinkTo(link1);

		Link link2 = new Link();
		link2.setSource(pr2Iface2);
		link2.setSink(bodIface2);
		pr2Iface2.setLinkTo(link2);
		bodIface2.setLinkTo(link2);

		topology.add(link1);
		topology.add(link2);

		NetworkModel physicalNetwork = new NetworkModel();
		physicalNetwork.setNetworkElements(topology);
		// ADD REFERENCES TO OPENNAAS RESOURCES (physical routers and autobahn)
		physicalNetwork.getResourceReferences().put("router:myre", "ID1");
		physicalNetwork.getResourceReferences().put("router:lola", "ID2");
		physicalNetwork.getResourceReferences().put("Autobahn", "ID3");

		return physicalNetwork;

	}

	public static NetworkModel generateSampleMapping(NetworkModel virtualModel,
			NetworkModel physicalModel) {

		List<NetworkElement> physicalTopology = physicalModel.getNetworkElements();
		List<NetworkElement> virtualTopology = virtualModel.getNetworkElements();

		List<NetworkElement> created = new ArrayList<NetworkElement>();

		// LR1
		Device lr1 = new Device();
		lr1.setName("LR1");
		Interface lr1Iface1 = new Interface();
		lr1Iface1.setName("ge-0/1/2.1");
		lr1Iface1.setDevice(lr1);
		Interface lr1Iface2 = new Interface();
		lr1Iface2.setName("lt-0/1/2.1");
		lr1Iface2.setDevice(lr1);
		Interface lr1Iface3 = new Interface();
		lr1Iface3.setName("ge-0/1/2.2");
		lr1Iface3.setDevice(lr1);
		lr1.getInterfaces().add(lr1Iface1);
		lr1.getInterfaces().add(lr1Iface2);
		lr1.getInterfaces().add(lr1Iface3);

		// set server interfaces (relation with physical topology)
		Interface phy1 = getInterfaceByName(physicalTopology, "router:lola:lt-0/1/2");
		Interface phy2 = getInterfaceByName(physicalTopology, "router:lola:ge-0/1/2");
		lr1Iface1.setServerInterface(phy2);
		lr1Iface2.setServerInterface(phy1);
		lr1Iface3.setServerInterface(phy2);

		// set implementedBy (mapping relationship)
		VirtualInterface downIface = (VirtualInterface) getInterfaceByName(virtualTopology, "lr1DOWN");
		VirtualInterface upIface = (VirtualInterface) getInterfaceByName(virtualTopology, "lr1UP");
		VirtualInterface interIface = (VirtualInterface) getInterfaceByName(virtualTopology, "lr1INTER");
		downIface.setImplementedBy(lr1Iface1);
		upIface.setImplementedBy(lr1Iface2);
		interIface.setImplementedBy(lr1Iface3);

		VirtualDevice vCPE1 = (VirtualDevice) getDeviceByName(virtualTopology, "vCPE1");
		vCPE1.setImplementedBy(lr1);

		// LR2
		// the same as in LR2
		Device lr2 = new Device();
		Interface lr2Iface1 = new Interface();
		Interface lr2Iface2 = new Interface();
		Interface lr2Iface3 = new Interface();
		Interface phy3 = getInterfaceByName(physicalTopology, "router:myre:lt-0/1/2");
		Interface phy4 = getInterfaceByName(physicalTopology, "router:myre:ge-0/1/2");
		// TODO to be completed, just like LR1

		// TRICK TO AGGREGATE PHYSICAL ROUTERS INTO A DOMAIN
		NetworkDomain nocAD = new NetworkDomain();
		Interface noc1 = new Interface();
		noc1.setName("router:lola:lt-0/1/2.2");
		noc1.setServerInterface(phy1);
		Interface noc2 = new Interface();
		noc2.setName("router:myre:lt-0/1/2.2");
		noc2.setServerInterface(phy3);
		nocAD.getHasInterface().add(noc1);
		nocAD.getHasInterface().add(noc2);

		Device phyRouter1 = getDeviceByName(physicalTopology, "router:lola");
		Device phyRouter2 = getDeviceByName(physicalTopology, "router:myre");
		noc1.setDevice(phyRouter1);
		noc2.setDevice(phyRouter2);
		nocAD.getHasDevice().add(phyRouter1);
		nocAD.getHasDevice().add(phyRouter2);

		VirtualNetworkDomain nocDomain = (VirtualNetworkDomain) getNetworkDomainByName(virtualTopology, "nocAD");
		nocDomain.setImplementedBy(nocAD);

		// Autobahn (create 3 links)
		Interface autobahnPhy1 = getInterfaceByName(physicalTopology, "autobahn:interface1ID");
		Interface autobahnPhy2 = getInterfaceByName(physicalTopology, "autobahn:interface2ID");
		Interface autobahnPhy3 = getInterfaceByName(physicalTopology, "autobahn:interface3ID");

		Interface autobahn1 = new Interface();
		Interface autobahn2 = new Interface();
		Link bodLink1 = new Link();
		bodLink1.setSource(autobahn1);
		bodLink1.setSink(autobahn2);
		autobahn1.setLinkTo(bodLink1);
		autobahn2.setLinkTo(bodLink1);
		autobahn1.setServerInterface(autobahnPhy1);
		autobahn2.setServerInterface(autobahnPhy2);

		Interface autobahn3 = new Interface();
		Interface autobahn4 = new Interface();
		Link bodLink2 = new Link();
		bodLink2.setSource(autobahn3);
		bodLink2.setSink(autobahn4);
		autobahn3.setLinkTo(bodLink2);
		autobahn4.setLinkTo(bodLink2);
		autobahn3.setServerInterface(autobahnPhy1);
		autobahn4.setServerInterface(autobahnPhy3); // client AD

		Interface autobahn5 = new Interface();
		Interface autobahn6 = new Interface();
		Link bodLink3 = new Link();
		bodLink3.setSource(autobahn5);
		bodLink3.setSink(autobahn6);
		autobahn5.setLinkTo(bodLink3);
		autobahn6.setLinkTo(bodLink3);
		autobahn5.setServerInterface(autobahnPhy2);
		autobahn6.setServerInterface(autobahnPhy3); // client AD

		// path inter
		// path inter: configure interfaces
		Link inter1 = new Link();
		inter1.setSource(lr1Iface3); // inter iface for LR1
		inter1.setSink(autobahn1);

		Link inter2 = new Link();
		inter2.setSource(lr2Iface3); // inter iface for LR2
		inter1.setSink(autobahn2);

		// path inter: created from three links
		Path interPath = new Path();
		interPath.getPathSegments().add(inter1);
		interPath.getPathSegments().add(bodLink1);
		interPath.getPathSegments().add(inter2);

		VirtualLink interLink = (VirtualLink) getLinkByName(virtualTopology, "inter");
		interLink.setImplementedBy(interPath);

		// pathdown1
		Link down11 = new Link();
		down11.setSource(lr1Iface1); // down iface for LR1
		down11.setSink(autobahn3);

		Path down1 = new Path();
		down1.getPathSegments().add(down11);
		down1.getPathSegments().add(bodLink2);

		VirtualLink down1Link = (VirtualLink) getLinkByName(virtualTopology, "down1");
		down1Link.setImplementedBy(down1);

		// pathdown2
		Link down21 = new Link();
		down21.setSource(lr2Iface1); // down iface for LR2
		down21.setSink(autobahn5);

		Path down2 = new Path();
		down1.getPathSegments().add(down21);
		down1.getPathSegments().add(bodLink3);

		VirtualLink down2Link = (VirtualLink) getLinkByName(virtualTopology, "down2");
		down2Link.setImplementedBy(down2);

		// linkup1
		Link up1 = new Link();
		up1.setSource(lr1Iface2); // up iface for LR1
		up1.setSink(noc1);

		VirtualLink up1Link = (VirtualLink) getLinkByName(virtualTopology, "up1");
		up1Link.setImplementedBy(up1);

		// linkup2
		Link up2 = new Link();
		up2.setSource(lr2Iface2); // up iface for LR1
		up2.setSink(noc2);

		VirtualLink up2Link = (VirtualLink) getLinkByName(virtualTopology, "up2");
		up2Link.setImplementedBy(up2);

		// add everything to created
		created.add(lr1);
		created.addAll(lr1.getInterfaces());
		created.add(lr2);
		created.addAll(lr2.getInterfaces());
		created.add(nocAD);
		created.addAll(nocAD.getHasInterface());
		created.add(autobahn1);
		created.add(autobahn2);
		created.add(autobahn3);
		created.add(autobahn4);
		created.add(autobahn5);
		created.add(autobahn6);
		created.add(interPath);
		created.addAll(interPath.getPathSegments());
		created.add(down1);
		created.addAll(down1.getPathSegments());
		created.add(down2);
		created.addAll(down2.getPathSegments());
		created.add(up1);
		created.add(up2);

		NetworkModel mappingNetwork = new NetworkModel();
		mappingNetwork.setNetworkElements(created);
		// ADD REFERENCES TO OPENNAAS RESOURCES (only LRs)
		mappingNetwork.getResourceReferences().put(lr1.getName(), "ID11");
		mappingNetwork.getResourceReferences().put(lr2.getName(), "ID12");

		return mappingNetwork;
	}

	private static NetworkDomain getNetworkDomainByName(List<NetworkElement> elements, String domainName) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Device getDeviceByName(List<NetworkElement> elements, String deviceName) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Link getLinkByName(List<NetworkElement> elements, String linkName) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Interface getInterfaceByName(List<NetworkElement> elements, String ifaceName) {
		// TODO Auto-generated method stub
		return null;
	}

}
