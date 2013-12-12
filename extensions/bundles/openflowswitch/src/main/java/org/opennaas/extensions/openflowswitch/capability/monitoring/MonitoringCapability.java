package org.opennaas.extensions.openflowswitch.capability.monitoring;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.repository.Activator;

/**
 * {@link IMonitoringCapability} implementation
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class MonitoringCapability extends AbstractCapability implements IMonitoringCapability {

	public static String	CAPABILITY_TYPE	= "monitoring";

	private Log				log				= LogFactory.getLog(MonitoringCapability.class);

	private String			resourceId		= "";

	public MonitoringCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Monitoring Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IMonitoringCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public Map<Integer, PortStatistics> getPortStatistics() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
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
			return Activator.getMonitoringActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);

		}
	}

	private ActionResponse executeAction(IAction action) throws CapabilityException {
		ActionResponse response;
		try {
			IProtocolManager protocolManager = Activator.getProtocolManagerService();
			IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(this.resourceId);

			response = action.execute(protocolSessionManager);

		} catch (ProtocolException pe) {
			log.error("Error with protocol session - " + pe.getMessage());
			throw new CapabilityException(pe);
		} catch (ActivatorException ae) {
			String errorMsg = "Error getting protocol manager - " + ae.getMessage();
			log.error(errorMsg);
			throw new CapabilityException(errorMsg, ae);
		} catch (ActionException ae) {
			log.error("Error executing " + action.getActionID() + " action - " + ae.getMessage());
			throw (ae);
		}

		if (!response.getStatus().equals(ActionResponse.STATUS.OK)) {
			String errMsg = "Error executing " + action.getActionID() + " action - " + response.getInformation();
			log.error(errMsg);
			throw new ActionException(errMsg);
		}
		return response;
	}

}
