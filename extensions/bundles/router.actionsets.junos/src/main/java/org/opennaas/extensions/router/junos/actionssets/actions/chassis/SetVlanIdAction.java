package org.opennaas.extensions.router.junos.actionssets.actions.chassis;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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
import org.opennaas.extensions.router.model.ProtocolEndpoint;

import com.google.common.collect.Iterables;

public class SetVlanIdAction extends JunosAction {

	private String	getSubInterfaceTemplate	= "/VM_files/getSubInterface.vm";

	public SetVlanIdAction() {
		this.setActionID(ActionConstants.SET_VLANID);
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {

			Response getInterfaceResponse = getInterfaceFromCandidate((LogicalPort) params, protocol);
			actionResponse.addResponse(getInterfaceResponse);

			if (getInterfaceResponse.getStatus().equals(Response.Status.OK)) {

				// Check params is in current candidate configuration
				// Not all interfaces are in config.
				// However, interfaces with tagged-ethernet should be, as they have something configured.
				checkInterfaceExists(getInterfaceResponse);

				// No need to check that params has tagged-ethernet encapsulation, router will fail if not.

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

		if (!(params instanceof NetworkPort)) {
			// this also assures it's a logical interface:
			// physical interfaces are of class LogicalPort
			// logical interfaces are of NetworkPort subclasses class.
			throw new ActionException("Invalid parameters type. A NetworkPort is expected");
		}

		if (((NetworkPort) params).getName() == null || ((NetworkPort) params).getName().isEmpty())
			throw new ActionException("Invalid parameter. A NetworkPort must have a name");

		if (isLoopbackInterface((NetworkPort) params)) {
			throw new ActionException("Vlan configuration in loopback interfaces is not supported.");
		}

		if (!isValidVlanId(obtainDesiredVlanId((NetworkPort) params))) {
			throw new ActionException("Invalid vlanId. Valid range is [0, 4096)");
		}

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {

		checkParams(params);

		setTemplateAccordingToParamsType((NetworkPort) params);

		String logicalRouterName = nullToEmpty(((ComputerSystem) modelToUpdate).getElementName());

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", logicalRouterName);
		extraParams.put("vlanId", obtainDesiredVlanId((NetworkPort) params));

		try {
			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private boolean isValidVlanId(int vlanId) {
		// The IEEE 802.1Q (VLAN) label.
		// The 12-bit value in the 802.1Q header for Tagged Ethernet.
		return (vlanId > 0 && vlanId < 4096);
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

	private int obtainDesiredVlanId(NetworkPort networkPort) throws ActionException {
		try {
			ProtocolEndpoint endpoint = Iterables.getOnlyElement(networkPort.getProtocolEndpoint());
			return Integer.parseInt(endpoint.getName()); // endpoint name stores the encapsulation label (vlanId in this case)
		} catch (NoSuchElementException e) {
			throw new ActionException("Invalid parameter. A vlanId must be specified.");
		}
	}

	private void setTemplateAccordingToParamsType(LogicalPort params) throws ActionException {
		if (isLogicalTunnelInterface(params)) {
			setTemplate("/VM_files/setVlanId.vm");
		} else if (isEthernetInterface(params)) {
			setTemplate("/VM_files/setVlanId.vm");
		} else {
			throw new ActionException("Failed to determine Velocity template in Action " + getActionID());
		}
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
			return prepareVelocityCommand(iface, getSubInterfaceTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private void checkInterfaceExists(Response getInterfaceResponse) throws ActionException {

		if (getInterfaceResponse.getInformation().equals("<configuration></configuration>")) {
			// an empty configuration means filter has failed
			throw new ActionException("Interface not found in this router");
		}
	}
}
