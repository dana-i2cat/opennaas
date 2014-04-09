package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;

public class PortMapJSONDeserializer extends JsonDeserializer<PortMap> {

    Log log = LogFactory.getLog(PortMapJSONDeserializer.class);
    /*{
     "portmap":{
     "logical_port_id":"PP-OF:00:00:00:00:00:00:00:05-s5-eth2","tagged":"false"}}
     */

    @Override
    public PortMap deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        PortMap portMap = new PortMap();
        JsonToken current;
        current = jp.nextToken();
        /*        if (current != JsonToken.START_OBJECT) {
         log.error("Error: root should be object: quiting.");
         }*/
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            if (fieldName.equals("portmap")) {
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    if (jp.getCurrentName().equals("logical_port_id")) {
                        portMap.setLogical_port_id(jp.getText());
                    }
                }
            }
        }
        return portMap;
    }
}
