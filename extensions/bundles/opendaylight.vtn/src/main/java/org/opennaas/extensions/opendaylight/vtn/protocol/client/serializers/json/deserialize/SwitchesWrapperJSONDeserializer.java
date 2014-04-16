package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.Switch;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.SwitchesWrapper;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class SwitchesWrapperJSONDeserializer extends JsonDeserializer<SwitchesWrapper> {
    
    public SwitchesWrapperJSONDeserializer() {
    }

    @Override
    public SwitchesWrapper deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        SwitchesWrapper wrapper = new SwitchesWrapper();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String flowType = jp.getCurrentName();//vbridges
            if (jp.getCurrentName() == null || !flowType.equals("switches")) {
                break;
            }
            while (jp.nextToken() != JsonToken.END_ARRAY) {//ports
                if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
                        jp.nextToken();
                }
                if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
                    break;
                }
                Switch sw = new Switch();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String n = jp.getCurrentName();
                    if (n == null) {
                        break;
                    }
                    if (n.equals("switch_id")) {
                        sw.setSwitch_id(jp.getText());
                    } 
                }

                // add sweitch
                if (sw.getSwitch_id() != null) {
                    wrapper.add(sw);
                }
            }
        }
        return wrapper;
    }

}
