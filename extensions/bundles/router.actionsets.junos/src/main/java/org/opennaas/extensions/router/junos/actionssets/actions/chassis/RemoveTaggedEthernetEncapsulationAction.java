package org.opennaas.extensions.router.junos.actionssets.actions.chassis;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants;
import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants.TargetConfiguration;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.GetNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.JunosCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;

public class RemoveTaggedEthernetEncapsulationAction extends JunosAction {

	private String	getInterfaceTemplate	= "/VM_files/getInterface.vm";
	private String	getSubInterfaceTemplate	= "/VM_files/getSubInterface.vm";

	public RemoveTaggedEthernetEncapsulationAction() {
		this.setActionID(ActionConstants.REMOVE_TAGGEDETHERNET_ENCAPSULATION);
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {

			Response getInterfaceResponse = getInterfaceFromCandidate((LogicalPort) params, protocol);
			actionResponse.addResponse(getInterfaceResponse);

			if (getInterfaceResponse.getStatus().equals(Response.Status.OK)) {

				// TODO Check params exists in the router.
				// checkInterfaceExists("", getInterfaceResponse);

				// Check params has tagged ethernet encapsulation
				if (interfaceHasTaggedEthernetEncapsulation((LogicalPort) params, getInterfaceResponse)) {

					// Check params does not have subinterfaces with configured vlan-id
					checkNoSubInterfaceWithVlanId((LogicalPort) params, getInterfaceResponse);

					// remove eth encapsulation
					EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage(), CommandNetconfConstants.NONE_OPERATION);
					command.initialize();
					actionResponse.addResponse(sendCommandToProtocol(command, protocol));
				}
			}
			validateAction(actionResponse);

		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// Nothing to parse by now. GetConfigurationAction will do it at the end of each queue execution
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (!(params instanceof LogicalPort))
			throw new ActionException("Invalid parameters type. A LogicalPort is expected");

		if (((LogicalPort) params).getName() == null || ((LogicalPort) params).getName().isEmpty())
			throw new ActionException("Invalid parameter. A LogicalPort must have a name");

		// check tagged ethernet encapsulation is supported by given interface type
		if (isLoopbackInterface((LogicalPort) params)) {
			throw new ActionException("Tagged ethernet encapsulation in loopback interfaces is not supported.");

		} else if (isLogicalTunnelInterface((LogicalPort) params)) {
			if (isPhysicalInterface((LogicalPort) params)) {
				throw new ActionException("Tagged ethernet encapsulation cannot applied to physical lt interfaces");
			}

		} else if (isEthernetInterface((LogicalPort) params)) {
			if (isLogicalInterface((LogicalPort) params)) {
				throw new ActionException("Tagged ethernet encapsulation cannot be applied to logical eth interfaces");
			}

		} else {
			throw new ActionException("Tagged ethernet encapsulation is not supported for given interface type");
		}

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {

		checkParams(getParams());

		setTemplateAccordingToParamsType((LogicalPort) params);

		String logicalRouterName = nullToEmpty(((ComputerSystem) modelToUpdate).getElementName());

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", logicalRouterName);

		try {
			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private void setTemplateAccordingToParamsType(LogicalPort params) throws ActionException {
		if (isLogicalTunnelInterface(params)) {
			setTemplate("/VM_files/removeTaggedEthEncapsulationInLT.vm");
		} else if (isEthernetInterface(params)) {
			setTemplate("/VM_files/removeTaggedEthEncapsulationInETH.vm");
		} else {
			throw new ActionException("Failed to determine Velocity template in Action " + getActionID());
		}
	}

	private boolean isPhysicalInterface(LogicalPort iface) {
		return !isLogicalInterface(iface);
	}

	private boolean isLogicalInterface(LogicalPort iface) {
		return (iface instanceof NetworkPort);
	}

	private boolean isEthernetInterface(LogicalPort iface) {
		return isEthernetInterfaceName(iface.getName());
	}

	private boolean isLogicalTunnelInterface(LogicalPort iface) {
		return isLogicalTunnelInterfaceName(iface.getName());
	}

	private boolean isLoopbackInterface(LogicalPort iface) {
		return isLoopbackInterfaceName(iface.getName());
	}

	private boolean isEthernetInterfaceName(String interfaceName) {
		return (interfaceName.startsWith("ge") || interfaceName.startsWith("fe"));
	}

	private boolean isLogicalTunnelInterfaceName(String interfaceName) {
		return (interfaceName.startsWith("lt"));
	}

	private boolean isLoopbackInterfaceName(String interfaceName) {
		return (interfaceName.startsWith("lo"));
	}

	private Response getInterfaceFromCandidate(LogicalPort iface, IProtocolSession protocol) throws ActionException, ProtocolException {

		String getInterfaceFilter = prepareGetInterfaceMessage(iface);

		JunosCommand getCommand = new GetNetconfCommand(getInterfaceFilter, TargetConfiguration.CANDIDATE);
		getCommand.initialize();
		return sendCommandToProtocol(getCommand, protocol);
	}

	private String prepareGetInterfaceMessage(LogicalPort iface) throws ActionException {

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(((ComputerSystem) getModelToUpdate()).getElementName()));

		try {
			String templateToUse;
			if (isLogicalInterface(iface)) {
				templateToUse = getSubInterfaceTemplate;
			} else {
				templateToUse = getInterfaceTemplate;
			}
			return prepareVelocityCommand(iface, templateToUse, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private void checkInterfaceExists(String ifaceName, Response getInterfaceResponse) throws ActionException {
		// Check params is in current candidate configuration
		// FIXME it may not be in candidate but exist. Not all interfaces are in config
		// It should fail only if interface does not exists in the router!
		if (getInterfaceResponse.getInformation().equals("<configuration></configuration>")) {
			// an empty configuration means filter has failed
			throw new ActionException("Interface " + ifaceName + " not found in this router configuration");
		}
	}

	private void checkNoSubInterfaceWithVlanId(LogicalPort iface, Response getInterfaceResponse) throws ActionException {

		if (isPhysicalInterface(iface) && getInterfaceResponse.getInformation().contains("vlan-id")) {
			throw new ActionException("Interface has subinterfaces with vlanId. Please remove them before changing encapsulation.");
		}
	}

	private boolean interfaceHasTaggedEthernetEncapsulation(LogicalPort iface, Response getInterfaceResponse) throws ActionException {
		if (isEthernetInterface(iface)) {
			return getInterfaceResponse.getInformation().contains("vlan-tagging");
		} else if (isLogicalTunnelInterface(iface)) {
			return getInterfaceResponse.getInformation().contains("<encapsulation>vlan</encapsulation>");
		} else {
			return false;
		}
	}

}
