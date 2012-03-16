package org.opennaas.network.capability.ospf;

import net.i2cat.mantychore.model.EnabledLogicalElement.EnabledState;
import net.i2cat.mantychore.model.OSPFService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class NetOSPFCapability extends AbstractCapability implements INetOSPFService {

	public static String	CAPABILITY_NAME	= "netospf";

	Log						log				= LogFactory.getLog(NetOSPFCapability.class);

	private String			resourceId		= "";

	/**
	 * NetOSPFCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public NetOSPFCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new netospf capability");
	}

	@Override
	public Object sendMessage(String idOperation, Object params) throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response activateOSPF() throws CapabilityException {

		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.ENABLED);
		// return (Response) sendMessage(ActionConstants.NET_OSPF_ACTIVATE, service);
		return null;
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
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}
}