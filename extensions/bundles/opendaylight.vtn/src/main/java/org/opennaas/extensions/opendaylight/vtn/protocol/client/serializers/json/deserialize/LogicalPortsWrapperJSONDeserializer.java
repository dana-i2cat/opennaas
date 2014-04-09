package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.LogicalPort;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.LogicalPortsOFFlowsWrapper;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class LogicalPortsWrapperJSONDeserializer extends JsonDeserializer<LogicalPortsOFFlowsWrapper> {

    public LogicalPortsWrapperJSONDeserializer() {
    }

    @Override
    public LogicalPortsOFFlowsWrapper deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        LogicalPortsOFFlowsWrapper wrapper = new LogicalPortsOFFlowsWrapper();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String flowType = jp.getCurrentName();//logical_ports
            if (jp.getCurrentName() == null) {
                break;
            }
            while (jp.nextToken() != JsonToken.END_ARRAY) {//[
                jp.nextToken();
                if (jp.getCurrentName() == null) {
                    break;
                }
                LogicalPort lport = new LogicalPort();
                while (jp.nextToken() != JsonToken.END_OBJECT) {//{
                    String n = jp.getCurrentName();
                    if (n == null) {
                        break;
                    }
                    if (n.equals("port_name")) {
                        lport.setPort_name(jp.getText());
                    } else if ("operdown_criteria".equals(n)) {
                        lport.setOperdown_criteria(jp.getText());
                    } else if ("logical_port_id".equals(n)) {
                        lport.setLogical_port_id(jp.getText());
                    } else if ("oper_status".equals(n)) {
                        lport.setOper_status(jp.getText());
                    } else if ("type".equals(n)) {
                        lport.setPort_name(jp.getText());
                    } else if ("switch_id".equals(n)) {
                        lport.setSwitch_id(jp.getText());
                    }
                }

                // add flow
                if (lport.getPort_name() != null) {
                    wrapper.add(lport);
                }
            }
        }
        return wrapper;
    }

}
