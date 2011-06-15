package net.i2cat.mantychore.commandskaraf;

import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DummyAction extends Action {

	static Log	log	= LogFactory.getLog(DummyAction.class);

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		log.info("Executing DUMMY ACTION");
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(getActionID());

		Response response = Response.okResponse("DUMMY");
		actionResponse.addResponse(response);

		return actionResponse;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

}
