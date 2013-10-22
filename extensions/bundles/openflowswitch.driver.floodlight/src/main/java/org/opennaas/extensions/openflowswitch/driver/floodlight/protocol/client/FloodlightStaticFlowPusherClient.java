package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.serializers.json.CustomJSONProvider;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.wrappers.FloodlightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

/**
 * Floodlight special client mixing CXF and Java clients allowing sending HTTP DELETE with body
 * 
 * @author logoff
 * 
 */
public class FloodlightStaticFlowPusherClient implements IFloodlightStaticFlowPusherClient {

	private ProtocolSessionContext				sessionContext;
	private IFloodlightStaticFlowPusherClient	cxfClient;

	public FloodlightStaticFlowPusherClient(IFloodlightStaticFlowPusherClient cxfClient, ProtocolSessionContext sessionContext) {
		this.cxfClient = cxfClient;
		this.sessionContext = sessionContext;
	}

	@Override
	public void addFlow(FloodlightOFFlow flow) throws ProtocolException, Exception {
		cxfClient.addFlow(flow);
	}

	@Override
	public void deleteFlow(FloodlightOFFlow flow) throws ProtocolException, Exception {
		String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);

		try {
			// / create URI based on base path, common static flow pusher path and JSON
			URL url = new URL(uri + "/wm/staticflowentrypusher" + "/json");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			// override HTTP method allowing sending body
			connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
			connection.setDoOutput(true);

			// prepare body
			String messageBody = new CustomJSONProvider().locateMapper(FloodlightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).writeValueAsString(
					flow);

			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write(messageBody);
			wr.flush();

			// get HTTP Response
			String response = IOUtils.toString(connection.getInputStream(), "UTF-8");

			// verify correct JSON response
			if (!response.equals("{\"status\" : \"Entry " + flow.getName() + " deleted\"}")) {
				throw new Exception("Invalid response: " + response);
			}
		} catch (IOException e) {
			throw new ProtocolException(e);
		}

	}

	@Override
	public void deleteFlowsForSwitch(String dpid) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void deleteAllFlows() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Map<String, List<FloodlightOFFlow>> getFlows() throws ProtocolException, Exception {
		return cxfClient.getFlows();
	}

	@Override
	public FloodlightOFFlowsWrapper getFlows(String dpid) throws ProtocolException, Exception {
		return cxfClient.getFlows(dpid);
	}

}
