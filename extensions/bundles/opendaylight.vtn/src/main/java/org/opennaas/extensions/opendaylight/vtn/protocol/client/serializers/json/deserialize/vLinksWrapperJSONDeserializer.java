package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vLinksWrapper;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class vLinksWrapperJSONDeserializer extends JsonDeserializer<vLinksWrapper> {

    public vLinksWrapperJSONDeserializer() {
    }

    @Override
    public vLinksWrapper deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        vLinksWrapper wrapper = new vLinksWrapper();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String flowType = jp.getCurrentName();//vbridges
            if (jp.getCurrentName() == null && flowType.equals("vlinks")) {
                break;
            }
            while (jp.nextToken() != JsonToken.END_ARRAY) {//ports
                jp.nextToken();
                vLink vlink = new vLink();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String n = jp.getCurrentName();
                    if (n == null) {
                        break;
                    }
                    if (n.equals("vlk_name")) {
                        vlink.setVlk_name(jp.getText());
                    }
                }

                // add vLink
                if (vlink.getVlk_name() != null) {
                    wrapper.add(vlink);
                }
            }
        }
        return wrapper;
    }

}
