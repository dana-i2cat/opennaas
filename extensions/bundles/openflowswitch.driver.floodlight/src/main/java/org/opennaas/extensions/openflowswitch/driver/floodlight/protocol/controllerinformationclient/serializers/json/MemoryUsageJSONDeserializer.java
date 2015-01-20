package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.controllerinformationclient.serializers.json;

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

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.MemoryUsage;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class MemoryUsageJSONDeserializer extends JsonDeserializer<MemoryUsage> {

	private static final String	TOTAL_MEMORY_NAME	= "total";
	private static final String	FREE_MEMORY_NAME	= "free";

	@Override
	public MemoryUsage deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {

		MemoryUsage memoryUsage = new MemoryUsage();

		while ((jp.nextToken()) != JsonToken.END_OBJECT) {
			if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME)
				throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());

			if (StringUtils.equals(TOTAL_MEMORY_NAME, jp.getCurrentName())) {
				jp.nextToken();
				long totalMemory = jp.getLongValue();
				memoryUsage.setTotal(totalMemory);
			}
			else if (StringUtils.equals(FREE_MEMORY_NAME, jp.getCurrentName())) {
				jp.nextToken();
				long freeMemory = jp.getLongValue();
				memoryUsage.setFree(freeMemory);

			}

		}

		return memoryUsage;
	}
}
