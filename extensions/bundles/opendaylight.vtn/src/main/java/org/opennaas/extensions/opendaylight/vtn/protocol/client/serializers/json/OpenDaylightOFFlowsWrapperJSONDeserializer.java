package org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.OpenDaylightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

public class OpenDaylightOFFlowsWrapperJSONDeserializer extends JsonDeserializer<OpenDaylightOFFlowsWrapper> {

    Log log = LogFactory.getLog(OpenDaylightOFFlowsWrapperJSONDeserializer.class);

    @Override
    public OpenDaylightOFFlowsWrapper deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
        // initialize object wrapper
        OpenDaylightOFFlowsWrapper wrapper = new OpenDaylightOFFlowsWrapper();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String flowType = jp.getCurrentName();//flowConfig
            if (jp.getCurrentName() == null) {
                break;
            }
            while (jp.nextToken() != JsonToken.END_ARRAY) {//flows
                jp.nextToken();
                if (jp.getCurrentName() == null) {
                    break;
                }
                OpenDaylightOFFlow flow = new OpenDaylightOFFlow();
                FloodlightOFMatch match = new FloodlightOFMatch();
                List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>(0);
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String n = jp.getCurrentName();
                    if (n == null) {
                        break;
                    }

                    if (n.equals("name")) {
                        flow.setName(jp.getText());
                    } else if (n == "node") {
                        jp.nextToken();
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            jp.nextToken();
                            if (jp.getCurrentName() == "id") {
                                flow.setSwitchId(jp.getText());
                            }
                        }
                    } else if (n == "actions") {
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            actions = parseActions(jp.getText());
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
                    } else if ("nwDst".equals(n)) {
                        match.setDstIp(jp.getText());
                    } else if (n == "src-port") {
                        match.setSrcPort(jp.getText());
                    } else if (n == "dst-port") {
                        match.setDstPort(jp.getText());
                    }
                }
                flow.setMatch(match);
                flow.setActions(actions);
                flow.setActive(true);
                // add flow
                if (flow.getName() != null) {
                    wrapper.add(flow);
                }
            }
        }
        return wrapper;
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

    private FloodlightOFMatch parseMatch(JsonParser jp) throws JsonParseException, IOException {
        // initialize match
        FloodlightOFMatch match = new FloodlightOFMatch();

        // match fields loop
        while ((jp.nextToken()) != JsonToken.END_OBJECT) {
            if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
                throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
            }

            // get field name and go to next token
            String fieldName = jp.getCurrentName();
            jp.nextToken();

            if (fieldName == "wildcards") {
                match.setWildcards(jp.getText());
            } else if (fieldName == "inputPort") {
                match.setIngressPort(jp.getText());
            } else if (fieldName == "dataLayerSource") {
                match.setSrcMac(jp.getText());
            } else if (fieldName == "dataLayerDestination") {
                match.setDstMac(jp.getText());
            } else if (fieldName == "dataLayerVirtualLan") {
                match.setVlanId(jp.getText());
            } else if (fieldName == "dataLayerVirtualLanPriorityCodePoint") {
                match.setVlanPriority(jp.getText());
            } else if (fieldName == "dataLayerType") {
                match.setEtherType(jp.getText());
            } else if (fieldName == "networkTypeOfService") {
                match.setTosBits(jp.getText());
            } else if (fieldName == "networkProtocol") {
                match.setProtocol(jp.getText());
            } else if (fieldName == "networkSource") {
                match.setSrcIp(jp.getText());
            } else if (fieldName == "networkDestination") {
                match.setDstIp(jp.getText());
            } else if (fieldName == "transportSource") {
                match.setSrcPort(jp.getText());
            } else if (fieldName == "transportDestination") {
                match.setDstPort(jp.getText());
            }
        }

        return match;
    }
}
