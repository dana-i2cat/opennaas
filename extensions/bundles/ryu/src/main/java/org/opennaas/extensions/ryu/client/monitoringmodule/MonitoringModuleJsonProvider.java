package org.opennaas.extensions.ryu.client.monitoringmodule;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * <p>
 * Custom JSON provider configuring JSON as default serializer/deserializer.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class MonitoringModuleJsonProvider extends JacksonJaxbJsonProvider {

	public MonitoringModuleJsonProvider() {
		super();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		super.setMapper(mapper);
	}

	public ObjectMapper getMappter() {
		return _mapperConfig.getConfiguredMapper();
	}

}
