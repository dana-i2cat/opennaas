package org.opennaas.extensions.ofertie.ncl.test;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Flow;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QoSRequirements;

public class ProvisionerSerializationTest {

	@Test
	public void flowSerializationTest() throws SerializationException {
		Flow original = generateSampleFlow();
		String xml = ObjectSerializer.toXml(original);
		Flow generated = (Flow) ObjectSerializer.fromXml(xml, Flow.class);
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
	}

	@Test
	public void flowRequestSerializationTest() throws SerializationException {
		FlowRequest original = generateSampleFlow().getFlowRequest();
		String xml = ObjectSerializer.toXml(original);
		FlowRequest generated = (FlowRequest) ObjectSerializer.fromXml(xml, FlowRequest.class);
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
	}

	// <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	// <flow>
	// <id>1</id>
	// <flowRequest>
	// <requestId>1</requestId>
	// <sourceIPAddress>192.168.0.1</sourceIPAddress>
	// <destinationIPAddress>192.168.0.2</destinationIPAddress>
	// <sourcePort>8080</sourcePort>
	// <destinationPort>8080</destinationPort>
	// <tos>1</tos>
	// <sourceVlanId>1100</sourceVlanId>
	// <destinationVlanId>1100</destinationVlanId>
	// <qoSRequirements>
	// <minDelay>-1</minDelay>
	// <maxDelay>10</maxDelay>
	// <minJitter>-1</minJitter>
	// <maxJitter>10</maxJitter>
	// <minBandwidth>100000000</minBandwidth>
	// <maxBandwidth>-1</maxBandwidth>
	// <minPacketLoss>-1</minPacketLoss>
	// <maxPacketLoss>10</maxPacketLoss>
	// </qoSRequirements>
	// </flowRequest>
	// </flow>
	private Flow generateSampleFlow() {

		QoSRequirements qoSRequirements = new QoSRequirements();
		qoSRequirements.setMinBandwidth(100 * 1000 * 1000);
		qoSRequirements.setMaxBandwidth(-1);
		qoSRequirements.setMinDelay(-1);
		qoSRequirements.setMaxDelay(10);
		qoSRequirements.setMinJitter(-1);
		qoSRequirements.setMaxJitter(10);
		qoSRequirements.setMinPacketLoss(-1);
		qoSRequirements.setMaxPacketLoss(10);

		FlowRequest request = new FlowRequest();
		request.setRequestId("1");
		request.setSourceIPAddress("192.168.0.1");
		request.setDestinationIPAddress("192.168.0.2");
		request.setSourcePort(8080);
		request.setDestinationPort(8080);
		request.setSourceVlanId(1100);
		request.setDestinationVlanId(1100);
		request.setTos(1);
		request.setQoSRequirements(qoSRequirements);

		Flow flow = new Flow();
		flow.setId("1");
		flow.setFlowRequest(request);

		return flow;
	}

}
