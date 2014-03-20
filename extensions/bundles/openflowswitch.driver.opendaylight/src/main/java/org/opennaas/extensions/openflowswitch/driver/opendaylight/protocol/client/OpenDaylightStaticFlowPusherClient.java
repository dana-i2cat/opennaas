package org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.serializers.json.CustomJSONProvider;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.wrappers.OpenDaylightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

/**
 * OpenDaylight special client mixing CXF and Java clients allowing sending HTTP
 * DELETE with body
 *
 * @author logoff
 *
 */
public class OpenDaylightStaticFlowPusherClient implements IOpenDaylightStaticFlowPusherClient {

    private ProtocolSessionContext sessionContext;
    private IOpenDaylightStaticFlowPusherClient cxfClient;
    Log log = LogFactory.getLog(OpenDaylightStaticFlowPusherClient.class);

    public OpenDaylightStaticFlowPusherClient(IOpenDaylightStaticFlowPusherClient cxfClient, ProtocolSessionContext sessionContext) {
        this.cxfClient = cxfClient;
        this.sessionContext = sessionContext;
    }

    @Override
    public void addFlow(OpenDaylightOFFlow flow, String DPID, String name) throws ProtocolException, Exception {
//        cxfClient.addFlow(flow, DPID, name);
        String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
        log.error("Uri " + uri);
        try {
            // / create URI based on base path, common static flow pusher path and JSON
            URL url = new URL(uri + "/controller/nb/v2/flowprogrammer/default/node/OF/" + DPID + "/staticFlow/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            log.error("URL " + url.toString());
            String userpass = "admin:admin";
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            connection.setRequestProperty("Authorization", basicAuth);

            // override HTTP method allowing sending body
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // prepare body
            String messageBody = new CustomJSONProvider().locateMapper(OpenDaylightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).writeValueAsString(flow);

//            messageBody = "{\"installInHw\":\"true\", \"name\":\""+name+"\", \"node\": {\"id\":\""+DPID+"\", \"type\":\"OF\"}, \"ingressPort\":\"2\", \"etherType\": \"0x800\", \"protocol\": \"6\", \"tpDst\": \"81\", \"priority\":\"65535\",\"actions\":[\"DROP\"]}";
            log.error(messageBody);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            log.error("Write");
            wr.write(messageBody);
            log.error("Writing");
            wr.flush();
            wr.close();
            log.error("Finish");
            String response = connection.getResponseMessage();
            log.error("response: " + response);
            // get HTTP Response
//            String response = IOUtils.toString(connection.getInputStream(), "UTF-8");

            // verify correct JSON response
//            log.error(response);
        } catch (IOException e) {
            log.error("Exception " + e.getMessage());
            throw new ProtocolException(e);
        }
    }

    @Override
    public void deleteFlow(OpenDaylightOFFlow flow, String DPID, String name) throws ProtocolException, Exception {
        String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
        log.error("Uri " + uri);
        String response = "";
        try {
            // / create URI based on base path, common static flow pusher path and JSON
            URL url = new URL(uri + "/controller/nb/v2/flowprogrammer/default/node/OF/" + DPID + "/staticFlow/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            log.error("URL " + url.toString());
            String userpass = "admin:admin";
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            connection.setRequestProperty("Authorization", basicAuth);

            // override HTTP method allowing sending body
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // prepare body
            String messageBody = new CustomJSONProvider().locateMapper(OpenDaylightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).writeValueAsString(flow);

//            messageBody = "{\"installInHw\":\"true\", \"name\":\""+name+"\", \"node\": {\"id\":\""+DPID+"\", \"type\":\"OF\"}, \"ingressPort\":\"2\", \"etherType\": \"0x800\", \"protocol\": \"6\", \"tpDst\": \"81\", \"priority\":\"65535\",\"actions\":[\"DROP\"]}";
            log.error(messageBody);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            log.error("Write");
            wr.write(messageBody);
            log.error("Writing");
            wr.flush();
            wr.close();
            log.error("Finish");
            response = connection.getResponseMessage();
            log.error("response: " + response);
            // get HTTP Response
//            String response = IOUtils.toString(connection.getInputStream(), "UTF-8");

            // verify correct JSON response
//            log.error(response);
        } catch (IOException e) {
            log.error("Exception " + e.getMessage());
            throw new ProtocolException(e);
        }
    }

    @Override
    public OpenDaylightOFFlowsWrapper getFlows(String dpid) throws ProtocolException, Exception {
//        return cxfClient.getFlows(dpid);

        String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
        log.error("Uri " + uri);
        try {
            // / create URI based on base path, common static flow pusher path and JSON
            URL url = new URL(uri + "/controller/nb/v2/flowprogrammer/default/node/OF/" + dpid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            log.error("URL " + url.toString());
            String userpass = "admin:admin";
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            connection.setRequestProperty("Authorization", basicAuth);

            // override HTTP method allowing sending body
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
//                return response.toString();
            log.error("Finish");
            // get HTTP Response
//            String response = IOUtils.toString(connection.getInputStream(), "UTF-8");

            // verify correct JSON response
//            log.error(response);
        } catch (IOException e) {
            log.error("Exception " + e.getMessage());
            throw new ProtocolException(e);
        }
        return null;
    }

    @Override
    public Map<String, List<OpenDaylightOFFlow>> getFlows() throws ProtocolException, Exception {
        return cxfClient.getFlows();
    }

    @Override
    public void deleteFlowsForSwitch(String dpid) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteAllFlows() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
