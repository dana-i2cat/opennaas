package org.opennaas.itests.core.queue;

import java.util.Vector;

import org.opennaas.extensions.protocols.netconf.NetconfProtocolSession;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CorruptedAction extends Action {
	private Log		log	= LogFactory.getLog(CorruptedAction.class);
	private String	actionID;

	@Override
	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		Response response = null;
		try {

			/*
			 * IT INCLUDES AN ERROR IN THE MASK
			 */
			String netconfXML = "<configuration><interfaces>" +
					"<interface>" +
					"<name>fe-0/1/2</name>" +
					"<unit operation=\"replace\">" +
					"<name>2<name>" +
					"<family><inet><address>192.168.1.3/60</address></inet></family>" +
					"</unit>" +
					"</interface></interfaces></configuration>";

			NetconfProtocolSession protocol = (NetconfProtocolSession) protocolSessionManager.obtainSessionByProtocol("netconf", false);
			Query query = QueryFactory.newEditConfig("candidate", null, null, null, netconfXML);
			Reply reply = (Reply) protocol.sendReceive(query);

			// extra control, it checks if is not null the error list
			response = checkResponse(reply, query);
			validateResponse(response);

		} catch (ProtocolException e) {
			throw new ActionException(e);
		}

		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(actionID);
		actionResponse.addResponse(response);
		return actionResponse;
	}

	public Response checkResponse(Object resp, Query query) {
		// Check if is it a wellformed reply message
		if (!(resp instanceof Reply)) {
			Vector<String> errors = new Vector<String>();
			errors.add("The response message is badformed. It is not a reply message");
			return Response.errorResponse(query.toXML(), errors);
		}
		Reply reply = (Reply) resp;
		// extra control, it checks if is not null the error list
		if (reply.isOk() || reply.getErrors() == null
				|| reply.getErrors().size() == 0) {
			// BUILD OK RESPONSE
			Response response = Response.okResponse(query.toXML());
			response.setInformation(reply.getContain());
			return response;
		} else {
			// BUILD ERROR MESSAGE
			Vector<String> errors = new Vector<String>();
			for (Error error : reply.getErrors())
				errors.add(error.getMessage() + " : " + error.getInfo());
			return Response.errorResponse(query.toXML(), errors);
		}
	}

	private void validateResponse(Response response) throws ActionException {

		if (response.getErrors() != null && response.getErrors().size() > 0) {
			String listErrors = new String();
			for (String error : response.getErrors())
				listErrors += "-" + error + "\n";
			ActionException actionException = new ActionException(listErrors);
			throw actionException;
		}
		if (!response.getStatus().equals(Response.Status.OK))
			throw new ActionException();

	}

	@Override
	public boolean checkParams(Object arg0) throws ActionException {
		return false;
	}

}
