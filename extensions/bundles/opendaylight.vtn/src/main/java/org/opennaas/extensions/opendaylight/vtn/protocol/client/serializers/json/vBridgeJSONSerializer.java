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
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;

public class vBridgeJSONSerializer extends JsonSerializer<OpenDaylightvBridge> {

    Log log = LogFactory.getLog(vBridgeJSONSerializer.class);

    @Override
    public void serialize(OpenDaylightvBridge vBridge, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
//        log.error("Serialize");
//        StringWriter sw = new StringWriter();//print log
//        jGen = new JsonFactory().createJsonGenerator(sw);//print log
        jGen.writeStartObject();
        jGen.writeObjectFieldStart("vbridge");
        jGen.writeStringField("vbr_name", vBridge.getVbr_name());
        jGen.writeStringField("controller_id", vBridge.getController_id());
//        if(){
        jGen.writeStringField("domain_id", "("+vBridge.getDomain_id()+")");
//        }
        jGen.writeEndObject();
        jGen.writeEndObject();
        jGen.close();
/*        sw.close();//print log
        String actual = sw.getBuffer().toString();//print log
        log.error(actual);//print log
*/    }

    @Override
    public Class<OpenDaylightvBridge> handledType() {
        return OpenDaylightvBridge.class;
    }
}
