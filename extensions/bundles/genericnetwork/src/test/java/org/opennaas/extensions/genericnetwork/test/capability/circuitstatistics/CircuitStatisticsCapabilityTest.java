package org.opennaas.extensions.genericnetwork.test.capability.circuitstatistics;

/*
 * #%L
 * OpenNaaS :: Generic Network
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.extensions.genericnetwork.capability.circuitstatistics.CircuitStatisticsCapability;
import org.opennaas.extensions.genericnetwork.model.CircuitStatistics;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimePeriod;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CircuitStatisticsCapabilityTest {

	private static final String	CIRCUIT_STATISTICS_CSV_URL	= "/circuitStatistics.csv";

	private static final String	FIRST_CIRCUIT_FLOW_ID		= "1";
	private static final String	FIRST_CIRCUIT_THROUGHPUT	= "10";
	private static final String	FIRST_CIRCUIT_PACKET_LOSS	= "2";
	private static final String	FIRST_CIRCUIT_DELAY			= "0";
	private static final String	FIRST_CIRCUIT_JITTER		= "0";
	private static final String	FIRST_CIRCUIT_FLOW_DATA		= "1234";

	private static final String	SECOND_CIRCUIT_FLOW_ID		= "2";
	private static final String	SECOND_CIRCUIT_THROUGHPUT	= "20";
	private static final String	SECOND_CIRCUIT_PACKET_LOSS	= "1";
	private static final String	SECOND_CIRCUIT_DELAY		= "4";
	private static final String	SECOND_CIRCUIT_JITTER		= "5";
	private static final String	SECOND_CIRCUIT_FLOW_DATA	= "1235";

	private static final String	THIRD_CIRCUIT_FLOW_ID		= "3";
	private static final String	THIRD_CIRCUIT_THROUGHPUT	= "0";
	private static final String	THIRD_CIRCUIT_PACKET_LOSS	= "1";
	private static final String	THIRD_CIRCUIT_DELAY			= "1";
	private static final String	THIRD_CIRCUIT_JITTER		= "1";
	private static final String	THIRD_CIRCUIT_FLOW_DATA		= "1233";

	private static final Long	FIRST_TIMESTAMP				= Long.valueOf("10000000000000");
	private static final long	SECOND_TIMESTAMP			= Long.valueOf("12378612378614");

	CircuitStatistics			firstStatistics;
	CircuitStatistics			secondStatistics;
	CircuitStatistics			thirdStatistics;

	CircuitStatisticsCapability	circuitStatisticsCapab;

	@Before
	public void prepareTest() {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		Information capabilityInformation = new Information(null, null, null);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);
		circuitStatisticsCapab = new CircuitStatisticsCapability(capabilityDescriptor, null);

		firstStatistics = generateCircuitStatistics(FIRST_CIRCUIT_FLOW_ID,
				FIRST_CIRCUIT_THROUGHPUT, FIRST_CIRCUIT_PACKET_LOSS, FIRST_CIRCUIT_DELAY, FIRST_CIRCUIT_JITTER, FIRST_CIRCUIT_FLOW_DATA);
		secondStatistics = generateCircuitStatistics(SECOND_CIRCUIT_FLOW_ID,
				SECOND_CIRCUIT_THROUGHPUT, SECOND_CIRCUIT_PACKET_LOSS, SECOND_CIRCUIT_DELAY, SECOND_CIRCUIT_JITTER, SECOND_CIRCUIT_FLOW_DATA);
		thirdStatistics = generateCircuitStatistics(THIRD_CIRCUIT_FLOW_ID,
				THIRD_CIRCUIT_THROUGHPUT, THIRD_CIRCUIT_PACKET_LOSS, THIRD_CIRCUIT_DELAY, THIRD_CIRCUIT_JITTER, THIRD_CIRCUIT_FLOW_DATA);

	}

	@Test
	public void parseCSVTest() throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		String csvFile = IOUtils.toString(this.getClass().getResourceAsStream(CIRCUIT_STATISTICS_CSV_URL));

		// call private method by reflection
		Method method = circuitStatisticsCapab.getClass().getDeclaredMethod("parseCSV", String.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		SortedMap<Long, List<CircuitStatistics>> statistics = (SortedMap<Long, List<CircuitStatistics>>) method.invoke(circuitStatisticsCapab,
				csvFile);

		Assert.assertNotNull("Parsed statistics should not be null.", statistics);
		Assert.assertEquals("Parsed statistics should contain 2 different timestamps", 2, statistics.keySet().size());

		Assert.assertNotNull("Parsed statistics should contains statistics for timestamp " + FIRST_TIMESTAMP,
				statistics.get(FIRST_TIMESTAMP));

		Assert.assertEquals("Parsed statistics should contain 2 statistics for timestamp" + FIRST_TIMESTAMP, 2, statistics.get(FIRST_TIMESTAMP)
				.size());

		CircuitStatistics firstStatistics = statistics.get(FIRST_TIMESTAMP).get(0);

		Assert.assertNotNull("First circuit statistics should not be null.", firstStatistics);

		Assert.assertEquals("Flow Id of first circuit statistics should be " + FIRST_CIRCUIT_FLOW_ID, FIRST_CIRCUIT_FLOW_ID,
				firstStatistics.getSlaFlowId());
		Assert.assertEquals("Throughput of first circuit statistics should be " + FIRST_CIRCUIT_THROUGHPUT, FIRST_CIRCUIT_THROUGHPUT,
				firstStatistics.getThroughput());
		Assert.assertEquals("Throughput of first circuit statistics should be " + FIRST_CIRCUIT_PACKET_LOSS, FIRST_CIRCUIT_PACKET_LOSS,
				firstStatistics.getPacketLoss());
		Assert.assertEquals("Jitter of first circuit statistics should be " + FIRST_CIRCUIT_JITTER, FIRST_CIRCUIT_JITTER,
				firstStatistics.getJitter());
		Assert.assertEquals("Flow Data of first circuit statistics should be " + FIRST_CIRCUIT_FLOW_DATA, FIRST_CIRCUIT_FLOW_DATA,
				firstStatistics.getFlowData());
		Assert.assertEquals("Delay of first circuit statistics should be " + FIRST_CIRCUIT_DELAY, FIRST_CIRCUIT_DELAY,
				firstStatistics.getDelay());

		CircuitStatistics secondStatistics = statistics.get(FIRST_TIMESTAMP).get(1);

		Assert.assertNotNull("Second circuit statistics should not be null.", secondStatistics);

		Assert.assertEquals("Flow Id of second circuit statistics should be " + SECOND_CIRCUIT_FLOW_ID, SECOND_CIRCUIT_FLOW_ID,
				secondStatistics.getSlaFlowId());
		Assert.assertEquals("Throughput of second circuit statistics should be " + SECOND_CIRCUIT_THROUGHPUT, SECOND_CIRCUIT_THROUGHPUT,
				secondStatistics.getThroughput());
		Assert.assertEquals("Throughput of second circuit statistics should be " + SECOND_CIRCUIT_PACKET_LOSS, SECOND_CIRCUIT_PACKET_LOSS,
				secondStatistics.getPacketLoss());
		Assert.assertEquals("Jitter of second circuit statistics should be " + SECOND_CIRCUIT_JITTER, SECOND_CIRCUIT_JITTER,
				secondStatistics.getJitter());
		Assert.assertEquals("Flow Data of second circuit statistics should be " + SECOND_CIRCUIT_FLOW_DATA, SECOND_CIRCUIT_FLOW_DATA,
				secondStatistics.getFlowData());
		Assert.assertEquals("Delay of second circuit statistics should be " + SECOND_CIRCUIT_DELAY, SECOND_CIRCUIT_DELAY,
				secondStatistics.getDelay());

		CircuitStatistics thirdStatistics = statistics.get(SECOND_TIMESTAMP).get(0);

		Assert.assertNotNull("Third circuit statistics should not be null.", thirdStatistics);

		Assert.assertEquals("Flow Id of Third circuit statistics should be " + THIRD_CIRCUIT_FLOW_ID, THIRD_CIRCUIT_FLOW_ID,
				thirdStatistics.getSlaFlowId());
		Assert.assertEquals("Throughput of Third circuit statistics should be " + THIRD_CIRCUIT_THROUGHPUT, THIRD_CIRCUIT_THROUGHPUT,
				thirdStatistics.getThroughput());
		Assert.assertEquals("Throughput of Third circuit statistics should be " + THIRD_CIRCUIT_PACKET_LOSS, THIRD_CIRCUIT_PACKET_LOSS,
				thirdStatistics.getPacketLoss());
		Assert.assertEquals("Jitter of Third circuit statistics should be " + THIRD_CIRCUIT_JITTER, THIRD_CIRCUIT_JITTER,
				thirdStatistics.getJitter());
		Assert.assertEquals("Flow Data of Third circuit statistics should be " + THIRD_CIRCUIT_FLOW_DATA, THIRD_CIRCUIT_FLOW_DATA,
				thirdStatistics.getFlowData());
		Assert.assertEquals("Delay of Third circuit statistics should be " + THIRD_CIRCUIT_DELAY, THIRD_CIRCUIT_DELAY,
				thirdStatistics.getDelay());

	}

	@Test
	public void writeToCSVTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException {

		SortedMap<Long, List<CircuitStatistics>> circuitStatistics = new TreeMap<Long, List<CircuitStatistics>>();

		circuitStatistics.put(FIRST_TIMESTAMP, Arrays.asList(firstStatistics, secondStatistics));
		circuitStatistics.put(SECOND_TIMESTAMP, Arrays.asList(thirdStatistics));

		// call private method by reflection
		Method method = circuitStatisticsCapab.getClass().getDeclaredMethod("writeToCSV", SortedMap.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		String parsedCSV = (String) method.invoke(circuitStatisticsCapab, circuitStatistics);

		String expectedCSV = IOUtils.toString(this.getClass().getResourceAsStream(CIRCUIT_STATISTICS_CSV_URL));

		Assert.assertEquals("Parsed CSV should be equals to the expected one.", expectedCSV, parsedCSV);

	}

	@Test
	@SuppressWarnings("unchecked")
	public void filterByTimePeriodTest() throws SecurityException, IllegalArgumentException, IllegalAccessException, CapabilityException,
			NoSuchMethodException, InvocationTargetException {

		GenericNetworkModel model = new GenericNetworkModel();

		SortedMap<Long, List<CircuitStatistics>> circuitStatistics = new TreeMap<Long, List<CircuitStatistics>>();
		circuitStatistics.put(FIRST_TIMESTAMP, Arrays.asList(firstStatistics, secondStatistics));
		circuitStatistics.put(SECOND_TIMESTAMP, Arrays.asList(thirdStatistics));

		model.setCircuitStatistics(circuitStatistics);
		IResource resource = new Resource();
		resource.setModel(model);

		injectPrivateField(circuitStatisticsCapab, resource, "resource");

		TimePeriod timePeriod = new TimePeriod(FIRST_TIMESTAMP - 2L, FIRST_TIMESTAMP - 1L);
		String statisticsInCSV = circuitStatisticsCapab.getStatistics(timePeriod);
		Assert.assertTrue("Capability should not contain any statistic for this time period", statisticsInCSV.isEmpty());

		timePeriod = new TimePeriod(SECOND_TIMESTAMP + 1L, SECOND_TIMESTAMP + 2L);
		statisticsInCSV = circuitStatisticsCapab.getStatistics(timePeriod);
		Assert.assertTrue("Capability should not contain any statistic for this time period", statisticsInCSV.isEmpty());

		timePeriod = new TimePeriod(FIRST_TIMESTAMP, SECOND_TIMESTAMP - 1L);
		statisticsInCSV = circuitStatisticsCapab.getStatistics(timePeriod);
		Assert.assertFalse("Capability should contain statistics for this time period", statisticsInCSV.isEmpty());

		// call private method by reflection in order to parse CSV and make assertions
		Method method = circuitStatisticsCapab.getClass().getDeclaredMethod("parseCSV", String.class);
		method.setAccessible(true);
		SortedMap<Long, List<CircuitStatistics>> statistics = (SortedMap<Long, List<CircuitStatistics>>) method.invoke(circuitStatisticsCapab,
				statisticsInCSV);

		Assert.assertEquals("There's only one timestamp between this period of time", 1, statistics.keySet().size());
		Assert.assertNotNull("There should be statistics between this period of time.", statistics.get(FIRST_TIMESTAMP));
		Assert.assertEquals("There should be two statistics between this period of time.", 2, statistics.get(FIRST_TIMESTAMP).size());
		Assert.assertEquals(statistics.get(FIRST_TIMESTAMP).get(0), firstStatistics);
		Assert.assertEquals(statistics.get(FIRST_TIMESTAMP).get(1), secondStatistics);

		timePeriod = new TimePeriod(SECOND_TIMESTAMP - 1L, SECOND_TIMESTAMP);
		statisticsInCSV = circuitStatisticsCapab.getStatistics(timePeriod);
		Assert.assertFalse("Capability should contain statistics for this time period", statisticsInCSV.isEmpty());

		// call private method by reflection in order to parse CSV and make assertions
		method = circuitStatisticsCapab.getClass().getDeclaredMethod("parseCSV", String.class);
		method.setAccessible(true);
		statistics = (SortedMap<Long, List<CircuitStatistics>>) method.invoke(circuitStatisticsCapab,
				statisticsInCSV);

		Assert.assertEquals("There's only one timestamp between this period of time", 1, statistics.keySet().size());
		Assert.assertNotNull("There should be statistics between this period of time.", statistics.get(SECOND_TIMESTAMP));
		Assert.assertEquals("There should be one statistic between this period of time.", 1, statistics.get(SECOND_TIMESTAMP).size());
		Assert.assertEquals(statistics.get(SECOND_TIMESTAMP).get(0), thirdStatistics);

	}

	private CircuitStatistics generateCircuitStatistics(String circuiSLAtFlowId,
			String circuitThroughput, String circuitPackgetLoss, String circuitDelay, String circuitJitter, String circuitFlowData) {

		CircuitStatistics circuitStatistics = new CircuitStatistics();

		circuitStatistics.setSlaFlowId(circuiSLAtFlowId);
		circuitStatistics.setThroughput(circuitThroughput);
		circuitStatistics.setPacketLoss(circuitPackgetLoss);
		circuitStatistics.setDelay(circuitDelay);
		circuitStatistics.setJitter(circuitJitter);
		circuitStatistics.setFlowData(circuitFlowData);

		return circuitStatistics;
	}

	private static <T, F> void injectPrivateField(T classInstance, F fieldInstance, String fieldName) throws SecurityException,
			IllegalArgumentException, IllegalAccessException {

		// get all super classes and add itself
		List<Class<?>> classes = ClassUtils.getAllSuperclasses(classInstance.getClass());
		classes.add(classInstance.getClass());

		for (Class<?> clazz : classes) {

			Field field;
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// try next class
				continue;
			}
			if (!field.getType().isAssignableFrom(fieldInstance.getClass())) {
				throw new IllegalArgumentException("Invalid fieldName received, fieldInstance can not assigned to field.");
			}

			field.setAccessible(true);
			field.set(classInstance, fieldInstance);
			field.setAccessible(false);
			return;

		}

		throw new IllegalArgumentException("Invalid fieldName received, a field with this name can not be found in this class or its superclasses.");
	}

}
