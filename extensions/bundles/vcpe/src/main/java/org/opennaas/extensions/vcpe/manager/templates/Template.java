/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.model.BGP;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.VRRP;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

/**
 * @author Jordi
 */
public class Template implements ITemplate {

	private static final String	TEMPLATE		= "/templates/template.properties";
	private static final String	BGP_TEMPLATE	= "/templates/bgpModel1.properties";

	private Properties			props;
	private Properties			bgpProps;

	/**
	 * @throws VCPENetworkManagerException
	 * 
	 */
	public Template() throws VCPENetworkManagerException {
		try {
			props = new Properties();
			props.load(this.getClass().getResourceAsStream(TEMPLATE));

			bgpProps = new Properties();
			bgpProps.load(this.getClass().getResourceAsStream(BGP_TEMPLATE));

		} catch (IOException e) {
			throw new VCPENetworkManagerException("can't load the template properties");
		}
	}

	/**
	 * Generate the model
	 * 
	 * @return VCPENetworkModel
	 */
	@Override
	public VCPENetworkModel buildModel(VCPENetworkModel initialModel) {
		VCPENetworkModel model = new VCPENetworkModel();
		model.setVcpeNetworkId(initialModel.getVcpeNetworkId());
		model.setVcpeNetworkName(initialModel.getVcpeNetworkName());
		model.setClientIpAddressRange(initialModel.getClientIpAddressRange());
		model.setTemplateName(initialModel.getTemplateName());

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		model.setElements(elements);

		// Generate the physical model
		List<VCPENetworkElement> physicalElements = generatePhysicalElements(initialModel);

		// Generate the logical model
		List<VCPENetworkElement> logicalElements = generateLogicalElements(initialModel);

		// set VRRP configuration
		model.setVrrp(configureVRRP(initialModel));

		model.setBgp(generateBGPConfig(initialModel));

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
		Router vcpe1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName(props.getProperty("vcpenetwork.logicalrouter1.name") + "-" + initialModel.getVcpeNetworkName());

		// Interfaces VCPE-router1
		Interface inter1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.INTER1_INTERFACE_LOCAL);
		Interface down1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.DOWN1_INTERFACE_LOCAL);
		Interface up1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.UP1_INTERFACE_LOCAL);

		// Other interfaces VCPE-router1
		String inter1OtherName = props.getProperty("vcpenetwork.logicalrouter1.interface.inter.other.name");
		String inter1OtherPort = props.getProperty("vcpenetwork.logicalrouter1.interface.inter.other.port");
		Long inter1OtherVlan = Long.valueOf(props.getProperty("vcpenetwork.logicalrouter1.interface.inter.other.vlan").trim());
		Interface inter1other = getInterface(inter1OtherName + "." + inter1OtherPort, VCPETemplate.INTER1_INTERFACE_AUTOBAHN, inter1OtherVlan, null,
				inter1OtherName, Integer.parseInt(inter1OtherPort));

		String down1OtherName = props.getProperty("vcpenetwork.logicalrouter1.interface.down.other.name");
		String down1OtherPort = props.getProperty("vcpenetwork.logicalrouter1.interface.down.other.port");
		Long down1OtherVlan = Long.valueOf(props.getProperty("vcpenetwork.logicalrouter1.interface.down.other.vlan"));
		Interface down1other = getInterface(down1OtherName + "." + down1OtherPort, VCPETemplate.DOWN1_INTERFACE_AUTOBAHN, down1OtherVlan, null,
				down1OtherName, Integer.parseInt(down1OtherPort));

		String up1OtherName = props.getProperty("vcpenetwork.logicalrouter1.interface.up.other.name");
		String up1OtherPort = props.getProperty("vcpenetwork.logicalrouter1.interface.up.other.port");
		Long up1OtherVlan = Long.valueOf(props.getProperty("vcpenetwork.logicalrouter1.interface.up.other.vlan"));
		String up1OtherIp = props.getProperty("vcpenetwork.logicalrouter1.interface.up.other.ipaddress");
		Interface up1other = getInterface(up1OtherName + "." + up1OtherPort, VCPETemplate.UP1_INTERFACE_PEER, up1OtherVlan, up1OtherIp,
				up1OtherName, Integer.parseInt(up1OtherPort));

		// ----------------------------- VCPE-router2 -----------------------------
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName(props.getProperty("vcpenetwork.logicalrouter2.name") + "-" + initialModel.getVcpeNetworkName());

		// Interfaces VCPE-router2
		Interface inter2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.INTER2_INTERFACE_LOCAL);
		Interface down2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.DOWN2_INTERFACE_LOCAL);
		Interface up2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.UP2_INTERFACE_LOCAL);

		// Other interfaces VCPE-router2
		String inter2OtherName = props.getProperty("vcpenetwork.logicalrouter2.interface.inter.other.name");
		String inter2OtherPort = props.getProperty("vcpenetwork.logicalrouter2.interface.inter.other.port");
		Long inter2OtherVlan = Long.valueOf(props.getProperty("vcpenetwork.logicalrouter2.interface.inter.other.vlan").trim());
		Interface inter2other = getInterface(inter2OtherName + "." + inter2OtherPort, VCPETemplate.INTER2_INTERFACE_AUTOBAHN, inter2OtherVlan, null,
				inter2OtherName, Integer.parseInt(inter2OtherPort));

		String down2OtherName = props.getProperty("vcpenetwork.logicalrouter2.interface.down.other.name");
		String down2OtherPort = props.getProperty("vcpenetwork.logicalrouter2.interface.down.other.port");
		Long down2OtherVlan = Long.valueOf(props.getProperty("vcpenetwork.logicalrouter2.interface.down.other.vlan").trim());
		Interface down2other = getInterface(down2OtherName + "." + down2OtherPort, VCPETemplate.DOWN2_INTERFACE_AUTOBAHN, down2OtherVlan, null,
				down2OtherName, Integer.parseInt(down2OtherPort));

		String up2OtherName = props.getProperty("vcpenetwork.logicalrouter2.interface.up.other.name");
		String up2OtherPort = props.getProperty("vcpenetwork.logicalrouter2.interface.up.other.port");
		Long up2OtherVlan = Long.valueOf(props.getProperty("vcpenetwork.logicalrouter2.interface.up.other.vlan").trim());
		String up2OtherIp = props.getProperty("vcpenetwork.logicalrouter2.interface.up.other.ipaddress");
		Interface up2other = getInterface(up2OtherName + "." + up2OtherPort, VCPETemplate.UP2_INTERFACE_PEER, up2OtherVlan, up2OtherIp,
				up2OtherName, Integer.parseInt(up2OtherPort));

		// Client interfaces
		Interface client1other = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		Interface client2other = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		// ----------------------------- Links ------------------------------------
		// Inter links
		String linkInter1otherId = props.getProperty("vcpenetwork.logicalrouter1.link.inter.other.id");

		Link linkInter1local = getLink(null, VCPETemplate.INTER1_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, inter1, inter1other);
		Link linkInter1other = getLink(linkInter1otherId, VCPETemplate.INTER_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, inter1other, inter2other);
		Link linkInter2local = getLink(null, VCPETemplate.INTER2_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, inter2, inter2other);

		// Down links
		String linkDown1otherId = props.getProperty("vcpenetwork.logicalrouter1.link.down.other.id");
		String linkDown2otherId = props.getProperty("vcpenetwork.logicalrouter2.link.down.other.id");

		Link linkDown1local = getLink(null, VCPETemplate.DOWN1_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, down1, down1other);
		Link linkDown1other = getLink(linkDown1otherId, VCPETemplate.DOWN1_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, down1other, client1other);

		Link linkDown2local = getLink(null, VCPETemplate.DOWN2_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, down2, down2other);
		Link linkDown2other = getLink(linkDown2otherId, VCPETemplate.DOWN2_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, down2other, client2other);

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

	private List<VCPENetworkElement> generatePhysicalElements(VCPENetworkModel initialModel) {

		// TODO MUST CHECK CREATED ELEMENTS EXIST IN PHYSICAL TOPOLOGY

		Router r1 = new Router();
		r1.setNameInTemplate(VCPETemplate.CPE1_PHY_ROUTER);
		r1.setName(props.getProperty("vcpenetwork.router1.name"));

		Interface inter1 = new Interface();
		inter1.setNameInTemplate(VCPETemplate.INTER1_PHY_INTERFACE_LOCAL);
		inter1.setName(props.getProperty("vcpenetwork.router1.interface.inter.name"));

		Interface inter1other = new Interface();
		inter1other.setNameInTemplate(VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);
		inter1other.setName(props.getProperty("vcpenetwork.router1.interface.inter.other.name"));

		Interface down1 = new Interface();
		down1.setNameInTemplate(VCPETemplate.DOWN1_PHY_INTERFACE_LOCAL);
		down1.setName(props.getProperty("vcpenetwork.router1.interface.down.name"));

		Interface down1other = new Interface();
		down1other.setNameInTemplate(VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);
		down1other.setName(props.getProperty("vcpenetwork.router1.interface.down.other.name"));

		Interface up1 = new Interface();
		up1.setNameInTemplate(VCPETemplate.UP1_PHY_INTERFACE_LOCAL);
		up1.setName(props.getProperty("vcpenetwork.router1.interface.up.name"));

		// select client1 interface using initialModel
		Interface inputClient1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);

		Interface client1 = new Interface();
		client1.setNameInTemplate(VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		client1.setName(inputClient1.getPhysicalInterfaceName());
		// TODO check there is a physical interface in physical topology with client1.getName() name

		List<Interface> r1Interfaces = new ArrayList<Interface>();
		r1Interfaces.add(inter1);
		r1Interfaces.add(down1);
		r1Interfaces.add(up1);
		r1.setInterfaces(r1Interfaces);

		Router r2 = new Router();
		r2.setNameInTemplate(VCPETemplate.CPE2_PHY_ROUTER);
		r2.setName(props.getProperty("vcpenetwork.router2.name"));

		Interface inter2 = new Interface();
		inter2.setNameInTemplate(VCPETemplate.INTER2_PHY_INTERFACE_LOCAL);
		inter2.setName(props.getProperty("vcpenetwork.router2.interface.inter.name"));

		Interface inter2other = new Interface();
		inter2other.setNameInTemplate(VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);
		inter2other.setName(props.getProperty("vcpenetwork.router2.interface.inter.other.name"));

		Interface down2 = new Interface();
		down2.setNameInTemplate(VCPETemplate.DOWN2_PHY_INTERFACE_LOCAL);
		down2.setName(props.getProperty("vcpenetwork.router2.interface.down.name"));

		Interface down2other = new Interface();
		down2other.setNameInTemplate(VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);
		down2other.setName(props.getProperty("vcpenetwork.router2.interface.down.other.name"));

		Interface up2 = new Interface();
		up2.setNameInTemplate(VCPETemplate.UP2_PHY_INTERFACE_LOCAL);
		up2.setName(props.getProperty("vcpenetwork.router2.interface.up.name"));

		// select client1 interface using initialModel
		Interface inputClient2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(initialModel, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		Interface client2 = new Interface();
		client2.setNameInTemplate(VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
		client2.setName(inputClient2.getPhysicalInterfaceName());
		// TODO check there is a physical interface in physical topology with client2.getName() name

		List<Interface> r2Interfaces = new ArrayList<Interface>();
		r2Interfaces.add(inter2);
		r2Interfaces.add(down2);
		r2Interfaces.add(up2);
		r2.setInterfaces(r2Interfaces);

		Domain autobahn = new Domain();
		autobahn.setNameInTemplate(VCPETemplate.AUTOBAHN);
		autobahn.setName(props.getProperty("vcpenetwork.bod.name"));

		List<Interface> autobahnInterfaces = new ArrayList<Interface>();
		autobahnInterfaces.add(inter1other);
		autobahnInterfaces.add(inter2other);
		autobahnInterfaces.add(down1other);
		autobahnInterfaces.add(down2other);
		autobahnInterfaces.add(client1);
		autobahnInterfaces.add(client2);
		autobahn.setInterfaces(autobahnInterfaces);

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		elements.add(r1);
		elements.addAll(r1.getInterfaces());
		elements.add(r2);
		elements.addAll(r2.getInterfaces());
		elements.add(autobahn);
		elements.addAll(autobahn.getInterfaces());

		return elements;
	}

	private VRRP configureVRRP(VCPENetworkModel model) {
		// VRRP group
		int vrrpGoup = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.group"));
		// configuration VCPE-router1
		int masterVRRPPriority = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.master.priority"));
		// configuration VCPE-router2
		int backupVRRPPriority = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.backup.priority"));

		// get master router and interface
		Router masterRouter = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Interface masterInterface = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.DOWN1_INTERFACE_LOCAL);
		masterRouter.getName();
		// get backup router and interface
		Router backupRouter = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);
		Interface backupInterface = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.DOWN2_INTERFACE_LOCAL);

		// set values
		VRRP vrrp = new VRRP();
		vrrp.setVirtualIPAddress(model.getVrrp().getVirtualIPAddress());
		vrrp.setGroup(vrrpGoup);
		vrrp.setPriorityMaster(masterVRRPPriority);
		vrrp.setPriorityBackup(backupVRRPPriority);
		vrrp.setMasterRouter(masterRouter);
		vrrp.setMasterInterface(masterInterface);
		vrrp.setBackupRouter(backupRouter);
		vrrp.setBackupInterface(backupInterface);
		return vrrp;
	}

	private ConfigureBGPRequestParameters generateBGPParameters(VCPENetworkModel model) {
		BGP bgp = model.getBgp();
		ConfigureBGPRequestParameters params = new ConfigureBGPRequestParameters();
		params.clientIPRanges = bgp.getCustomerPrefixes();
		params.clientASNumber = bgp.getClientASNumber();
		params.remoteASNum = bgp.getNocASNumber();

		params.loAddr1 = "193.1.190.141/30"; // TODO get this from GUI
		params.upRemoteAddr1 = "193.1.190.134/30"; // TODO get this from GUI
		params.interAddr1 = ((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.INTER1_INTERFACE_LOCAL))
				.getIpAddress();

		params.loAddr2 = "193.1.190.145/30"; // TODO get this from GUI
		params.upRemoteAddr2 = "193.1.190.130/30"; // TODO get this from GUI
		params.interAddr2 = ((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.INTER2_INTERFACE_LOCAL))
				.getIpAddress();

		return params;
	}

	private BGP generateBGPConfig(VCPENetworkModel initialModel) {

		BGP bgp = initialModel.getBgp();

		ConfigureBGPRequestParameters bgpParams = generateBGPParameters(initialModel);

		// FACTORY 1
		String router1id = IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.loAddr1)[0];

		Properties props1 = (Properties) bgpProps.clone();
		props1.setProperty("bgp.routerid", router1id); // no mask

		props1.setProperty("bgp.group.0.peeras", bgpParams.remoteASNum);
		props1.setProperty("bgp.group.0.session.0.peeras", bgpParams.remoteASNum);
		props1.setProperty("bgp.group.0.session.0.peername", IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.upRemoteAddr1)[0]); // no
																																				// mask
		props1.setProperty("bgp.group.1.peeras", bgpParams.clientASNumber);
		props1.setProperty("bgp.group.1.session.0.peeras", bgpParams.clientASNumber);
		props1.setProperty("bgp.group.1.session.0.peername", IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.interAddr2)[0]); // no mask

		// prefixes
		props1.setProperty("prefixlist.2.prefixes.size", Integer.toString(bgp.getCustomerPrefixes().size() + 1));
		props1.setProperty("prefixlist.2.prefix." + 0, IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.loAddr1)[0] + "/32");
		for (int i = 0; i < bgp.getCustomerPrefixes().size(); i++) {
			props1.setProperty("prefixlist.2.prefix." + (i + 1), bgp.getCustomerPrefixes().get(i));
		}

		// policies
		props1.setProperty("policy.0.rule.0.condition.0.filterlist.0.entries.size", Integer.toString(bgp.getCustomerPrefixes().size() + 1));
		props1.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + 0 + ".type", "routeFilterEntry");
		props1.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + 0 + ".address", IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.loAddr1)[0] + "/32");
		props1.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + 0 + ".option", "exact");
		for (int i = 0; i < bgp.getCustomerPrefixes().size(); i++) {
			props1.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + (i + 1) + ".type", "routeFilterEntry");
			props1.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + (i + 1) + ".address", bgp.getCustomerPrefixes().get(i));
			props1.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + (i + 1) + ".option", "exact");
		}

		BGPModelFactory factory1 = new BGPModelFactory(props1);
		bgp.setBgpConfigForMaster(factory1.createRouterWithBGP());

		// FACTORY 2
		String router2id = IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.loAddr2)[0];

		Properties props2 = (Properties) bgpProps.clone();
		props2.setProperty("bgp.routerid", router2id); // no mask

		props2.setProperty("bgp.group.0.peeras", bgpParams.remoteASNum);
		props2.setProperty("bgp.group.0.session.0.peeras", bgpParams.remoteASNum);
		props2.setProperty("bgp.group.0.session.0.peername", IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.upRemoteAddr2)[0]); // no
																																				// mask
		props2.setProperty("bgp.group.1.peeras", bgpParams.clientASNumber);
		props2.setProperty("bgp.group.1.session.0.peeras", bgpParams.clientASNumber);
		props2.setProperty("bgp.group.1.session.0.peername", IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.interAddr1)[0]); // no mask

		// prefixes
		props2.setProperty("prefixlist.2.prefixes.size", Integer.toString(bgp.getCustomerPrefixes().size() + 1));
		props2.setProperty("prefixlist.2.prefix." + 0, IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.loAddr2)[0] + "/32");
		for (int i = 0; i < bgp.getCustomerPrefixes().size(); i++) {
			props2.setProperty("prefixlist.2.prefix." + (i + 1), bgp.getCustomerPrefixes().get(i));
		}

		// policies
		props2.setProperty("policy.0.rule.0.condition.0.filterlist.0.entries.size", Integer.toString(bgp.getCustomerPrefixes().size() + 1));
		props2.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + 0 + ".type", "routeFilterEntry");
		props2.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + 0 + ".address", IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.loAddr2)[0] + "/32");
		props2.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + 0 + ".option", "exact");
		for (int i = 0; i < bgp.getCustomerPrefixes().size(); i++) {
			props2.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + (i + 1) + ".type", "routeFilterEntry");
			props2.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + (i + 1) + ".address", bgp.getCustomerPrefixes().get(i));
			props2.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + (i + 1) + ".option", "exact");
		}

		BGPModelFactory factory2 = new BGPModelFactory(props2);
		bgp.setBgpConfigForBackup(factory2.createRouterWithBGP());

		return bgp;
	}

	/**
	 * @param name
	 * @param nameInTemplate
	 * @param vlan
	 * @param ipAddress
	 * @return the interface
	 */
	private Interface getInterface(String name, String nameInTemplate, long vlan, String ipAddress, String physicalInterfaceName, int portNum) {
		Interface iface = new Interface();
		iface.setName(name);
		iface.setNameInTemplate(nameInTemplate);
		iface.setIpAddress(ipAddress);
		iface.setVlanId(vlan);
		iface.setPhysicalInterfaceName(physicalInterfaceName);
		iface.setPortNumber(portNum);
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
