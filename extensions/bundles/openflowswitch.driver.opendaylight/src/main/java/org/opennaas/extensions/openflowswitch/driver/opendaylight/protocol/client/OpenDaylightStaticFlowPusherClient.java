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
import org.opennaas.extensions.openflowswitch.model.OFFlow;
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
        cxfClient.addFlow(flow, DPID, name);
    }

    @Override
    public void deleteFlow(String DPID, String name) throws ProtocolException, Exception {
        cxfClient.deleteFlow(DPID, name);
    }

    @Override
    public OpenDaylightOFFlowsWrapper getFlows(String dpid) throws ProtocolException, Exception {
        return cxfClient.getFlows(dpid);
    }

    @Override
    public OpenDaylightOFFlow getFlow(String dpid, String name) throws ProtocolException, Exception {
        return cxfClient.getFlow(dpid, name);
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
