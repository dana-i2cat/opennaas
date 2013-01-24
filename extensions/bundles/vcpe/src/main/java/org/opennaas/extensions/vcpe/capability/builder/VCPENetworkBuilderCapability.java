package org.opennaas.extensions.vcpe.capability.builder;

import static com.google.common.collect.Iterables.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.bgp.BGPCapability;
import org.opennaas.extensions.router.capability.bgp.IBGPCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.capability.builder.helpers.AutobahnHelper;
import org.opennaas.extensions.vcpe.capability.builder.helpers.IPHelper;
import org.opennaas.extensions.vcpe.capability.builder.helpers.InterfaceHelper;
import org.opennaas.extensions.vcpe.capability.builder.helpers.LogicalRouterHelper;
import org.opennaas.extensions.vcpe.capability.builder.helpers.StaticRouteHelper;
import org.opennaas.extensions.vcpe.capability.builder.helpers.VRRPHelper;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPENetworkBuilderCapability extends AbstractCapability implements IVCPENetworkBuilderCapability {

	Log							log				= LogFactory.getLog(VCPENetworkBuilderCapability.class);

	public static final String	CAPABILITY_TYPE	= "vcpenet_builder";
	private String				resourceId;

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public VCPENetworkBuilderCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IVCPENetworkBuilderCapability.class.getName());
		setState(State.ACTIVE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		setState(State.INACTIVE);
		registration.unregister();
		super.deactivate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityName()
	 */
	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#getActionSet()
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability#buildVCPENetwork(org.opennaas.extensions.vcpe.model.VCPENetworkModel
	 * )
	 */
	@Override
	public VCPENetworkModel buildVCPENetwork(VCPENetworkModel desiredScenario) throws CapabilityException {

		if (((VCPENetworkModel) resource.getModel()).isCreated())
			throw new CapabilityException("VCPE already created");

		try {
			return buildDesiredScenario(resource, desiredScenario);
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability#destroyVCPENetwork()
	 */
	@Override
	public void destroyVCPENetwork() throws CapabilityException {

		if (!((VCPENetworkModel) resource.getModel()).isCreated())
			throw new CapabilityException("VCPE has not been created");

		try {
			unbuildScenario(resource, (VCPENetworkModel) resource.getModel());
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability#updateIps(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public void updateIps(VCPENetworkModel updatedModel) throws CapabilityException {

		if (!((VCPENetworkModel) resource.getModel()).isCreated())
			throw new CapabilityException("VCPE has not been created");

		VCPENetworkModel currentModel = (VCPENetworkModel) resource.getModel();

		// launch commands
		try {
			for (Router router : filter(updatedModel.getElements(), Router.class)) {
				for (Interface iface : router.getInterfaces()) {
					Interface outDatedIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(currentModel, iface.getTemplateName());
					if (!outDatedIface.getIpAddress().equals(iface.getIpAddress())) {
						setIP(router, outDatedIface, iface.getIpAddress(), currentModel);
					}
				}
			}
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

		// execute queues
		try {
			executeLogicalRouters(currentModel);
			executePhysicalRouters(currentModel);
		} catch (Exception e) {
			throw new CapabilityException(e);
		}

		// update IP addresses in model
		for (Router router : filter(updatedModel.getElements(), Router.class)) {
			for (Interface iface : router.getInterfaces()) {
				Interface outDatedIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(currentModel, iface.getTemplateName());
				if (!outDatedIface.getIpAddress().equals(iface.getIpAddress())) {
					outDatedIface.setIpAddress(iface.getIpAddress());
				}
			}
		}
	}

	/**
	 * @param router
	 * @param iface
	 * @param ipAddress
	 * @param model
	 * @throws ResourceException
	 */
	private void setIP(Router router, Interface iface, String ipAddress, VCPENetworkModel model) throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		IIPCapability capability = (IIPCapability) routerResource.getCapabilityByInterface(IIPCapability.class);

		LogicalPort port = VCPEToRouterModelTranslator.vCPEInterfaceToLogicalPort(iface, model);
		IPProtocolEndpoint ipPEP = VCPEToRouterModelTranslator.ipAddressToProtocolEndpoint(ipAddress);
		capability.setIPv4(port, ipPEP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.VCPENetworkBuilderCapability#updateVRRPIp(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public void updateVRRPIp(VCPENetworkModel vcpeModel) throws CapabilityException {
		log.debug("Updating VRRP ip");
		try {
			Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(vcpeModel, VCPETemplate.VCPE1_ROUTER);
			Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(vcpeModel, VCPETemplate.VCPE2_ROUTER);

			IResource router1 = getResourceManager().getResource(
					getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
			IResource router2 = getResourceManager().getResource(
					getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

			IVRRPCapability capability1 = (IVRRPCapability) router1.getCapabilityByInterface(IVRRPCapability.class);
			IVRRPCapability capability2 = (IVRRPCapability) router2.getCapabilityByInterface(IVRRPCapability.class);

			VRRPProtocolEndpoint endpointRouter1 = getVRRPProtocolEndpointDeepCopy((ComputerSystem) router1.getModel());
			((VRRPGroup) endpointRouter1.getService()).setVirtualIPAddress(vcpeModel.getVrrp().getVirtualIPAddress());

			VRRPProtocolEndpoint endpointRouter2 = getVRRPProtocolEndpointDeepCopy((ComputerSystem) router2.getModel());
			((VRRPGroup) endpointRouter2.getService()).setVirtualIPAddress(vcpeModel.getVrrp().getVirtualIPAddress());

			capability1.updateVRRPVirtualIPAddress(endpointRouter1);
			capability2.updateVRRPVirtualIPAddress(endpointRouter2);

			execute(router1);
			execute(router2);

		} catch (Exception e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.vcpe.capability.builder.VCPENetworkBuilderCapability#changeVRRPPriority(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public VCPENetworkModel changeVRRPPriority(VCPENetworkModel model) throws CapabilityException {
		log.debug("Change the priority VRRP");
		try {
			Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
			Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

			IResource router1 = getResourceManager().getResource(
					getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
			IResource router2 = getResourceManager().getResource(
					getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

			// get VRRPProtocolEndpoint deep copies
			VRRPProtocolEndpoint vrrpProtocolEndpoint1 = getVRRPProtocolEndpointDeepCopy((ComputerSystem) router1.getModel());
			VRRPProtocolEndpoint vrrpProtocolEndpoint2 = getVRRPProtocolEndpointDeepCopy((ComputerSystem) router2.getModel());

			// swap priorities
			int priority1 = vrrpProtocolEndpoint1.getPriority();
			int priority2 = vrrpProtocolEndpoint2.getPriority();
			vrrpProtocolEndpoint1.setPriority(priority2);
			vrrpProtocolEndpoint2.setPriority(priority1);

			// call capabilities
			IVRRPCapability capability1 = (IVRRPCapability) router1.getCapabilityByInterface(IVRRPCapability.class);
			capability1.updateVRRPPriority(vrrpProtocolEndpoint1);
			IVRRPCapability capability2 = (IVRRPCapability) router2.getCapabilityByInterface(IVRRPCapability.class);
			capability2.updateVRRPPriority(vrrpProtocolEndpoint2);

			// execute queues
			execute(router1);
			execute(router2);

			// Update the priority in the model and return the model
			IResource vcpeResource = getResourceManager().getResourceById(resourceId);
			VCPENetworkModel vcpeNetworkModel = (VCPENetworkModel) vcpeResource.getModel();
			if (vcpeNetworkModel != null) {
				vcpeNetworkModel.getVrrp().setPriorityMaster(vrrpProtocolEndpoint1.getPriority());
				vcpeNetworkModel.getVrrp().setPriorityBackup(vrrpProtocolEndpoint2.getPriority());
			}
			return (VCPENetworkModel) (vcpeResource != null ? vcpeResource.getModel() : null);
		} catch (Exception e) {
			throw new CapabilityException(e);
		}
	}

	private VCPENetworkModel buildDesiredScenario(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		log.debug("building scenario in resource" + resource.getResourceDescriptor().getInformation().getName());

		createExternalLinks(resource, desiredScenario);

		try {
			executeAutobahn(desiredScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		createLogicalRouters(resource, desiredScenario);

		try {
			executePhysicalRouters(desiredScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		startLogicalRouters(resource, desiredScenario);

		createSubInterfaces(resource, desiredScenario);

		assignIPAddresses(resource, desiredScenario);

		try {
			executeLogicalRouters(desiredScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		configureEGP(resource, desiredScenario);

		VRRPHelper.configureVRRP(resource, desiredScenario);

		try {
			executePhysicalRouters(desiredScenario);
			executeLogicalRouters(desiredScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		// TODO return created model, not the desired one
		resource.setModel(desiredScenario);
		((VCPENetworkModel) resource.getModel()).setCreated(true);
		return (VCPENetworkModel) resource.getModel();
	}

	private VCPENetworkModel unbuildScenario(IResource resource, VCPENetworkModel currentScenario) throws ResourceException {

		// not necessary because subinterfaces will be removed afterwards
		// removeIPAddresses(resource, currentScenario);

		stopLogicalRouters(resource, currentScenario);

		removeLogicalRouters(resource, currentScenario);

		try {
			executePhysicalRouters(currentScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		destroyExternalLinks(resource, currentScenario);

		try {
			executeAutobahn(currentScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		removeResources(currentScenario);

		// return the model after deleting all LR and subInterfaces from it
		VCPENetworkModel model = new VCPENetworkModel();
		model.setId(currentScenario.getId());
		model.setName(currentScenario.getName());
		model.setCreated(false);
		resource.setModel(model);

		return (VCPENetworkModel) resource.getModel();
	}

	private void createLogicalRouters(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		LogicalRouterHelper.createLR(phy1, lr1, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		LogicalRouterHelper.createLR(phy2, lr2, desiredScenario);
	}

	private void removeLogicalRouters(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		LogicalRouterHelper.removeLR(phy1, lr1, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		LogicalRouterHelper.removeLR(phy2, lr2, desiredScenario);
	}

	public void createSubInterfaces(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		log.debug("Configuring subinterfaces");

		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		List<Interface> ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr1.getInterfaces());

		InterfaceHelper.createInterfaces(lr1, ifaces, desiredScenario);

		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr2.getInterfaces());

		InterfaceHelper.createInterfaces(lr2, ifaces, desiredScenario);
	}

	private void removeResources(VCPENetworkModel model) throws ResourceException {

		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		LogicalRouterHelper.removeLRFromRM(lr1);
		LogicalRouterHelper.removeLRFromRM(lr2);
	}

	private void assignIPAddresses(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Assigning IP addresses");

		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		for (Interface iface : lr1.getInterfaces()) {
			IPHelper.setIP(lr1, iface, model);
		}
		for (Interface iface : lr2.getInterfaces()) {
			IPHelper.setIP(lr2, iface, model);
		}
	}

	private void configureEGP(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Configuring EGPs");
		configureBGP(model);
		// only static routes by now
		configureStaticRoutes(resource, model);
	}

	/**
	 * @param model
	 * @throws ResourceException
	 */
	private void configureBGP(VCPENetworkModel model) throws ResourceException {
		log.debug("Configuring BGP");
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		IResource router1Resource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		IResource router2Resource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

		IBGPCapability capability1 = (IBGPCapability) router1Resource.getCapabilityByInterface(BGPCapability.class);
		capability1.configureBGP(model.getBgp().getBgpConfigForMaster());

		IBGPCapability capability2 = (IBGPCapability) router2Resource.getCapabilityByInterface(BGPCapability.class);
		capability2.configureBGP(model.getBgp().getBgpConfigForBackup());

		model.getBgp().setBgpConfigForMaster(null);
		model.getBgp().setBgpConfigForBackup(null);
	}

	private void configureStaticRoutes(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Configuring static routes");

		// configureStaticRoutesInProvider(resource, model);
		// Notice this requires logical routers to be started
		configureStaticRoutesInClient(resource, model);
	}

	private void configureStaticRoutesInClient(IResource resource, VCPENetworkModel model) throws ResourceException {
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Interface iface1 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP1_INTERFACE_PEER);

		String[] addressAndMask1 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface1.getIpAddress());

		String ipRange = model.getBgp().getCustomerPrefixes().get(0);

		String nextHopIpAddress = "";

		StaticRouteHelper.setStaticRoute(lr1, model, ipRange, nextHopIpAddress);

		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		Interface iface2 = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP2_INTERFACE_PEER);
		String[] addressAndMask2 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface2.getIpAddress());
		String nextHopIpAddress2 = addressAndMask2[0];

		StaticRouteHelper.setStaticRoute(lr2, model, ipRange, nextHopIpAddress2);

	}

	private void startLogicalRouters(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Starting logical routers");

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		try {
			LogicalRouterHelper.registerContexts(phy1, lr1);
			LogicalRouterHelper.registerContexts(phy2, lr2);
		} catch (ProtocolException e) {
			throw new ResourceException("Failed to start logical louters", e);
		}

		getResourceManager().startResource(getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		getResourceManager().startResource(getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));
	}

	private void stopLogicalRouters(IResource resource, VCPENetworkModel model) throws ResourceException {
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		getResourceManager().stopResource(getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		getResourceManager().stopResource(getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));
	}

	private void createExternalLinks(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Creating bod links");

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

	private void destroyExternalLinks(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Removing bod links");

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

	private void executePhysicalRouters(VCPENetworkModel model) throws ResourceException, ProtocolException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);

		IResource phyResource1 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy1.getName()));
		IResource phyResource2 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy2.getName()));

		execute(phyResource1);
		execute(phyResource2);
	}

	private void executeLogicalRouters(VCPENetworkModel model) throws ResourceException, ProtocolException {
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		IResource lrResource1 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		IResource lrResource2 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

		execute(lrResource1);
		execute(lrResource2);
	}

	private void executeAutobahn(VCPENetworkModel model) throws ResourceException, ProtocolException {

		Domain bod = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.AUTOBAHN);

		IResource autobahn = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("bod", bod.getName()));

		execute(autobahn);
	}

	private void execute(IResource resource) throws ResourceException, ProtocolException {
		IQueueManagerCapability qCapability = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		QueueResponse response = qCapability.execute();
		if (!response.isOk()) {
			String commitMsg = response.getConfirmResponse().getInformation();
			throw new ResourceException(
					"Failed to execute queue for resource " + resource.getResourceDescriptor().getInformation().getName() + ": " + commitMsg);
		}
	}

	private IResourceManager getResourceManager() throws ResourceException {
		try {
			return Activator.getResourceManagerService();
		} catch (ActivatorException e) {
			throw new ResourceException("Could not find ResourceManager", e);
		}
	}

	/**
	 * Get a deep copy of VRRPProtocolEndpoint with all necessary elements
	 * 
	 * @param originalRouter original ComputerSystem where the VRRProtocolEndpoint is
	 * @return
	 * @throws Exception
	 */
	private static VRRPProtocolEndpoint getVRRPProtocolEndpointDeepCopy(ComputerSystem originalRouter) throws Exception {
		VRRPGroup vrrpGroup = null;
		// ComputerSystem copy
		ComputerSystem newRouter = new ComputerSystem();

		VRRPGroup newVRRPGroup = null;
		List<Service> services = originalRouter.getHostedService();
		for (Service service : services) {
			if (service instanceof VRRPGroup) {
				vrrpGroup = (VRRPGroup) service;
				// VRRPGroup copy
				newVRRPGroup = new VRRPGroup();
				// copy attributes (name & virtual IP address)
				newVRRPGroup.setVrrpName(vrrpGroup.getVrrpName());
				newVRRPGroup.setVirtualIPAddress(vrrpGroup.getVirtualIPAddress());
				// add Service to ComputerSystem
				newRouter.addHostedService(newVRRPGroup);
				break;
			}
		}

		VRRPProtocolEndpoint vrrpProtocolEndpoint = null;
		VRRPProtocolEndpoint newVRRPProtocolEndpoint = null;
		if (vrrpGroup != null) {
			List<ProtocolEndpoint> protocolEndpoints = vrrpGroup.getProtocolEndpoint();
			for (ProtocolEndpoint protocolEndpoint : protocolEndpoints) {
				if (((VRRPGroup) ((VRRPProtocolEndpoint) protocolEndpoint).getService()).getVrrpName() == vrrpGroup.getVrrpName()) {
					vrrpProtocolEndpoint = (VRRPProtocolEndpoint) protocolEndpoint;
					// VRRPProtocolEndpoint copy
					newVRRPProtocolEndpoint = new VRRPProtocolEndpoint();
					// copy attributes (priority)
					newVRRPProtocolEndpoint.setPriority(vrrpProtocolEndpoint.getPriority());
					// set VRRPGroup as Service of VRRPProtocolEndpoint
					newVRRPProtocolEndpoint.setService(newVRRPGroup);

					// IPProtocolEndpoint copy
					IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) vrrpProtocolEndpoint.getBindedProtocolEndpoints().get(0);
					IPProtocolEndpoint newIPProtocolEndpoint = new IPProtocolEndpoint();
					// set attributes (IPv4 address & subnet mask)
					newIPProtocolEndpoint.setIPv4Address(ipProtocolEndpoint.getIPv4Address());
					newIPProtocolEndpoint.setSubnetMask(ipProtocolEndpoint.getSubnetMask());
					// bind ServiceAccesPoint (IPProtocolEndpoint) to VRRPProtocolEndpoint
					newVRRPProtocolEndpoint.bindServiceAccessPoint(newIPProtocolEndpoint);

					// NetworkPort copy
					NetworkPort networkPort = (NetworkPort) ipProtocolEndpoint.getLogicalPorts().get(0);
					NetworkPort newNetworkPort = new NetworkPort();
					// set attributes (name & port)
					newNetworkPort.setName(networkPort.getName());
					newNetworkPort.setPortNumber(networkPort.getPortNumber());
					// add ProtocolEndpoint (IPProtocolEndpoint) to NetworkPort
					newNetworkPort.addProtocolEndpoint(newIPProtocolEndpoint);

					// add LogicalDevice (NetworkPort) to ComputerSystem
					newRouter.addLogicalDevice(newNetworkPort);
				}
			}
			if (vrrpProtocolEndpoint == null) {
				throw new Exception("VRRPProtocolEndpoint not found");
			}
		} else {
			throw new Exception("VRRPGroup not found");
		}
		return newVRRPProtocolEndpoint;
	}
}
