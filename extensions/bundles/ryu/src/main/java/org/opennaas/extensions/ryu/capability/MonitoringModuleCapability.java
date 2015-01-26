package org.opennaas.extensions.ryu.capability;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class MonitoringModuleCapability extends AbstractCapability implements IMonitoringModuleCapability {

	public static String	CAPABILITY_TYPE	= "monitoringmodule";

	Log						log				= LogFactory.getLog(MonitoringModuleCapability.class);

	private String			resourceId		= "";

	public MonitoringModuleCapability(CapabilityDescriptor descriptor, String resourceId) {

		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new VNMapping Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("MonitoringModuleCapability has no actionset.");

	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("MonitoringModuleCapability has no queue.");

	}

}
