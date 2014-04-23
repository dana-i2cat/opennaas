package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;

public class ControllerJSONSerializer extends JsonSerializer<OpenDaylightController> {

    @Override
    public void serialize(OpenDaylightController controller, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeObjectFieldStart("controller");
        jGen.writeStringField("controller_id", controller.getController_id());
        jGen.writeStringField("ipaddr", controller.getIpaddr());
        jGen.writeStringField("type", controller.getType());
        jGen.writeStringField("version", controller.getVersion());
        jGen.writeStringField("auditstatus", controller.getAuditstatus());
        jGen.writeEndObject();
        jGen.writeEndObject();
        jGen.close();
    }

    @Override
    public Class<OpenDaylightController> handledType() {
        return OpenDaylightController.class;
    }
}
