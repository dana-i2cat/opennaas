package org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.serializers.json;

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
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

public class OpenDaylightOFFlowJSONDeserializer extends
        JsonDeserializer<OpenDaylightOFFlow> {

    @Override
    public OpenDaylightOFFlow deserialize(JsonParser jp,
            DeserializationContext ctx) throws IOException,
            JsonProcessingException {

        OpenDaylightOFFlow flow = new OpenDaylightOFFlow();
        FloodlightOFMatch match = new FloodlightOFMatch();
        List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>(0);

        JsonToken token;
        // token = jp.nextToken();
        // if ((token = jp.getCurrentToken()) != JsonToken.START_OBJECT) {
        // throw new IOException("Expected START_OBJECT");
        // }
        while ((token = jp.nextToken()) != JsonToken.END_OBJECT) {
            if ((token = jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
                throw new IOException("Expected FIELD_NAME");
            }

            String n = jp.getCurrentName();
            token = jp.nextToken();
            if (jp.getText().equals("")) {
                continue;
            }
            
            if (n == "name") {
                flow.setName(jp.getText());
            } else if (n == "node") {
                jp.nextToken();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    if (jp.getText() == "id") {
                        jp.nextToken();
                        flow.setSwitchId(jp.getText());
                    }
                }
            } else if (n == "actions") {
                actions = new ArrayList<FloodlightOFAction>();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    FloodlightOFAction action = new FloodlightOFAction();
                    action = parseAction(jp.getText());
                    actions.add(action);
                }
            } else if (n == "priority") {
                flow.setPriority(jp.getText());
            } else if (n == "active") {
                flow.setActive(Boolean.parseBoolean(jp.getText()));
            } else if (n == "wildcards") {
                match.setWildcards(jp.getText());
            } else if (n == "ingressPort") {
                match.setIngressPort(jp.getText());
            } else if (n == "dlSrc") {
                match.setSrcMac(jp.getText());
            } else if (n == "dlDst") {
                match.setDstMac(jp.getText());
            } else if (n == "vlan-id") {
                match.setVlanId(jp.getText());
            } else if (n == "vlan-priority") {
                match.setVlanPriority(jp.getText());
            } else if (n == "etherType") {
                match.setEtherType(jp.getText());
            } else if (n == "tos-bits") {
                match.setTosBits(jp.getText());
            } else if (n == "protocol") {
                match.setProtocol(jp.getText());
            } else if (n == "nwSrc") {
                match.setSrcIp(jp.getText());
            } else if (n == "nwDst") {
                match.setDstIp(jp.getText());
            } else if (n == "src-port") {
                match.setSrcPort(jp.getText());
            } else if (n == "dst-port") {
                match.setDstPort(jp.getText());
            }
        }
        flow.setMatch(match);
        flow.setActions(actions);
        return flow;
    }

    private List<FloodlightOFAction> parseActions(String actionstr) {
        List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
        FloodlightOFAction currentAction;
        String type;
        String value;

        if (actionstr != null) {
            actionstr = actionstr.toLowerCase();
            for (String subaction : actionstr.split(",")) {

                type = subaction.split("[=:]")[0];
                if (subaction.split("[=:]").length > 1) {
                    value = subaction.split("[=:]")[1];
                } else {
                    value = null;
                }

                currentAction = new FloodlightOFAction();
                currentAction.setType(type);
                currentAction.setValue(value);

                actions.add(currentAction);
            }
        }
        return actions;
    }
    
    private FloodlightOFAction parseAction(String actionstr) {
        FloodlightOFAction currentAction;
        String type;
        String value;

        if (actionstr != null) {
            type = actionstr.split("[=:]")[0];
            value = actionstr.split("[=:]")[1];
            
            currentAction = new FloodlightOFAction();
            currentAction.setType(type);
            currentAction.setValue(value);

            return currentAction;
        }
        return null;
    }
}
