package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.serializers.json;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFFlow;

public class CustomJSONProvider extends JacksonJsonProvider {
	
	public CustomJSONProvider() {
		super();

		ObjectMapper mapper = new ObjectMapper();
		
		SimpleModule myModule = new SimpleModule("MyFloodlightOFFlowJSONSerializerDeserializerModule", new Version(1, 0, 0, null));
		myModule.addSerializer(new FloodlightOFFlowJSONSerializer()); // assuming FloodlightOFFlowJSONSerializer declares correct class to bind to
		myModule.addDeserializer(FloodlightOFFlow.class, new FloodlightOFFlowJSONDeserializer());
		mapper.registerModule(myModule);
		
		mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
		mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
//		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);

		super.setMapper(mapper);
	}
}
