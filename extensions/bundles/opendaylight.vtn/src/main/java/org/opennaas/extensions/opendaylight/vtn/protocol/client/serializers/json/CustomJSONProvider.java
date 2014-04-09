package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.PortMapJSONDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.LogicalPortsWrapperJSONDeserializer;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.BoundaryJSONDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.BoundaryWrapperJSONDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.vBridgeInterfacesWrapperJSONDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.vBridgeJSONDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.vBridgesWrapperJSONDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.vLinkJSONDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.deserialize.vLinksWrapperJSONDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.BoundaryWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.LogicalPortsOFFlowsWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgeInterfacesWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgesWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vLinksWrapper;

public class CustomJSONProvider extends JacksonJsonProvider {

    public CustomJSONProvider() {
        super();

        ObjectMapper mapper = new ObjectMapper();

        SimpleModule myModule = new SimpleModule("MyOpenDaylightVTNJSONSerializerDeserializerModule", new Version(1, 0, 0, null));
        myModule.addSerializer(new VTNJSONSerializer()); // assuming OpenDaylightOFFlowJSONSerializer declares correct class to bind to
        myModule.addSerializer(new ControllerJSONSerializer());
        myModule.addSerializer(new vBridgeJSONSerializer());
        myModule.addSerializer(new vbrInterfaceJSONSerializer());
        myModule.addSerializer(new BoundaryJSONSerializer());
        myModule.addSerializer(new vLinkJSONSerializer());
        myModule.addSerializer(new PortMapJSONSerializer());
        myModule.addDeserializer(LogicalPortsOFFlowsWrapper.class, new LogicalPortsWrapperJSONDeserializer());
        myModule.addDeserializer(vBridgesWrapper.class, new vBridgesWrapperJSONDeserializer());
        myModule.addDeserializer(vBridgeInterfacesWrapper.class, new vBridgeInterfacesWrapperJSONDeserializer());
        myModule.addDeserializer(OpenDaylightvBridge.class, new vBridgeJSONDeserializer());
        myModule.addDeserializer(Boundary.class, new BoundaryJSONDeserializer());
        myModule.addDeserializer(BoundaryWrapper.class, new BoundaryWrapperJSONDeserializer());
        myModule.addDeserializer(vLink.class, new vLinkJSONDeserializer());
        myModule.addDeserializer(vLinksWrapper.class, new vLinksWrapperJSONDeserializer());
        myModule.addDeserializer(PortMap.class, new PortMapJSONDeserializer());
        mapper.registerModule(myModule);

        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        // mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        super.setMapper(mapper);
    }
}
