package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.serializers.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFMatch;

public class FloodlightOFFlowJSONDeserializer extends
		JsonDeserializer<FloodlightOFFlow> {

	@Override
	public FloodlightOFFlow deserialize(JsonParser jp,
			DeserializationContext ctx) throws IOException,
			JsonProcessingException {

		FloodlightOFFlow flow = new FloodlightOFFlow();
		FloodlightOFMatch match = new FloodlightOFMatch();
		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>(0);

		JsonToken token;
		
//		token = jp.nextToken();
//		if ((token = jp.getCurrentToken()) != JsonToken.START_OBJECT) {
//			throw new IOException("Expected START_OBJECT");
//		}

		while ((token = jp.nextToken()) != JsonToken.END_OBJECT) {
			if ((token = jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME");
			}

			String n = jp.getCurrentName();
			token = jp.nextToken();
			if (jp.getText().equals(""))
				continue;

			if (n == "name")
				flow.setName(jp.getText());
			else if (n == "switch")
				flow.setSwitchId(jp.getText());
			else if (n == "actions") {
				actions = parseActions(jp.getText());
			} else if (n == "priority")
				flow.setPriority(jp.getText());
			else if (n == "active")
				flow.setActive(Boolean.parseBoolean(jp.getText()));
			else if (n == "wildcards")
				match.setWildcards(jp.getText());
			else if (n == "ingress-port")
				match.setIngressPort(jp.getText());
			else if (n == "src-mac")
				match.setSrcMac(jp.getText());
			else if (n == "dst-mac")
				match.setDstMac(jp.getText());
			else if (n == "vlan-id")
				match.setVlanId(jp.getText());
			else if (n == "vlan-priority")
				match.setVlanPriority(jp.getText());
			else if (n == "ether-type")
				match.setEtherType(jp.getText());
			else if (n == "tos-bits")
				match.setTosBits(jp.getText());
			else if (n == "protocol")
				match.setProtocol(jp.getText());
			else if (n == "src-ip")
				match.setSrcIp(jp.getText());
			else if (n == "dst-ip")
				match.setDstIp(jp.getText());
			else if (n == "src-port")
				match.setSrcPort(jp.getText());
			else if (n == "dst-port")
				match.setDstPort(jp.getText());
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
}
