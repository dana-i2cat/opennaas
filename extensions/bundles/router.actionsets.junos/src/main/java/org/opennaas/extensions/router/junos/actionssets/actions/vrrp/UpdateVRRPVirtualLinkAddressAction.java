package org.opennaas.extensions.router.junos.actionssets.actions.vrrp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

public class UpdateVRRPVirtualLinkAddressAction extends JunosAction {

	private static final String	VELOCITY_TEMPLATE	= "/VM_files/updateVRRPVirtualLinkAddress.vm";

	public UpdateVRRPVirtualLinkAddressAction() {
		super();
		this.template = VELOCITY_TEMPLATE;
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {
		this.setActionID(ActionConstants.VRRP_UPDATE_VIRTUAL_LINK_ADDRESS);
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage());
			command.initialize();
			actionResponse.addResponse(sendCommandToProtocol(command, protocol));
		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
		validateAction(actionResponse);

	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null) {
			return false;
		}
		// Param is not instance of VRRPGroup
		if (!(params instanceof VRRPGroup))
			return false;

		VRRPGroup group = (VRRPGroup) params;

		// if virtual link address is not ipv6
		if (group.getVirtualLinkAddress().isEmpty() || IPUtilsHelper.validateIpAddressPattern(group.getVirtualLinkAddress()))
			return false;

		// VRRPGroup must contain VRRPProtocolEndpoint
		if (group.getProtocolEndpoint().isEmpty())
			return false;

		if (!(group.getProtocolEndpoint().get(0) instanceof VRRPProtocolEndpoint))
			return false;

		VRRPProtocolEndpoint vE = (VRRPProtocolEndpoint) group.getProtocolEndpoint().get(0);

		// ProtocolIFtype not set or not correct.
		if (vE.getProtocolIFType() == null)
			return false;

		if (!vE.getProtocolIFType().equals(ProtocolIFType.IPV6))
			return false;

		List<ProtocolEndpoint> protocolEndpoints = vE.getBindedProtocolEndpoints();
		// VRRPProtocolEndpoint has 1 ProtocolEndpoint
		if (protocolEndpoints.size() != 1)
			return false;
		// protocolEndpoint is an instance of IPProtocolEndpoint
		ProtocolEndpoint protocolEndpoint = protocolEndpoints.get(0);
		if (!(protocolEndpoint instanceof IPProtocolEndpoint))
			return false;

		// ProtocolEndpoint and VRRPProtocolEndpoint with different protocols.
		if (protocolEndpoint.getProtocolIFType() == null || !protocolEndpoint.getProtocolIFType().equals(vE.getProtocolIFType()))
			return false;

		// protocolEndpoint has 1 LogicalPort
		List<LogicalPort> logicalPorts = ((IPProtocolEndpoint) protocolEndpoint).getLogicalPorts();
		if (logicalPorts.size() != 1)
			return false;
		// logicalPort is an instance of NetworkPort
		LogicalPort logicalPort = logicalPorts.get(0);
		if (!(logicalPort instanceof NetworkPort))
			return false;

		// structure correct
		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {

		if (!checkParams(params)) {
			throw new ActionException("Params for updateVRRPVirtualLinkAddres are not valid");
		}

		try {
			Map<String, Object> extraParams = new HashMap<String, Object>();

			// physical or logical router
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
				// TODO If we don't have a ManagedElement initialized
			} else if (params != null && params instanceof ManagedElement && ((ManagedElement) params).getElementName() == null) {
				((ManagedElement) params).setElementName("");
			}

			// VRRPGroup
			VRRPGroup group = (VRRPGroup) params;
			IPProtocolEndpoint pE = (IPProtocolEndpoint) group.getProtocolEndpoint().get(0).getBindedProtocolEndpoints().get(0);
			NetworkPort netPort = (NetworkPort) pE.getLogicalPorts().get(0);
			extraParams.put("ipProtocolEndpoint", pE);
			extraParams.put("networkPort", netPort);

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);

		}
	}
}
