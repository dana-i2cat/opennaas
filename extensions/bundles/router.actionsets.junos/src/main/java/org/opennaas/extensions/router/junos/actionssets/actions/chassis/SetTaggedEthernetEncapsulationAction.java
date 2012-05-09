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
import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants.TargetConfiguration;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.GetNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.JunosCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;

public class SetTaggedEthernetEncapsulationAction extends JunosAction {

	private String	getInterfaceTemplate	= "/VM_files/getInterface.vm";
	private String	getSubInterfaceTemplate	= "/VM_files/getSubInterface.vm";

	public SetTaggedEthernetEncapsulationAction() {
		this.setActionID(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION);
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

				// Check params does not have subinterfaces without configured vlan-id
				checkNoSubInterfacesWithoutVlanId((LogicalPort) params, getInterfaceResponse);

				// set eth encapsulation
				EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage());
				command.initialize();
				actionResponse.addResponse(sendCommandToProtocol(command, protocol));
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
				throw new ActionException("Tagged ethernet encapsulation cannot be applied to physical lt interfaces");
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
			setTemplate("/VM_files/setTaggedEthEncapsulationInLT.vm");
		} else if (isEthernetInterface(params)) {
			setTemplate("/VM_files/setTaggedEthEncapsulationInETH.vm");
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
			throw new ActionException("Interface " + ifaceName + " not found in this router");
		}
	}

	private void checkNoSubInterfacesWithoutVlanId(LogicalPort iface, Response getInterfaceResponse) throws ActionException {

		// there should be a vlan-id per unit
		int unitCount = occurrences(getInterfaceResponse.getInformation(), "<unit>");
		int vlanCount = occurrences(getInterfaceResponse.getInformation(), "<vlan-id>");

		int expectedVlanCount;
		if (isLogicalInterface(iface)) {
			// given interface unit will be present, but its vlanId may not
			expectedVlanCount = unitCount - 1;
		} else {
			expectedVlanCount = unitCount;
		}

		// vlanCount can be > expected, in the case isLogicalInterface(iface) and iface has a vlanId
		if (vlanCount < expectedVlanCount) {
			throw new ActionException("Interface has subinterfaces without vlanId. Please remove them before changing encapsulation.");
		}
	}

	private static int occurrences(String base, String toFind) {
		int count = 0;
		int index = 0;
		while (index < base.length() && index != -1) {
			index = base.indexOf(toFind, index);
			if (index != -1) {
				count++;
				index += toFind.length();
			}
		}
		return count;
	}

}
