package org.opennaas.extensions.sdnnetwork.capability.ofprovision;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class OFProvisioningNetworkCapability extends AbstractCapability implements IOFProvisioningNetworkCapability {
	
	public static String	CAPABILITY_TYPE	= "ofprovisionnet";
	
	Log						log				= LogFactory.getLog(OFProvisioningNetworkCapabilityFactory.class);
	
	private String			resourceId		= "";

	public OFProvisioningNetworkCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Openflow Provisioning Network Capability");
	}
	
	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

}
