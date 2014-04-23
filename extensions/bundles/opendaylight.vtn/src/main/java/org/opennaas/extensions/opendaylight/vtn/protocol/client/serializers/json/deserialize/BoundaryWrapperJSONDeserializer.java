package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.BoundaryWrapper;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class BoundaryWrapperJSONDeserializer extends JsonDeserializer<BoundaryWrapper> {
    
    public BoundaryWrapperJSONDeserializer() {
    }

    @Override
    public BoundaryWrapper deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        BoundaryWrapper wrapper = new BoundaryWrapper();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String flowType = jp.getCurrentName();//vbridges
            if (jp.getCurrentName() == null || !flowType.equals("boundaries")) {
                break;
            }
            while (jp.nextToken() != JsonToken.END_ARRAY) {//ports
                if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
                        jp.nextToken();
                }
                if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
                    break;
                }
                Boundary bound = new Boundary();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String n = jp.getCurrentName();
                    if (n == null) {
                        break;
                    }
                    if (n.equals("boundary_id")) {
                        bound.setBoundary_id(jp.getText());
                    } 
                }

                // add boundary
                if (bound.getBoundary_id() != null) {
                    wrapper.add(bound);
                }
            }
        }
        return wrapper;
    }

}
