/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates.sp;

import static com.google.common.collect.Iterables.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.model.VCPEManagerModel;
import org.opennaas.extensions.vcpe.manager.model.VCPEPhysicalInfrastructure;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
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
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * @author Jordi
 */
public class SingleProviderTemplate implements ITemplate {

	private static final String		BGP_TEMPLATE	= "/templates/bgpModel1.properties";

	private String					templateType	= ITemplate.SP_VCPE_TEMPLATE;

	private Properties				bgpProps;
	private VCPETemplateSuggestor	suggestor;

	/**
	 * @throws VCPENetworkManagerException
	 * 
	 */
	public SingleProviderTemplate() throws VCPENetworkManagerException {
		try {
			suggestor = new VCPETemplateSuggestor();

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
		model.setNocIpRange(initialModel.getNocIpRange());
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
		VCPENetworkModel mappedFromProperties = suggestor.getSuggestionForPhysicalModel(generated);
		// TODO MUST CHECK MAPPED ELEMENTS EXIST IN PHYSICAL TOPOLOGY
		return mappedFromProperties;
	}

	@Override
	public VCPENetworkModel getLogicalInfrastructureSuggestion(VCPENetworkModel physicalInfrastructure) throws VCPENetworkManagerException {

		// complete physicalInfrastructure
		VCPENetworkModel phy = generateAndMapPhysicalElements(physicalInfrastructure);

		VCPENetworkModel generated = generateLogicalElements();
		VCPENetworkModel mappedWithPhy = mapLogicalAndPhysical(phy, generated);
		VCPENetworkModel suggested = suggestor.getSuggestionForLogicalModel(mappedWithPhy);

		// TODO MUST CHECK MAPPED ELEMENTS EXIST IN PHYSICAL INFRASTRUCTURE
		return suggested;
	}

	// FIXME TEMPORAL METHOD. REMOVE WHEN REFACTOR IS FINISHED
	private VCPENetworkModel generateAndMapPhysicalElements(VCPENetworkModel initialModel) {

		VCPENetworkModel generated = generatePhysicalElements();
		VCPENetworkModel mappedFromProperties = suggestor.getSuggestionForPhysicalModel(generated);
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

		Interface client1 = new Interface();
		client1.setTemplateName(VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);

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

		Interface client2 = new Interface();
		client2.setTemplateName(VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);

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
		autobahnInterfaces.add(client1);
		autobahnInterfaces.add(client2);
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
		bodInterfaces.add(inter1other);
		bodInterfaces.add(down1other);
		bodInterfaces.add(inter2other);
		bodInterfaces.add(down2other);
		bodInterfaces.add(client1other);
		bodInterfaces.add(client2other);

		inter1other.setTemplateName(VCPETemplate.INTER1_INTERFACE_AUTOBAHN);
		down1other.setTemplateName(VCPETemplate.DOWN1_INTERFACE_AUTOBAHN);
		inter2other.setTemplateName(VCPETemplate.INTER2_INTERFACE_AUTOBAHN);
		down2other.setTemplateName(VCPETemplate.DOWN2_INTERFACE_AUTOBAHN);
		client1other.setTemplateName(VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		client2other.setTemplateName(VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		// noc network interface
		// Notice these logical interfaces are not inside a router object (by now)
		Interface up1other = new Interface();
		Interface up2other = new Interface();
		Interface corelo = new Interface();

		up1other.setTemplateName(VCPETemplate.UP1_INTERFACE_PEER);
		up2other.setTemplateName(VCPETemplate.UP2_INTERFACE_PEER);
		corelo.setTemplateName(VCPETemplate.CORE_LO_INTERFACE);

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
		elements.add(corelo);
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

	private VCPENetworkModel mapPhysicalElementsFromInputModel(VCPENetworkModel model, VCPENetworkModel inputModel) {

		// TODO update ALL elements with data in inputModel (everything may have changed)

		// Client phy interfaces may not be present in inputModel
		// select client1 interface using inputModel
		Interface inputClient1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel,
				VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		if (inputClient1 != null) {
			Interface client1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
			client1.setName(inputClient1.getName());
			client1.setPhysicalInterfaceName(inputClient1.getPhysicalInterfaceName());
		}

		// select client2 interface using inputModel
		Interface inputClient2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel,
				VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
		if (inputClient2 != null) {
			Interface client2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
			client2.setName(inputClient2.getName());
			client2.setPhysicalInterfaceName(inputClient2.getPhysicalInterfaceName());
		}

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
			VCPENetworkModelHelper.updateInterface(inter1, inter1input.getName(), inter1input.getVlan(), inter1input.getIpAddress(),
					inter1input.getPhysicalInterfaceName(),
					inter1input.getPort());
		if (down1input != null)
			VCPENetworkModelHelper.updateInterface(down1, down1input.getName(), down1input.getVlan(), down1input.getIpAddress(),
					down1input.getPhysicalInterfaceName(),
					down1input.getPort());
		if (up1input != null)
			VCPENetworkModelHelper.updateInterface(up1, up1input.getName(), up1input.getVlan(), up1input.getIpAddress(),
					up1input.getPhysicalInterfaceName(),
					up1input.getPort());
		if (lo1input != null)
			VCPENetworkModelHelper.updateInterface(lo1, lo1input.getName(), lo1input.getVlan(), lo1input.getIpAddress(),
					lo1input.getPhysicalInterfaceName(),
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
			VCPENetworkModelHelper.updateInterface(inter2, inter2input.getName(), inter2input.getVlan(), inter2input.getIpAddress(),
					inter2input.getPhysicalInterfaceName(),
					inter2input.getPort());
		if (down2input != null)
			VCPENetworkModelHelper.updateInterface(down2, down2input.getName(), down2input.getVlan(), down2input.getIpAddress(),
					down2input.getPhysicalInterfaceName(),
					down2input.getPort());
		if (up2input != null)
			VCPENetworkModelHelper.updateInterface(up2, up2input.getName(), up2input.getVlan(), up2input.getIpAddress(),
					up2input.getPhysicalInterfaceName(),
					up2input.getPort());
		if (lo2input != null)
			VCPENetworkModelHelper.updateInterface(lo2, lo2input.getName(), lo2input.getVlan(), lo2input.getIpAddress(),
					lo2input.getPhysicalInterfaceName(),
					lo2input.getPort());

		// BOD
		Interface client1input = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		Interface client2input = (Interface) VCPENetworkModelHelper.getElementByTemplateName(inputModel, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		Interface client1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);
		Interface client2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		VCPENetworkModelHelper.updateInterface(client1, client1input.getName(), client1input.getVlan(), client1input.getIpAddress(),
				client1input.getPhysicalInterfaceName(), client1input.getPort());
		VCPENetworkModelHelper.updateInterface(client2, client2input.getName(), client2input.getVlan(), client2input.getIpAddress(),
				client2input.getPhysicalInterfaceName(), client2input.getPort());

		// Update BoD client phy interfaces from data in logical ones
		Interface client1phy = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		client1phy.setName(client1input.getPhysicalInterfaceName());
		client1phy.setPhysicalInterfaceName(client1input.getPhysicalInterfaceName());

		Interface client2phy = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);
		client2phy.setName(client2input.getPhysicalInterfaceName());
		client2phy.setPhysicalInterfaceName(client2input.getPhysicalInterfaceName());

		// VRRP
		if (inputModel.getVrrp().getGroup() != null)
			model.getVrrp().setGroup(inputModel.getVrrp().getGroup());
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

		mapDependantLogicalElements(model);

		return model;
	}

	private VCPENetworkModel mapDependantLogicalElements(VCPENetworkModel model) {
		// vlans of direct links should be equal
		model = updateInterfacesVlanFromLinks(model);
		return model;
	}

	private VCPENetworkModel updateInterfacesVlanFromLinks(VCPENetworkModel model) {
		List<Interface> allIfaces = VCPENetworkModelHelper.getInterfaces(model.getElements());
		List<Interface> toUpdate = new ArrayList<Interface>(6);

		toUpdate.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(allIfaces, VCPETemplate.INTER1_INTERFACE_AUTOBAHN));
		toUpdate.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(allIfaces, VCPETemplate.INTER2_INTERFACE_AUTOBAHN));

		toUpdate.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(allIfaces, VCPETemplate.DOWN1_INTERFACE_AUTOBAHN));
		toUpdate.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(allIfaces, VCPETemplate.DOWN2_INTERFACE_AUTOBAHN));

		// TODO Question: should the appl be able to modify up peer vlans (they are in core router)???
		toUpdate.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(allIfaces, VCPETemplate.UP1_INTERFACE_PEER));
		toUpdate.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(allIfaces, VCPETemplate.UP2_INTERFACE_PEER));

		List<Link> allLinks = VCPENetworkModelHelper.getLinks(model.getElements());
		List<Link> toUpdateLinks = new ArrayList<Link>(6);
		toUpdateLinks.add((Link) VCPENetworkModelHelper.getElementByTemplateName(allLinks, VCPETemplate.INTER1_LINK_LOCAL));
		toUpdateLinks.add((Link) VCPENetworkModelHelper.getElementByTemplateName(allLinks, VCPETemplate.INTER2_LINK_LOCAL));
		toUpdateLinks.add((Link) VCPENetworkModelHelper.getElementByTemplateName(allLinks, VCPETemplate.DOWN1_LINK_LOCAL));
		toUpdateLinks.add((Link) VCPENetworkModelHelper.getElementByTemplateName(allLinks, VCPETemplate.DOWN2_LINK_LOCAL));
		// TODO Question: should the appl be able to modify up peer vlans???
		toUpdateLinks.add((Link) VCPENetworkModelHelper.getElementByTemplateName(allLinks, VCPETemplate.UP1_LINK));
		toUpdateLinks.add((Link) VCPENetworkModelHelper.getElementByTemplateName(allLinks, VCPETemplate.UP2_LINK));

		for (Link link : toUpdateLinks) {
			if (toUpdate.contains(link.getSource())) {
				VCPENetworkModelHelper.updateIfaceVLANFromLink(link.getSource(), link);
			} else {
				VCPENetworkModelHelper.updateIfaceVLANFromLink(link.getSink(), link);
			}
		}
		return model;
	}

	private VCPENetworkModel mapLogicalAndPhysical(VCPENetworkModel physicalInfrastructure, VCPENetworkModel logicalInfrastructure) {

		// put all physical elements in logicalInfrastructure
		logicalInfrastructure.getElements().addAll(physicalInfrastructure.getElements());

		// put up logical interfaces into core router
		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.CORE_PHY_ROUTER);
		core.getInterfaces().add((Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.UP1_INTERFACE_PEER));
		core.getInterfaces().add((Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.UP2_INTERFACE_PEER));
		core.getInterfaces().add((Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.CORE_LO_INTERFACE));

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

		// Assign logical routers to physical ones
		Router phyRouter = (Router) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.CPE1_PHY_ROUTER);
		LogicalRouter logRouter = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.VCPE1_ROUTER);
		logRouter.setPhysicalRouter(phyRouter);

		phyRouter = (Router) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.CPE2_PHY_ROUTER);
		logRouter = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, VCPETemplate.VCPE2_ROUTER);
		logRouter.setPhysicalRouter(phyRouter);

		return logicalInfrastructure;
	}

	private ConfigureBGPRequestParameters generateBGPParameters(VCPENetworkModel model) {
		BGP bgp = model.getBgp();
		ConfigureBGPRequestParameters params = new ConfigureBGPRequestParameters();
		params.clientIPRanges = bgp.getCustomerPrefixes();
		params.clientASNumber = bgp.getClientASNumber();
		params.remoteASNum = bgp.getNocASNumber();

		params.loAddr1 = ((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.LO1_INTERFACE))
				.getIpAddress();
		params.upRemoteAddr1 = ((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP1_INTERFACE_PEER))
				.getIpAddress();
		params.interAddr1 = ((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_INTERFACE_LOCAL))
				.getIpAddress();

		params.loAddr2 = ((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.LO2_INTERFACE))
				.getIpAddress();
		params.upRemoteAddr2 = ((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP2_INTERFACE_PEER))
				.getIpAddress();
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
		props1.setProperty("as.asnum", bgpParams.clientASNumber);
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
		props2.setProperty("as.asnum", bgpParams.clientASNumber);
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
