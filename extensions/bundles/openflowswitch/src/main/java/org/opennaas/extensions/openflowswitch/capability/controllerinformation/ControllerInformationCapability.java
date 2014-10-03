package org.opennaas.extensions.openflowswitch.capability.controllerinformation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.HealthState;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.MemoryUsage;
import org.opennaas.extensions.openflowswitch.repository.Activator;

public class ControllerInformationCapability extends AbstractCapability implements IControllerInformationCapability {

	public static String	CAPABILITY_TYPE	= "controllerinformation";

	private Log				log				= LogFactory.getLog(ControllerInformationCapability.class);

	private String			resourceId		= "";

	public ControllerInformationCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Controller Information Capability");
	}

	@Override
	public String getCapabilityName() {
		return this.CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getControllerInformationActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);

		}
	}

	// ###############################################
	// ### IControllerInformationCapability methods ###
	// ###############################################

	@Override
	public HealthState getHealthState() throws CapabilityException {
		return null;

	}

	@Override
	public MemoryUsage getControllerMemoryUsage() {

		return null;
	}

}
