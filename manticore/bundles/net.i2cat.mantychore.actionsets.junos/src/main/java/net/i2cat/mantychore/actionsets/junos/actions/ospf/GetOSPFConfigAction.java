package net.i2cat.mantychore.actionsets.junos.actions.ospf;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.JunosAction;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class GetOSPFConfigAction extends JunosAction {

	/**
	 * 
	 */
	public GetOSPFConfigAction() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {
		setActionID(ActionConstants.OSPF_GET_CONFIGURATION);
		setTemplate("/VM_files/getOSPFConfiguration.vm");
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
