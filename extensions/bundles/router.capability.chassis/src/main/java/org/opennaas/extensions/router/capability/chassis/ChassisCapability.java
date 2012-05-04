package org.opennaas.extensions.router.capability.chassis;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerService;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;

public class ChassisCapability extends AbstractCapability {

	public final static String	CHASSIS		= "chassis";

	Log							log			= LogFactory.getLog(ChassisCapability.class);

	private String				resourceId	= "";

	public ChassisCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Chassis Capability");
	}

	@Override
	protected void initializeCapability() throws CapabilityException {

	}

	@Override
	public Object sendMessage(String idOperation, Object params) {
		log.debug("Sending message to Chassis Capability");

		// FIXME temporary hack, should be removed when this capability get its own interface
		if (idOperation.equals(ActionConstants.SETENCAPSULATION)) {
			return uncheckedSetEncapsulation(params);
		} else if (idOperation.equals(ActionConstants.SETENCAPSULATIONLABEL)) {
			return uncheckedSetEncapsulationLabel(params);
		}

		try {
			IQueueManagerService queueManager = Activator.getQueueManagerService(resourceId);
			IAction action = createAction(idOperation);

			action.setParams(params);
			action.setModelToUpdate(resource.getModel());
			queueManager.queueAction(action);

		} catch (Exception e) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
			return Response.errorResponse(idOperation, errorMsgs);
		}

		return Response.queuedResponse(idOperation);
	}

	@Override
	protected void activateCapability() throws CapabilityException {

	}

	@Override
	protected void deactivateCapability() throws CapabilityException {

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {

	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getChassisActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	private Response prepareErrorMessage(String nameError, String message) {
		Vector<String> errorMsgs = new Vector<String>();
		errorMsgs.add(message);
		return Response.errorResponse(nameError, errorMsgs);

	}

	private Response uncheckedSetEncapsulation(Object params) {
		if (!(params instanceof LogicalPort)) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs.add("Invalid parameter. LogicalPort is expected.");
			return Response.errorResponse(ActionConstants.SETENCAPSULATION, errorMsgs);
		} else {
			return setEncapsulation((LogicalPort) params);
		}
	}

	/**
	 * This method is likely to be part of this capability interface implementation, when it gets an interface!!
	 * 
	 * @param port
	 * @return
	 */
	private Response setEncapsulation(LogicalPort port) {

		Response removeResponse = removeCurrentEncapsulation(port);

		if (removeResponse.getStatus().equals(Status.ERROR))
			return removeResponse;

		Response setEncapsulationResponse = setDesiredEncapsulation(port);
		return setEncapsulationResponse;
	}

	private Response uncheckedSetEncapsulationLabel(Object params) {
		if (!(params instanceof LogicalPort)) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs.add("Invalid parameter. LogicalPort is expected.");
			return Response.errorResponse(ActionConstants.SETENCAPSULATIONLABEL, errorMsgs);
		} else {
			return setEncapsulationLabel((LogicalPort) params);
		}
	}

	/**
	 * This method is likely to be part of this capability interface implementation, when it gets an interface!!
	 * 
	 * @param port
	 * @return
	 */
	private Response setEncapsulationLabel(LogicalPort port) {
		// FIXME it assumes there is only TAGGED_ETHERNET and NO encapsulation
		return (Response) sendMessage(ActionConstants.SET_VLANID, port);
	}

	private boolean requiresTaggedEthernetEncapsulation(LogicalPort port) {
		boolean hasTaggedEthernetEndpoint = false;
		for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
			if (endpoint.getProtocolIFType().equals(ProtocolIFType.LAYER_2_VLAN_USING_802_1Q)) {
				hasTaggedEthernetEndpoint = true;
				break;
			}
		}
		return hasTaggedEthernetEndpoint;
	}

	private boolean requiresNoEncapsulation(LogicalPort port) {
		return port.getProtocolEndpoint().isEmpty();
	}

	private Response removeCurrentEncapsulation(LogicalPort port) {
		// FIXME it assumes there is only TAGGED_ETHERNET and NO encapsulation

		if (requiresTaggedEthernetEncapsulation(port)) {
			// nothing to remove, as current can only be no encapsulation or required one
			return Response.okResponse("removeEncapsulation");
		} else {
			// it is save to remove TAGGED_ETHERNET, as current can only be tagged or none
			// and removing when it does not exists does not fail
			return (Response) sendMessage(ActionConstants.REMOVE_TAGGEDETHERNET_ENCAPSULATION, port);
		}
	}

	private Response setDesiredEncapsulation(LogicalPort port) {
		Response toReturn;
		if (requiresNoEncapsulation(port)) {
			// nothing to set
			toReturn = Response.okResponse(ActionConstants.SETENCAPSULATION);
		} else {
			if (requiresTaggedEthernetEncapsulation(port)) {
				toReturn = (Response) sendMessage(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION, port);
			} else {
				Vector<String> errorMsgs = new Vector<String>();
				errorMsgs.add("Unsupported encapsulation type");
				toReturn = Response.errorResponse(ActionConstants.SETENCAPSULATION, errorMsgs);
			}
		}
		return toReturn;
	}

}
