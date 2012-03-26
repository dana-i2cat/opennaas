package net.i2cat.mantychore.actionsets.junos.actions.chassis;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.JunosAction;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

import com.google.common.collect.Iterables;

public class SetVlanIdAction extends JunosAction {

	public SetVlanIdAction() {
		this.setActionID(ActionConstants.SET_VLANID);
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		// TODO Check params is in current candidate configuration

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
			setTemplate("/VM_files/setVlanIdInLT.vm");
		} else if (isEthernetInterface(params)) {
			setTemplate("/VM_files/setVlanIdInETH.vm");
		} else {
			throw new ActionException("Failed to determine Velocity template in Action " + getActionID());
		}
	}

}
