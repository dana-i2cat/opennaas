package org.opennaas.extensions.ofnetwork.driver.internal.actionsets.actions;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.ofnetwork.Activator;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.opennaas.extensions.ofnetwork.model.OFNetworkModel;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class DeallocateFlowAction extends Action {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		NetOFFlow netFlow = (NetOFFlow) params;

		try {
			String resourceName = netFlow.getResourceId();
			IResource resource = getResourceByName(resourceName);
			IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource
					.getCapabilityByInterface(IOpenflowForwardingCapability.class);

			forwardingCapability.removeOpenflowForwardingRule(netFlow.getName());

			// update model
			((OFNetworkModel) getModelToUpdate()).getNetFlowsPerResource().get(resourceName).remove(netFlow);

		} catch (Exception e) {
			throw new ActionException("Error deallocating flow : ", e);
		}

		return ActionResponse.okResponse(getActionID());
	}

	private IResource getResourceByName(String resourceName) throws ResourceException {
		try {
			IResourceManager resourceManager = Activator.getResourceManagerService();
			return resourceManager.getResource(resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName));
		} catch (ActivatorException e) {
			throw new ResourceException("Couldn't get resource. Failed to get ResourceManagerService", e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof NetOFFlow)) {
			throw new ActionException("Action parameters must be set with a NetOFFlow");
		}

		return true;
	}

}
