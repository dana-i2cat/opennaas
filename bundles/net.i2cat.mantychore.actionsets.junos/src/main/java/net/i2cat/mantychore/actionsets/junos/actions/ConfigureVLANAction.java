package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.protocol.IProtocolSession;

public class ConfigureVLANAction extends JunosAction {

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// TODO Auto-generated method stub
		return false;
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
	public void prepareMessage() throws ActionException {
		// TODO Auto-generated method stub

	}

}
