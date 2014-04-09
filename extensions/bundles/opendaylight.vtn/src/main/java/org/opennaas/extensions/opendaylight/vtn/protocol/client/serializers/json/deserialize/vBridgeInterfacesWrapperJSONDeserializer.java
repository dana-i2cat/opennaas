package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.vBridgeInterfaces;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgeInterfacesWrapper;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class vBridgeInterfacesWrapperJSONDeserializer extends JsonDeserializer<vBridgeInterfacesWrapper> {
    
    public vBridgeInterfacesWrapperJSONDeserializer() {
    }

    @Override
    public vBridgeInterfacesWrapper deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        vBridgeInterfacesWrapper wrapper = new vBridgeInterfacesWrapper();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String flowType = jp.getCurrentName();//vbridges
            if (jp.getCurrentName() == null && flowType.equals("interfaces")) {
                break;
            }
            while (jp.nextToken() != JsonToken.END_ARRAY) {//ports
                jp.nextToken();
/*                if (jp.getCurrentName() == null) {
                    break;
                }
*/                vBridgeInterfaces vbrs = new vBridgeInterfaces();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String n = jp.getCurrentName();
                    if (n == null) {
                        break;
                    }
                    if (n.equals("if_name")) {
                        vbrs.setIf_name(jp.getText());
                    } 
                }

                // add flow
                if (vbrs.getIf_name() != null) {
                    wrapper.add(vbrs);
                }
            }
        }
        return wrapper;
    }

}
