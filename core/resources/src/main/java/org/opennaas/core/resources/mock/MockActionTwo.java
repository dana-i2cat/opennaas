package org.opennaas.core.resources.mock;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class MockActionTwo extends Action {
	private Log		log	= LogFactory.getLog(MockActionTwo.class);
	private String	actionID;

	@Override
	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		log.info("----> Executing action: MOCK ACTION");
		return null;
	}

	public boolean checkParams(Object params) throws ActionException {
		// TODO Auto-generated method stub
		return false;
	}

}
