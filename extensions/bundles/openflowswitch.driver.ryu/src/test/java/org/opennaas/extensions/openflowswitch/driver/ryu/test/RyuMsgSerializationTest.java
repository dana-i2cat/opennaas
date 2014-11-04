package org.opennaas.extensions.openflowswitch.driver.ryu.test;

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

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.serializers.json.RyuJSONProvider;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.skyscreamer.jsonassert.JSONAssert;

public class RyuMsgSerializationTest {

	private static final String	GET_FLOWS_JSON	= "{\n" +
														"  \"1\": [\n" +
														"    {\n" +
														"      \"table_id\": 10,\n" +
														"      \"priority\": 10,\n" +
														"      \"idle_timeout\": 10,\n" +
														"      \"hard_timeout\": 10,\n" +
														"      \"flags\": 10,\n" +
														"      \"cookie\": 10,\n" +
														"      \"match\": {\n" +
														"        \"in_port\": 1\n" +
														"      },\n" +
														"      \"actions\": [\n" +
														"        \"OUTPUT:2\"\n" +
														"      ]\n" +
														"    }\n" +
														"  ]\n" +
														"}";	;

	private static final String	POST_FLOW_JSON	= "{ \n" +
														"    \"dpid\": 1, \n" +
														"    \"cookie\": 10, \n" +
														"    \"table_id\": 10, \n" +
														"    \"idle_timeout\": 10, \n" +
														"    \"hard_timeout\": 10, \n" +
														"    \"priority\": 10, \n" +
														"    \"flags\": 10, \n" +
														"    \"match\":{ \n" +
														"        \"in_port\":1 \n" +
														"    }, \n" +
														"    \"actions\":[ \n" +
														"        { \n" +
														"            \"type\":\"OUTPUT\", \n" +
														"            \"port\": 2 \n" +
														"        } \n" +
														"    ] \n" +
														" }";
	private RyuOFFlow			expectedFlow;

	private RyuJSONProvider		provider;

	@Before
	public void initFlow() {
		expectedFlow = new RyuOFFlow();
		expectedFlow.setDpid("1");
		expectedFlow.setCookie("10");
		expectedFlow.setTableId("10");
		expectedFlow.setPriority("10");
		expectedFlow.setIdleTimeout("10");
		expectedFlow.setHardTimeout("10");
		expectedFlow.setFlags("10");

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort("1");
		expectedFlow.setMatch(match);

		FloodlightOFAction action = new FloodlightOFAction();
		action.setType(FloodlightOFAction.TYPE_OUTPUT);
		action.setValue("2");

		expectedFlow.setActions(Arrays.asList(action));
	}

	@Before
	public void initProvider() {
		provider = new RyuJSONProvider();
	}

	@Test
	public void flowSerializationDeserializationTest() throws JsonParseException, JsonMappingException, IOException, JSONException {
		// deserialization test
		RyuOFFlowListWrapper generatedGetFlows = provider.locateMapper(RyuOFFlowListWrapper.class, MediaType.APPLICATION_JSON_TYPE).readValue(
				GET_FLOWS_JSON, RyuOFFlowListWrapper.class);
		RyuOFFlowListWrapper expectedFlowListWrapper = new RyuOFFlowListWrapper();
		expectedFlowListWrapper.add(expectedFlow);
		Assert.assertEquals(expectedFlowListWrapper, generatedGetFlows);

		// serialization test
		String generatedPostFlowJSON = provider.locateMapper(RyuOFFlow.class, MediaType.APPLICATION_JSON_TYPE).writeValueAsString(expectedFlow);
		JSONAssert.assertEquals(POST_FLOW_JSON, generatedPostFlowJSON, false);
	}

}
