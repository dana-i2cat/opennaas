package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.model.Link;

public class BoundaryJSONDeserializer extends JsonDeserializer<Boundary> {

    /*
     {"boundary":{"boundary_id":"b1","link":{"logical_port2_id":"PP-OF:00:00:00:00:00:00:00:04-s4-eth3","domain2_id":"(DEFAULT)","logical_port1_id":"PP-OF:00:00:00:00:00:00:00:01-s1-eth3","controller1_id":"odc1","controller2_id":"odc2","domain1_id":"(DEFAULT)"},"operstatus":"unknown"}}
     */

    @Override
    public Boundary deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        Boundary bound = new Boundary();
        JsonToken current;
        current = jp.nextToken();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            if (fieldName.equals("boundary")) {
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    if (jp.getCurrentName().equals("boundary_id")) {
                        bound.setBoundary_id(jp.getText());
                    }
                    fieldName = jp.getCurrentName();
                    if (fieldName.equals("link")) {
                        Link link = new Link();
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            if (jp.getCurrentName().equals("logical_port1_id")) {
                                link.setLogical_port1_id(jp.getText());
                            }
                            if (jp.getCurrentName().equals("domain1_id")) {
                                link.setDomain1_id(jp.getText());
                            }
                            if (jp.getCurrentName().equals("controller1_id")) {
                                link.setController1_id(jp.getText());
                            }
                            if (jp.getCurrentName().equals("logical_port2_id")) {
                                link.setLogical_port2_id(jp.getText());
                            }
                            if (jp.getCurrentName().equals("domain2_id")) {
                                link.setDomain2_id(jp.getText());
                            }
                            if (jp.getCurrentName().equals("controller2_id")) {
                                link.setController2_id(jp.getText());
                            }
                        }
                        bound.setLink(link);
                    }
                }
            }
        }
        return bound;
    }
}
