package org.opennaas.extensions.openflowswitch.driver.floodlight.test;

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

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.MemoryUsage;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.controllerinformationclient.model.Healthy;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.controllerinformationclient.serializers.json.ControllerInformationJSONProvider;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class FloodlightControllerInformationClientTest {

	private static final long	TOTAL_MEMORY	= 41496576;
	private static final long	FREE_MEMORY		= 12269448;

	ControllerInformationJSONProvider			provider;

	@Before
	public void prepareTest() {
		provider = new ControllerInformationJSONProvider();
	}

	@Test
	public void healthyDeserializationTest() throws JsonParseException, JsonMappingException, IOException {
		String json = "{ \"healthy\": true }";
		Healthy healthy = provider.locateMapper(Healthy.class, MediaType.APPLICATION_JSON_TYPE).readValue(json,
				Healthy.class);
		Assert.assertNotNull("Deserialized health should not be null.", healthy);
		Assert.assertTrue("Deserialized health should be true.", healthy.isHealthy());

		json = "{ \"healthy\": false }";
		healthy = provider.locateMapper(Healthy.class, MediaType.APPLICATION_JSON_TYPE).readValue(json,
				Healthy.class);
		Assert.assertNotNull("Deserialized health should not be null.", healthy);
		Assert.assertFalse("Deserialized health should be false.", healthy.isHealthy());
	}

	@Test
	public void memoryInformationDeserializationTest() throws JsonParseException, JsonMappingException, IOException {

		StringBuilder sb = new StringBuilder();
		sb.append("{\"total\":").append(TOTAL_MEMORY).append(", \"free\":").append(FREE_MEMORY).append("}");

		MemoryUsage memoryUsage = provider.locateMapper(MemoryUsage.class, MediaType.APPLICATION_JSON_TYPE).readValue(sb.toString(),
				MemoryUsage.class);

		Assert.assertNotNull("Deserialized MemoryUsage should not be null", memoryUsage);
		Assert.assertEquals("Total Memory value should be " + TOTAL_MEMORY, TOTAL_MEMORY, memoryUsage.getTotal());
		Assert.assertEquals("Free Memory value should be " + FREE_MEMORY, FREE_MEMORY, memoryUsage.getFree());
	}
}
