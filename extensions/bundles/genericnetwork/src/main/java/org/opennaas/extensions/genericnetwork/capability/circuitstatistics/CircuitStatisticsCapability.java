package org.opennaas.extensions.genericnetwork.capability.circuitstatistics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.model.TimePeriod;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CircuitStatisticsCapability extends AbstractCapability implements ICircuitStatisticsCapability {

	public static final String	CAPABILITY_TYPE	= "circuitstatistics";

	private Log					log				= LogFactory.getLog(CircuitStatisticsCapability.class);

	private String				resourceId		= "";

	public CircuitStatisticsCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Circuit Statistics Capability");
	}

	@Override
	public String getCapabilityName() {
		return this.CAPABILITY_TYPE;
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				CircuitStatisticsCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	// ############################################
	// ### ICircuitStatisticsCapability methods ###
	// ############################################

	@Override
	public void reportStatistics(String csvStatistics) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStatistics(TimePeriod timePeriod) {
		// TODO Auto-generated method stub
		return null;
	}

}
