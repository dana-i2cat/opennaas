package org.opennaas.extensions.vcpe.capability.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder;
import org.opennaas.extensions.vcpe.capability.builder.builders.VCPENetworkBuilderFactory;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

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

}
