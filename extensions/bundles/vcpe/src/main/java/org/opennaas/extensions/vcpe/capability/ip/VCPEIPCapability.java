package org.opennaas.extensions.vcpe.capability.ip;

import static com.google.common.collect.Iterables.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.GenericHelper;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPEIPCapability extends AbstractCapability implements IVCPEIPCapability {

	public static final String	CAPABILITY_TYPE	= "vcpenet_ip";
	private Log					log				= LogFactory.getLog(VCPEIPCapability.class);
	private String				resourceId;

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public VCPEIPCapability(CapabilityDescriptor descriptor, String resourceId) {
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
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IVCPEIPCapability.class.getName());
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
	 * @see org.opennaas.extensions.vcpe.capability.ip.IVCPEIPCapability#updateIps(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
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

	/**
	 * @param model
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	private void executePhysicalRouters(VCPENetworkModel model) throws ResourceException, ProtocolException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);

		IResource phyResource1 = GenericHelper.getResourceManager().getResource(
				GenericHelper.getResourceManager().getIdentifierFromResourceName("router", phy1.getName()));
		IResource phyResource2 = GenericHelper.getResourceManager().getResource(
				GenericHelper.getResourceManager().getIdentifierFromResourceName("router", phy2.getName()));

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

		IResource lrResource1 = GenericHelper.getResourceManager().getResource(
				GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr1.getName()));
		IResource lrResource2 = GenericHelper.getResourceManager().getResource(
				GenericHelper.getResourceManager().getIdentifierFromResourceName("router", lr2.getName()));

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
		IResource routerResource = GenericHelper.getResourceManager().getResource(
				GenericHelper.getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		IIPCapability capability = (IIPCapability) routerResource.getCapabilityByInterface(IIPCapability.class);

		LogicalPort port = VCPEToRouterModelTranslator.vCPEInterfaceToLogicalPort(iface, model);
		IPProtocolEndpoint ipPEP = VCPEToRouterModelTranslator.ipAddressToProtocolEndpoint(ipAddress);
		capability.setIPv4(port, ipPEP);
	}

}
