package org.opennaas.extensions.vcpe.capability.builder;

import static com.google.common.collect.Iterables.filter;

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
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder;
import org.opennaas.extensions.vcpe.capability.builder.builders.VCPENetworkBuilderFactory;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.GenericHelper;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPENetworkBuilderCapability extends AbstractCapability implements IVCPENetworkBuilderCapability {

	public static final String	CAPABILITY_TYPE	= "vcpenet_builder";
	private Log					log				= LogFactory.getLog(VCPENetworkBuilderCapability.class);
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
		log.info("Build a VCPENetwork with template: " + desiredScenario.getTemplateType());
		try {
			if (((VCPENetworkModel) resource.getModel()).isCreated()) {
				throw new CapabilityException("VCPE already created");
			}
			IVCPENetworkBuilder builder = VCPENetworkBuilderFactory.getBuilder(desiredScenario.getTemplateType());
			return builder.build(resource, desiredScenario);
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
		log.info("Destroy a VCPENetwork");
		try {
			if (!((VCPENetworkModel) resource.getModel()).isCreated()) {
				throw new CapabilityException("VCPE has not been created");
			}
			VCPENetworkModel model = (VCPENetworkModel) resource.getModel();
			IVCPENetworkBuilder builder = VCPENetworkBuilderFactory.getBuilder(model.getTemplateType());
			builder.destroy(resource, model);
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
		log.info("Update the Ip's");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.vcpe.capability.builder.VCPENetworkBuilderCapability#updateVRRPIp(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public void updateVRRPIp(VCPENetworkModel vcpeModel) throws CapabilityException {
		log.info("Updating VRRP ip");
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

			GenericHelper.executeQueue(router1);
			GenericHelper.executeQueue(router2);

		} catch (Exception e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.vcpe.capability.builder.VCPENetworkBuilderCapability#changeVRRPPriority(org.opennaas.extensions.vcpe.model.VCPENetworkModel
	 * )
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
			GenericHelper.executeQueue(router1);
			GenericHelper.executeQueue(router2);

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

	/**
	 * @param model
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	private void executePhysicalRouters(VCPENetworkModel model) throws ResourceException, ProtocolException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);

		IResource phyResource1 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy1.getName()));
		IResource phyResource2 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy2.getName()));

		GenericHelper.executeQueue(phyResource1);
		GenericHelper.executeQueue(phyResource2);
	}

	/**
	 * @param model
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	private void executeLogicalRouters(VCPENetworkModel model) throws ResourceException, ProtocolException {
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

		IResource lrResource1 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		IResource lrResource2 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

		GenericHelper.executeQueue(lrResource1);
		GenericHelper.executeQueue(lrResource2);
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

	/**
	 * Get a deep copy of VRRPProtocolEndpoint with all necessary elements
	 * 
	 * @param originalRouter original ComputerSystem where the VRRProtocolEndpoint is
	 * @return
	 * @throws Exception
	 */
	private VRRPProtocolEndpoint getVRRPProtocolEndpointDeepCopy(ComputerSystem originalRouter) throws Exception {
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

	/**
	 * @return
	 * @throws ResourceException
	 */
	private IResourceManager getResourceManager() throws ResourceException {
		try {
			return Activator.getResourceManagerService();
		} catch (ActivatorException e) {
			throw new ResourceException("Could not find ResourceManager", e);
		}
	}

}
