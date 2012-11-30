package org.opennaas.extensions.vcpe.capability.builder;

import static com.google.common.collect.Iterables.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.bgp.BGPCapability;
import org.opennaas.extensions.router.capability.bgp.IBGPCapability;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.capability.staticroute.IStaticRouteCapability;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.capability.vrrp.VRRPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.VCPEToBoDModelTranslator;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.VRRP;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPENetworkBuilder extends AbstractCapability implements IVCPENetworkBuilder {

	Log							log				= LogFactory.getLog(VCPENetworkBuilder.class);

	public static final String	CAPABILITY_TYPE	= "vcpenet_builder";
	private String				resourceId;

	public VCPENetworkBuilder(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IVCPENetworkBuilder.class.getName());
		setState(State.ACTIVE);
	}

	@Override
	public void deactivate() throws CapabilityException {
		setState(State.INACTIVE);
		registration.unregister();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	// /////////////////////////////////////
	// IVCPENetworkBuilder implementation //
	// /////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilder#buildVCPENetwork(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
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
	 * @see org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilder#destroyVCPENetwork()
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
	 * @see org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilder#updateIps(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
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
					Interface outDatedIface = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(currentModel, iface.getNameInTemplate());
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
				Interface outDatedIface = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(currentModel, iface.getNameInTemplate());
				if (!outDatedIface.getIpAddress().equals(iface.getIpAddress())) {
					outDatedIface.setIpAddress(iface.getIpAddress());
				}
			}
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

		createSubInterfaces(resource, desiredScenario);

		assignIPAddresses(resource, desiredScenario);

		createLogicalRouters(resource, desiredScenario);

		try {
			executePhysicalRouters(desiredScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		startLogicalRouters(resource, desiredScenario);

		configureEGP(resource, desiredScenario);

		configureVRRP(resource, desiredScenario);

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

		unconfigureEGP(resource, currentScenario);

		// not necessary because subinterfaces will be removed afterwards
		// removeIPAddresses(resource, currentScenario);

		stopLogicalRouters(resource, currentScenario);

		removeLogicalRouters(resource, currentScenario);

		removeSubInterfaces(resource, currentScenario);

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
		model.setVcpeNetworkId(currentScenario.getVcpeNetworkId());
		model.setVcpeNetworkName(currentScenario.getVcpeNetworkName());
		model.setCreated(false);
		resource.setModel(model);

		return (VCPENetworkModel) resource.getModel();
	}

	private void createLogicalRouters(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		createLR(phy1, lr1, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		createLR(phy2, lr2, desiredScenario);
	}

	private void removeLogicalRouters(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		removeLR(phy1, lr1, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		removeLR(phy2, lr2, desiredScenario);
	}

	private void createLR(Router phy, Router lr, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		ComputerSystem lrModel = VCPEToRouterModelTranslator.vCPERouterToRouter(lr, model);

		IChassisCapability lrCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);
		lrCapability.createLogicalRouter(lrModel);
	}

	private void removeLR(Router phy, Router lr, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		// remove LR from phyRouter config
		ComputerSystem lrModel = VCPEToRouterModelTranslator.vCPERouterToRouter(lr, model);
		IChassisCapability lrCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);
		lrCapability.deleteLogicalRouter(lrModel);

		// removing it from RM will be done after executing the queue
	}

	private void createSubInterfaces(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		log.debug("Configuring subinterfaces");

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		List<Interface> ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr1.getInterfaces());

		createInterfaces(phy1, ifaces, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr2.getInterfaces());

		createInterfaces(phy2, ifaces, desiredScenario);
	}

	private void removeSubInterfaces(IResource resource, VCPENetworkModel currentScenario) throws ResourceException {
		// SubInterfaces assigned to logical routers will be destroyed with them.
		// There is only need to remove other interfaces (if any)
		// no other interfaces, so nothing to do :P

		log.debug("Removing subinterfaces");
	}

	private void createInterfaces(Router phy, List<Interface> ifaces, VCPENetworkModel model) throws ResourceException {

		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));
		IChassisCapability chassisCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);

		for (Interface iface : ifaces) {
			NetworkPort port = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(iface, model);
			chassisCapability.createSubInterface(port);
			// Note: this call will NOT assign IP addresses to given interfaces
		}

	}

	private void removeInterfaces(Router phy, List<Interface> ifaces, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		IChassisCapability chassisCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);

		for (Interface iface : ifaces) {
			NetworkPort port = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(iface, model);
			chassisCapability.deleteSubInterface(port);
		}
	}

	private void removeResources(VCPENetworkModel model) throws ResourceException {

		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);

		removeFromRM(lr1);
		removeFromRM(lr2);

	}

	private void removeFromRM(Router router) throws ResourceException {
		IResourceIdentifier lrId = getResourceManager().getIdentifierFromResourceName("router", router.getName());
		IResource lrResource = getResourceManager().getResource(lrId);
		if (lrResource.getState().equals(State.ACTIVE)) {
			getResourceManager().stopResource(lrId);
		}
		getResourceManager().removeResource(lrId);

	}

	private void assignIPAddresses(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Assigning IP addresses");

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE2_PHY_ROUTER);

		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);

		// we assign addresses in physical routers
		// logical ones may not exist yet
		for (Interface iface : lr1.getInterfaces()) {
			setIP(phy1, iface, model);
		}
		for (Interface iface : lr2.getInterfaces()) {
			setIP(phy2, iface, model);
		}
	}

	private void setIP(Router router, Interface iface, VCPENetworkModel model) throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		IIPCapability capability = (IIPCapability) routerResource.getCapabilityByInterface(IIPCapability.class);

		LogicalPort port = VCPEToRouterModelTranslator.vCPEInterfaceToLogicalPort(iface, model);
		for (ProtocolEndpoint pep : port.getProtocolEndpoint()) {
			if (pep instanceof IPProtocolEndpoint) {
				capability.setIPv4(port, (IPProtocolEndpoint) pep);
			}
		}
	}

	private void setIP(Router router, Interface iface, String ipAddress, VCPENetworkModel model) throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		IIPCapability capability = (IIPCapability) routerResource.getCapabilityByInterface(IIPCapability.class);

		LogicalPort port = VCPEToRouterModelTranslator.vCPEInterfaceToLogicalPort(iface, model);
		IPProtocolEndpoint ipPEP = VCPEToRouterModelTranslator.ipAddressToProtocolEndpoint(ipAddress);
		capability.setIPv4(port, ipPEP);
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
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);

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

	private void unconfigureEGP(IResource resource, VCPENetworkModel model) throws ResourceException {
		// only static routes by now
		unconfigureStaticRoutes(resource, model);
	}

	private void configureStaticRoutes(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Configuring static routes");

		// configureStaticRoutesInProvider(resource, model);
		// Notice this requires logical routers to be started
		configureStaticRoutesInClient(resource, model);
	}

	private void unconfigureStaticRoutes(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Removing static routes");

		// unconfigureStaticRoutesInProvider(resource, model);

		// not necessary because logical routers will be dropped anyway
		// unconfigureStaticRoutesInClient(resource, model);
	}

	private void unconfigureStaticRoutesInClient(IResource resource, VCPENetworkModel model) throws ResourceException {
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Interface iface1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP1_INTERFACE_PEER);

		String[] addressAndMask1 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface1.getIpAddress());

		String ipRange = "0.0.0.0/0";
		String nextHopIpAddress = addressAndMask1[0];

		deleteStaticRoute(lr1, model, ipRange, nextHopIpAddress);

		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);
		Interface iface2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP2_INTERFACE_PEER);
		String[] addressAndMask2 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface2.getIpAddress());
		String nextHopIpAddress2 = addressAndMask2[0];

		deleteStaticRoute(lr2, model, ipRange, nextHopIpAddress2);
	}

	private void unconfigureStaticRoutesInProvider(IResource resource, VCPENetworkModel model) throws ResourceException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE1_PHY_ROUTER);
		Interface iface1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP1_INTERFACE_LOCAL);

		String[] addressAndMask1 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface1.getIpAddress());

		String ipRange = model.getClientIpAddressRange();
		String nextHopIpAddress1 = addressAndMask1[0];

		deleteStaticRoute(phy1, model, ipRange, nextHopIpAddress1);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE2_PHY_ROUTER);
		Interface iface2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP2_INTERFACE_LOCAL);

		String[] addressAndMask2 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface2.getIpAddress());
		String nextHopIpAddress2 = addressAndMask2[0];

		deleteStaticRoute(phy2, model, ipRange, nextHopIpAddress2);
	}

	private void configureStaticRoutesInProvider(IResource resource, VCPENetworkModel model) throws ResourceException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE1_PHY_ROUTER);
		Interface iface1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP1_INTERFACE_LOCAL);

		String[] addressAndMask1 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface1.getIpAddress());

		String ipRange = model.getClientIpAddressRange();
		String nextHopIpAddress1 = addressAndMask1[0];

		setStaticRoute(phy1, model, ipRange, nextHopIpAddress1);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE2_PHY_ROUTER);
		Interface iface2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP2_INTERFACE_LOCAL);

		String[] addressAndMask2 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface2.getIpAddress());
		String nextHopIpAddress2 = addressAndMask2[0];

		setStaticRoute(phy2, model, ipRange, nextHopIpAddress2);
	}

	private void configureStaticRoutesInClient(IResource resource, VCPENetworkModel model) throws ResourceException {
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Interface iface1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP1_INTERFACE_PEER);

		String[] addressAndMask1 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface1.getIpAddress());

		String ipRange = model.getBgp().getCustomerPrefixes().get(0);

		String nextHopIpAddress = "";

		setStaticRoute(lr1, model, ipRange, nextHopIpAddress);

		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);
		Interface iface2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP2_INTERFACE_PEER);
		String[] addressAndMask2 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface2.getIpAddress());
		String nextHopIpAddress2 = addressAndMask2[0];

		setStaticRoute(lr2, model, ipRange, nextHopIpAddress2);

	}

	private void setStaticRoute(Router router, VCPENetworkModel model, String ipRange, String nextHopIpAddress)
			throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		String[] ipRangeAddressAndMask = IPUtilsHelper.composedIPAddressToIPAddressAndMask(ipRange);

		if (ipRangeAddressAndMask.length < 1) {
			throw new ResourceException("Invalid IP address range (missing mask): " + ipRange);
		}

		IStaticRouteCapability capability = (IStaticRouteCapability) routerResource.getCapabilityByInterface(IStaticRouteCapability.class);
		capability.createStaticRoute(ipRangeAddressAndMask[0], ipRangeAddressAndMask[1], nextHopIpAddress, "true");
	}

	private void deleteStaticRoute(Router router, VCPENetworkModel model, String ipRange, String nextHopIpAddress) throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		String[] ipRangeAddressAndMask = IPUtilsHelper.composedIPAddressToIPAddressAndMask(ipRange);

		if (ipRangeAddressAndMask.length < 1) {
			throw new ResourceException("Invalid IP address range (missing mask): " + ipRange);
		}

		IStaticRouteCapability capability = (IStaticRouteCapability) routerResource.getCapabilityByInterface(IStaticRouteCapability.class);
		capability.deleteStaticRoute(ipRangeAddressAndMask[0], ipRangeAddressAndMask[1], nextHopIpAddress);
	}

	private void startLogicalRouters(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Starting logical routers");

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);

		try {
			registerContexts(phy1, lr1);
			registerContexts(phy2, lr2);
		} catch (ProtocolException e) {
			throw new ResourceException("Failed to start logical louters", e);
		}

		getResourceManager().startResource(getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		getResourceManager().startResource(getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));
	}

	private void stopLogicalRouters(IResource resource, VCPENetworkModel model) throws ResourceException {
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);

		getResourceManager().stopResource(getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		getResourceManager().stopResource(getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));
	}

	private void registerContexts(Router physical, Router logical) throws ProtocolException, ResourceException {

		IProtocolSessionManager phyPSM = getProtocolManager().getProtocolSessionManager(
				getResourceManager().getIdentifierFromResourceName("router", physical.getName()).getId());

		IProtocolSessionManager logPSM = getProtocolManager().getProtocolSessionManager(
				getResourceManager().getIdentifierFromResourceName("router", logical.getName()).getId());

		List<ProtocolSessionContext> phyContexts = phyPSM.getRegisteredContexts();
		for (ProtocolSessionContext context : phyContexts) {
			logPSM.registerContext(context.clone());
		}
	}

	private void createExternalLinks(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Creating bod links");

		List<Link> links = VCPENetworkModelHelper.getLinks(model.getElements());

		// inter link
		Link inter = (Link) VCPENetworkModelHelper.getElementByNameInTemplate(links, VCPETemplate.INTER_LINK);
		long interSrcVlan = inter.getSource().getVlanId();
		long interDstVlan = inter.getSink().getVlanId();

		Interface interSrc = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);
		Interface interDst = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);

		createAutobahnLink(model, interSrc, interDst, interSrcVlan, interDstVlan);

		// // down1 link
		// Link down1 = (Link) VCPENetworkModelHelper.getElementByNameInTemplate(links, VCPETemplate.DOWN1_LINK);
		// long down1SrcVlan = down1.getSource().getVlanId();
		// long down1DstVlan = down1.getSink().getVlanId();
		//
		// Interface down1Src = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);
		// Interface down1Dst = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);
		//
		// createAutobahnLink(model, down1Src, down1Dst, down1SrcVlan, down1DstVlan);

		// down 2 link
		Link down2 = (Link) VCPENetworkModelHelper.getElementByNameInTemplate(links, VCPETemplate.DOWN2_LINK);
		long down2SrcVlan = down2.getSource().getVlanId();
		long down2DstVlan = down2.getSink().getVlanId();

		Interface down2Src = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);
		Interface down2Dst = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);

		createAutobahnLink(model, down2Src, down2Dst, down2SrcVlan, down2DstVlan);
	}

	private void destroyExternalLinks(IResource resource, VCPENetworkModel model) throws ResourceException {

		log.debug("Removing bod links");

		List<Link> links = VCPENetworkModelHelper.getLinks(model.getElements());

		// inter link
		Link inter = (Link) VCPENetworkModelHelper.getElementByNameInTemplate(links, VCPETemplate.INTER_LINK);
		long interSrcVlan = inter.getSource().getVlanId();
		long interDstVlan = inter.getSink().getVlanId();

		Interface interSrc = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN);
		Interface interDst = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN);

		destroyAutobahnLink(model, interSrc, interDst, interSrcVlan, interDstVlan);

		// down1 link
		Link down1 = (Link) VCPENetworkModelHelper.getElementByNameInTemplate(links, VCPETemplate.DOWN1_LINK);
		long down1SrcVlan = down1.getSource().getVlanId();
		long down1DstVlan = down1.getSink().getVlanId();

		Interface down1Src = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN);
		Interface down1Dst = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN);

		destroyAutobahnLink(model, down1Src, down1Dst, down1SrcVlan, down1DstVlan);

		// down 2 link
		Link down2 = (Link) VCPENetworkModelHelper.getElementByNameInTemplate(links, VCPETemplate.DOWN2_LINK);
		long down2SrcVlan = down2.getSource().getVlanId();
		long down2DstVlan = down2.getSink().getVlanId();

		Interface down2Src = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN);
		Interface down2Dst = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN);

		destroyAutobahnLink(model, down2Src, down2Dst, down2SrcVlan, down2DstVlan);
	}

	private void createAutobahnLink(VCPENetworkModel model, Interface src, Interface dst, long srcVlan, long dstVlan) throws ResourceException {

		Domain bod = (Domain) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.AUTOBAHN);

		IResource autobahn = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("bod", bod.getName()));

		RequestConnectionParameters requestParams = createL2BoDCreateConnectionRequest(src, dst, srcVlan, dstVlan, autobahn);

		IL2BoDCapability capability = (IL2BoDCapability) autobahn.getCapabilityByInterface(IL2BoDCapability.class);
		capability.requestConnection(requestParams);
	}

	private void destroyAutobahnLink(VCPENetworkModel model, Interface src, Interface dst, long srcVlan, long dstVlan) throws ResourceException {

		Domain bod = (Domain) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.AUTOBAHN);

		IResource autobahn = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("bod", bod.getName()));

		RequestConnectionParameters requestParams = createL2BoDCreateConnectionRequest(src, dst, srcVlan, dstVlan, autobahn);

		IL2BoDCapability capability = (IL2BoDCapability) autobahn.getCapabilityByInterface(IL2BoDCapability.class);
		capability.shutDownConnection(requestParams);
	}

	private RequestConnectionParameters createL2BoDCreateConnectionRequest(Interface src, Interface dst, long srcVlan, long dstVlan,
			IResource autobahn) {

		// TODO hardcoded! Read from config file or from NOC input
		// set link capacity to 100 MB/s
		long capacity = 100 * 1000000L;
		// TODO hardcoded! Read from config file or from NOC input
		// set link endTime to 31/12/2012 12:00h
		DateTime endTime = new DateTime(2012, 12, 31, 12, 0);

		NetworkModel model = (NetworkModel) autobahn.getModel();

		RequestConnectionParameters parameters = new RequestConnectionParameters(
				VCPEToBoDModelTranslator.vCPEInterfaceToBoDInterface(src, model),
				VCPEToBoDModelTranslator.vCPEInterfaceToBoDInterface(dst, model),
				capacity,
				Integer.parseInt(Long.toString(srcVlan)), Integer.parseInt(Long.toString(dstVlan)),
				DateTime.now(), endTime);

		return parameters;
	}

	// TODO
	private void configureVRRP(IResource resource, VCPENetworkModel model) throws ResourceException {
		log.debug("Configuring VRRP");

		// obtain VRRP from VCPENetworkModel
		VRRP vrrp = model.getVrrp();

		// create VRRPGroup and VRRPProtocolEndpoint's
		VRRPGroup vrrpGroup = new VRRPGroup();

		// set VRRPGroup parameters
		vrrpGroup.setVrrpName(vrrp.getGroup());
		vrrpGroup.setVirtualIPAddress(vrrp.getVirtualIPAddress());

		// obtain CIM model NetworkPort's (router interfaces)
		NetworkPort masterNetworkPort = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(vrrp.getMasterInterface(), model);
		NetworkPort backupNetworkPort = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(vrrp.getBackupInterface(), model);

		// configure both VRRPProtocolEndpoint's
		configureVRRPProtocolEndpoint(vrrp.getPriorityMaster(), vrrp.getMasterInterface(), vrrp.getMasterRouter(), vrrpGroup, masterNetworkPort);
		configureVRRPProtocolEndpoint(vrrp.getPriorityBackup(), vrrp.getBackupInterface(), vrrp.getBackupRouter(), vrrpGroup, backupNetworkPort);

	}

	private void configureVRRPProtocolEndpoint(int vrrpPriority, Interface iface, Router router, VRRPGroup vrrgrGroup, NetworkPort networkPort)
			throws ResourceException {
		// create VRRPProtocolEndpoint
		VRRPProtocolEndpoint vrrpProtocolEndpoint = new VRRPProtocolEndpoint();

		// set VRRPProtocolEndpoint' parameters
		vrrpProtocolEndpoint.setPriority(vrrpPriority);

		// link CIM VRRP model elements
		vrrpProtocolEndpoint.setService(vrrgrGroup);

		// link VRRP CIM model elements to CIM model elements
		// obtain router interface
		if (networkPort.getName().equals(iface.getPhysicalInterfaceName()) &&
				networkPort.getPortNumber() == (iface.getPortNumber())) {
			// obtain master interface IP address
			List<ProtocolEndpoint> ipAddresses = networkPort.getProtocolEndpoint();
			for (ProtocolEndpoint protocolEndpoint : ipAddresses) {
				if (protocolEndpoint instanceof IPProtocolEndpoint &&
						(((IPProtocolEndpoint) protocolEndpoint).getIPv4Address() + "/" + IPUtilsHelper
								.parseLongToShortIpv4NetMask(((IPProtocolEndpoint) protocolEndpoint).getSubnetMask())).equals(iface.getIpAddress())) {
					// link VRRPProtocolEndpoint with IPProtocolEndpoint
					vrrpProtocolEndpoint.bindServiceAccessPoint(protocolEndpoint);
				}
			}
		}

		// obtain VRRPCapability and apply the configuration to interface
		IResource routerResource = getResourceManager().getResource(getResourceManager().getIdentifierFromResourceName("router", router.getName()));
		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapabilityByInterface(VRRPCapability.class);
		vrrpCapability.configureVRRP(vrrpProtocolEndpoint);
	}

	// TODO
	private void unconfigureVRRP(IResource resource, VCPENetworkModel model) throws ResourceException {
		log.debug("Unconfiguring VRRP");

	}

	private void executePhysicalRouters(VCPENetworkModel model) throws ResourceException, ProtocolException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE2_PHY_ROUTER);

		IResource phyResource1 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy1.getName()));
		IResource phyResource2 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy2.getName()));

		execute(phyResource1);
		execute(phyResource2);
	}

	private void executeLogicalRouters(VCPENetworkModel model) throws ResourceException, ProtocolException {
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);

		IResource lrResource1 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		IResource lrResource2 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

		execute(lrResource1);
		execute(lrResource2);
	}

	private void executeAutobahn(VCPENetworkModel model) throws ResourceException, ProtocolException {

		Domain bod = (Domain) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.AUTOBAHN);

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

	private IProtocolManager getProtocolManager() throws ResourceException {
		try {
			return Activator.getProtocolManagerService();
		} catch (ActivatorException e) {
			throw new ResourceException("Could not find ProtocolManager", e);
		}
	}

}
