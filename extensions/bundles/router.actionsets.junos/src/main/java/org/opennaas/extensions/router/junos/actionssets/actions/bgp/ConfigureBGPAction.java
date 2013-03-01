package org.opennaas.extensions.router.junos.actionssets.actions.bgp;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.BGPAction;
import org.opennaas.extensions.router.model.BasicAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.FilterEntry;
import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.PacketFilterCondition;
import org.opennaas.extensions.router.model.PrefixListFilterEntry;
import org.opennaas.extensions.router.model.RouteFilterEntry;
import org.opennaas.extensions.router.model.utils.BGPUtils;

public class ConfigureBGPAction extends JunosAction {

	private String	bgpTemplate		= "/VM_files/configureBGP.vm";
	private String	policyTemplate	= "/VM_files/configurePolicyOptions.vm";

	public ConfigureBGPAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.CONFIGURE_BGP);
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		actionResponse.addResponse(execEditCommand(prepareConfigBGPMessage(), protocol));
		actionResponse.addResponse(execEditCommand(prepareConfigPolicyOptionsMessage(), protocol));

		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// TODO Auto-generated method stub

	}

	/**
	 * Params must be a BGPService.
	 * 
	 * @param param
	 *            : BGPService to configure
	 * @throws ActionException
	 *             if params is null or is not a BGPService
	 */
	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || !(params instanceof ComputerSystem))
			throw new ActionException("Invalid parameters for action " + getActionID());

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		checkParams(params);
		// Nothing to prepare
		// Messages are prepared in executeListCommand
	}

	private Response execEditCommand(String netconfMsg, IProtocolSession protocol) throws ActionException {
		try {
			EditNetconfCommand command = new EditNetconfCommand(netconfMsg);
			command.initialize();
			return sendCommandToProtocol(command, protocol);
		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
	}

	private String prepareConfigBGPMessage() throws ActionException {

		boolean isLogical = false;
		String lrName = null;
		if (((ComputerSystem) modelToUpdate).getElementName() != null) {
			// is logicalRouter, add LRName param
			isLogical = true;
			lrName = ((ComputerSystem) modelToUpdate).getElementName();
			((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
			// TODO If we don't have a ManagedElement initialized
		}

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("bgpUtils", new BGPUtils());
		extraParams.put("isLR", isLogical);
		if (isLogical)
			extraParams.put("lrName", lrName);

		try {
			return XmlHelper.formatXML(prepareVelocityCommand(params, bgpTemplate, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String prepareConfigPolicyOptionsMessage() throws ActionException {

		boolean isLogical = false;
		String lrName = null;
		if (((ComputerSystem) modelToUpdate).getElementName() != null) {
			// is logicalRouter, add LRName param
			isLogical = true;
			lrName = ((ComputerSystem) modelToUpdate).getElementName();
			((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
			// TODO If we don't have a ManagedElement initialized
		}

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("bgpUtils", new BGPUtils());
		extraParams.put("isLR", isLogical);
		extraParams.put("packetFilterCondition", new PacketFilterCondition());
		extraParams.put("filterEntry", new FilterEntry());
		extraParams.put("routeFilterEntry", new RouteFilterEntry());
		extraParams.put("prefixListFilterEntry", new PrefixListFilterEntry());
		extraParams.put("basicAction", new BasicAction());
		extraParams.put("bgpAction", new BGPAction());
		// extraParams.put("trafficType", FilterEntry.TrafficType.ANY);
		// extraParams.put("matchConditionType", FilterEntry.MatchConditionType.ANY);
		// extraParams.put("bgpActionAction", BGPAction.Action.ORIGIN);
		// extraParams.put("basicActionAction", BasicAction.Action.PERMIT);

		if (isLogical)
			extraParams.put("lrName", lrName);

		try {
			return XmlHelper.formatXML(prepareVelocityCommand(params, policyTemplate, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

}
