package org.opennaas.extensions.quantum.utils;

/*
 * #%L
 * OpenNaaS :: Quantum
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

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class CustomJSONProvider extends JacksonJaxbJsonProvider {

	public CustomJSONProvider() {
		super();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		super.setMapper(mapper);
	}
}
