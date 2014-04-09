package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;

public class vLinkJSONSerializer extends JsonSerializer<vLink> {

    @Override
    public void serialize(vLink vLink, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeObjectFieldStart("vlink");
        jGen.writeStringField("vlk_name", vLink.getVlk_name());
        jGen.writeStringField("vnode1_name", vLink.getVnode1_name());
        jGen.writeStringField("if1_name", vLink.getIf1_name());
        jGen.writeStringField("vnode2_name", vLink.getVnode2_name());
        jGen.writeStringField("if2_name", vLink.getIf2_name());

        jGen.writeObjectFieldStart("boundary_map");
        jGen.writeStringField("boundary_id", vLink.getBoundaryMap().getBoundary_id());
        jGen.writeStringField("vlan_id", vLink.getBoundaryMap().getVlan_id());
        jGen.writeEndObject();

        jGen.writeEndObject();
        jGen.writeEndObject();
        jGen.close();
    }

    @Override
    public Class<vLink> handledType() {
        return vLink.class;
    }
}
