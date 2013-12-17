package org.opennaas.extensions.sdnnetwork.capability.monitoring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.sdnnetwork.Activator;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.model.NetworkStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class MonitoringNetworkCapability extends AbstractCapability implements IMonitoringNetworkCapability {

	public static final String	CAPABILITY_TYPE	= "netmonitoring";

	Log							log				= LogFactory.getLog(MonitoringNetworkCapability.class);

	private String				resourceId		= "";

	public MonitoringNetworkCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Monitoring Network Capability");
	}

	// //////////////////////////////////// //
	// IMonitoringNetworkCapability Methods //
	// //////////////////////////////////// //

	@Override
	public NetworkStatistics getNetworkStatistics() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	// ////////////////////////// //
	// AbstractCapability Methods //
	// ////////////////////////// //

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");

	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getActionSetService(OFProvisioningNetworkCapability.CAPABILITY_TYPE, name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				IMonitoringNetworkCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

}
