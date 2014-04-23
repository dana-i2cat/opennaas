package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.opendaylight.vtn.model.vBridgeInterfaces;

public class vbrInterfaceJSONSerializer extends JsonSerializer<vBridgeInterfaces> {

    @Override
    public void serialize(vBridgeInterfaces vBrI, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeObjectFieldStart("interface");
        jGen.writeStringField("if_name", vBrI.getIf_name());
        jGen.writeEndObject();
        jGen.writeEndObject();
        jGen.close();
    }

    @Override
    public Class<vBridgeInterfaces> handledType() {
        return vBridgeInterfaces.class;
    }
}
