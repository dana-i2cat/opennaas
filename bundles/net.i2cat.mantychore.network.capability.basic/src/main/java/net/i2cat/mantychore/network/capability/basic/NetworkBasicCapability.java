package net.i2cat.mantychore.network.capability.basic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class NetworkBasicCapability extends AbstractCapability {

	public static final String	CAPABILITY_NAME	= "basicNetwork";

	Log							log				= LogFactory.getLog(NetworkBasicCapability.class);

	private String				resourceId		= "";

	public NetworkBasicCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Network Basic Capability");
	}

	@Override
	public Object sendMessage(String idOperation, Object paramsModel) throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// FIXME obtain actionSet dynamically
		return new NetActionSet();
	}

	@Override
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void activateCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deactivateCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

}
