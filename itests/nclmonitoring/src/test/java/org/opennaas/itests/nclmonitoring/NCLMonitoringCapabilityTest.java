package org.opennaas.itests.nclmonitoring;

/*
 * #%L
 * OpenNaaS :: iTests :: NCL Monitoring
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.extensions.genericnetwork.capability.nclmonitoring.NCLMonitoringCapability;
import org.opennaas.extensions.genericnetwork.capability.statistics.INetworkStatisticsCapability;
import org.opennaas.extensions.genericnetwork.capability.statistics.NetworkStatisticsCapability;
import org.opennaas.extensions.genericnetwork.events.PortCongestionEvent;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.NetworkStatistics;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.Switch;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.SwitchPortStatistics;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NCLMonitoringCapabilityTest implements EventHandler {

	private static final String			ACTIONSET_NAME					= "internal";
	private static final String			ACTIONSET_VERSION				= "1.0.0";

	private static final String			GENERIC_NETWORK_RESOURCE_TYPE	= "genericnetwork";
	private static final String			RESOURCE_NAME					= "net1";

	private static final String			SWITCH_ID						= "ofswitch:switch1";
	private static final String			PORT1_ID						= "s1_1";
	private static final String			PORT2_ID						= "s2_1";

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.genericnetwork)", timeout = 50000)
	private BlueprintContainer			genericnetworkBlueprintContainer;

	@Inject
	private BundleContext				bundleContext;

	@Inject
	private IResourceManager			resourceManager;

	private List<PortCongestionEvent>	receivedPortCongestionEvents;
	private ServiceRegistration			eventListenerRegistration;

	private final Object				lock							= new Object();

	private IResource					network;
	private NetworkStatistics			netStatistics;

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-genericnetwork", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(), 
				OpennaasExamOptions.keepLogConfiguration(),
				// OpennaasExamOptions.openDebugSocket(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Before
	public void init() throws ResourceException {
		generateNetStatistics();
		createGenericNetworkWithNCLMonitoringCapability();
		registerAsEventListener();
	}

	@After
	public void stop() throws ResourceException {
		removeGenericNetwork();
		unregisterAsEventListener();
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof PortCongestionEvent) {
			synchronized (lock) {
				receivedPortCongestionEvents.add((PortCongestionEvent) event);
				lock.notify();
			}
		}
	}

	@Test
	public void NCLMonitoringCapabilityLaunchesPortCongestionEventTest() throws InterruptedException {

		synchronized (lock) {
			// fail if after 30 seconds an event has not been received
			lock.wait(30 * 1000);
			Assert.assertFalse(receivedPortCongestionEvents.isEmpty());
			// check ports are marked as congested in model
			for (PortCongestionEvent event : receivedPortCongestionEvents) {
				for (NetworkElement ne : ((GenericNetworkModel) network.getModel()).getTopology().getNetworkElements()) {
					for (Port port : ne.getPorts()) {
						if (port.getId().equals(event.getProperty(PortCongestionEvent.PORT_ID_KEY))) {
							Assert.assertTrue(port.getState().isCongested());
						}
					}
				}

			}
		}

	}

	private void registerAsEventListener() {
		receivedPortCongestionEvents = new ArrayList<PortCongestionEvent>();

		Properties properties = new Properties();
		properties.put(EventConstants.EVENT_TOPIC, PortCongestionEvent.TOPIC);
		properties.put(PortCongestionEvent.NETWORK_ID_KEY, network.getResourceIdentifier().getId());

		eventListenerRegistration = bundleContext.registerService(EventHandler.class.getName(), this, properties);
	}

	public void unregisterAsEventListener() {
		eventListenerRegistration.unregister();
	}

	private void createGenericNetworkWithNCLMonitoringCapability() throws ResourceException {

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor nclMonitoringCapab = ResourceHelper.newCapabilityDescriptor(ACTIONSET_NAME,
				ACTIONSET_VERSION, NCLMonitoringCapability.CAPABILITY_TYPE, null);

		lCapabilityDescriptors.add(nclMonitoringCapab);

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, GENERIC_NETWORK_RESOURCE_TYPE,
				null, RESOURCE_NAME);

		network = resourceManager.createResource(resourceDescriptor);

		// Start resource
		resourceManager.startResource(network.getResourceIdentifier());

		// set topology with a single switch
		((GenericNetworkModel) network.getModel()).setTopology(generateSampleTopology());

		// Replace NetworkStatisticsCapability with a mocked one.
		INetworkStatisticsCapability mockedNetStatisticsCapab = generateMockedNetStatisticsCapability();

		List<ICapability> newCapabilities = new ArrayList<ICapability>();
		for (ICapability capability : network.getCapabilities()) {
			if (!(capability instanceof INetworkStatisticsCapability))
				newCapabilities.add(capability);
		}
		newCapabilities.add(mockedNetStatisticsCapab);
		((Resource) network).setCapabilities(newCapabilities);
	}

	private void removeGenericNetwork() throws ResourceException {

		if (network.getState().equals(State.ACTIVE))
			resourceManager.stopResource(network.getResourceIdentifier());

		resourceManager.removeResource(network.getResourceIdentifier());
	}

	private Topology generateSampleTopology() {

		Port port1 = new Port();
		port1.setId(PORT1_ID);

		Port port2 = new Port();
		port2.setId(PORT2_ID);

		Switch switch1 = new Switch();
		switch1.setId(SWITCH_ID);

		Set<Port> switch1Ports = new HashSet<Port>();
		switch1Ports.addAll(Arrays.asList(port1, port2));
		switch1.setPorts(switch1Ports);

		Set<NetworkElement> networkElements = new HashSet<NetworkElement>();
		networkElements.add(switch1);

		Topology topology = new Topology();
		topology.setNetworkElements(networkElements);

		return topology;
	}

	private INetworkStatisticsCapability generateMockedNetStatisticsCapability() {
		INetworkStatisticsCapability mock = PowerMockito.mock(NetworkStatisticsCapability.class);
		try {
			OngoingStubbing<NetworkStatistics> stub = Mockito.when(mock.getNetworkStatistics()).thenReturn(netStatistics);
			for (int i = 0; i < 5; i++) {
				netStatistics = increase1GTraffic(netStatistics);
				stub.thenReturn(netStatistics);
			}
		} catch (CapabilityException e) {
			// ignored
			// will not throw exception while mocking!!!
		}
		return mock;
	}

	private void generateNetStatistics() {
		netStatistics = generateNetStatisticsWithoutTraffic();
	}

	private NetworkStatistics generateNetStatisticsWithoutTraffic() {

		PortStatistics port1Statistics = generatePortStatisticsWithoutTraffic();
		port1Statistics.setPort(1);

		PortStatistics port2Statistics = generatePortStatisticsWithoutTraffic();
		port2Statistics.setPort(2);

		Map<String, PortStatistics> switch1PortStatisticsMap = new HashMap<String, PortStatistics>();
		switch1PortStatisticsMap.put(PORT1_ID, port1Statistics);
		switch1PortStatisticsMap.put(PORT2_ID, port1Statistics);

		SwitchPortStatistics switch1PortStatistics = new SwitchPortStatistics();
		switch1PortStatistics.setStatistics(switch1PortStatisticsMap);

		Map<String, SwitchPortStatistics> netStatisticsMap = new HashMap<String, SwitchPortStatistics>();
		netStatisticsMap.put(SWITCH_ID, switch1PortStatistics);

		NetworkStatistics netStatistics = new NetworkStatistics();
		netStatistics.setSwitchStatistics(netStatisticsMap);

		return netStatistics;

	}

	private PortStatistics generatePortStatisticsWithoutTraffic() {

		PortStatistics statistics = new PortStatistics();
		statistics.setReceiveBytes(0);

		return statistics;
	}

	private NetworkStatistics increase1GTraffic(NetworkStatistics netStatistics) {

		Map<String, SwitchPortStatistics> netStatisticsMap = new HashMap<String, SwitchPortStatistics>();

		for (String switchId : netStatistics.getSwitchStatistics().keySet()) {
			SwitchPortStatistics switchStatistics = netStatistics.getSwitchStatistics().get(switchId);
			Map<String, PortStatistics> switchStatisticsMap = new HashMap<String, PortStatistics>();
			for (String portId : switchStatistics.getStatistics().keySet()) {
				PortStatistics portStatistics = switchStatistics.getStatistics().get(portId);
				PortStatistics portStatisticsIncreased = new PortStatistics();
				portStatisticsIncreased.setReceiveBytes(portStatistics.getReceiveBytes() + 1000000000);
				switchStatisticsMap.put(portId, portStatisticsIncreased);
			}
			SwitchPortStatistics switchStatisticsCopy = new SwitchPortStatistics();
			switchStatisticsCopy.setStatistics(switchStatisticsMap);
			netStatisticsMap.put(switchId, switchStatisticsCopy);
		}

		NetworkStatistics networkStatisticsCopy = new NetworkStatistics();
		networkStatisticsCopy.setSwitchStatistics(netStatisticsMap);
		return networkStatisticsCopy;
	}

}
