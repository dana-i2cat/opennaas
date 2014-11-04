package org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.serializers.json;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;

/**
 * Custom Provider adding {@link RyuOFFlowJSONSerializer} and {@link RyuOFFlowListWrapperJSONDeserializer}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class RyuJSONProvider extends JacksonJsonProvider {

	public RyuJSONProvider() {
		super();

		ObjectMapper mapper = new ObjectMapper();

		SimpleModule myModule = new SimpleModule("RyuOFFlowJSONSerializerDeserializerModule", new Version(1, 0, 0, null));
		myModule.addSerializer(new RyuOFFlowJSONSerializer()); // assuming RyuOFFlowJSONSerializer declares correct class to bind to
		myModule.addDeserializer(RyuOFFlowListWrapper.class, new RyuOFFlowListWrapperJSONDeserializer());
		mapper.registerModule(myModule);

		mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
		mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
		// mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);

		super.setMapper(mapper);
	}
}
