package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;

public class vBridgeJSONSerializer extends JsonSerializer<OpenDaylightvBridge> {

    @Override
    public void serialize(OpenDaylightvBridge vBridge, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeObjectFieldStart("vbridge");
        jGen.writeStringField("vbr_name", vBridge.getVbr_name());
        jGen.writeStringField("controller_id", vBridge.getController_id());
        jGen.writeStringField("domain_id", "(" + vBridge.getDomain_id() + ")");
        jGen.writeEndObject();
        jGen.writeEndObject();
        jGen.close();
    }

    @Override
    public Class<OpenDaylightvBridge> handledType() {
        return OpenDaylightvBridge.class;
    }
}
