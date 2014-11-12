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
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * {@link RyuOFFlow} {@link JsonSerializer}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class RyuOFFlowJSONSerializer extends JsonSerializer<RyuOFFlow> {

	@Override
	public void serialize(RyuOFFlow flow, JsonGenerator jGen, SerializerProvider serializer) throws IOException, JsonProcessingException {

		jGen.writeStartObject();

		if (flow.getDpid() == null || flow.getDpid().isEmpty()) {
			throw new IOException("dpid field must be set!");
		}

		jGen.writeNumberField("dpid", Integer.parseInt(flow.getDpid()));

		if (flow.getPriority() != null)
			jGen.writeNumberField("priority", Integer.parseInt(flow.getPriority()));
		if (flow.getCookie() != null)
			jGen.writeNumberField("cookie", Integer.parseInt(flow.getCookie()));
		if (flow.getCookieMask() != null)
			jGen.writeNumberField("cookie_mask", Integer.parseInt(flow.getCookieMask()));
		if (flow.getTableId() != null)
			jGen.writeNumberField("table_id", Integer.parseInt(flow.getTableId()));
		if (flow.getIdleTimeout() != null)
			jGen.writeNumberField("idle_timeout", Integer.parseInt(flow.getIdleTimeout()));
		if (flow.getHardTimeout() != null)
			jGen.writeNumberField("hard_timeout", Integer.parseInt(flow.getHardTimeout()));
		if (flow.getBufferId() != null)
			jGen.writeNumberField("buffer_id", Integer.parseInt(flow.getBufferId()));
		if (flow.getFlags() != null)
			jGen.writeNumberField("flags", Integer.parseInt(flow.getFlags()));

		if (flow.getMatch() != null) {
			jGen.writeFieldName("match");
			jGen.writeStartObject();
			serializeMatch(flow.getMatch(), jGen, serializer);
			jGen.writeEndObject();
		}

		if (flow.getActions() != null) {
			jGen.writeFieldName("actions");
			serializeActions(flow.getActions(), jGen, serializer);
		}

		jGen.writeEndObject();
	}

	/**
	 * Fields in a Match are serialized as fields in the flow (no Match object separators)
	 * 
	 * @param match
	 * @param jGen
	 * @param serializer
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private void serializeMatch(FloodlightOFMatch match, JsonGenerator jGen, SerializerProvider serializer) throws IOException,
			JsonProcessingException {

		if (match == null)
			return;

		if (match.getWildcards() != null && !match.getWildcards().isEmpty())
			jGen.writeStringField("wildcards", match.getWildcards());

		if (match.getIngressPort() != null && !match.getIngressPort().isEmpty())
			jGen.writeNumberField("in_port", Integer.parseInt(match.getIngressPort()));

		if (match.getSrcMac() != null && !match.getSrcMac().isEmpty())
			jGen.writeStringField("dl_src", match.getSrcMac());

		if (match.getDstMac() != null && !match.getDstMac().isEmpty())
			jGen.writeStringField("dl_dst", match.getDstMac());

		if (match.getVlanId() != null && !match.getVlanId().isEmpty())
			jGen.writeStringField("dl_vlan", match.getVlanId());

		if (match.getVlanPriority() != null && !match.getVlanPriority().isEmpty())
			jGen.writeStringField("dl_vlan_pcp", match.getVlanPriority());

		if (match.getEtherType() != null && !match.getEtherType().isEmpty())
			jGen.writeStringField("dl_type", match.getEtherType());

		if (match.getTosBits() != null && !match.getTosBits().isEmpty())
			jGen.writeStringField("nw_tos", match.getTosBits());

		if (match.getProtocol() != null && !match.getProtocol().isEmpty())
			jGen.writeStringField("nw_proto", match.getProtocol());

		if (match.getSrcIp() != null && !match.getSrcIp().isEmpty())
			jGen.writeStringField("nw_src", match.getSrcIp());

		if (match.getDstIp() != null && !match.getDstIp().isEmpty())
			jGen.writeStringField("nw_dst", match.getDstIp());

		if (match.getSrcPort() != null && !match.getSrcPort().isEmpty())
			jGen.writeStringField("tcp_src", match.getSrcPort());

		if (match.getDstPort() != null && !match.getDstPort().isEmpty())
			jGen.writeStringField("tcp_dst", match.getDstPort());
	}

	/**
	 * The list of actions is serialized as this sample: "[{"type":"OUTPUT","port":2}]"
	 * 
	 * @param actions
	 * @param jGen
	 * @param serializer
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private void serializeActions(List<FloodlightOFAction> actions, JsonGenerator jGen, SerializerProvider serializer) throws IOException,
			JsonProcessingException {

		if (actions == null || actions.isEmpty())
			return;

		jGen.writeStartArray();
		for (FloodlightOFAction action : actions) {
			jGen.writeStartObject();
			jGen.writeStringField("type", action.getType().toUpperCase());
			if (action.getType().equalsIgnoreCase(FloodlightOFAction.TYPE_OUTPUT)) {
				jGen.writeNumberField("port", Integer.parseInt(action.getValue()));
			}
			jGen.writeEndObject();
		}
		jGen.writeEndArray();

	}

	@Override
	public Class<RyuOFFlow> handledType() {
		return RyuOFFlow.class;
	}

}
