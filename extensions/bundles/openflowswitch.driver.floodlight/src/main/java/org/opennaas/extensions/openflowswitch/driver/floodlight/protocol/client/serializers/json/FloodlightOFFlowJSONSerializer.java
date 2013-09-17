package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.serializers.json;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model.FloodlightOFMatch;

public class FloodlightOFFlowJSONSerializer extends
		JsonSerializer<FloodlightOFFlow> {

	@Override
	public void serialize(FloodlightOFFlow flow, JsonGenerator jGen,
			SerializerProvider serializer) throws IOException,
			JsonProcessingException {

		jGen.writeStartObject();
		if (flow.getSwitchId() != null)
			jGen.writeStringField("switch", flow.getSwitchId());
		if (flow.getName() != null)
			jGen.writeStringField("name", flow.getName());
		if (flow.getPriority() != null)
			jGen.writeStringField("priority", flow.getPriority());
		
		jGen.writeStringField("active", String.valueOf(flow.isActive()));
		
		if (flow.getMatch() != null)
			serializeMatch(flow.getMatch(), jGen, serializer);
		
		if (flow.getActions() != null)
			serializeActions(flow.getActions(), jGen, serializer);
		
		jGen.writeEndObject();

	}
	
	/**
	 * Fields in a Match are serialized as fields in the flow (no Match object separators)
	 * @param match
	 * @param jGen
	 * @param serializer
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private void serializeMatch(FloodlightOFMatch match, JsonGenerator jGen,
			SerializerProvider serializer) throws IOException,
			JsonProcessingException {
		
		if  (match == null)
			return;
		
		if (match.getWildcards() != null && !match.getWildcards().isEmpty())
			jGen.writeStringField("wildcards", match.getWildcards());
		
		if (match.getIngressPort() != null && !match.getIngressPort().isEmpty())
			jGen.writeStringField("ingress-port", match.getIngressPort());
		
		if (match.getSrcMac() != null && !match.getSrcMac().isEmpty())
			jGen.writeStringField("src-mac", match.getSrcMac());
		
		if (match.getDstMac() != null && !match.getDstMac().isEmpty())
			jGen.writeStringField("dst-mac", match.getDstMac());
		
		if (match.getVlanId() != null && !match.getVlanId().isEmpty())
			jGen.writeStringField("vlan-id", match.getVlanId());
		
		if (match.getVlanPriority() != null && !match.getVlanPriority().isEmpty())
			jGen.writeStringField("vlan-priority", match.getVlanPriority());
		
		if (match.getEtherType() != null && !match.getEtherType().isEmpty())
			jGen.writeStringField("ether-type", match.getEtherType());
		
		if (match.getTosBits() != null && !match.getTosBits().isEmpty())
			jGen.writeStringField("tos-bits", match.getTosBits());
		
		if (match.getProtocol() != null && !match.getProtocol().isEmpty())
			jGen.writeStringField("protocol", match.getProtocol());
		
		if (match.getSrcIp() != null && !match.getSrcIp().isEmpty())
			jGen.writeStringField("src-ip", match.getSrcIp());
		
		if (match.getDstIp() != null && !match.getDstIp().isEmpty())
			jGen.writeStringField("dst-ip", match.getDstIp());
		
		if (match.getSrcPort() != null && !match.getSrcPort().isEmpty())
			jGen.writeStringField("src-port", match.getSrcPort());
		
		if (match.getDstPort() != null && !match.getDstPort().isEmpty())
			jGen.writeStringField("dst-port", match.getDstPort());
	}
	
	/**
	 * The list of actions is serialized as a comma separated list of type=value elements:
	 * "actions":"output=2,set-vlan-id=1" 
	 * 
	 * @param actions
	 * @param jGen
	 * @param serializer
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private void serializeActions(List<FloodlightOFAction> actions, JsonGenerator jGen,
			SerializerProvider serializer) throws IOException,
			JsonProcessingException {
		
		if (actions == null || actions.isEmpty())
			return;
		
		StringBuilder sb = new StringBuilder();
		for (FloodlightOFAction action : actions) {
			sb.append(",");
			sb.append(action.getType());
			if (action.getValue() != null) {
				sb.append("=");
				sb.append(action.getValue());
			}
		}
		sb.deleteCharAt(0);	// delete first comma
		
		jGen.writeStringField("actions", sb.toString());
		
	}

	@Override
	public Class<FloodlightOFFlow> handledType() {
		return FloodlightOFFlow.class;
	}

}
