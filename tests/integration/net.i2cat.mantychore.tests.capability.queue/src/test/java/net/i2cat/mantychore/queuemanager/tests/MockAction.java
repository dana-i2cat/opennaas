package net.i2cat.mantychore.queuemanager.tests;

import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockAction extends Action {
	private Log		log	= LogFactory.getLog(MockAction.class);
	private String	actionID;

	@Override
	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		log.info("----> Executing action: MOCK ACTION");
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(actionID);
		return actionResponse;
	}

	@Override
	public boolean checkParams(Object arg0) throws ActionException {
		// TODO Auto-generated method stub
		return false;
	}

}
