package org.opennaas.itests.circuitprovisioning.internal;

/*
 * #%L
 * OpenNaaS :: iTests :: PathFinding :: Internal
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

import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.genericnetwork.actionsets.internal.circuitprovisioning.CircuitProvisioningActionsetImplementation;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.CircuitProvisioningCapability;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.ICircuitProvisioningCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.INCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.NCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;
import org.opennaas.extensions.genericnetwork.model.driver.NetworkConnectionImplementationId;
import org.opennaas.extensions.genericnetwork.model.topology.Domain;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.Switch;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.genericnetwork.repository.GenericNetworkRepository;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.repository.OpenflowSwitchRepository;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * {@link CircuitProvisioningCapability} integration test
 * 
 * @author Julio Carlos Barrera
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CircuitProvisioningTest {

	private final static Log	log								= LogFactory.getLog(CircuitProvisioningTest.class);

	@Inject
	protected IResourceManager	resourceManager;

	@Inject
	protected IProtocolManager	protocolManager;

	protected IResource			genericNetwork;

	protected IResource			mockedGenericNetwork;

	protected IResource			mockedOFSwitch;

	private static final String	GENERICNET_RESOURCE_NAME		= "sampleGenericNetwork";
	private static final String	MOCKED_GENERICNET_RESOURCE_NAME	= "mockGenericNetwork";
	private static final String	MOCKED_SWITCH_RESOURCE_NAME		= "mockSwitch";

	private static final String	MOCKED_CIRCUIT_ID				= "mockedCircuitId";
	private static final String	MOCKED_CIRCUIT_ID_BIS			= "mockedCircuitIdBis";

	private static final String	CAPABILITY_VERSION				= "1.0.0";

	private static final String	MOCK_URI						= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String	SRC_PORT_EXTERNAL				= "p0In";
	private static final String	DST_PORT_EXTERNAL				= "p0Out";

	private static final String	SRC_PORT_INTERNAL				= "p0InInternal";
	private static final String	DST_PORT_INTERNAL				= "p0OutInternal";

	private static final String	TOS								= "4";
	private static final String	TOS_BIS							= "16";

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-openflowswitch", "opennaas-openflowswitch-driver-floodlight",
						"opennaas-genericnetwork", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(),
				OpennaasExamOptions.keepRuntimeFolder(),
				OpennaasExamOptions.keepLogConfiguration()
		// , OpennaasExamOptions.openDebugSocket()
		);
	}

	@Before
	public void prepareTest() throws ResourceException, ProtocolException, IOException {
		startResource();
		startMockedGenericNetwork();
		startMockedOFSwitch();
	}

	@After
	public void shutdownTest() throws ResourceException, ProtocolException {
		stopAndRemoveResource();
		stopAndRemoveMockedGenericNetwork();
		stopAndRemoveMockedSwitch();
	}

	@Test
	public void allocateReplaceDeallocateWithOneDomainTest() throws Exception {
		log.info("Start allocateDeallocateWithOneDomainTest");

		// inject generated topology to generic network
		injectToplogyToResource(genericNetwork, generateDomainTopology());

		// inject and get INCLProvisionerCapability from the mocked generic network
		INCLProvisionerCapability mockedNCLProvisionerCapability = injectMockedGenericNetwork(mockedGenericNetwork);

		// ******************************//
		// **********ALLOCATE************//
		// ******************************//

		// generate a test Circuit to be allocated
		Circuit d0Circuit = generateCircuit();

		// prepare a mocked returned ID
		Mockito.when(mockedNCLProvisionerCapability.allocateCircuit(Mockito.any(CircuitRequest.class))).thenReturn(MOCKED_CIRCUIT_ID);

		// call ICircuitProvisioningCapability allocate() method of generic network
		ICircuitProvisioningCapability circuitProvisioningCapability = (ICircuitProvisioningCapability) genericNetwork
				.getCapabilityByInterface(ICircuitProvisioningCapability.class);
		circuitProvisioningCapability.allocate(d0Circuit);

		// capture CircuitRequest
		ArgumentCaptor<CircuitRequest> capturedCircuitRequest = ArgumentCaptor.forClass(CircuitRequest.class);

		// verifications and asserts of the captured CircuitRequest
		Mockito.verify(mockedNCLProvisionerCapability, Mockito.times(1)).allocateCircuit(capturedCircuitRequest.capture());

		CircuitRequest receivedCircuitRequest = capturedCircuitRequest.getValue();
		Assert.assertNotNull("CircuitRequest must be set", receivedCircuitRequest);

		Assert.assertEquals("Generated Label must be ToS multiplied by 4", String.valueOf(Integer.parseInt(TOS) * 4),
				receivedCircuitRequest.getLabel());

		Assert.assertEquals("Generated QoS Policy must be the same", generateQoSPolicy(), receivedCircuitRequest.getQosPolicy());

		FloodlightOFMatch expectedTrafficFilter = generateTrafficFilter(SRC_PORT_INTERNAL);
		Assert.assertEquals("Generated Source IP Address must be the same", expectedTrafficFilter.getSrcIp(), receivedCircuitRequest.getSource()
				.getAddress());
		Assert.assertEquals("Generated Source Port must be the same", expectedTrafficFilter.getSrcPort(), receivedCircuitRequest.getSource()
				.getTransportPort());
		Assert.assertEquals("Generated Destination IP Address must be the same", expectedTrafficFilter.getDstIp(), receivedCircuitRequest
				.getDestination().getAddress());
		Assert.assertEquals("Generated Destination Port must be the same", expectedTrafficFilter.getDstPort(), receivedCircuitRequest
				.getDestination().getTransportPort());

		// asserts of the model
		GenericNetworkModel model = ((GenericNetworkModel) genericNetwork.getModel());

		Assert.assertEquals("Allocated circuit map must contain the recently created circuit", d0Circuit,
				model.getAllocatedCircuits().get(d0Circuit.getCircuitId()));

		List<NetworkConnectionImplementationId> expectedCircuitImplementation = model.getCircuitImplementation().get(d0Circuit.getCircuitId());
		List<NetworkConnectionImplementationId> generatedCircuitImplementation = Arrays.asList(generateConnectionImplementationId(MOCKED_CIRCUIT_ID,
				generateDomainTopology().getNetworkElements().iterator().next()
						.getId()));
		Assert.assertEquals("CircuitImplementation map must be the same", expectedCircuitImplementation.size(), generatedCircuitImplementation.size());
		Assert.assertEquals("CircuitImplementation must be the same", expectedCircuitImplementation.get(0), generatedCircuitImplementation.get(0));

		// ******************************//
		// **********REPLACE*************//
		// ******************************//

		// generate a new circuit
		Circuit d1Circuit = generateCircuit();
		d1Circuit.setCircuitId("C1BIS");
		d1Circuit.getTrafficFilter().setTosBits(TOS_BIS);

		// prepare a mocked returned ID
		Mockito.when(mockedNCLProvisionerCapability.allocateCircuit(Mockito.any(CircuitRequest.class))).thenReturn(MOCKED_CIRCUIT_ID_BIS);

		circuitProvisioningCapability.replace(Arrays.asList(d0Circuit), Arrays.asList(d1Circuit));

		// capture CircuitRequest and CircuitId
		capturedCircuitRequest = ArgumentCaptor.forClass(CircuitRequest.class);
		ArgumentCaptor<String> capturedCircuitId = ArgumentCaptor.forClass(String.class);

		// verifications and asserts of the captured CircuitRequest ad CircuitId
		Mockito.verify(mockedNCLProvisionerCapability, Mockito.times(1)).deallocateCircuit(capturedCircuitId.capture());
		Mockito.verify(mockedNCLProvisionerCapability, Mockito.times(2)).allocateCircuit(capturedCircuitRequest.capture());

		String receivedCircuitId = capturedCircuitId.getValue();
		Assert.assertEquals("Generated cicuitId must be previous created one", MOCKED_CIRCUIT_ID, receivedCircuitId);

		receivedCircuitRequest = capturedCircuitRequest.getValue();
		Assert.assertNotNull("CircuitRequest must be set", receivedCircuitRequest);

		Assert.assertEquals("Generated Label must be ToS multiplied by 4", String.valueOf(Integer.parseInt(TOS_BIS) * 4),
				receivedCircuitRequest.getLabel());

		Assert.assertEquals("Generated QoS Policy must be the same", generateQoSPolicy(), receivedCircuitRequest.getQosPolicy());

		expectedTrafficFilter = generateTrafficFilter(SRC_PORT_INTERNAL);
		Assert.assertEquals("Generated Source IP Address must be the same", expectedTrafficFilter.getSrcIp(), receivedCircuitRequest.getSource()
				.getAddress());
		Assert.assertEquals("Generated Source Port must be the same", expectedTrafficFilter.getSrcPort(), receivedCircuitRequest.getSource()
				.getTransportPort());
		Assert.assertEquals("Generated Destination IP Address must be the same", expectedTrafficFilter.getDstIp(), receivedCircuitRequest
				.getDestination().getAddress());
		Assert.assertEquals("Generated Destination Port must be the same", expectedTrafficFilter.getDstPort(), receivedCircuitRequest
				.getDestination().getTransportPort());

		// asserts of the model
		Assert.assertEquals("Allocated circuit map must contain the recently created circuit", d1Circuit,
				model.getAllocatedCircuits().get(d1Circuit.getCircuitId()));

		expectedCircuitImplementation = model.getCircuitImplementation().get(d1Circuit.getCircuitId());
		generatedCircuitImplementation = Arrays.asList(generateConnectionImplementationId(MOCKED_CIRCUIT_ID_BIS, generateDomainTopology()
				.getNetworkElements().iterator().next().getId()));
		Assert.assertEquals("CircuitImplementation map must be the same", expectedCircuitImplementation.size(), generatedCircuitImplementation.size());
		Assert.assertEquals("CircuitImplementation must be the same", expectedCircuitImplementation.get(0), generatedCircuitImplementation.get(0));

		// ******************************//
		// *********DEALLOCATE***********//
		// ******************************//
		circuitProvisioningCapability.deallocate(d1Circuit.getCircuitId());

		// capture CircuitId
		capturedCircuitId = ArgumentCaptor.forClass(String.class);

		// verifications and asserts of the captured CircuitId
		Mockito.verify(mockedNCLProvisionerCapability, Mockito.times(2)).deallocateCircuit(capturedCircuitId.capture());

		receivedCircuitId = capturedCircuitId.getValue();

		Assert.assertEquals("Generated cicuitId must be previous created one", MOCKED_CIRCUIT_ID_BIS, receivedCircuitId);

		// asserts of the model
		Assert.assertEquals("Allocated circuit map must contain zero entries", 0, model.getAllocatedCircuits().size());

		Assert.assertEquals("CircuitImplementation map must contain zero entries", 0, model.getCircuitImplementation().size());

		log.info("End allocateDeallocateWithOneDomainTest");
	}

	@Test
	public void allocateDeallocateWithOneSwitchTest() throws Exception {
		log.info("Start allocateDeallocateWithOneSwitchTest");

		// inject generated topology to generic network
		injectToplogyToResource(genericNetwork, generateSwitchTopology());

		// inject and get INCLProvisionerCapability from the mocked generic network
		IOpenflowForwardingCapability mockedOpenflowForwardingCapability = injectMockedSwitch(mockedOFSwitch);

		// ******************************//
		// **********ALLOCATE************//
		// ******************************//

		// generate a test Circuit to be allocated
		Circuit s0Circuit = generateCircuit();

		// call ICircuitProvisioningCapability allocate() method of generic network
		ICircuitProvisioningCapability circuitProvisioningCapability = (ICircuitProvisioningCapability) genericNetwork
				.getCapabilityByInterface(ICircuitProvisioningCapability.class);
		circuitProvisioningCapability.allocate(s0Circuit);

		// capture FloodlightOFFlow
		ArgumentCaptor<FloodlightOFFlow> capturedFloodlightOFFlow = ArgumentCaptor.forClass(FloodlightOFFlow.class);

		// verifications and asserts of the captured FloodlightOFFlow
		Mockito.verify(mockedOpenflowForwardingCapability, Mockito.times(1)).createOpenflowForwardingRule(capturedFloodlightOFFlow.capture());

		FloodlightOFFlow receivedFloodlightOFFlow = capturedFloodlightOFFlow.getValue();
		Assert.assertNotNull("FloodlightOFFlow must be set", receivedFloodlightOFFlow);

		Assert.assertEquals("Generated FloodlightOFMatch must be the same", generateTrafficFilter(SRC_PORT_INTERNAL),
				receivedFloodlightOFFlow.getMatch());

		// asserts of the model
		GenericNetworkModel model = ((GenericNetworkModel) genericNetwork.getModel());

		Assert.assertEquals("Allocated circuit map must only one circuit", 1, model.getAllocatedCircuits().size());

		Entry<String, Circuit> allocatedCircuitsMapEntry = model.getAllocatedCircuits().entrySet().iterator().next();
		Assert.assertEquals("Allocated circuit map must contain the recently created circuit", s0Circuit, allocatedCircuitsMapEntry.getValue());

		/* it is not possible knowing the driver internal ID */
		List<NetworkConnectionImplementationId> expectedCircuitImplementation = Arrays.asList(generateConnectionImplementationId(null,
				generateSwitchTopology().getNetworkElements().iterator().next().getId()));
		List<NetworkConnectionImplementationId> generatedCircuitImplementation = model.getCircuitImplementation().get(s0Circuit.getCircuitId());
		Assert.assertEquals("CircuitImplementation map must be the same", expectedCircuitImplementation.size(), generatedCircuitImplementation.size());
		Assert.assertEquals("CircuitImplementation must be the same", expectedCircuitImplementation.get(0).getDeviceId(),
				generatedCircuitImplementation.get(0).getDeviceId());

		// ******************************//
		// **********REPLACE*************//
		// ******************************//

		// generate a new circuit
		Circuit s1Circuit = generateCircuit();
		s1Circuit.setCircuitId("C1BIS");
		s1Circuit.getTrafficFilter().setTosBits(TOS_BIS);

		circuitProvisioningCapability.replace(Arrays.asList(s0Circuit), Arrays.asList(s1Circuit));

		// capture FloodlightOFFlowId and FloodlightOFFlow
		ArgumentCaptor<String> capturedFloodlightOFFlowId = ArgumentCaptor.forClass(String.class);
		capturedFloodlightOFFlow = ArgumentCaptor.forClass(FloodlightOFFlow.class);

		// verifications and asserts of the captured CircuitRequest ad CircuitId
		Mockito.verify(mockedOpenflowForwardingCapability, Mockito.times(1)).removeOpenflowForwardingRule(capturedFloodlightOFFlowId.capture());
		Mockito.verify(mockedOpenflowForwardingCapability, Mockito.times(2)).createOpenflowForwardingRule(capturedFloodlightOFFlow.capture());

		String receivedFloodlightOFFlowId = capturedFloodlightOFFlowId.getValue();
		String previousFloodlightOFFlowId = receivedFloodlightOFFlow.getName();

		Assert.assertEquals("Generated floodlightOFFlowId must be previous created one", previousFloodlightOFFlowId, receivedFloodlightOFFlowId);

		receivedFloodlightOFFlow = capturedFloodlightOFFlow.getValue();
		Assert.assertNotNull("FloodlightOFFlow must be set", receivedFloodlightOFFlow);

		FloodlightOFMatch expectedTrafficFilter = generateTrafficFilter(SRC_PORT_INTERNAL);
		expectedTrafficFilter.setTosBits(TOS_BIS);
		Assert.assertEquals("Generated FloodlightOFMatch must be the same", expectedTrafficFilter, receivedFloodlightOFFlow.getMatch());

		// asserts of the model
		model = ((GenericNetworkModel) genericNetwork.getModel());

		Assert.assertEquals("Allocated circuit map must only one circuit", 1, model.getAllocatedCircuits().size());

		allocatedCircuitsMapEntry = model.getAllocatedCircuits().entrySet().iterator().next();
		Assert.assertEquals("Allocated circuit map must contain the recently created circuit", s1Circuit, allocatedCircuitsMapEntry.getValue());

		/* it is not possible knowing the driver internal ID */
		expectedCircuitImplementation = Arrays.asList(generateConnectionImplementationId(null,
				generateSwitchTopology().getNetworkElements().iterator().next().getId()));
		generatedCircuitImplementation = model.getCircuitImplementation().get(s1Circuit.getCircuitId());
		Assert.assertEquals("CircuitImplementation map must be the same", expectedCircuitImplementation.size(), generatedCircuitImplementation.size());
		Assert.assertEquals("CircuitImplementation must be the same", expectedCircuitImplementation.get(0).getDeviceId(),
				generatedCircuitImplementation.get(0).getDeviceId());

		// ******************************//
		// *********DEALLOCATE***********//
		// ******************************//
		circuitProvisioningCapability.deallocate(s1Circuit.getCircuitId());

		// capture FloodlightOFFlowId
		capturedFloodlightOFFlowId = ArgumentCaptor.forClass(String.class);

		// verifications and asserts of the captured FloodlightOFFlowId
		Mockito.verify(mockedOpenflowForwardingCapability, Mockito.times(2)).removeOpenflowForwardingRule(capturedFloodlightOFFlowId.capture());

		receivedFloodlightOFFlowId = capturedFloodlightOFFlowId.getValue();
		previousFloodlightOFFlowId = receivedFloodlightOFFlow.getName();

		Assert.assertEquals("Generated floodlightOFFlowId must be previous created one", previousFloodlightOFFlowId, receivedFloodlightOFFlowId);

		// asserts of the model
		Assert.assertEquals("Allocated circuit map must contain zero entries", 0, model.getAllocatedCircuits().size());

		Assert.assertEquals("CircuitImplementation map must contain zero entries", 0, model.getCircuitImplementation().size());

		log.info("End allocateDeallocateWithOneSwitchTest");
	}

	private static NetworkConnectionImplementationId generateConnectionImplementationId(String driverId, String networkelementId) {
		NetworkConnectionImplementationId networkConnectionImplementationId = new NetworkConnectionImplementationId();
		networkConnectionImplementationId.setCircuitId(driverId);
		networkConnectionImplementationId.setDeviceId(networkelementId);
		return networkConnectionImplementationId;
	}

	private static Circuit generateCircuit() {
		Circuit circuit = new Circuit();

		circuit.setTrafficFilter(generateTrafficFilter(SRC_PORT_EXTERNAL));

		circuit.setCircuitId("C1");

		circuit.setQos(generateQoSPolicy());

		// set Route with a NetworkConnection
		Route route = new Route();
		route.setId("R1");

		List<NetworkConnection> networkConnections = new ArrayList<NetworkConnection>();
		NetworkConnection networkConnection = new NetworkConnection();
		networkConnection.setName("NC1");
		networkConnection.setId("NC1");
		networkConnection.setSource(generatePort(SRC_PORT_EXTERNAL));
		networkConnection.setDestination(generatePort(DST_PORT_EXTERNAL));
		networkConnections.add(networkConnection);

		route.setNetworkConnections(networkConnections);

		circuit.setRoute(route);

		return circuit;
	}

	private static Topology generateDomainTopology() {
		Topology topology = new Topology();

		// domain d0, the name is <resource_type:resource_name>
		Domain d0 = new Domain();
		d0.setId(GenericNetworkRepository.GENERIC_NETWORK_RESOURCE_TYPE + ":" + MOCKED_GENERICNET_RESOURCE_NAME);

		// ports of domain d0
		Set<Port> d0Ports = new HashSet<Port>();

		d0Ports.add(generatePort(SRC_PORT_EXTERNAL));
		d0Ports.add(generatePort(DST_PORT_EXTERNAL));
		d0.setPorts(d0Ports);

		topology.setNetworkElements(new HashSet<NetworkElement>());
		topology.getNetworkElements().add(d0);

		// generate translation BiMap
		BiMap<String, DevicePortId> biMap = HashBiMap.create();

		biMap.put(SRC_PORT_EXTERNAL, generateDevicePortId(d0.getId(), SRC_PORT_INTERNAL));
		biMap.put(DST_PORT_EXTERNAL, generateDevicePortId(d0.getId(), DST_PORT_INTERNAL));

		topology.setNetworkDevicePortIdsMap(biMap);

		return topology;
	}

	private static DevicePortId generateDevicePortId(String deviceId, String portId) {
		DevicePortId devicePortId = new DevicePortId();
		devicePortId.setDeviceId(deviceId);
		devicePortId.setDevicePortId(portId);
		return devicePortId;
	}

	private static Topology generateSwitchTopology() {
		Topology topology = new Topology();

		// switch s0, the name is <resource_type:resource_name>
		Switch s0 = new Switch();
		s0.setId(OpenflowSwitchRepository.OF_SWITCH_RESOURCE_TYPE + ":" + MOCKED_SWITCH_RESOURCE_NAME);

		// ports of domain s0
		Set<Port> s0Ports = new HashSet<Port>();

		s0Ports.add(generatePort(SRC_PORT_EXTERNAL));
		s0Ports.add(generatePort(DST_PORT_EXTERNAL));
		s0.setPorts(s0Ports);

		topology.setNetworkElements(new HashSet<NetworkElement>());
		topology.getNetworkElements().add(s0);

		// generate translation BiMap
		BiMap<String, DevicePortId> biMap = HashBiMap.create();

		biMap.put(SRC_PORT_EXTERNAL, generateDevicePortId(s0.getId(), SRC_PORT_INTERNAL));
		biMap.put(DST_PORT_EXTERNAL, generateDevicePortId(s0.getId(), DST_PORT_INTERNAL));

		topology.setNetworkDevicePortIdsMap(biMap);

		return topology;
	}

	private static FloodlightOFMatch generateTrafficFilter(String ingressPort) {
		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(ingressPort);
		match.setTosBits(TOS);
		return match;
	}

	private static QoSPolicy generateQoSPolicy() {
		QoSPolicy qoSPolicy = new QoSPolicy();
		qoSPolicy.setMinJitter(123);
		qoSPolicy.setMaxJitter(234);
		qoSPolicy.setMinLatency(345);
		qoSPolicy.setMaxLatency(456);
		qoSPolicy.setMinThroughput(567);
		qoSPolicy.setMaxThroughput(678);
		qoSPolicy.setMinPacketLoss(789);
		qoSPolicy.setMaxPacketLoss(890);
		return qoSPolicy;
	}

	private static Port generatePort(String portId) {
		Port port = new Port();
		port.setId(portId);
		return port;
	}

	private static void injectToplogyToResource(IResource genericNetwork, Topology topology) {
		((GenericNetworkModel) genericNetwork.getModel()).setTopology(topology);
	}

	private static INCLProvisionerCapability injectMockedGenericNetwork(IResource genericNetwork) throws Exception {
		// Replace NCLProvisionerCapability with a mocked one.
		INCLProvisionerCapability mockedNCLProvisionerCapability = generateMockedNCLProvisionerCapability();

		List<ICapability> newCapabilities = new ArrayList<ICapability>();
		for (ICapability capability : genericNetwork.getCapabilities()) {
			if (!(capability instanceof INCLProvisionerCapability))
				newCapabilities.add(capability);
		}
		newCapabilities.add(mockedNCLProvisionerCapability);
		((Resource) genericNetwork).setCapabilities(newCapabilities);

		return mockedNCLProvisionerCapability;
	}

	private static INCLProvisionerCapability generateMockedNCLProvisionerCapability() throws Exception {
		INCLProvisionerCapability cap = PowerMockito.mock(NCLProvisionerCapability.class);

		// initialize internal loggers (Capability and AbstractCapability instances)
		// Whitebox.setInternalState(cap, Log.class, LogFactory.getLog(INCLProvisionerCapability.class));
		Whitebox.setInternalState(cap, Log.class, LogFactory.getLog(INCLProvisionerCapability.class), AbstractCapability.class);

		return cap;
	}

	private static IOpenflowForwardingCapability injectMockedSwitch(IResource switchResource) throws Exception {
		// Replace IOpenflowForwardingCapability with a mocked one.
		IOpenflowForwardingCapability mockedOpenflowForwardingCapability = generateMockedOpenflowForwardingCapability();

		List<ICapability> newCapabilities = new ArrayList<ICapability>();
		for (ICapability capability : switchResource.getCapabilities()) {
			if (!(capability instanceof IOpenflowForwardingCapability))
				newCapabilities.add(capability);
		}
		newCapabilities.add(mockedOpenflowForwardingCapability);
		((Resource) switchResource).setCapabilities(newCapabilities);

		return mockedOpenflowForwardingCapability;
	}

	private static IOpenflowForwardingCapability generateMockedOpenflowForwardingCapability() throws Exception {
		IOpenflowForwardingCapability cap = PowerMockito.mock(OpenflowForwardingCapability.class);

		// initialize internal loggers (Capability and AbstractCapability instances)
		Whitebox.setInternalState(cap, Log.class, LogFactory.getLog(IOpenflowForwardingCapability.class));
		Whitebox.setInternalState(cap, Log.class, LogFactory.getLog(IOpenflowForwardingCapability.class), AbstractCapability.class);

		return cap;
	}

	/**
	 * Start genericnetwork resource with {@link CircuitProvisioningCapability}
	 * 
	 * @throws IOException
	 * 
	 */
	protected void startResource() throws ResourceException, ProtocolException, IOException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor circuitProvisioningDescriptor = ResourceHelper.newCapabilityDescriptor(
				CircuitProvisioningActionsetImplementation.ACTIONSET_ID, CAPABILITY_VERSION, CircuitProvisioningCapability.CAPABILITY_TYPE, MOCK_URI);
		lCapabilityDescriptors.add(circuitProvisioningDescriptor);

		// GenericNetwork Resource Descriptor
		ResourceDescriptor genericNetworkDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors,
				GenericNetworkRepository.GENERIC_NETWORK_RESOURCE_TYPE, MOCK_URI, GENERICNET_RESOURCE_NAME);

		// Create resource
		genericNetwork = resourceManager.createResource(genericNetworkDescriptor);

		// Start resource
		resourceManager.startResource(genericNetwork.getResourceIdentifier());
	}

	/**
	 * Stop and remove the genericnetwork resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void stopAndRemoveResource() throws ResourceException, ProtocolException {
		// Stop resource
		resourceManager.stopResource(genericNetwork.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(genericNetwork.getResourceIdentifier());
	}

	/**
	 * Start genericnetwork mocked resource with {@link NCLProvisionerCapability}
	 * 
	 * @throws IOException
	 * 
	 */
	protected void startMockedGenericNetwork() throws ResourceException, ProtocolException, IOException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor mockedNCLProvisionerCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(null, CAPABILITY_VERSION,
				NCLProvisionerCapability.CAPABILITY_TYPE, MOCK_URI);
		lCapabilityDescriptors.add(mockedNCLProvisionerCapabilityDescriptor);

		// GenericNetwork Resource Descriptor
		ResourceDescriptor mockedGenericNetworkDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors,
				GenericNetworkRepository.GENERIC_NETWORK_RESOURCE_TYPE, MOCK_URI, MOCKED_GENERICNET_RESOURCE_NAME);

		// Create resource
		mockedGenericNetwork = resourceManager.createResource(mockedGenericNetworkDescriptor);

		// Start resource
		resourceManager.startResource(mockedGenericNetwork.getResourceIdentifier());
	}

	/**
	 * Stop and remove the mocked genericnetwork resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void stopAndRemoveMockedGenericNetwork() throws ResourceException, ProtocolException {
		// Stop resource
		resourceManager.stopResource(mockedGenericNetwork.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(mockedGenericNetwork.getResourceIdentifier());
	}

	/**
	 * Start ofswitch mocked resource with {@link IOpenflowForwardingCapability}
	 * 
	 * @throws IOException
	 * 
	 */
	protected void startMockedOFSwitch() throws ResourceException, ProtocolException, IOException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor mockedOpenflowForwardingCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(null, CAPABILITY_VERSION,
				OpenflowForwardingCapability.CAPABILITY_TYPE, MOCK_URI);
		lCapabilityDescriptors.add(mockedOpenflowForwardingCapabilityDescriptor);

		// GenericNetwork Resource Descriptor
		ResourceDescriptor mockedOFSwitchDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors,
				OpenflowSwitchRepository.OF_SWITCH_RESOURCE_TYPE, MOCK_URI, MOCKED_SWITCH_RESOURCE_NAME);

		// Create resource
		mockedOFSwitch = resourceManager.createResource(mockedOFSwitchDescriptor);

		// add context
		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, MOCKED_SWITCH_RESOURCE_NAME);

		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, mockedOFSwitch.getResourceIdentifier().getId(),
				"http://no_server/", "floodlight", sessionParameters);

		// Start resource
		resourceManager.startResource(mockedOFSwitch.getResourceIdentifier());
	}

	/**
	 * Stop and remove the mocked ofswitch resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void stopAndRemoveMockedSwitch() throws ResourceException, ProtocolException {
		// Stop resource
		resourceManager.stopResource(mockedOFSwitch.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(mockedOFSwitch.getResourceIdentifier());
	}

}
