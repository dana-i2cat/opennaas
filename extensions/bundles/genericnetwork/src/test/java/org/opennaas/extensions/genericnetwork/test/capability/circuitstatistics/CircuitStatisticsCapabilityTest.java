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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.extensions.genericnetwork.capability.circuitstatistics.CircuitStatisticsCapability;
import org.opennaas.extensions.genericnetwork.model.CircuitStatistics;
import org.opennaas.extensions.genericnetwork.model.TimePeriod;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CircuitStatisticsCapabilityTest {

	private static final String		CIRCUIT_STATISTICS_CSV_URL	= "/circuitStatistics.csv";

	private static final long		FIRST_CIRCUIT_START_TIME	= Long.valueOf("12378612378613").longValue();
	private static final long		FIRST_CIRCUIT_END_TIME		= Long.valueOf("22378612378613").longValue();
	private static final String		FIRST_CIRCUIT_FLOW_ID		= "1";
	private static final String		FIRST_CIRCUIT_THROUGHPUT	= "10";
	private static final String		FIRST_CIRCUIT_PACKGET_LOSS	= "2";
	private static final String		FIRST_CIRCUIT_DELAY			= "0";
	private static final String		FIRST_CIRCUIT_JITTER		= "0";
	private static final String		FIRST_CIRCUIT_FLOW_DATA		= "1234";

	private static final long		SECOND_CIRCUIT_START_TIME	= Long.valueOf("12378612378614").longValue();
	private static final long		SECOND_CIRCUIT_END_TIME		= Long.valueOf("32378912378123").longValue();
	private static final String		SECOND_CIRCUIT_FLOW_ID		= "2";
	private static final String		SECOND_CIRCUIT_THROUGHPUT	= "0";
	private static final String		SECOND_CIRCUIT_PACKGET_LOSS	= "1";
	private static final String		SECOND_CIRCUIT_JITTER		= "1";
	private static final String		SECOND_CIRCUIT_DELAY		= "1";
	private static final String		SECOND_CIRCUIT_FLOW_DATA	= "1233";

	private CapabilityDescriptor	capabilityDescriptor;

	CircuitStatisticsCapability		circuitStatisticsCapab;

	@Before
	public void prepareTest() {
		capabilityDescriptor = new CapabilityDescriptor();
		Information capabilityInformation = new Information(null, null, null);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);
		circuitStatisticsCapab = new CircuitStatisticsCapability(capabilityDescriptor, null);
	}

	@Test
	public void parseCSVTest() throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		String csvFile = IOUtils.toString(this.getClass().getResourceAsStream(CIRCUIT_STATISTICS_CSV_URL));

		// call private method by reflection
		Method method = circuitStatisticsCapab.getClass().getDeclaredMethod("parseCSV", String.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		Set<CircuitStatistics> statistics = (Set<CircuitStatistics>) method.invoke(circuitStatisticsCapab, csvFile);

		Assert.assertNotNull("Parsed statistics should not be null.", statistics);
		Assert.assertEquals("Parsed statistics should contain 2 circuit statistics", 2, statistics.size());

		Iterator<CircuitStatistics> iterator = statistics.iterator();

		CircuitStatistics firstStatistics = iterator.next();

		Assert.assertNotNull("First circuit statistics should not be null.", firstStatistics);
		Assert.assertNotNull("Time period of first circuit statistics should not be null.", firstStatistics.getTimePeriod());

		Assert.assertEquals("Start time of first circuit statistics should be " + FIRST_CIRCUIT_START_TIME, FIRST_CIRCUIT_START_TIME, firstStatistics
				.getTimePeriod().getStartTime());
		Assert.assertEquals("End time of first circuit statistics should be " + FIRST_CIRCUIT_END_TIME, FIRST_CIRCUIT_END_TIME, firstStatistics
				.getTimePeriod().getEndTime());
		Assert.assertEquals("Flow Id of first circuit statistics should be " + FIRST_CIRCUIT_FLOW_ID, FIRST_CIRCUIT_FLOW_ID,
				firstStatistics.getSlaFlowId());
		Assert.assertEquals("Throughput of first circuit statistics should be " + FIRST_CIRCUIT_THROUGHPUT, FIRST_CIRCUIT_THROUGHPUT,
				firstStatistics.getThroughput());
		Assert.assertEquals("Throughput of first circuit statistics should be " + FIRST_CIRCUIT_PACKGET_LOSS, FIRST_CIRCUIT_PACKGET_LOSS,
				firstStatistics.getPacketLoss());
		Assert.assertEquals("Jitter of first circuit statistics should be " + FIRST_CIRCUIT_JITTER, FIRST_CIRCUIT_JITTER, firstStatistics.getJitter());
		Assert.assertEquals("Flow Data of first circuit statistics should be " + FIRST_CIRCUIT_FLOW_DATA, FIRST_CIRCUIT_FLOW_DATA,
				firstStatistics.getFlowData());
		Assert.assertEquals("Delay of first circuit statistics should be " + FIRST_CIRCUIT_DELAY, FIRST_CIRCUIT_DELAY,
				firstStatistics.getDelay());

		CircuitStatistics secondStatistics = iterator.next();

		Assert.assertNotNull("Second circuit statistics should not be null.", secondStatistics);
		Assert.assertNotNull("Second period of first circuit statistics should not be null.", secondStatistics.getTimePeriod());

		Assert.assertEquals("Start time of second circuit statistics should be " + SECOND_CIRCUIT_START_TIME, SECOND_CIRCUIT_START_TIME,
				secondStatistics.getTimePeriod().getStartTime());
		Assert.assertEquals("End time of second circuit statistics should be " + SECOND_CIRCUIT_END_TIME, SECOND_CIRCUIT_END_TIME, secondStatistics
				.getTimePeriod().getEndTime());
		Assert.assertEquals("Flow Id of second circuit statistics should be " + SECOND_CIRCUIT_FLOW_ID, SECOND_CIRCUIT_FLOW_ID,
				secondStatistics.getSlaFlowId());
		Assert.assertEquals("Throughput of second circuit statistics should be " + SECOND_CIRCUIT_THROUGHPUT, SECOND_CIRCUIT_THROUGHPUT,
				secondStatistics.getThroughput());
		Assert.assertEquals("Throughput of second circuit statistics should be " + SECOND_CIRCUIT_PACKGET_LOSS, SECOND_CIRCUIT_PACKGET_LOSS,
				secondStatistics.getPacketLoss());
		Assert.assertEquals("Jitter of second circuit statistics should be " + SECOND_CIRCUIT_JITTER, SECOND_CIRCUIT_JITTER,
				secondStatistics.getJitter());
		Assert.assertEquals("Flow Data of second circuit statistics should be " + SECOND_CIRCUIT_FLOW_DATA, SECOND_CIRCUIT_FLOW_DATA,
				secondStatistics.getFlowData());
		Assert.assertEquals("Delay of second circuit statistics should be " + SECOND_CIRCUIT_DELAY, SECOND_CIRCUIT_DELAY,
				secondStatistics.getDelay());

	}

	@Test
	public void writeToCSVTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException {

		CircuitStatistics firstStatistics = generateCircuitStatistics(FIRST_CIRCUIT_START_TIME, FIRST_CIRCUIT_END_TIME, FIRST_CIRCUIT_FLOW_ID,
				FIRST_CIRCUIT_THROUGHPUT, FIRST_CIRCUIT_PACKGET_LOSS, FIRST_CIRCUIT_DELAY, FIRST_CIRCUIT_JITTER, FIRST_CIRCUIT_FLOW_DATA);
		CircuitStatistics secondStatistics = generateCircuitStatistics(SECOND_CIRCUIT_START_TIME, SECOND_CIRCUIT_END_TIME, SECOND_CIRCUIT_FLOW_ID,
				SECOND_CIRCUIT_THROUGHPUT, SECOND_CIRCUIT_PACKGET_LOSS, SECOND_CIRCUIT_DELAY, SECOND_CIRCUIT_JITTER, SECOND_CIRCUIT_FLOW_DATA);

		Set<CircuitStatistics> circuitStatistics = new HashSet<CircuitStatistics>();
		circuitStatistics.addAll(Arrays.asList(firstStatistics, secondStatistics));

		// call private method by reflection
		Method method = circuitStatisticsCapab.getClass().getDeclaredMethod("writeToCSV", Set.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		String parsedCSV = (String) method.invoke(circuitStatisticsCapab, circuitStatistics);

		String expectedCSV = IOUtils.toString(this.getClass().getResourceAsStream(CIRCUIT_STATISTICS_CSV_URL));

		Assert.assertEquals("Parsed CSV should be equals to the expected one.", expectedCSV, parsedCSV);

	}

	private CircuitStatistics generateCircuitStatistics(long circuitStartTime, long circuitEndTime, String circuiSLAtFlowId,
			String circuitThroughput, String circuitPackgetLoss, String circuitDelay, String circuitJitter, String circuitFlowData) {

		TimePeriod timePeriod = new TimePeriod(circuitStartTime, circuitEndTime);
		CircuitStatistics circuitStatistics = new CircuitStatistics();

		circuitStatistics.setTimePeriod(timePeriod);
		circuitStatistics.setSlaFlowId(circuiSLAtFlowId);
		circuitStatistics.setThroughput(circuitThroughput);
		circuitStatistics.setPacketLoss(circuitPackgetLoss);
		circuitStatistics.setDelay(circuitDelay);
		circuitStatistics.setJitter(circuitJitter);
		circuitStatistics.setFlowData(circuitFlowData);

		return circuitStatistics;
	}
}
