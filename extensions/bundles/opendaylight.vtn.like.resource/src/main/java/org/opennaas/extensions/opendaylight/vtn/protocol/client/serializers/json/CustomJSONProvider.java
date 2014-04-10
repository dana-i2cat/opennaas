package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.OpenDaylightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

public class CustomJSONProvider extends JacksonJsonProvider {

    public CustomJSONProvider() {
        super();

        ObjectMapper mapper = new ObjectMapper();

        SimpleModule myModule = new SimpleModule("MyOpenDaylightOFFlowJSONSerializerDeserializerModule", new Version(1, 0, 0, null));
        myModule.addSerializer(new OpenDaylightOFFlowJSONSerializer()); // assuming OpenDaylightOFFlowJSONSerializer declares correct class to bind to
        myModule.addDeserializer(OpenDaylightOFFlow.class, new OpenDaylightOFFlowJSONDeserializer());
        myModule.addDeserializer(OpenDaylightOFFlowsWrapper.class, new OpenDaylightOFFlowsWrapperJSONDeserializer());
        mapper.registerModule(myModule);

        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        // mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        super.setMapper(mapper);
    }
}
