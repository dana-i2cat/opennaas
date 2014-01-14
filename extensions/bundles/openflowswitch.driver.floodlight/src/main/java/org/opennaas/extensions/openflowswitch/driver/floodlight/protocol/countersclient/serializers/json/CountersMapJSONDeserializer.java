package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.serializers.json;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
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

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.wrappers.CountersMap;

/**
 * {@link CountersMap} custom JSON deserializer
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class CountersMapJSONDeserializer extends JsonDeserializer<CountersMap> {
	@Override
	public CountersMap deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
		// initialize object wrapper
		CountersMap wrapper = new CountersMap();

		// counters loop
		while ((jp.nextToken()) != JsonToken.END_OBJECT) {
			if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
			}

			// get counter ID
			String counterId = jp.getCurrentName();

			// get counter value
			if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) {
				throw new IOException("Expected VALUE_NUMBER_INT and it was " + jp.getCurrentToken());
			}
			long counterValue = jp.getLongValue();
			wrapper.addCounter(counterId, counterValue);
		}

		return wrapper;
	}
}
