package org.opennaas.extensions.ofertie.ncl.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.eclipse.jetty.http.HttpStatus;

public class ProviderOutInterceptor extends AbstractPhaseInterceptor<Message> {

	private final static Log	log					= LogFactory.getLog(ProviderOutInterceptor.class);

	private final static String	FILTER_METHOD		= HttpMethod.POST;
	private final static String	FILTER_PATH			= "/opennaas/ofertie/ncl/flows";

	private final static int	CODE_RESPONSE_201	= HttpStatus.CREATED_201;

	public ProviderOutInterceptor() {
		super(Phase.SEND);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		log.info("Intercepting Provisioner output message.");

		Message inMessage = message.getExchange().getInMessage();
		String method = (String) inMessage.get(Message.HTTP_REQUEST_METHOD);
		String path = (String) inMessage.get(Message.PATH_INFO);

		if (method.equals(FILTER_METHOD) && path.equals(FILTER_PATH)) {
			setFlowInHeader(message);
			modifyStatusCode(message, CODE_RESPONSE_201);
		}

	}

	private void setFlowInHeader(Message message) {

		String flowId = getFlowIdFromBody(message);
		String location = buildLocation(message, flowId);

		HttpServletResponse response = (HttpServletResponse)
				message.getExchange().getInMessage().get(AbstractHTTPDestination.HTTP_RESPONSE);

		response.setHeader(HttpHeaders.LOCATION, location);
	}

	private String buildLocation(Message message, String flowId) {

		HttpServletRequest request = (HttpServletRequest)
				message.getExchange().getInMessage().get(AbstractHTTPDestination.HTTP_REQUEST);

		String serverName = request.getServerName();
		int serverPort = request.getServerPort();

		String location = "http://" + serverName + ":" + String.valueOf(serverPort) + FILTER_PATH + "/" + flowId;

		return location;
	}

	private void modifyStatusCode(Message message, int status) {
		HttpServletResponse response = (HttpServletResponse)
				message.get(AbstractHTTPDestination.HTTP_RESPONSE);

		response.setStatus(status);

	}

	private String getFlowIdFromBody(Message message) {
		String flowId = null;

		MessageContentsList contents = MessageContentsList.getContentsList(message);
		if (contents != null && contents.size() == 1)
			flowId = (String) contents.get(0);

		return flowId;
	}

}
