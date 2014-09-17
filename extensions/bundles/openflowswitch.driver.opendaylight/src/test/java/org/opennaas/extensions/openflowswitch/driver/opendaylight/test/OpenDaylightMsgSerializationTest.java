package org.opennaas.extensions.openflowswitch.driver.opendaylight.test;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: OpenDaylight
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
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.serializers.json.CustomJSONProvider;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFAction;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.skyscreamer.jsonassert.JSONAssert;

public class OpenDaylightMsgSerializationTest {

    String flowJSON;
    OpenDaylightOFFlow flow;

    CustomJSONProvider provider;

    @Before
    public void initFlow() {
        flowJSON = "{\"flowConfig\": [{\"installInHw\": \"true\",\"name\": \"flow1\",\"node\": {\"type\": \"OF\",\"id\": \"00:00:00:00:00:00:00:01\"},\"ingressPort\": \"1\",\"priority\": \"500\",\"etherType\": \"0x800\",\"nwSrc\":\"9.9.1.1\",\"actions\": [\"OUTPUT=2\"]}]}";
        
        flow = new OpenDaylightOFFlow();
        flow.setSwitchId("00:00:00:00:00:00:00:01");
        flow.setName("flow1");
        flow.setPriority("500");
        flow.setActive(true);

        FloodlightOFMatch match = new FloodlightOFMatch();
        match.setIngressPort("1");
        match.setSrcIp("9.9.1.1");
        match.setEtherType("0x800");
        flow.setMatch(match);

        FloodlightOFAction action = new FloodlightOFAction();
        action.setType(OpenDaylightOFAction.TYPE_OUTPUT);
        action.setValue("2");

        flow.setActions(Arrays.asList(action));
    }

    @Before
    public void initProvider() {
        provider = new CustomJSONProvider();
    }

    @Test
    public void flowSerializationDeserializationTest() throws JsonParseException, JsonMappingException, IOException, JSONException {

        String generatedJSON = provider.locateMapper(OpenDaylightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).writeValueAsString(flow);
        JSONAssert.assertEquals(flowJSON, generatedJSON, false);

        OpenDaylightOFFlow generatedFlow = provider.locateMapper(OpenDaylightOFFlow.class, MediaType.APPLICATION_JSON_TYPE).readValue(flowJSON,
                OpenDaylightOFFlow.class);
        Assert.assertEquals(flow, generatedFlow);
    }

}
