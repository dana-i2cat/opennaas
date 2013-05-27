package org.opennaas.extensions.bod.actionsets.dummy.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.bod.actionsets.dummy.ActionConstants;
import org.opennaas.extensions.bod.capability.l2bod.BoDLink;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.NetworkModel;

public class RequestConnectionAction extends Action {

	static Log	log	= LogFactory.getLog(ShutDownConnectionAction.class);

	public RequestConnectionAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.REQUESTCONNECTION);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager sessionManager) throws ActionException {

		if (modelToUpdate == null) {
			modelToUpdate = new NetworkModel();
		}
		BoDLink link = new BoDLink();
		link.setName("link" + LinkIdProvider.getInstance().getNewId());
		link.setRequestParameters((RequestConnectionParameters) params);
		((NetworkModel) modelToUpdate).getNetworkElements().add(link);

		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(this.actionID);
		actionResponse.addResponse(Response.okResponse("Correct! I do nothing"));
		actionResponse.setStatus(STATUS.OK);
		return actionResponse;
	}

	@Override
	public boolean checkParams(Object arg0) throws ActionException {
		return true;
	}

}