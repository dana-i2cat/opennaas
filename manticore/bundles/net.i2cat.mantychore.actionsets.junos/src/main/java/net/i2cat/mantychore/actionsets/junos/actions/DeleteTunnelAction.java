package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class DeleteTunnelAction extends JunosAction {

	public DeleteTunnelAction() {
		super();
		initialize();
	}

	public void initialize() {
		this.setActionID(ActionConstants.DELETETUNNEL);
		setTemplate("");
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void prepareMessage() throws ActionException {
		// TODO Auto-generated method stub

	}
}