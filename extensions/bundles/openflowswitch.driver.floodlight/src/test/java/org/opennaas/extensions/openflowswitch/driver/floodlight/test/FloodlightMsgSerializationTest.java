package org.opennaas.extensions.openflowswitch.driver.floodlight.test;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;


import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.serializers.json.CustomJSONProvider;
import org.skyscreamer.jsonassert.JSONAssert;

public class FloodlightMsgSerializationTest {
	
	String flowJSON;
	FloodlightOFFlow flow;

	CustomJSONProvider provider;
	
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
		action.setType("output");
		action.setValue("2");
		
		flow.setActions(Arrays.asList(action));
	}
	
	@Before
	public void initProvider(){
		provider = new CustomJSONProvider();
	}
	
	@Test
	public void flowSerializationDeserializationTest() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		String generatedJSON = provider.locateMapper(FloodlightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).writeValueAsString(flow);
		JSONAssert.assertEquals(flowJSON, generatedJSON, false);
		
		FloodlightOFFlow generatedFlow = provider.locateMapper(FloodlightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).readValue(flowJSON, FloodlightOFFlow.class);
		Assert.assertEquals(flow, generatedFlow);
	}

}
