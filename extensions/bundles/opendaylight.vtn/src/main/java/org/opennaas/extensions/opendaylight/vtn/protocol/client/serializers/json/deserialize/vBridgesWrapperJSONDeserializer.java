package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgesWrapper;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class vBridgesWrapperJSONDeserializer extends JsonDeserializer<vBridgesWrapper> {

    public vBridgesWrapperJSONDeserializer() {
    }

    @Override
    public vBridgesWrapper deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        vBridgesWrapper wrapper = new vBridgesWrapper();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String flowType = jp.getCurrentName();//vbridges
            if (jp.getCurrentName() == null && flowType.equals("vbridges")) {
                break;
            }
            while (jp.nextToken() != JsonToken.END_ARRAY) {//ports
                jp.nextToken();
                OpenDaylightvBridge vbrs = new OpenDaylightvBridge();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String n = jp.getCurrentName();
                    if (n == null) {
                        break;
                    }
                    if (n.equals("vbr_name")) {
                        vbrs.setVbr_name(jp.getText());
                    }
                }

                // add flow
                if (vbrs.getVbr_name() != null) {
                    wrapper.add(vbrs);
                }
            }
        }
        return wrapper;
    }

}
