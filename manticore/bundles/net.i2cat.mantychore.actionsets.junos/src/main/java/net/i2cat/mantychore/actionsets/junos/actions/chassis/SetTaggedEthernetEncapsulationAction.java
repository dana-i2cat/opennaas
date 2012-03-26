package net.i2cat.mantychore.actionsets.junos.actions.chassis;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.JunosAction;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class SetTaggedEthernetEncapsulationAction extends JunosAction {

	public SetTaggedEthernetEncapsulationAction() {
		this.setActionID(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION);
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		// TODO Check params is in current candidate configuration

		// TODO Check params does not have subinterfaces without configured vlan-id

		EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage());
		command.initialize();

		try {
			actionResponse.addResponse(sendCommandToProtocol(command, protocol));
		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
		validateAction(actionResponse);
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

}
