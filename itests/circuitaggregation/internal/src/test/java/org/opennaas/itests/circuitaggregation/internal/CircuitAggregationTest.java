package org.opennaas.itests.circuitaggregation.internal;

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

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.genericnetwork.actionsets.internal.circuitaggregation.CircuitAggregationActionsetImplementation;
import org.opennaas.extensions.genericnetwork.capability.circuitaggregation.CircuitAggregationCapability;
import org.opennaas.extensions.genericnetwork.capability.circuitaggregation.ICircuitAggregationCapability;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.helpers.GenericNetworkModelUtils;
import org.opennaas.extensions.genericnetwork.repository.GenericNetworkRepository;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * {@link CircuitAggregationCapability} integration test
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class CircuitAggregationTest {

	private final static Log	log							= LogFactory.getLog(CircuitAggregationTest.class);

	@Inject
	protected IResourceManager	resourceManager;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.genericnetwork)", timeout = 50000)
	private BlueprintContainer	genericNetworkBlueprintContainer;

	protected IResource			genericNetwork;
	protected IResource			genericNetworkAggregating;

	private static final String	GENERICNET1_RESOURCE_NAME	= "sampleGenericNetwork";
	private static final String	GENERICNET2_RESOURCE_NAME	= "aggregatingGenericNetwork";

	private static final String	CAPABILITY_VERSION			= "1.0.0";

	private static final String	MOCK_URI					= "mock://user:pass@host.net:2212/mocksubsystem";

	public static final String	SRC_BASE_IP24_1				= "192.168.1.";
	public static final String	DST_BASE_IP24_1				= "192.168.2.";
	public static final String	SRC_BASE_IP24_2				= "192.168.3.";
	public static final String	DST_BASE_IP24_2				= "192.168.4.";
	public static final String	TOS_1						= "4";
	public static final String	TOS_2						= "8";

	Set<Circuit>				notAggregated;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-genericnetwork", "itests-helpers"),
				noConsole(),
				KarafDistributionOption.doNotModifyLogConfiguration(),
				keepRuntimeFolder()
		// , OpennaasExamOptions.openDebugSocket()
		);
	}

	@Before
	public void prepareTest() throws ResourceException, ProtocolException, IOException {
		resourceManager.destroyAllResources();
		genericNetwork = startGenericNetworkResource(GENERICNET1_RESOURCE_NAME, false);
		genericNetworkAggregating = startGenericNetworkResource(GENERICNET2_RESOURCE_NAME, true);
		notAggregated = generateSampleCircuits();
	}

	@After
	public void shutdownTest() throws ResourceException, ProtocolException {
		stopAndRemoveResources();
	}

	@Test
	public void capabCallsDontModifyParameters() throws ResourceException {

		Set<Circuit> toAggregate = generateSampleCircuits();
		Set<Circuit> toAggregateCopy = generateSampleCircuits();
		Assert.assertEquals("sample circuits must be equal", toAggregate, toAggregateCopy);

		ICircuitAggregationCapability capab = (ICircuitAggregationCapability) genericNetworkAggregating
				.getCapabilityByInterface(ICircuitAggregationCapability.class);
		capab.aggregateCircuits(toAggregate);
		Assert.assertEquals("aggregateCircuits method should not modify given parameters", toAggregateCopy, toAggregate);

		ICircuitAggregationCapability capabNotAggregating = (ICircuitAggregationCapability) genericNetwork
				.getCapabilityByInterface(ICircuitAggregationCapability.class);
		capabNotAggregating.aggregateCircuits(toAggregate);
		Assert.assertEquals("aggregateCircuits method should not modify given parameters", toAggregateCopy, toAggregate);
	}

	@Test
	public void disabledAggregationReturnsGivenCircuits() throws ResourceException {

		ICircuitAggregationCapability capabNotAggregating = (ICircuitAggregationCapability) genericNetwork
				.getCapabilityByInterface(ICircuitAggregationCapability.class);
		Set<Circuit> returned = capabNotAggregating.aggregateCircuits(notAggregated);
		Assert.assertEquals("aggregateCircuits should return same given circuits when aggregation is disabled", notAggregated, returned);
	}

	@Test
	public void aggregationTest() throws ResourceException {

		ICircuitAggregationCapability capab = (ICircuitAggregationCapability) genericNetworkAggregating
				.getCapabilityByInterface(ICircuitAggregationCapability.class);
		Set<Circuit> aggregated = capab.aggregateCircuits(notAggregated);
		Assert.assertEquals("There must be 3 aggregated circuits", 3, aggregated.size());

		Circuit aggregatedWithIp1Tos1 = null;
		Circuit aggregatedWithIp2Tos1 = null;
		Circuit aggregatedWithIp2Tos2 = null;
		for (Circuit c : aggregated) {
			if (c.getTrafficFilter().getSrcIp().equals(SRC_BASE_IP24_1 + "0/24")
					&& c.getTrafficFilter().getDstIp().equals(DST_BASE_IP24_1 + "0/24")
					&& c.getTrafficFilter().getTosBits().equals(TOS_1)) {
				aggregatedWithIp1Tos1 = c;
			} else if (c.getTrafficFilter().getSrcIp().equals(SRC_BASE_IP24_2 + "0/24")
					&& c.getTrafficFilter().getDstIp().equals(DST_BASE_IP24_2 + "0/24")
					&& c.getTrafficFilter().getTosBits().equals(TOS_1)) {
				aggregatedWithIp2Tos1 = c;
			} else if (c.getTrafficFilter().getSrcIp().equals(SRC_BASE_IP24_2 + "0/24")
					&& c.getTrafficFilter().getDstIp().equals(DST_BASE_IP24_2 + "0/24")
					&& c.getTrafficFilter().getTosBits().equals(TOS_2)) {
				aggregatedWithIp2Tos2 = c;
			}
		}
		Assert.assertNotNull("There must be an aggregated circuit reoresenting circuits 0-9", aggregatedWithIp1Tos1);
		Assert.assertNotNull("There must be an aggregated circuit reoresenting circuits 10-19", aggregatedWithIp2Tos1);
		Assert.assertNotNull("There must be an aggregated circuit reoresenting circuits 1010-1019", aggregatedWithIp2Tos2);

		Assert.assertFalse("Aggregated circuits are not equals eachother (1-2)", aggregatedWithIp1Tos1.equals(aggregatedWithIp2Tos1));
		Assert.assertFalse("Aggregated circuits are not equals eachother (1-3)", aggregatedWithIp1Tos1.equals(aggregatedWithIp2Tos2));
		Assert.assertFalse("Aggregated circuits are not equals eachother (2-3)", aggregatedWithIp2Tos1.equals(aggregatedWithIp2Tos2));

	}

	/**
	 * Returns the a Set containing 30 Circuits.<br/>
	 * 
	 * 10 of them have same IPAddress/24 and ToS. They have ids in range [0,10) <br/>
	 * 10 of them have same IPAddress/24 and ToS, but different IPAddress/24 than previous 10. They have ids in range [10,20) <br/>
	 * 10 of them have same IPAddress/24 and ToS, but different ToS than previous 10. They have ids in range [1010,1020) <br/>
	 * 
	 * These circuits are meant to be aggregated in 3 circuits (one per group), when aggregation is active.
	 * 
	 * Different calls to this method returns equal results.
	 * 
	 * @return
	 * @throws Exception
	 */
	private Set<Circuit> generateSampleCircuits() {

		Set<Circuit> circuits = new HashSet<Circuit>();
		// aggregation group
		for (int i = 0; i < 10; i++) {
			Circuit circuit1 = GenericNetworkModelUtils.generateSampleCircuit();
			circuit1.setCircuitId(String.valueOf(i));
			circuit1.getTrafficFilter().setSrcIp(SRC_BASE_IP24_1 + String.valueOf(i));
			circuit1.getTrafficFilter().setDstIp(DST_BASE_IP24_1 + String.valueOf(i + 100));
			circuit1.getTrafficFilter().setTosBits(TOS_1);
			circuits.add(circuit1);
		}

		// aggregation group using different ip/24
		for (int i = 10; i < 20; i++) {
			Circuit circuit1 = GenericNetworkModelUtils.generateSampleCircuit();
			circuit1.setCircuitId(String.valueOf(i));
			circuit1.getTrafficFilter().setSrcIp(SRC_BASE_IP24_2 + String.valueOf(i));
			circuit1.getTrafficFilter().setDstIp(DST_BASE_IP24_2 + String.valueOf(i + 100));
			circuit1.getTrafficFilter().setTosBits(TOS_1);
			circuits.add(circuit1);
		}

		// aggregation group using different ToS
		for (int i = 10; i < 20; i++) {
			Circuit circuit1 = GenericNetworkModelUtils.generateSampleCircuit();
			circuit1.setCircuitId(String.valueOf(i + 1000));
			circuit1.getTrafficFilter().setTosBits(TOS_2);
			circuit1.getTrafficFilter().setSrcIp(SRC_BASE_IP24_2 + String.valueOf(i));
			circuit1.getTrafficFilter().setDstIp(DST_BASE_IP24_2 + String.valueOf(i + 100));
			circuits.add(circuit1);
		}

		return circuits;
	}

	/**
	 * Start a genericnetwork resource with {@link CircuitAggregationCapability}
	 * 
	 * @throws IOException
	 * 
	 */
	protected IResource startGenericNetworkResource(String resourceName, boolean shouldAggregate) throws ResourceException, ProtocolException,
			IOException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor circuitAggregationDescriptor = ResourceHelper.newCapabilityDescriptor(
				CircuitAggregationActionsetImplementation.ACTIONSET_ID, CAPABILITY_VERSION, CircuitAggregationCapability.CAPABILITY_TYPE, MOCK_URI);
		if (shouldAggregate) {
			CapabilityProperty aggregationEnabledProperty = new CapabilityProperty();
			aggregationEnabledProperty.setName(CircuitAggregationCapability.USE_AGGREGATION_PROPERTY);
			aggregationEnabledProperty.setValue("true");
			circuitAggregationDescriptor.getCapabilityProperties().add(aggregationEnabledProperty);
		}
		lCapabilityDescriptors.add(circuitAggregationDescriptor);

		// GenericNetwork Resource Descriptor
		ResourceDescriptor genericNetworkDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors,
				GenericNetworkRepository.GENERIC_NETWORK_RESOURCE_TYPE, MOCK_URI, resourceName);

		// Create resource
		IResource network = resourceManager.createResource(genericNetworkDescriptor);

		// Start resource
		resourceManager.startResource(network.getResourceIdentifier());

		return network;
	}

	/**
	 * Stop and remove the genericNetwork and genericNetworkAggregating resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void stopAndRemoveResources() throws ResourceException, ProtocolException {
		if (genericNetwork.getState().equals(State.ACTIVE)) {
			// Stop resource
			resourceManager.stopResource(genericNetwork.getResourceIdentifier());
		}
		// Remove resource
		resourceManager.removeResource(genericNetwork.getResourceIdentifier());

		if (genericNetworkAggregating.getState().equals(State.ACTIVE)) {
			// Stop resource
			resourceManager.stopResource(genericNetworkAggregating.getResourceIdentifier());
		}
		// Remove resource
		resourceManager.removeResource(genericNetworkAggregating.getResourceIdentifier());

	}

}
