package org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.serializers.json;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.RyuConstants;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * {@link RyuOFFlowListWrapper} {@link JSONDeserializer}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class RyuOFFlowListWrapperJSONDeserializer extends JsonDeserializer<RyuOFFlowListWrapper> {

	@Override
	public RyuOFFlowListWrapper deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
		// initialize object wrapper
		RyuOFFlowListWrapper wrapper = new RyuOFFlowListWrapper();

		// dpids loop
		while ((jp.nextToken()) != JsonToken.END_OBJECT) {
			if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
			}

			// get switch ID
			String dpid = jp.getCurrentName();

			// expect switch start object
			if (jp.nextToken() == JsonToken.VALUE_NULL) {
				// no object inside, stop deserializing
				break;
			}
			else if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
				throw new IOException("Expected START_ARRAY and it was " + jp.getCurrentToken());
			}

			// array loop
			while ((jp.nextToken()) != JsonToken.END_ARRAY) {
				if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
					throw new IOException("Expected START_OBJECT and it was " + jp.getCurrentToken());
				}

				RyuOFFlow flow = new RyuOFFlow();
				FloodlightOFMatch match = new FloodlightOFMatch();
				List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>(0);

				flow.setDpid(dpid);

				// flow loop
				while (jp.nextToken() != JsonToken.END_OBJECT) {
					if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
						throw new IOException("Expected FIELD_NAME");
					}

					String n = jp.getCurrentName();
					jp.nextToken();
					if (jp.getText().equals("")) {
						continue;
					}

					else if (n.equals("actions"))
						actions = parseActions(jp);
					else if (n.equals("priority"))
						flow.setPriority(jp.getText());
					else if (n.equals("buffer_id"))
						flow.setBufferId(jp.getText());
					else if (n.equals("cookie"))
						flow.setCookie(jp.getText());
					else if (n.equals("cookie_mask"))
						flow.setCookieMask(jp.getText());
					else if (n.equals("flags"))
						flow.setFlags(jp.getText());
					else if (n.equals("hard_timeout"))
						flow.setHardTimeout(jp.getText());
					else if (n.equals("idle_timeout"))
						flow.setIdleTimeout(jp.getText());
					else if (n.equals("table_id"))
						flow.setTableId(jp.getText());
					else if (n.equals("match")) {
						match = parseMatch(jp);
					}
				}
				flow.setMatch(match);
				flow.setActions(actions);

				convertRyuDefaultsToNull(flow);

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
		Map<String, String> actionMap = new HashMap<String, String>();
		while (jp.nextToken() != JsonToken.END_ARRAY) {

			String actionstr = jp.getText();
			if (actionstr != null) {
				actionstr = actionstr.toLowerCase();
				for (String subaction : actionstr.split(",")) {

					String[] split = subaction.split("[=:]");
					String type = split[0];
					String value;
					if (split.length > 1) {
						value = split[1];
					} else {
						value = null;
					}

					actionMap.put(type.toLowerCase(), value);
				}
			}

		}

		// fill action list following some rules, by now only OUTPUT actions are parsed
		if (actionMap.containsKey(FloodlightOFAction.TYPE_OUTPUT.toLowerCase())) {
			// fill action
			FloodlightOFAction action = new FloodlightOFAction();

			// set type and value
			action.setType(FloodlightOFAction.TYPE_OUTPUT.toLowerCase());
			action.setValue(actionMap.get(FloodlightOFAction.TYPE_OUTPUT.toLowerCase()));

			actions.add(action);
		} else {
			// no more known types
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

			if (fieldName.equals("wildcards"))
				match.setWildcards(jp.getText());
			else if (fieldName.equals("in_port"))
				match.setIngressPort(jp.getText());
			else if (fieldName.equals("dl_src"))
				match.setSrcMac(jp.getText());
			else if (fieldName.equals("dl_dst"))
				match.setDstMac(jp.getText());
			else if (fieldName.equals("dl_vlan"))
				match.setVlanId(jp.getText());
			else if (fieldName.equals("dl_vlan_pcp"))
				match.setVlanPriority(jp.getText());
			else if (fieldName.equals("dl_type"))
				match.setEtherType(jp.getText());
			else if (fieldName.equals("ip_dscp"))
				match.setTosBits(jp.getText());
			else if (fieldName.equals("nw_proto"))
				match.setProtocol(jp.getText());
			else if (fieldName.equals("nw_src"))
				match.setSrcIp(jp.getText());
			else if (fieldName.equals("nw_dst"))
				match.setDstIp(jp.getText());
			else if (fieldName.equals("tp_src"))
				match.setSrcPort(jp.getText());
			else if (fieldName.equals("tp_dst"))
				match.setDstPort(jp.getText());
		}

		return match;
	}

	private static void convertRyuDefaultsToNull(RyuOFFlow flow) {

		// fields
		if (flow.getCookie() != null && flow.getCookie().equals(RyuConstants.DEFAULT_COOKIE)) {
			flow.setCookie(null);
		}
		if (flow.getIdleTimeout() != null && flow.getIdleTimeout().equals(RyuConstants.DEFAULT_IDLE_TIMEOUT)) {
			flow.setIdleTimeout(null);
		}
		if (flow.getHardTimeout() != null && flow.getHardTimeout().equals(RyuConstants.DEFAULT_HARD_TIMEOUT)) {
			flow.setHardTimeout(null);
		}
		if (flow.getTableId() != null && flow.getTableId().equals(RyuConstants.DEFAULT_TABLE_ID)) {
			flow.setTableId(null);
		}

		// match fields
		if (flow.getMatch() != null) {
			if (flow.getMatch().getWildcards() != null && flow.getMatch().getWildcards().equals(RyuConstants.DEFAULT_MATCH_WILDCARDS)) {
				flow.getMatch().setWildcards(null);
			}
			if (flow.getMatch().getIngressPort() != null && flow.getMatch().getIngressPort().equals(RyuConstants.DEFAULT_MATCH_INGRESS_PORT)) {
				flow.getMatch().setIngressPort(null);
			}
			if (flow.getMatch().getSrcMac() != null && flow.getMatch().getSrcMac().equals(RyuConstants.DEFAULT_MATCH_SRC_MAC)) {
				flow.getMatch().setSrcMac(null);
			}
			if (flow.getMatch().getDstMac() != null && flow.getMatch().getDstMac().equals(RyuConstants.DEFAULT_MATCH_DST_MAC)) {
				flow.getMatch().setDstMac(null);
			}
			if (flow.getMatch().getVlanId() != null && flow.getMatch().getVlanId().equals(RyuConstants.DEFAULT_MATCH_VLAN_ID)) {
				flow.getMatch().setVlanId(null);
			}
			if (flow.getMatch().getVlanPriority() != null && flow.getMatch().getVlanPriority().equals(RyuConstants.DEFAULT_MATCH_VLAN_PRIORITY)) {
				flow.getMatch().setVlanPriority(null);
			}
			if (flow.getMatch().getEtherType() != null && flow.getMatch().getEtherType().equals(RyuConstants.DEFAULT_MATCH_ETHER_TYPE)) {
				flow.getMatch().setEtherType(null);
			}
			if (flow.getMatch().getProtocol() != null && flow.getMatch().getProtocol().equals(RyuConstants.DEFAULT_MATCH_PROTOCOL)) {
				flow.getMatch().setProtocol(null);
			}
			if (flow.getMatch().getSrcIp() != null && flow.getMatch().getSrcIp().equals(RyuConstants.DEFAULT_MATCH_SRC_IP)) {
				flow.getMatch().setSrcIp(null);
			}
			if (flow.getMatch().getDstIp() != null && flow.getMatch().getDstIp().equals(RyuConstants.DEFAULT_MATCH_DST_IP)) {
				flow.getMatch().setDstIp(null);
			}
			if (flow.getMatch().getSrcPort() != null && flow.getMatch().getSrcPort().equals(RyuConstants.DEFAULT_MATCH_SRC_PORT)) {
				flow.getMatch().setSrcPort(null);
			}
			if (flow.getMatch().getDstPort() != null && flow.getMatch().getDstPort().equals(RyuConstants.DEFAULT_MATCH_DST_PORT)) {
				flow.getMatch().setDstPort(null);
			}
		}

	}
}
