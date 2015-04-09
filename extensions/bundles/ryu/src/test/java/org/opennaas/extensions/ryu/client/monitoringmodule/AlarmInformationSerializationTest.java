package org.opennaas.extensions.ryu.client.monitoringmodule;

/*
 * #%L
 * OpenNaaS :: Ryu Resource
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
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

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.ryu.alarm.AlarmInformation;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class AlarmInformationSerializationTest {

	private static final String				THRESHOLD	= "10";
	private static final String				HOST		= "192.168.1.1";
	private static final String				PORT		= "8080";
	private static final String				URL_PREFIX	= "/xifi/raise_alarm/";

	private String							JSON_FILE	= "/alarmInformation.json";

	private MonitoringModuleJsonProvider	jsonProvider;
	private ObjectMapper					objectMapper;

	@Before
	public void prepareTest() {
		jsonProvider = new MonitoringModuleJsonProvider();
		objectMapper = jsonProvider.getMappter();
	}

	@Test
	public void serializationTest() throws JsonGenerationException, JsonMappingException, IOException, JSONException {

		AlarmInformation alarmInformation = new AlarmInformation(THRESHOLD, HOST, PORT, URL_PREFIX);

		String serializedJson = objectMapper.writeValueAsString(alarmInformation);
		String expectedJson = IOUtils.toString(this.getClass().getResourceAsStream(JSON_FILE));

		JSONAssert.assertEquals(expectedJson, serializedJson, true);

	}

}
