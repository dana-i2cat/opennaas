package org.opennaas.extensions.vcpe.capability.vrrp;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.GenericHelper;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPEVRRPCapability extends AbstractCapability implements IVCPEVRRPCapability {

	public static final String	CAPABILITY_TYPE	= "vcpenet_vrrp";
	private Log					log				= LogFactory.getLog(VCPEVRRPCapability.class);
	private String				resourceId;

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public VCPEVRRPCapability(CapabilityDescriptor descriptor, String resourceId) {
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
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IVCPEVRRPCapability.class.getName());
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
	 * @see org.opennaas.extensions.vcpe.capability.vrrp.IVCPEVRRPCapability#updateVRRPIp(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public void updateVRRPIp(VCPENetworkModel vcpeModel) throws CapabilityException {
		log.info("Updating VRRP ip");
		try {
			Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(vcpeModel, VCPETemplate.VCPE1_ROUTER);
			Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(vcpeModel, VCPETemplate.VCPE2_ROUTER);

			IResource router1 = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
			IResource router2 = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

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
	 * @see org.opennaas.extensions.vcpe.capability.vrrp.IVCPEVRRPCapability#changeVRRPPriority(org.opennaas.extensions.vcpe.model.VCPENetworkModel )
	 */
	@Override
	public VCPENetworkModel changeVRRPPriority(VCPENetworkModel model) throws CapabilityException {
		log.debug("Change the priority VRRP");
		try {
			Router lr1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
			Router lr2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);

			IResource router1 = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
			IResource router2 = GenericHelper.getResourceManager().getResource(
					GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

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
			IResource vcpeResource = GenericHelper.getResourceManager().getResourceById(resourceId);
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
	 * Get a deep copy of VRRPProtocolEndpoint with all necessary elements
	 * 
	 * @param originalRouter
	 *            original ComputerSystem where the VRRProtocolEndpoint is
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
}
