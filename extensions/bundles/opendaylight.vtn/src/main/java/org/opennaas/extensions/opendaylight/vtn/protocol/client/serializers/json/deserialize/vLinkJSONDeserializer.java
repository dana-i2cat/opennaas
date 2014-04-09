package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.model.BoundaryMap;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;

public class vLinkJSONDeserializer extends JsonDeserializer<vLink> {

    @Override
    public vLink deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        vLink vlink = new vLink();
        JsonToken current;
        current = jp.nextToken();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            if (fieldName.equals("vlink")) {
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    if (jp.getCurrentName().equals("vlk_name")) {
                        vlink.setVlk_name(jp.getText());
                    }
                    if (jp.getCurrentName().equals("vnode1_name")) {
                        vlink.setVnode1_name(jp.getText());
                    }
                    if (jp.getCurrentName().equals("if1_name")) {
                        vlink.setIf1_name(jp.getText());
                    }
                    if (jp.getCurrentName().equals("vnode2_name")) {
                        vlink.setVnode2_name(jp.getText());
                    }
                    if (jp.getCurrentName().equals("if2_name")) {
                        vlink.setIf2_name(jp.getText());
                    }
                    fieldName = jp.getCurrentName();
                    if (fieldName.equals("boundary_map")) {
                        BoundaryMap bMap = new BoundaryMap();
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            if (jp.getCurrentName().equals("boundary_id")) {
                                bMap.setBoundary_id(jp.getText());
                            }
                            if (jp.getCurrentName().equals("vlan_id")) {
                                bMap.setVlan_id(jp.getText());
                            }
                        }
                        vlink.setBoundaryMap(bMap);
                    }
                }
            }
        }
        return vlink;
    }
}
