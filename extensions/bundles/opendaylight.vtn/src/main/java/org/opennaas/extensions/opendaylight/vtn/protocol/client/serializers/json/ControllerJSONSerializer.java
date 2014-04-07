package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;

public class ControllerJSONSerializer extends JsonSerializer<OpenDaylightController> {

    Log log = LogFactory.getLog(ControllerJSONSerializer.class);

    @Override
    public void serialize(OpenDaylightController controller, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
//        log.error("Serialize");
//        StringWriter sw = new StringWriter();//print log
//        jGen = new JsonFactory().createJsonGenerator(sw);//print log
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
        /*
        sw.close();//print log
        String actual = sw.getBuffer().toString();//print log
        log.error(actual);//print log
                */
    }

    @Override
    public Class<OpenDaylightController> handledType() {
        return OpenDaylightController.class;
    }
}
