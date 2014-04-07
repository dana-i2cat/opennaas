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
import org.opennaas.extensions.opendaylight.vtn.model.Boundary;

public class BoundaryJSONSerializer extends JsonSerializer<Boundary> {

    Log log = LogFactory.getLog(Boundary.class);

    @Override
    public void serialize(Boundary bound, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
/*        log.error("Serialize");
        StringWriter sw = new StringWriter();//print log
        jGen = new JsonFactory().createJsonGenerator(sw);//print l
*/        jGen.writeStartObject();
        jGen.writeObjectFieldStart("boundary");
        jGen.writeStringField("boundary_id", bound.getBoundary_id());
        
        jGen.writeObjectFieldStart("link");
        jGen.writeStringField("controller1_id", bound.getLink().getController1_id());
        jGen.writeStringField("domain1_id", "("+bound.getLink().getDomain1_id()+")");
        jGen.writeStringField("logical_port1_id", bound.getLink().getLogical_port1_id());
        jGen.writeStringField("controller2_id", bound.getLink().getController2_id());
        jGen.writeStringField("domain2_id", "("+bound.getLink().getDomain2_id()+")");
        jGen.writeStringField("logical_port2_id", bound.getLink().getLogical_port2_id());
        jGen.writeEndObject();
        
        jGen.writeEndObject();
        jGen.writeEndObject();
        jGen.close();
/*        sw.close();//print log
        String actual = sw.getBuffer().toString();//print log
        log.error(actual);//print log
*/    }

    @Override
    public Class<Boundary> handledType() {
        return Boundary.class;
    }
}
