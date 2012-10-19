package org.opennaas.extensions.vcpe.capability.builder;

import java.util.ArrayList;
import java.util.List;

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
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.capability.staticroute.IStaticRouteCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPENetworkBuilder extends AbstractCapability implements IVCPENetworkBuilder {

	public static final String	CAPABILITY_TYPE	= "vcpenet_builder";
	private String				resourceId;

	public VCPENetworkBuilder(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
	}

	@Override
	public void activate() throws CapabilityException {
		// registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IVCPENetworkBuilder.class.getName());
		setState(State.ACTIVE);
	}

	@Override
	public void deactivate() throws CapabilityException {
		// unregisterService();
		setState(State.INACTIVE);
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

	@Override
	public VCPENetworkModel buildVCPENetwork(VCPENetworkModel desiredScenario) throws CapabilityException {
		try {
			return buildDesiredScenario(resource, desiredScenario);
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void destroyVCPENetwork() throws CapabilityException {
		try {
			unbuildScenario(resource, (VCPENetworkModel) resource.getModel());
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}
	}

	private VCPENetworkModel buildDesiredScenario(IResource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		// createExternalLinks(resource, desiredScenario);

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

		try {
			executePhysicalRouters(desiredScenario);
			executeLogicalRouters(desiredScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		// TODO return created model, not the desired one
		return desiredScenario;
	}

	private VCPENetworkModel unbuildScenario(IResource resource, VCPENetworkModel currentScenario) throws ResourceException {

		unconfigureEGP(resource, currentScenario);

		// not necessary because subinterfaces will be removed afterwards
		// removeIPAddresses(resource, currentScenario);

		stopLogicalRouters(resource, currentScenario);

		removeLogicalRouters(resource, currentScenario);

		removeSubInterfaces(resource, currentScenario);

		// destroyExternalLinks(resource, currentScenario);

		try {
			executePhysicalRouters(currentScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		removeResources(currentScenario);

		// TODO return the model after deleting all LR and subInterfaces from it
		return currentScenario;
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
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		List<Interface> ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr1.getInterfaces());
		ifaces.add((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.UP1_INTERFACE_PEER));

		createInterfaces(phy1, ifaces, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr2.getInterfaces());
		ifaces.add((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.UP2_INTERFACE_PEER));

		createInterfaces(phy2, ifaces, desiredScenario);
	}

	private void removeSubInterfaces(IResource resource, VCPENetworkModel currentScenario) throws ResourceException {
		// SubInterfaces assigned to logical routers will be destroyed with them.
		// There is only need to remove other interfaces

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(currentScenario, VCPETemplate.CPE1_PHY_ROUTER);
		List<Interface> ifaces = new ArrayList<Interface>();
		ifaces.add((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(currentScenario, VCPETemplate.UP1_INTERFACE_PEER));

		removeInterfaces(phy1, ifaces, currentScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(currentScenario, VCPETemplate.CPE2_PHY_ROUTER);
		ifaces = new ArrayList<Interface>();
		ifaces.add((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(currentScenario, VCPETemplate.UP2_INTERFACE_PEER));

		removeInterfaces(phy2, ifaces, currentScenario);

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

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE2_PHY_ROUTER);
		Interface up1 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP1_INTERFACE_PEER);
		Interface up2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP2_INTERFACE_PEER);

		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);

		setIP(phy1, up1, model);
		setIP(phy2, up2, model);

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

	private void configureEGP(IResource resource, VCPENetworkModel model) throws ResourceException {
		// only static routes by now
		configureStaticRoutes(resource, model);
	}

	private void unconfigureEGP(IResource resource, VCPENetworkModel model) throws ResourceException {
		// only static routes by now
		unconfigureStaticRoutes(resource, model);
	}

	private void configureStaticRoutes(IResource resource, VCPENetworkModel model) throws ResourceException {
		configureStaticRoutesInProvider(resource, model);
		// Notice this requires logical routers to be started
		configureStaticRoutesInClient(resource, model);
	}

	private void unconfigureStaticRoutes(IResource resource, VCPENetworkModel model) throws ResourceException {
		// TODO uncomment when unconfiguring static routes is available
		// unconfigureStaticRoutesInProvider(resource, model);

		// not necessary because logical routers will be dropped anyway
		// unconfigureStaticRoutesInClient(resource, model);
	}

	private void unconfigureStaticRoutesInClient(IResource resource, VCPENetworkModel model) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	private void unconfigureStaticRoutesInProvider(IResource resource, VCPENetworkModel model) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
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

		String ipRange = "0.0.0.0/0";
		String nextHopIpAddress = addressAndMask1[0];

		setStaticRoute(lr1, model, ipRange, nextHopIpAddress);

		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);
		Interface iface2 = (Interface) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.UP2_INTERFACE_PEER);
		String[] addressAndMask2 = IPUtilsHelper.composedIPAddressToIPAddressAndMask(iface2.getIpAddress());
		String nextHopIpAddress2 = addressAndMask2[0];

		setStaticRoute(lr2, model, ipRange, nextHopIpAddress2);

	}

	private void setStaticRoute(Router router, VCPENetworkModel model, String ipRange, String nextHopIpAddress) throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		String[] ipRangeAddressAndMask = IPUtilsHelper.composedIPAddressToIPAddressAndMask(ipRange);

		if (ipRangeAddressAndMask.length < 1) {
			throw new ResourceException("Invalid IP address range (missing mask): " + ipRange);
		}

		IStaticRouteCapability capability = (IStaticRouteCapability) routerResource.getCapabilityByInterface(IStaticRouteCapability.class);
		capability.createStaticRoute(ipRangeAddressAndMask[0], ipRangeAddressAndMask[1], nextHopIpAddress);
	}

	private void startLogicalRouters(IResource resource, VCPENetworkModel model) throws ResourceException {
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

	private void execute(IResource resource) throws ResourceException, ProtocolException {
		IQueueManagerCapability qCapability = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		QueueResponse response = qCapability.execute();
		if (!response.isOk()) {
			throw new ResourceException("Failed to execute queue for resource " + resource.getResourceDescriptor().getInformation().getName());
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
