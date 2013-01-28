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
