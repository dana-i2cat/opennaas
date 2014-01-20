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
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.serializers.json.CustomJSONProvider;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.skyscreamer.jsonassert.JSONAssert;

public class FloodlightMsgSerializationTest {

	String				flowJSON;
	FloodlightOFFlow	flow;

	CustomJSONProvider	provider;

	@Before
	public void initFlow() {
		flowJSON = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-1\", \"priority\":\"32768\", \"ingress-port\":\"1\",\"active\":\"true\", \"actions\":\"output=2\"}";

		flow = new FloodlightOFFlow();
		flow.setSwitchId("00:00:00:00:00:00:00:01");
		flow.setName("flow-mod-1");
		flow.setPriority("32768");
		flow.setActive(true);

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort("1");
		flow.setMatch(match);

		FloodlightOFAction action = new FloodlightOFAction();
		action.setType(FloodlightOFAction.TYPE_OUTPUT);
		action.setValue("2");

		flow.setActions(Arrays.asList(action));
	}

	@Before
	public void initProvider() {
		provider = new CustomJSONProvider();
	}

	@Test
	public void flowSerializationDeserializationTest() throws JsonParseException, JsonMappingException, IOException, JSONException {

		String generatedJSON = provider.locateMapper(FloodlightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).writeValueAsString(flow);
		JSONAssert.assertEquals(flowJSON, generatedJSON, false);

		FloodlightOFFlow generatedFlow = provider.locateMapper(FloodlightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).readValue(flowJSON,
				FloodlightOFFlow.class);
		Assert.assertEquals(flow, generatedFlow);
	}

}
