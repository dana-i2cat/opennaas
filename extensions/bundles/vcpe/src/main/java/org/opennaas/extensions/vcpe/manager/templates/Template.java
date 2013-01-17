/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import static com.google.common.collect.Iterables.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.model.VCPEManagerModel;
import org.opennaas.extensions.vcpe.manager.model.VCPEPhysicalInfrastructure;
import org.opennaas.extensions.vcpe.model.BGP;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
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

	private String				templateType	= TemplateSelector.VCPE_TEMPLATE;

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

	public String getTemplateType() {
		return templateType;
	}

	/**
	 * Generate the model
	 * 
	 * @return VCPENetworkModel
	 */
	@Override
	public VCPENetworkModel buildModel(VCPENetworkModel initialModel) throws VCPENetworkManagerException {
		VCPENetworkModel model = new VCPENetworkModel();
		model.setId(initialModel.getId());
		model.setName(initialModel.getName());
		model.setClientIpRange(initialModel.getClientIpRange());
		model.setTemplateType(initialModel.getTemplateType());

		// FIXME TEMPORAL CODE. REMOVE WHEN REFACTOR IS COMPLETED. initialModel will be a complete model.
		// Generate the physical model
		VCPENetworkModel phy = generateAndMapPhysicalElements(initialModel);
		// checkPhysicalAvailability(physicalElements, managerModel);
		// Generate the logical model
		VCPENetworkModel logical = generateAndMapLogicalElements(phy, initialModel);

		// set VRRP configuration
		// model.setVrrp(configureVRRP(logical));
		model.setVrrp(logical.getVrrp());
		model.setBgp(generateBGPConfig(logical));

		// Add all elements
		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		model.setElements(elements);
		elements.addAll(logical.getElements());
		return model;
	}

	@Override
	public VCPENetworkModel getPhysicalInfrastructureSuggestion() throws VCPENetworkManagerException {

		VCPENetworkModel generated = generatePhysicalElements();
		// TODO suggested mapping should be more intelligent, not properties driven
		VCPENetworkModel mappedFromProperties = mapPhysicalElementsFromProperties(generated, props);
		// TODO MUST CHECK MAPPED ELEMENTS EXIST IN PHYSICAL TOPOLOGY
		return mappedFromProperties;
	}

	@Override
	public VCPENetworkModel getLogicalInfrastructureSuggestion(VCPENetworkModel physicalInfrastructure) {

		VCPENetworkModel phy = generateAndMapPhysicalElements(physicalInfrastructure);

		VCPENetworkModel generated = generateLogicalElements();
		// TODO suggested mapping should be more intelligent, not properties driven
		VCPENetworkModel mappedFromProperties = mapLogicalElementsFromProperties(generated, props);
		VCPENetworkModel mappedWithPhy = mapLogicalAndPhysical(phy, mappedFromProperties);
		// TODO MUST CHECK MAPPED ELEMENTS EXIST IN PHYSICAL INFRASTRUCTURE
		return mappedWithPhy;
	}

	// FIXME TEMPORAL METHOD. REMOVE WHEN REFACTOR IS FINISHED
	private VCPENetworkModel generateAndMapPhysicalElements(VCPENetworkModel initialModel) {

		VCPENetworkModel generated = generatePhysicalElements();
		VCPENetworkModel mappedFromProperties = mapPhysicalElementsFromProperties(generated, props);
		VCPENetworkModel mapped = mapPhysicalElementsFromInputModel(mappedFromProperties, initialModel);
		// TODO MUST CHECK MAPPED ELEMENTS EXIST IN PHYSICAL TOPOLOGY
		return mapped;
	}

	// FIXME TEMPORAL METHOD. REMOVE WHEN REFACTOR IS FINISHED
	private VCPENetworkModel generateAndMapLogicalElements(VCPENetworkModel phy, VCPENetworkModel initialModel) {

		VCPENetworkModel suggested = getLogicalInfrastructureSuggestion(phy);
		VCPENetworkModel mapped = mapLogicalElementsFromInputModel(suggested, initialModel);
		// TODO MUST CHECK MAPPED ELEMENTS EXIST IN PHYSICAL TOPOLOGY
		return mapped;
	}

	private VCPENetworkModel generatePhysicalElements() {

		Router core = new Router();
		core.setTemplateName(VCPETemplate.CORE_PHY_ROUTER);

		Interface coreMaster = new Interface();
		coreMaster.setTemplateName(VCPETemplate.CORE_PHY_INTERFACE_MASTER);

		Interface coreBkp = new Interface();
		coreBkp.setTemplateName(VCPETemplate.CORE_PHY_INTERFACE_BKP);

		Interface coreLo = new Interface();
		coreLo.setTemplateName(VCPETemplate.CORE_PHY_LO_INTERFACE);

		List<Interface> coreInterfaces = new ArrayList<Interface>();
		coreInterfaces.add(coreMaster);
		coreInterfaces.add(coreBkp);
		coreInterfaces.add(coreLo);
		core.setInterfaces(coreInterfaces);

		Router r1 = new Router();
		r1.setTemplateName(VCPETemplate.CPE1_PHY_ROUTER);

		Interface inter1 = new Interface();
		inter1.setTemplateName(VCPETemplate.INTER1_PHY_INTERFACE_LOCAL);

		Interface inter1other = new Interface();
		inter1other.setTemplateName(VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);

		Interface down1 = new Interface();
		down1.setTemplateName(VCPETemplate.DOWN1_PHY_INTERFACE_LOCAL);

		Interface down1other = new Interface();
		down1other.setTemplateName(VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);

		Interface up1 = new Interface();
		up1.setTemplateName(VCPETemplate.UP1_PHY_INTERFACE_LOCAL);

		// Interface client1 = new Interface();
		// client1.setTemplateName(VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);

		Interface lo1 = new Interface();
		lo1.setTemplateName(VCPETemplate.LO1_PHY_INTERFACE);

		List<Interface> r1Interfaces = new ArrayList<Interface>();
		r1Interfaces.add(inter1);
		r1Interfaces.add(down1);
		r1Interfaces.add(up1);
		r1Interfaces.add(lo1);
		r1.setInterfaces(r1Interfaces);

		Router r2 = new Router();
		r2.setTemplateName(VCPETemplate.CPE2_PHY_ROUTER);

		Interface inter2 = new Interface();
		inter2.setTemplateName(VCPETemplate.INTER2_PHY_INTERFACE_LOCAL);

		Interface inter2other = new Interface();
		inter2other.setTemplateName(VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);

		Interface down2 = new Interface();
		down2.setTemplateName(VCPETemplate.DOWN2_PHY_INTERFACE_LOCAL);

		Interface down2other = new Interface();
		down2other.setTemplateName(VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);

		Interface up2 = new Interface();
		up2.setTemplateName(VCPETemplate.UP2_PHY_INTERFACE_LOCAL);

		// Interface client2 = new Interface();
		// client2.setTemplateName(VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);

		Interface lo2 = new Interface();
		lo2.setTemplateName(VCPETemplate.LO2_PHY_INTERFACE);

		List<Interface> r2Interfaces = new ArrayList<Interface>();
		r2Interfaces.add(inter2);
		r2Interfaces.add(down2);
		r2Interfaces.add(up2);
		r2Interfaces.add(lo2);
		r2.setInterfaces(r2Interfaces);

		Domain autobahn = new Domain();
		autobahn.setTemplateName(VCPETemplate.AUTOBAHN);

		List<Interface> autobahnInterfaces = new ArrayList<Interface>();
		autobahnInterfaces.add(inter1other);
		autobahnInterfaces.add(inter2other);
		autobahnInterfaces.add(down1other);
		autobahnInterfaces.add(down2other);
		// autobahnInterfaces.add(client1);
		// autobahnInterfaces.add(client2);
		autobahn.setInterfaces(autobahnInterfaces);

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		elements.add(core);
		elements.addAll(core.getInterfaces());
		elements.add(r1);
		elements.addAll(r1.getInterfaces());
		elements.add(r2);
		elements.addAll(r2.getInterfaces());
		elements.add(autobahn);
		elements.addAll(autobahn.getInterfaces());

		VCPENetworkModel model = new VCPENetworkModel();
		model.setElements(elements);
		model.setTemplateType(getTemplateType());
		model.setCreated(false);

		return model;
	}

	private VCPENetworkModel generateLogicalElements() {

		// LogicalRouter 1
		Router vcpe1 = new LogicalRouter();
		vcpe1.setTemplateName(VCPETemplate.VCPE1_ROUTER);

		List<Interface> interfaces = new ArrayList<Interface>();
		vcpe1.setInterfaces(interfaces);
		Interface inter1 = new Interface();
		Interface down1 = new Interface();
		Interface up1 = new Interface();
		Interface lo1 = new Interface();
		interfaces.add(inter1);
		interfaces.add(down1);
		interfaces.add(up1);
		interfaces.add(lo1);

		inter1.setTemplateName(VCPETemplate.INTER1_INTERFACE_LOCAL);
		down1.setTemplateName(VCPETemplate.DOWN1_INTERFACE_LOCAL);
		up1.setTemplateName(VCPETemplate.UP1_INTERFACE_LOCAL);
		lo1.setTemplateName(VCPETemplate.LO1_INTERFACE);

		// LogicalRouter 2
		Router vcpe2 = new LogicalRouter();
		vcpe2.setTemplateName(VCPETemplate.VCPE2_ROUTER);

		interfaces = new ArrayList<Interface>();
		vcpe2.setInterfaces(interfaces);
		Interface inter2 = new Interface();
		Interface down2 = new Interface();
		Interface up2 = new Interface();
		Interface lo2 = new Interface();
		interfaces.add(inter2);
		interfaces.add(down2);
		interfaces.add(up2);
		interfaces.add(lo2);

		inter2.setTemplateName(VCPETemplate.INTER2_INTERFACE_LOCAL);
		down2.setTemplateName(VCPETemplate.DOWN2_INTERFACE_LOCAL);
		up2.setTemplateName(VCPETemplate.UP2_INTERFACE_LOCAL);
		lo2.setTemplateName(VCPETemplate.LO2_INTERFACE);

		// BoD
		// Notice these logical interfaces are not inside a BoD object (by now)
		List<Interface> bodInterfaces = new ArrayList<Interface>();
		Interface inter1other = new Interface();
		Interface down1other = new Interface();
		Interface inter2other = new Interface();
		Interface down2other = new Interface();
		Interface client1other = new Interface();
		Interface client2other = new Interface();
		Interface client1otherPhy = new Interface();
		Interface client2otherPhy = new Interface();

		bodInterfaces.add(inter1other);
		bodInterfaces.add(down1other);
		bodInterfaces.add(inter2other);
		bodInterfaces.add(down2other);
		bodInterfaces.add(client1other);
		bodInterfaces.add(client2other);
		bodInterfaces.add(client2otherPhy);
		bodInterfaces.add(client2otherPhy);

		inter1other.setTemplateName(VCPETemplate.INTER1_INTERFACE_AUTOBAHN);
		down1other.setTemplateName(VCPETemplate.DOWN1_INTERFACE_AUTOBAHN);
		inter2other.setTemplateName(VCPETemplate.INTER2_INTERFACE_AUTOBAHN);
		down2other.setTemplateName(VCPETemplate.DOWN2_INTERFACE_AUTOBAHN);
		client1other.setTemplateName(VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		client2other.setTemplateName(VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);
		client1other.setTemplateName(VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		client2other.setTemplateName(VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);

		// noc network interface
		// Notice these logical interfaces are not inside a router object (by now)
		Interface up1other = new Interface();
		Interface up2other = new Interface();

		up1other.setTemplateName(VCPETemplate.UP1_INTERFACE_PEER);
		up2other.setTemplateName(VCPETemplate.UP2_INTERFACE_PEER);

		// LINKS

		// Inter links
		Link linkInter1local = getLink(null, VCPETemplate.INTER1_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, inter1, inter1other);
		Link linkInter1other = getLink(null, VCPETemplate.INTER_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, inter1other, inter2other);
		Link linkInter2local = getLink(null, VCPETemplate.INTER2_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, inter2, inter2other);

		// Down links
		Link linkDown1local = getLink(null, VCPETemplate.DOWN1_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, down1, down1other);
		Link linkDown1other = getLink(null, VCPETemplate.DOWN1_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, down1other, client1other);

		Link linkDown2local = getLink(null, VCPETemplate.DOWN2_LINK_LOCAL, VCPETemplate.LINK_TYPE_ETH, down2, down2other);
		Link linkDown2other = getLink(null, VCPETemplate.DOWN2_LINK_AUTOBAHN, VCPETemplate.LINK_TYPE_AUTOBAHN, down2other, client2other);

		// Up links
		Link linkUp1 = getLink(null, VCPETemplate.UP1_LINK, VCPETemplate.LINK_TYPE_LT, up1, up1other);
		Link linkUp2 = getLink(null, VCPETemplate.UP2_LINK, VCPETemplate.LINK_TYPE_LT, up2, up2other);

		// Virtual links
		Link linkInter = getLink(null, VCPETemplate.INTER_LINK, VCPETemplate.LINK_TYPE_VIRTUAL, inter1, inter2);
		List<Link> subLinks = new ArrayList<Link>();
		subLinks.add(linkInter1local);
		subLinks.add(linkInter1other);
		subLinks.add(linkInter2local);
		linkInter.setImplementedBy(subLinks);
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
		elements.add(vcpe2);
		elements.addAll(vcpe2.getInterfaces());
		elements.addAll(bodInterfaces);
		elements.add(up1other);
		elements.add(up2other);
		elements.add(linkInter);
		elements.addAll(linkInter.getImplementedBy());
		elements.add(linkdown1);
		elements.addAll(linkdown1.getImplementedBy());
		elements.add(linkdown2);
		elements.addAll(linkdown2.getImplementedBy());
		elements.add(linkUp1);
		elements.add(linkUp2);

		// configure VRRP
		VRRP vrrp = new VRRP();
		vrrp.setMasterRouter(vcpe1);
		vrrp.setMasterInterface(down1);
		vrrp.setBackupRouter(vcpe2);
		vrrp.setBackupInterface(down2);

		VCPENetworkModel model = new VCPENetworkModel();
		model.setElements(elements);
		model.setTemplateType(getTemplateType());
		model.setCreated(false);
		model.setVrrp(vrrp);
		model.setBgp(new BGP());

		return model;
	}

	private VCPENetworkModel mapPhysicalElementsFromProperties(VCPENetworkModel model, Properties props) {

		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CORE_PHY_ROUTER);
		core.setName(props.getProperty("vcpenetwork.routercore.name"));

		Interface coremaster = (Interface) VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(),
				VCPETemplate.CORE_PHY_INTERFACE_MASTER);
		coremaster.setName(props.getProperty("vcpenetwork.routercore.interface.master.name"));
		coremaster.setPhysicalInterfaceName(props.getProperty("vcpenetwork.routercore.interface.master.name"));

		Interface corebkp = (Interface) VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(), VCPETemplate.CORE_PHY_INTERFACE_BKP);
		corebkp.setName(props.getProperty("vcpenetwork.routercore.interface.bkp.name"));
		corebkp.setPhysicalInterfaceName(props.getProperty("vcpenetwork.routercore.interface.bkp.name"));

		Interface corelo = (Interface) VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(), VCPETemplate.CORE_PHY_LO_INTERFACE);
		corelo.setName(props.getProperty("vcpenetwork.routercore.interface.lo.name"));
		corelo.setPhysicalInterfaceName(props.getProperty("vcpenetwork.routercore.interface.lo.name"));

		// Router1
		Router r1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
		r1.setName(props.getProperty("vcpenetwork.router1.name"));

		Interface inter1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(r1.getInterfaces(), VCPETemplate.INTER1_PHY_INTERFACE_LOCAL);
		inter1.setName(props.getProperty("vcpenetwork.router1.interface.inter.name"));
		inter1.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router1.interface.inter.name"));

		Interface down1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(r1.getInterfaces(), VCPETemplate.DOWN1_PHY_INTERFACE_LOCAL);
		down1.setName(props.getProperty("vcpenetwork.router1.interface.down.name"));
		down1.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router1.interface.down.name"));

		Interface up1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(r1.getInterfaces(), VCPETemplate.UP1_PHY_INTERFACE_LOCAL);
		up1.setName(props.getProperty("vcpenetwork.router1.interface.up.name"));
		up1.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router1.interface.up.name"));

		Interface lo1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(r1.getInterfaces(), VCPETemplate.LO1_PHY_INTERFACE);
		lo1.setName(props.getProperty("vcpenetwork.router1.interface.lo.name"));
		lo1.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router1.interface.lo.name"));

		// Router2
		Router r2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);
		r2.setName(props.getProperty("vcpenetwork.router2.name"));

		Interface inter2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(r2.getInterfaces(), VCPETemplate.INTER2_PHY_INTERFACE_LOCAL);
		inter2.setName(props.getProperty("vcpenetwork.router2.interface.inter.name"));
		inter2.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router2.interface.inter.name"));

		Interface down2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(r2.getInterfaces(), VCPETemplate.DOWN2_PHY_INTERFACE_LOCAL);
		down2.setName(props.getProperty("vcpenetwork.router2.interface.down.name"));
		down2.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router2.interface.down.name"));

		Interface up2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(r2.getInterfaces(), VCPETemplate.UP2_PHY_INTERFACE_LOCAL);
		up2.setName(props.getProperty("vcpenetwork.router2.interface.up.name"));
		up2.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router2.interface.up.name"));

		Interface lo2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(r2.getInterfaces(), VCPETemplate.LO2_PHY_INTERFACE);
		lo2.setName(props.getProperty("vcpenetwork.router2.interface.lo.name"));
		lo2.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router2.interface.lo.name"));

		// BoD
		Domain autobahn = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.AUTOBAHN);
		autobahn.setName(props.getProperty("vcpenetwork.bod.name"));

		Interface inter1other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(autobahn.getInterfaces(),
				VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);
		inter1other.setName(props.getProperty("vcpenetwork.router1.interface.inter.other.name"));
		inter1other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router1.interface.inter.other.name"));

		Interface down1other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(autobahn.getInterfaces(),
				VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);
		down1other.setName(props.getProperty("vcpenetwork.router1.interface.down.other.name"));
		down1other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router1.interface.down.other.name"));

		Interface inter2other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(autobahn.getInterfaces(),
				VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);
		inter2other.setName(props.getProperty("vcpenetwork.router2.interface.inter.other.name"));
		inter2other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router2.interface.inter.other.name"));

		Interface down2other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(autobahn.getInterfaces(),
				VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);
		down2other.setName(props.getProperty("vcpenetwork.router2.interface.down.other.name"));
		down2other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.router2.interface.down.other.name"));

		// TODO BoD client interfaces
		// NOT SUGGESTED (they change in every client)

		return model;
	}

	private VCPENetworkModel mapLogicalElementsFromProperties(VCPENetworkModel model, Properties props) {

		// Logical Router 1
		Router vcpe1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName(props.getProperty("vcpenetwork.logicalrouter1.name"));

		Interface ifaceInter = (Interface) VCPENetworkModelHelper
				.getElementByTemplateName(vcpe1.getInterfaces(), VCPETemplate.INTER1_INTERFACE_LOCAL);
		ifaceInter.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter1.interface.inter.name"));
		ifaceInter.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.inter.port").trim()));
		ifaceInter.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.inter.vlan").trim()));
		ifaceInter.setIpAddress(props.getProperty("vcpenetwork.logicalrouter1.interface.inter.ipaddress"));
		ifaceInter.setName(ifaceInter.getPhysicalInterfaceName() + "." + ifaceInter.getPort());

		Interface ifaceDown = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1.getInterfaces(), VCPETemplate.DOWN1_INTERFACE_LOCAL);
		ifaceDown.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter1.interface.down.name"));
		ifaceDown.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.down.port").trim()));
		ifaceDown.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.down.vlan").trim()));
		ifaceDown.setIpAddress(props.getProperty("vcpenetwork.logicalrouter1.interface.down.ipaddress"));
		ifaceDown.setName(ifaceDown.getPhysicalInterfaceName() + "." + ifaceDown.getPort());

		Interface ifaceUp = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1.getInterfaces(), VCPETemplate.UP1_INTERFACE_LOCAL);
		ifaceUp.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter1.interface.up.name"));
		ifaceUp.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.up.port").trim()));
		ifaceUp.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.up.vlan").trim()));
		ifaceUp.setIpAddress(props.getProperty("vcpenetwork.logicalrouter1.interface.up.ipaddress"));
		ifaceUp.setName(ifaceUp.getPhysicalInterfaceName() + "." + ifaceUp.getPort());

		Interface ifaceLo = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1.getInterfaces(), VCPETemplate.LO1_INTERFACE);
		ifaceLo.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter1.interface.lo.name"));
		ifaceLo.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.lo.port").trim()));
		ifaceLo.setIpAddress(props.getProperty("vcpenetwork.logicalrouter1.interface.lo.ipaddress"));
		ifaceLo.setName(ifaceLo.getPhysicalInterfaceName() + "." + ifaceLo.getPort());

		// Logical Router 2
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName(props.getProperty("vcpenetwork.logicalrouter2.name"));

		ifaceInter = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2.getInterfaces(), VCPETemplate.INTER2_INTERFACE_LOCAL);
		ifaceInter.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter2.interface.inter.name"));
		ifaceInter.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.inter.port").trim()));
		ifaceInter.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.inter.vlan").trim()));
		ifaceInter.setIpAddress(props.getProperty("vcpenetwork.logicalrouter2.interface.inter.ipaddress"));
		ifaceInter.setName(ifaceInter.getPhysicalInterfaceName() + "." + ifaceInter.getPort());

		ifaceDown = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2.getInterfaces(), VCPETemplate.DOWN2_INTERFACE_LOCAL);
		ifaceDown.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter2.interface.down.name"));
		ifaceDown.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.down.port").trim()));
		ifaceDown.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.down.vlan").trim()));
		ifaceDown.setIpAddress(props.getProperty("vcpenetwork.logicalrouter2.interface.down.ipaddress"));
		ifaceDown.setName(ifaceDown.getPhysicalInterfaceName() + "." + ifaceDown.getPort());

		ifaceUp = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2.getInterfaces(), VCPETemplate.UP2_INTERFACE_LOCAL);
		ifaceUp.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter2.interface.up.name"));
		ifaceUp.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.up.port").trim()));
		ifaceUp.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.up.vlan").trim()));
		ifaceUp.setIpAddress(props.getProperty("vcpenetwork.logicalrouter2.interface.up.ipaddress"));
		ifaceUp.setName(ifaceUp.getPhysicalInterfaceName() + "." + ifaceUp.getPort());

		ifaceLo = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2.getInterfaces(), VCPETemplate.LO2_INTERFACE);
		ifaceLo.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter2.interface.lo.name"));
		ifaceLo.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.lo.port").trim()));
		ifaceLo.setIpAddress(props.getProperty("vcpenetwork.logicalrouter2.interface.lo.ipaddress"));
		ifaceLo.setName(ifaceLo.getPhysicalInterfaceName() + "." + ifaceLo.getPort());

		// BoD
		Interface ifaceClient1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		ifaceClient1.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter1.interface.client.name"));
		ifaceClient1.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.client.port").trim()));
		ifaceClient1.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.client.vlan").trim()));
		ifaceClient1.setName(ifaceClient1.getPhysicalInterfaceName() + "." + ifaceClient1.getPort());

		Interface ifaceClient2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);
		ifaceClient2.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter2.interface.client.name"));
		ifaceClient2.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.client.port").trim()));
		ifaceClient2.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.client.vlan").trim()));
		ifaceClient2.setName(ifaceClient2.getPhysicalInterfaceName() + "." + ifaceClient2.getPort());

		Interface inter1other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_INTERFACE_AUTOBAHN);
		inter1other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter1.interface.inter.other.name"));
		inter1other.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.inter.other.port").trim()));
		inter1other.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.inter.other.vlan").trim()));
		inter1other.setName(inter1other.getPhysicalInterfaceName() + "." + inter1other.getPort());

		Interface down1other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN1_INTERFACE_AUTOBAHN);
		down1other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter1.interface.down.other.name"));
		down1other.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.down.other.port").trim()));
		down1other.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.down.other.vlan").trim()));
		down1other.setName(down1other.getPhysicalInterfaceName() + "." + down1other.getPort());

		Interface inter2other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER2_INTERFACE_AUTOBAHN);
		inter2other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter2.interface.inter.other.name"));
		inter2other.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.inter.other.port").trim()));
		inter2other.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.inter.other.vlan").trim()));
		inter2other.setName(inter2other.getPhysicalInterfaceName() + "." + inter2other.getPort());

		Interface down2other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN2_INTERFACE_AUTOBAHN);
		down2other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter2.interface.down.other.name"));
		down2other.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.down.other.port").trim()));
		down2other.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.down.other.vlan").trim()));
		down2other.setName(down2other.getPhysicalInterfaceName() + "." + down2other.getPort());

		// Noc network
		Interface up1other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP1_INTERFACE_PEER);
		up1other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter1.interface.up.other.name"));
		up1other.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.up.other.port").trim()));
		up1other.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter1.interface.up.other.vlan").trim()));
		up1other.setIpAddress(props.getProperty("vcpenetwork.logicalrouter1.interface.up.other.ipaddress"));
		up1other.setName(up1other.getPhysicalInterfaceName() + "." + up1other.getPort());

		Interface up2other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP2_INTERFACE_PEER);
		up2other.setPhysicalInterfaceName(props.getProperty("vcpenetwork.logicalrouter2.interface.up.other.name"));
		up2other.setPort(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.up.other.port").trim()));
		up2other.setVlan(Integer.parseInt(props.getProperty("vcpenetwork.logicalrouter2.interface.up.other.vlan").trim()));
		up2other.setIpAddress(props.getProperty("vcpenetwork.logicalrouter2.interface.up.other.ipaddress"));
		up2other.setName(up2other.getPhysicalInterfaceName() + "." + up2other.getPort());

		// VRRP
		int vrrpGoup = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.group").trim());
		int masterVRRPPriority = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.master.priority").trim());
		int backupVRRPPriority = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.backup.priority").trim());
		model.getVrrp().setGroup(vrrpGoup);
		model.getVrrp().setPriorityMaster(masterVRRPPriority);
		model.getVrrp().setPriorityBackup(backupVRRPPriority);
		model.getVrrp().setVirtualIPAddress(props.getProperty("vcpenetwork.vrrp.virtualIPAddress"));

		// BGP
		model.getBgp().setClientASNumber(props.getProperty("vcpenetwork.bgp.clientASNumber"));
		model.getBgp().setNocASNumber(props.getProperty("vcpenetwork.bgp.nocASNumber"));
		List<String> clientPrefixes = new ArrayList<String>();
		clientPrefixes.add(props.getProperty("vcpenetwork.bgp.clientPrefixes"));
		model.getBgp().setCustomerPrefixes(clientPrefixes);

		// VCPE
		model.setClientIpRange(props.getProperty("vcpenetwork.client.iprange"));

		return model;
	}

	private VCPENetworkModel mapPhysicalElementsFromInputModel(VCPENetworkModel model, VCPENetworkModel inputModel) {

		// TODO update ALL elements with data in inputModel (everything may have changed)

		// // select client1 interface using inputModel
		// Interface inputClient1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel,
		// VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		//
		// Interface client1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		// client1.setName(inputClient1.getName());
		// client1.setPhysicalInterfaceName(inputClient1.getPhysicalInterfaceName());
		//
		// // select client2 interface using inputModel
		// Interface inputClient2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel,
		// VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
		//
		// Interface client2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
		// client2.setName(inputClient2.getName());
		// client2.setPhysicalInterfaceName(inputClient2.getPhysicalInterfaceName());

		return model;
	}

	private VCPENetworkModel mapLogicalElementsFromInputModel(VCPENetworkModel model, VCPENetworkModel inputModel) {

		// TODO update ALL elements with data in inputModel (everything may have changed)

		// LR1
		Router vcpe1input = (Router) VCPENetworkModelHelper.getElementByTemplateName(inputModel, VCPETemplate.VCPE1_ROUTER);
		Router vcpe1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName(vcpe1input.getName() + "-" + inputModel.getName());

		Interface inter1input = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1input.getInterfaces(),
				VCPETemplate.INTER1_INTERFACE_LOCAL);
		Interface down1input = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1input.getInterfaces(),
				VCPETemplate.DOWN1_INTERFACE_LOCAL);
		Interface up1input = (Interface) VCPENetworkModelHelper
				.getElementByTemplateName(vcpe1input.getInterfaces(), VCPETemplate.UP1_INTERFACE_LOCAL);
		Interface lo1input = (Interface) VCPENetworkModelHelper
				.getElementByTemplateName(vcpe1input.getInterfaces(), VCPETemplate.LO1_INTERFACE);

		Interface inter1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1.getInterfaces(), VCPETemplate.INTER1_INTERFACE_LOCAL);
		Interface down1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1.getInterfaces(), VCPETemplate.DOWN1_INTERFACE_LOCAL);
		Interface up1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1.getInterfaces(), VCPETemplate.UP1_INTERFACE_LOCAL);
		Interface lo1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe1.getInterfaces(), VCPETemplate.LO1_INTERFACE);

		if (inter1input != null)
			updateInterface(inter1, inter1input.getName(), inter1input.getVlan(), inter1input.getIpAddress(), inter1input.getPhysicalInterfaceName(),
					inter1input.getPort());
		if (down1input != null)
			updateInterface(down1, down1input.getName(), down1input.getVlan(), down1input.getIpAddress(), down1input.getPhysicalInterfaceName(),
					down1input.getPort());
		if (up1input != null)
			updateInterface(up1, up1input.getName(), up1input.getVlan(), up1input.getIpAddress(), up1input.getPhysicalInterfaceName(),
					up1input.getPort());
		if (lo1input != null)
			updateInterface(lo1, lo1input.getName(), lo1input.getVlan(), lo1input.getIpAddress(), lo1input.getPhysicalInterfaceName(),
					lo1input.getPort());

		// LR2
		Router vcpe2input = (Router) VCPENetworkModelHelper.getElementByTemplateName(inputModel, VCPETemplate.VCPE2_ROUTER);
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName(vcpe2input.getName() + "-" + inputModel.getName());

		Interface inter2input = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2input.getInterfaces(),
				VCPETemplate.INTER2_INTERFACE_LOCAL);
		Interface down2input = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2input.getInterfaces(),
				VCPETemplate.DOWN2_INTERFACE_LOCAL);
		Interface up2input = (Interface) VCPENetworkModelHelper
				.getElementByTemplateName(vcpe2input.getInterfaces(), VCPETemplate.UP2_INTERFACE_LOCAL);
		Interface lo2input = (Interface) VCPENetworkModelHelper
				.getElementByTemplateName(vcpe2input.getInterfaces(), VCPETemplate.LO2_INTERFACE);

		Interface inter2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2.getInterfaces(), VCPETemplate.INTER2_INTERFACE_LOCAL);
		Interface down2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2.getInterfaces(), VCPETemplate.DOWN2_INTERFACE_LOCAL);
		Interface up2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2.getInterfaces(), VCPETemplate.UP2_INTERFACE_LOCAL);
		Interface lo2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(vcpe2.getInterfaces(), VCPETemplate.LO2_INTERFACE);

		if (inter2input != null)
			updateInterface(inter2, inter2input.getName(), inter2input.getVlan(), inter2input.getIpAddress(), inter2input.getPhysicalInterfaceName(),
					inter2input.getPort());
		if (down2input != null)
			updateInterface(down2, down2input.getName(), down2input.getVlan(), down2input.getIpAddress(), down2input.getPhysicalInterfaceName(),
					down2input.getPort());
		if (up2input != null)
			updateInterface(up2, up2input.getName(), up2input.getVlan(), up2input.getIpAddress(), up2input.getPhysicalInterfaceName(),
					up2input.getPort());
		if (lo2input != null)
			updateInterface(lo2, lo2input.getName(), lo2input.getVlan(), lo2input.getIpAddress(), lo2input.getPhysicalInterfaceName(),
					lo2input.getPort());

		// BOD
		Interface client1input = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		Interface client2input = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		Interface client1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		Interface client2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		updateInterface(client1, client1input.getName(), client1input.getVlan(), client1input.getIpAddress(),
				client1input.getPhysicalInterfaceName(), client1input.getPort());
		updateInterface(client2, client2input.getName(), client2input.getVlan(), client2input.getIpAddress(),
				client2input.getPhysicalInterfaceName(), client2input.getPort());

		// set client physical interfaces
		// select client1 interface using inputModel
		Interface inputClient1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel,
				VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);

		Interface client1phy = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		client1phy.setName(inputClient1.getName());
		client1phy.setPhysicalInterfaceName(inputClient1.getPhysicalInterfaceName());

		// select client2 interface using inputModel
		Interface inputClient2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel,
				VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		Interface client2phy = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
		client2phy.setName(inputClient2.getName());
		client2phy.setPhysicalInterfaceName(inputClient2.getPhysicalInterfaceName());

		// VRRP
		if (inputModel.getVrrp().getGroup() != null)
			model.getVrrp().setGroup(inputModel.getVrrp().getGroup()); // TODO MUST CHANGE BETWEEN VCPEs
		if (inputModel.getVrrp().getPriorityMaster() != null)
			model.getVrrp().setPriorityMaster(inputModel.getVrrp().getPriorityMaster());
		if (inputModel.getVrrp().getPriorityBackup() != null)
			model.getVrrp().setPriorityBackup(inputModel.getVrrp().getPriorityBackup());
		if (inputModel.getVrrp().getVirtualIPAddress() != null)
			model.getVrrp().setVirtualIPAddress(inputModel.getVrrp().getVirtualIPAddress());

		// BGP
		if (inputModel.getBgp().getClientASNumber() != null)
			model.getBgp().setClientASNumber(inputModel.getBgp().getClientASNumber());
		if (inputModel.getBgp().getNocASNumber() != null)
			model.getBgp().setNocASNumber(inputModel.getBgp().getNocASNumber());
		if (inputModel.getBgp().getCustomerPrefixes() != null && !inputModel.getBgp().getCustomerPrefixes().isEmpty())
			model.getBgp().setCustomerPrefixes(inputModel.getBgp().getCustomerPrefixes());

		// TODO mapDependantLogicalElements(model);

		return model;
	}

	private VCPENetworkModel mapLogicalAndPhysical(VCPENetworkModel physicalInfrastructure, VCPENetworkModel logicalInfrastructure) {

		// put all physical elements in logicalInfrastructure
		logicalInfrastructure.getElements().addAll(physicalInfrastructure.getElements());

		// put up logical interfaces into core router
		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.CORE_PHY_ROUTER);
		core.getInterfaces().add((Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.UP1_INTERFACE_PEER));
		core.getInterfaces().add((Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.UP2_INTERFACE_PEER));

		// put bod logical interfaces into bod
		Domain bod = (Domain) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.AUTOBAHN);
		bod.getInterfaces().add(
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN));
		bod.getInterfaces().add(
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN));
		bod.getInterfaces().add(
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.INTER1_INTERFACE_AUTOBAHN));
		bod.getInterfaces().add(
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.DOWN1_INTERFACE_AUTOBAHN));
		bod.getInterfaces().add(
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.INTER2_INTERFACE_AUTOBAHN));
		bod.getInterfaces().add(
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.DOWN2_INTERFACE_AUTOBAHN));

		return logicalInfrastructure;
	}

	/**
	 * @param initialModel
	 * @return
	 */
	private List<VCPENetworkElement> generateLogicalElements(VCPENetworkModel initialModel) {
		// ----------------------------- VCPE-router1 -----------------------------
		Router vcpe1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName(props.getProperty("vcpenetwork.logicalrouter1.name") + "-" + initialModel.getName());

		// Interfaces VCPE-router1
		Interface inter1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.INTER1_INTERFACE_LOCAL);
		Interface down1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.DOWN1_INTERFACE_LOCAL);
		Interface up1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.UP1_INTERFACE_LOCAL);

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

		// Loopback interface VCPE-router1
		String loopback1Name = props.getProperty("vcpenetwork.logicalrouter1.interface.lo.name");
		String loopback1Port = props.getProperty("vcpenetwork.logicalrouter1.interface.lo.port");
		Long loopback1Vlan = 0L;
		String loopback1Ip = props.getProperty("vcpenetwork.logicalrouter1.interface.lo.ipaddress");
		Interface loopback1 = getInterface(loopback1Name + "." + loopback1Port, VCPETemplate.LO1_INTERFACE, loopback1Vlan, loopback1Ip,
				loopback1Name, Integer.parseInt(loopback1Port));
		vcpe1.getInterfaces().add(loopback1);

		// ----------------------------- VCPE-router2 -----------------------------
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName(props.getProperty("vcpenetwork.logicalrouter2.name") + "-" + initialModel.getName());

		// Interfaces VCPE-router2
		Interface inter2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.INTER2_INTERFACE_LOCAL);
		Interface down2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.DOWN2_INTERFACE_LOCAL);
		Interface up2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.UP2_INTERFACE_LOCAL);

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

		// Loopback interface VCPE-router2
		String loopback2Name = props.getProperty("vcpenetwork.logicalrouter2.interface.lo.name");
		String loopback2Port = props.getProperty("vcpenetwork.logicalrouter2.interface.lo.port");
		Long loopback2Vlan = 0L;
		String loopback2Ip = props.getProperty("vcpenetwork.logicalrouter2.interface.lo.ipaddress");
		Interface loopback2 = getInterface(loopback2Name + "." + loopback2Port, VCPETemplate.LO2_INTERFACE, loopback2Vlan, loopback2Ip,
				loopback2Name, Integer.parseInt(loopback2Port));
		vcpe2.getInterfaces().add(loopback2);

		// Client interfaces
		Interface client1other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		Interface client2other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(initialModel, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

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

	private VRRP configureVRRP(VCPENetworkModel model) {
		// VRRP group
		int vrrpGoup = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.group"));
		// configuration VCPE-router1
		int masterVRRPPriority = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.master.priority"));
		// configuration VCPE-router2
		int backupVRRPPriority = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.backup.priority"));

		// get master router and interface
		Router masterRouter = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Interface masterInterface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN1_INTERFACE_LOCAL);

		// get backup router and interface
		Router backupRouter = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		Interface backupInterface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN2_INTERFACE_LOCAL);

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
		params.interAddr1 = ((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_INTERFACE_LOCAL))
				.getIpAddress();

		params.loAddr2 = "193.1.190.145/30"; // TODO get this from GUI
		params.upRemoteAddr2 = "193.1.190.130/30"; // TODO get this from GUI
		params.interAddr2 = ((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER2_INTERFACE_LOCAL))
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
		props1.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + 0 + ".address",
				IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.loAddr1)[0] + "/32");
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
		props2.setProperty("policy.0.rule.0.condition.0.filterlist.0.entry." + 0 + ".address",
				IPUtilsHelper.composedIPAddressToIPAddressAndMask(bgpParams.loAddr2)[0] + "/32");
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
	 * @param templateName
	 * @param vlan
	 * @param ipAddress
	 * @return the interface
	 */
	private Interface getInterface(String name, String templateName, long vlan, String ipAddress, String physicalInterfaceName, int port) {
		Interface iface = new Interface();
		iface.setName(name);
		iface.setTemplateName(templateName);
		iface.setIpAddress(ipAddress);
		iface.setVlan(vlan);
		iface.setPhysicalInterfaceName(physicalInterfaceName);
		iface.setPort(port);
		return iface;
	}

	/**
	 * @param name
	 * @param templateName
	 * @param vlan
	 * @param ipAddress
	 * @return the interface
	 */
	private Interface updateInterface(Interface iface, String name, long vlan, String ipAddress, String physicalInterfaceName, int port) {
		iface.setName(name);
		iface.setIpAddress(ipAddress);
		iface.setVlan(vlan);
		iface.setPhysicalInterfaceName(physicalInterfaceName);
		iface.setPort(port);
		return iface;
	}

	/**
	 * @param id
	 * @param templateName
	 * @param type
	 * @param source
	 * @param sink
	 * @return
	 */
	private Link getLink(String id, String templateName, String type, Interface source, Interface sink) {
		Link link = new Link();
		link.setId(id);
		link.setTemplateName(templateName);
		link.setType(type);
		link.setSource(source);
		link.setSink(sink);
		return link;
	}

	private void checkPhysicalAvailability(List<VCPENetworkElement> toBeChecked, VCPEManagerModel managerModel) throws VCPENetworkManagerException {
		checkExistenceInPhysicalInsfrastructure(toBeChecked, managerModel.getPhysicalInfrastructure());
	}

	private void checkExistenceInPhysicalInsfrastructure(List<VCPENetworkElement> toBeChecked, VCPEPhysicalInfrastructure phyInfrastructure) {

		List<VCPENetworkElement> availablePhysicalElements = phyInfrastructure.getAllElements();

		for (Domain domain : filter(toBeChecked, Domain.class)) {
			if (!availablePhysicalElements.contains(domain))
				throw new VCPENetworkManagerException("Domain " + domain.getName() + " is not available in physical insfrastructure");

			Domain phyInfrDomain = (Domain) availablePhysicalElements.get(availablePhysicalElements.indexOf(domain));

			for (Interface iface : domain.getInterfaces()) {
				if (!phyInfrDomain.getInterfaces().contains(iface))
					throw new VCPENetworkManagerException(
							"Interface " + iface.getName() + " for domain " + domain.getName() + " is not available in physical insfrastructure");
			}
		}

		for (Router router : filter(toBeChecked, Router.class)) {
			if (!availablePhysicalElements.contains(router))
				throw new VCPENetworkManagerException("Router " + router.getName() + " is not available in physical insfrastructure");

			Router phyInfrRouter = (Router) availablePhysicalElements.get(availablePhysicalElements.indexOf(router));

			for (Interface iface : router.getInterfaces()) {
				if (!phyInfrRouter.getInterfaces().contains(iface))
					throw new VCPENetworkManagerException(
							"Interface " + iface.getName() + " for router " + router.getName() + " is not available in physical insfrastructure");
			}
		}

		for (Link link : filter(toBeChecked, Link.class)) {
			if (!availablePhysicalElements.contains(link))
				throw new VCPENetworkManagerException("Link " + link.getName() + " is not available in physical insfrastructure");
		}
	}

}
