package org.opennaas.extensions.router.junos.actionssets.actions.vrrp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

public class UpdateVRRPPriorityAction extends JunosAction {
	Log	log	= LogFactory.getLog(UpdateVRRPPriorityAction.class);

	public UpdateVRRPPriorityAction() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {
		this.setActionID(ActionConstants.VRRP_UPDATE_PRIORITY);
		setTemplate("/VM_files/updateVRRPPriority.vm");
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
		// correct instance received
		if (!(params instanceof VRRPProtocolEndpoint)) {
			return false;
		} else {
			List<ProtocolEndpoint> protocolEndpoints = ((VRRPProtocolEndpoint) params).getBindedProtocolEndpoints();
			// VRRPProtocolEndpoint has 1 ProtocolEndpoint
			if (protocolEndpoints.size() != 1) {
				return false;
			} else {
				// protocolEndpoint is an instance of IPProtocolEndpoint
				ProtocolEndpoint protocolEndpoint = protocolEndpoints.get(0);
				if (!(protocolEndpoint instanceof IPProtocolEndpoint)) {
					return false;
				} else {
					// protocolEndpoint has 1 LogicalPort
					List<LogicalPort> logicalPorts = ((IPProtocolEndpoint) protocolEndpoint).getLogicalPorts();
					if (logicalPorts.size() != 1) {
						return false;
					} else {
						// logicalPort is an instance of NetworkPort
						LogicalPort logicalPort = logicalPorts.get(0);
						if (!(logicalPort instanceof NetworkPort)) {
							return false;
						} else {
							// service is an instance of VRRPGroup
							Service service = ((VRRPProtocolEndpoint) params).getService();
							if (!(service instanceof VRRPGroup)) {
								return false;
							}
						}
					}
				}
			}
		}
		// structure correct
		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
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

			// IP address and subnet mask of interface
			IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) ((VRRPProtocolEndpoint) params).getBindedProtocolEndpoints().get(0);
			String ipAddress = ipProtocolEndpoint.getIPv4Address();
			extraParams.put("ipAddress", ipAddress);
			String subnetMask = IPUtilsHelper.parseLongToShortIpv4NetMask(ipProtocolEndpoint.getSubnetMask());
			extraParams.put("subnetMask", subnetMask);

			// router interface
			NetworkPort networkInterface = (NetworkPort) ipProtocolEndpoint.getLogicalPorts().get(0);
			extraParams.put("networkInterface", networkInterface);

			// VRRPGroup
			VRRPGroup vrrpGroup = (VRRPGroup) ((VRRPProtocolEndpoint) params).getService();
			extraParams.put("vrrpGroup", vrrpGroup);

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}
}
