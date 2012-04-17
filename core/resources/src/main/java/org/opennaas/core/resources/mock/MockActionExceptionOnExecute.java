package org.opennaas.core.resources.mock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class MockActionExceptionOnExecute extends MockAction {
	private Log				log				= LogFactory.getLog(MockActionExceptionOnExecute.class);
	private ActionResponse	actionResponse	= new ActionResponse();

	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		log.info("----> Executing action: MOCK ACTION: " + actionID);
		actionResponse.setActionID(actionID);
		super.executed = true;
		throw new ActionException("ERROR");
	}
}
