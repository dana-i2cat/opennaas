/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.capability.bgp.BGPCapability;
import org.opennaas.extensions.router.capability.bgp.IBGPCapability;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.AutobahnHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.GenericHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.IPHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.InterfaceHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.LogicalRouterHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.StaticRouteHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.VRRPHelper;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

/**
 * @author Jordi
 */
public class VCPESingleProvider implements IVCPENetworkBuilder {

	private Log	log	= LogFactory.getLog(VCPESingleProvider.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder#build(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public VCPENetworkModel build(IResource vcpe, VCPENetworkModel desiredScenario) throws ResourceException {
		log.info("Build a vCPE single provider network of the resource: " + ((VCPENetworkModel) vcpe.getModel()).getId());

		boolean linksCreated = false;
		boolean lrsCreated = false;

		try {
			// Create and execute autobahn links
			createExternalLinks(vcpe, desiredScenario);
			executeAutobahn(desiredScenario);
			linksCreated = true;

			// Create logical routers in physical and start
			createLogicalRouters(vcpe, desiredScenario);
			executePhysicalRouters(desiredScenario);
			lrsCreated = true;
			startLogicalRouters(vcpe, desiredScenario);

			// Configure subinterfaces
			createSubInterfaces(vcpe, desiredScenario);
			assignIPAddresses(vcpe, desiredScenario);
			executeLogicalRouters(desiredScenario);

			// Configure routing protocols
			configureEGP(vcpe, desiredScenario);
			configureVRRP(vcpe, desiredScenario);
			executeLogicalRouters(desiredScenario);

			// TODO return created model, not the desired one
			vcpe.setModel(desiredScenario);
			((VCPENetworkModel) vcpe.getModel()).setCreated(true);

			return (VCPENetworkModel) vcpe.getModel();

		} catch (ResourceException e) {
			destroy(vcpe, desiredScenario, lrsCreated, linksCreated);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder#destroy()
	 */
	@Override
	public VCPENetworkModel destroy(IResource vcpe, VCPENetworkModel currentScenario) throws ResourceException {
		return destroy(vcpe, currentScenario, true, true);
	}

	private VCPENetworkModel destroy(IResource vcpe, VCPENetworkModel currentScenario, boolean destroyLRs, boolean destroyLinks)
			throws ResourceException {
		log.info("Destroy a vCPE Network of the resource: " + ((VCPENetworkModel) vcpe.getModel()).getId());

		if (destroyLRs) {
			// Destroy logical routers and this will destroy
			// their subinterfaces and routing protocols
			stopLogicalRouters(vcpe, currentScenario);
			removeLogicalRouters(vcpe, currentScenario);
			executePhysicalRouters(currentScenario);
		}

		if (destroyLinks) {
			// Destroy autobahn links
			destroyExternalLinks(vcpe, currentScenario);
			executeAutobahn(currentScenario);
		}

		if (destroyLRs) {
			removeResources(currentScenario);
		}

		// return the model after deleting all LR and subInterfaces from it
		VCPENetworkModel model = new VCPENetworkModel();
		model.setId(currentScenario.getId());
		model.setName(currentScenario.getName());
		model.setCreated(false);
		vcpe.setModel(model);

		return (VCPENetworkModel) vcpe.getModel();
	}

	/**
	 * Create the external links
	 * 
	 * @param vcpe
	 * @param model
	 * @throws ResourceException
	 */
	private void createExternalLinks(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		log.info("Create the external links");
		List<Link> links = VCPENetworkModelHelper.getLinks(model.getElements());

		// inter link
		Link inter = (Link) VCPENetworkModelHelper.getElementByTemplateName(links, VCPETemplate.INTER_LINK);
		long interSrcVlan = inter.getSource().getVlan();
		long interDstVlan = inter.getSink().getVlan();

		Interface interSrc = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);
		Interface interDst = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);

		// // down1 link
		// Link down1 = (Link) VCPENetworkModelHelper.getElementByTemplateName(links, VCPETemplate.DOWN1_LINK);
		// long down1SrcVlan = down1.getSource().getVlanId();
		// long down1DstVlan = down1.getSink().getVlanId();
		//
		// Interface down1Src = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);
		// Interface down1Dst = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		//
		// createAutobahnLink(model, down1Src, down1Dst, down1SrcVlan, down1DstVlan);

		// down 2 link
		Link down2 = (Link) VCPENetworkModelHelper.getElementByTemplateName(links, VCPETemplate.DOWN2_LINK);
		long down2SrcVlan = down2.getSource().getVlan();
		long down2DstVlan = down2.getSink().getVlan();

		Interface down2Src = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);
		Interface down2Dst = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);

		Domain bod = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.AUTOBAHN);

		AutobahnHelper.createAutobahnLink(model, bod, interSrc, interDst, interSrcVlan, interDstVlan);
		AutobahnHelper.createAutobahnLink(model, bod, down2Src, down2Dst, down2SrcVlan, down2DstVlan);
	}

	/**
	 * Execute the autobahn resource
	 * 
	 * @param model
	 * @throws ResourceException
	 */
	private void executeAutobahn(VCPENetworkModel model) throws ResourceException {
		log.info("Execute autobahn");
		try {
			Domain bod = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.AUTOBAHN);
			IResource autobahn = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("bod", bod.getName()));
			GenericHelper.executeQueue(autobahn);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * Create the logicals routers, master and backup
	 * 
	 * @param resource
	 * @param desiredScenario
	 * @throws ResourceException
	 */
	private void createLogicalRouters(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {
		log.info("Create the logical routers");
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		LogicalRouterHelper.createLR(phy1, lr1, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		LogicalRouterHelper.createLR(phy2, lr2, desiredScenario);
	}

	/**
	 * Execute the physical routers
	 * 
	 * @param model
	 * @throws ResourceException
	 */
	private void executePhysicalRouters(VCPENetworkModel model) throws ResourceException {
		log.info("Create the physical routers");
		try {
			Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
			Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);

			IResource phyResource1 = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("router", phy1.getName()));
			IResource phyResource2 = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("router", phy2.getName()));

			GenericHelper.executeQueue(phyResource1);
			GenericHelper.executeQueue(phyResource2);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

	}

	/**
	 * Start the physical routers
	 * 
	 * @param vcpe
	 * @param model
	 * @throws ResourceException
	 */
	private void startLogicalRouters(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		log.info("Start logical routers");
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		try {
			LogicalRouterHelper.copyContextPhysicaltoLogical(phy1, lr1);
			LogicalRouterHelper.copyContextPhysicaltoLogical(phy2, lr2);
		} catch (ProtocolException e) {
			throw new ResourceException("Failed to start logical louters", e);
		}

		GenericHelper.getResourceManager().startResource(GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		GenericHelper.getResourceManager().startResource(GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));
	}

	/**
	 * Create the subinterfaces
	 * 
	 * @param resource
	 * @param desiredScenario
	 * @throws ResourceException
	 */
	private void createSubInterfaces(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {
		log.info("Create the subtinterfaces");
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		List<Interface> ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr1.getInterfaces());

		InterfaceHelper.createInterfaces(lr1, ifaces, desiredScenario);

		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr2.getInterfaces());

		InterfaceHelper.createInterfaces(lr2, ifaces, desiredScenario);
	}

	/**
	 * Assign the ip addresses
	 * 
	 * @param vcpe
	 * @param model
	 * @throws ResourceException
	 */
	private void assignIPAddresses(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		log.info("Assign the ip addresses");
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		for (Interface iface : lr1.getInterfaces()) {
			IPHelper.setIP(lr1, iface, model);
		}
		for (Interface iface : lr2.getInterfaces()) {
			IPHelper.setIP(lr2, iface, model);
		}
	}

	/**
	 * Execute the logical routers
	 * 
	 * @param model
	 * @throws ResourceException
	 */
	private void executeLogicalRouters(VCPENetworkModel model) throws ResourceException {
		log.info("Execute the logical routers");
		try {
			Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
			Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

			IResource lrResource1 = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
			IResource lrResource2 = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

			GenericHelper.executeQueue(lrResource1);
			GenericHelper.executeQueue(lrResource2);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * Configure BGP and static routes
	 * 
	 * @param vcpe
	 * @param model
	 * @throws ResourceException
	 */
	private void configureEGP(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		log.info("Configure EGP, static routes and BGP");
		configureBGP(model);
		// only static routes by now
		configureStaticRoutes(vcpe, model);
	}

	/**
	 * Configure BGP
	 * 
	 * @param model
	 * @throws ResourceException
	 */
	private void configureBGP(VCPENetworkModel model) throws ResourceException {
		log.info("Create BGP");
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		IResource router1Resource = GenericHelper.getResourceManager().getResource(
				GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		IResource router2Resource = GenericHelper.getResourceManager().getResource(
				GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

		IBGPCapability capability1 = (IBGPCapability) router1Resource.getCapabilityByInterface(BGPCapability.class);
		capability1.configureBGP(model.getBgp().getBgpConfigForMaster());

		IBGPCapability capability2 = (IBGPCapability) router2Resource.getCapabilityByInterface(BGPCapability.class);
		capability2.configureBGP(model.getBgp().getBgpConfigForBackup());

		model.getBgp().setBgpConfigForMaster(null);
		model.getBgp().setBgpConfigForBackup(null);
	}

	/**
	 * Configure the static routes in client
	 * 
	 * @param vcpe
	 * @param model
	 * @throws ResourceException
	 */
	private void configureStaticRoutes(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		log.info("Configure the logical routers");
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Interface iface1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP1_INTERFACE_PEER);

		String[] addressAndMask1 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface1.getIpAddress());

		String ipRange = model.getBgp().getCustomerPrefixes().get(0);

		String nextHopIpAddress = "";

		StaticRouteHelper.setStaticRoute(lr1, model, ipRange, nextHopIpAddress, true);

		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		Interface iface2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP2_INTERFACE_PEER);
		String[] addressAndMask2 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface2.getIpAddress());
		String nextHopIpAddress2 = addressAndMask2[0];

		StaticRouteHelper.setStaticRoute(lr2, model, ipRange, nextHopIpAddress2, true);
	}

	/**
	 * Configure the VRRP
	 * 
	 * @param vcpe
	 * @param desiredScenario
	 * @throws ResourceException
	 */
	private void configureVRRP(IResource vcpe, VCPENetworkModel desiredScenario) throws ResourceException {
		log.info("Configure VRRP");
		VRRPHelper.configureVRRP(vcpe, desiredScenario);
	}

	/**
	 * Stop the logical routers
	 * 
	 * @param vcpe
	 * @param model
	 * @throws ResourceException
	 */
	private void stopLogicalRouters(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		log.info("Stop the logical routers");
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		GenericHelper.getResourceManager().stopResource(GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		GenericHelper.getResourceManager().stopResource(GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));
	}

	/**
	 * Remove the logical routers from the physical router configuration
	 * 
	 * @param resource
	 * @param desiredScenario
	 * @throws ResourceException
	 */
	private void removeLogicalRouters(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		LogicalRouterHelper.removeLR(phy1, lr1, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		LogicalRouterHelper.removeLR(phy2, lr2, desiredScenario);
	}

	/**
	 * Destroy the external links
	 * 
	 * @param vcpe
	 * @param model
	 * @throws ResourceException
	 */
	private void destroyExternalLinks(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		log.info("Destroy the external links");
		List<Link> links = VCPENetworkModelHelper.getLinks(model.getElements());

		// inter link
		Link inter = (Link) VCPENetworkModelHelper.getElementByTemplateName(links, VCPETemplate.INTER_LINK);
		long interSrcVlan = inter.getSource().getVlan();
		long interDstVlan = inter.getSink().getVlan();

		Interface interSrc = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);
		Interface interDst = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);

		// down1 link
		// Link down1 = (Link) VCPENetworkModelHelper.getElementByTemplateName(links, VCPETemplate.DOWN1_LINK);
		// long down1SrcVlan = down1.getSource().getVlanId();
		// long down1DstVlan = down1.getSink().getVlanId();
		//
		// Interface down1Src = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);
		// Interface down1Dst = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		//
		// destroyAutobahnLink(model, down1Src, down1Dst, down1SrcVlan, down1DstVlan);

		// down 2 link
		Link down2 = (Link) VCPENetworkModelHelper.getElementByTemplateName(links, VCPETemplate.DOWN2_LINK);
		long down2SrcVlan = down2.getSource().getVlan();
		long down2DstVlan = down2.getSink().getVlan();

		Interface down2Src = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);
		Interface down2Dst = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);

		Domain bod = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.AUTOBAHN);

		AutobahnHelper.destroyAutobahnLink(model, bod, interSrc, interDst, interSrcVlan, interDstVlan);
		AutobahnHelper.destroyAutobahnLink(model, bod, down2Src, down2Dst, down2SrcVlan, down2DstVlan);
	}

	/**
	 * Remove the resource from opennaas
	 * 
	 * @param model
	 * @throws ResourceException
	 */
	private void removeResources(VCPENetworkModel model) throws ResourceException {
		log.info("Remove resources");

		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		LogicalRouterHelper.removeLRFromRM(lr1);
		LogicalRouterHelper.removeLRFromRM(lr2);
	}

}
