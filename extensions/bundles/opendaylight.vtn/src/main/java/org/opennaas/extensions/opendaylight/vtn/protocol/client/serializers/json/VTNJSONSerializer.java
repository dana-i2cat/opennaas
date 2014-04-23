package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;

public class VTNJSONSerializer extends JsonSerializer<VTN> {

    @Override
    public void serialize(VTN vtn, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeObjectFieldStart("vtn");
        jGen.writeStringField("vtn_name", vtn.getVtn_name());
        jGen.writeEndObject();
        jGen.writeEndObject();
        jGen.close();
    }

    @Override
    public Class<VTN> handledType() {
        return VTN.class;
    }
}
