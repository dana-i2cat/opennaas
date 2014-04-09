package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;

public class vBridgeJSONDeserializer extends JsonDeserializer<OpenDaylightvBridge> {

    Log log = LogFactory.getLog(vBridgeJSONDeserializer.class);
    /*
     {"vbridges":[{"vbr_name":"vbr1"},{"vbr_name":"vbr2"}]}
     */

    @Override
    public OpenDaylightvBridge deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        OpenDaylightvBridge vbr = new OpenDaylightvBridge();
        JsonToken current;
        current = jp.nextToken();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            if (fieldName.equals("vbridge")) {
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    if (jp.getCurrentName().equals("controller_id")) {
                        vbr.setController_id(jp.getText());
                    }
                    if (jp.getCurrentName().equals("vbr_name")) {
                        vbr.setVbr_name(jp.getText());
                    }
                    if (jp.getCurrentName().equals("domain_id")) {
                        vbr.setDomain_id(jp.getText());
                    }
                }
            }
        }
        return vbr;
    }
}
