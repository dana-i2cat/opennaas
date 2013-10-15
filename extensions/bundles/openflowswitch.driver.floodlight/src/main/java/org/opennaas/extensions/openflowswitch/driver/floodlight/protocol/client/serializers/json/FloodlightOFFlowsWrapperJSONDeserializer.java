package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.serializers.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.wrappers.FloodlightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

public class FloodlightOFFlowsWrapperJSONDeserializer extends JsonDeserializer<FloodlightOFFlowsWrapper> {

	@Override
	public FloodlightOFFlowsWrapper deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
		// initialize object wrapper
		FloodlightOFFlowsWrapper wrapper = new FloodlightOFFlowsWrapper();

		// switch IDs loop
		while ((jp.nextToken()) != JsonToken.END_OBJECT) {
			if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
			}

			// get switch ID
			String switchId = jp.getCurrentName();

			// expect switch start object
			if (jp.nextToken() != JsonToken.START_OBJECT) {
				throw new IOException("Expected START_OBJECT and it was " + jp.getCurrentToken());
			}

			// flow IDs loop
			while ((jp.nextToken()) != JsonToken.END_OBJECT) {
				if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
					throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
				}

				// get flow name and go to next token
				String flowName = jp.getCurrentName();
				jp.nextToken();

				// initialize the new flow
				FloodlightOFFlow flow = new FloodlightOFFlow();
				flow.setName(flowName);
				flow.setSwitchId(switchId);

				FloodlightOFMatch match = new FloodlightOFMatch();
				List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>(0);

				// expect flow start object
				if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
					throw new IOException("Expected START_OBJECT and it was " + jp.getCurrentToken());
				}

				while ((jp.nextToken()) != JsonToken.END_OBJECT) {
					if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
						throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
					}

					// get token name and go to the next token
					String nodeName = jp.getCurrentName();
					jp.nextToken();

					if (jp.getText().equals(""))
						continue;

					if (nodeName == "actions") {
						actions = parseActions(jp);
					} else if (nodeName == "priority")
						flow.setPriority(jp.getText());
					else if (nodeName == "active")
						flow.setActive(Boolean.parseBoolean(jp.getText()));
					else if (nodeName == "match") {
						match = parseMatch(jp);
					}
				}

				// set flows and match
				flow.setMatch(match);
				flow.setActions(actions);
				// add flow
				wrapper.add(flow);
			}

		}

		return wrapper;
	}

	private List<FloodlightOFAction> parseActions(JsonParser jp) throws JsonParseException, IOException {
		// initialize action list
		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();

		// expect start array
		if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
			throw new IOException("Expected START_ARRAY and it was " + jp.getCurrentToken());
		}

		// action array loop
		while (jp.nextToken() != JsonToken.END_ARRAY) {
			// expect action start object
			if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
				throw new IOException("Expected START_OBJECT and it was " + jp.getCurrentToken());
			}

			// action fields loop
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
					throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
				}
				FloodlightOFAction action = new FloodlightOFAction();

				// get field name and go to next token
				String fieldName = jp.getCurrentName();
				jp.nextToken();

				action.setType(fieldName);
				action.setValue(jp.getText());

				actions.add(action);
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

			if (fieldName == "wildcards")
				match.setWildcards(jp.getText());
			else if (fieldName == "inputPort")
				match.setIngressPort(jp.getText());
			else if (fieldName == "dataLayerSource")
				match.setSrcMac(jp.getText());
			else if (fieldName == "dataLayerDestination")
				match.setDstMac(jp.getText());
			else if (fieldName == "dataLayerVirtualLan")
				match.setVlanId(jp.getText());
			else if (fieldName == "dataLayerVirtualLanPriorityCodePoint")
				match.setVlanPriority(jp.getText());
			else if (fieldName == "dataLayerType")
				match.setEtherType(jp.getText());
			else if (fieldName == "networkTypeOfService")
				match.setTosBits(jp.getText());
			else if (fieldName == "networkProtocol")
				match.setProtocol(jp.getText());
			else if (fieldName == "networkSource")
				match.setSrcIp(jp.getText());
			else if (fieldName == "networkDestination")
				match.setDstIp(jp.getText());
			else if (fieldName == "transportSource")
				match.setSrcPort(jp.getText());
			else if (fieldName == "transportDestination")
				match.setDstPort(jp.getText());
		}

		return match;
	}
}
