package org.opennaas.extensions.genericnetwork.capability.nclprovisioner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.genericnetwork.model.path.PathRequest;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */

public class NCLProvisionerCapability extends AbstractCapability implements INCLProvisionerCapability {

	public static final String	CAPABILITY_TYPE	= "nclprovisioner";

	private Log					log				= LogFactory.getLog(NCLProvisionerCapability.class);
	private String				resourceId		= "";

	public NCLProvisionerCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new NCLProvisioner Capability");

	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public String allocateFlow(PathRequest pathRequest) throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("This capability does not contain actionset.");

	}

}
