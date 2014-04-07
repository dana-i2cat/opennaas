package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;

public class PortMapJSONSerializer extends JsonSerializer<PortMap> {

    Log log = LogFactory.getLog(PortMapJSONSerializer.class);

    @Override
    public void serialize(PortMap portMap, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeObjectFieldStart("portmap");
        jGen.writeStringField("logical_port_id", portMap.getLogical_port_id());
        jGen.writeEndObject();
        jGen.writeEndObject();
        jGen.close();
    }

    @Override
    public Class<PortMap> handledType() {
        return PortMap.class;
    }
}
