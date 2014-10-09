package org.opennaas.extensions.genericnetwork.capability.circuitstatistics;

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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.model.CircuitStatistics;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimePeriod;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CircuitStatisticsCapability extends AbstractCapability implements ICircuitStatisticsCapability {

	public static final String	CAPABILITY_TYPE	= "circuitstatistics";

	private Log					log				= LogFactory.getLog(CircuitStatisticsCapability.class);

	private String				resourceId		= "";

	public CircuitStatisticsCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Circuit Statistics Capability");

	}

	@Override
	public String getCapabilityName() {
		return this.CAPABILITY_TYPE;
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				CircuitStatisticsCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	// ############################################
	// ### ICircuitStatisticsCapability methods ###
	// ############################################

	@Override
	public void reportStatistics(String csvStatistics) throws CapabilityException {

		log.info("Circuit Statistics report received.");

		try {
			SortedMap<Long, List<CircuitStatistics>> circuitStatistics = parseCSV(csvStatistics);
			GenericNetworkModel model = (GenericNetworkModel) resource.getModel();

			SortedMap<Long, List<CircuitStatistics>> modelStatistics = model.getCircuitStatistics();
			for (Long timestamp : circuitStatistics.keySet()) {
				if (modelStatistics.keySet().contains(timestamp))
					modelStatistics.get(timestamp).addAll(circuitStatistics.get(timestamp));
				else
					modelStatistics.put(timestamp, circuitStatistics.get(timestamp));
			}

		} catch (Exception e) {
			log.info("Error parsing received CSV", e);
			throw new CapabilityException(e);
		}

		log.info("Circuits statistics stored.");

	}

	@Override
	public String getStatistics(TimePeriod timePeriod) throws CapabilityException {
		try {
			GenericNetworkModel model = (GenericNetworkModel) resource.getModel();

			SortedMap<Long, List<CircuitStatistics>> filteredStatistics = model.getCircuitStatistics().subMap(timePeriod.getInit(),
					timePeriod.getEnd() + 1L); // we add 1 to the long, since the "to" of the subMap is exclusive

			String csvStatistics = writeToCSV(filteredStatistics);

			return csvStatistics;
		} catch (IOException io) {
			log.error("Error parsing cirucir statistics to CSV.", io);
			throw new CapabilityException(io);
		}
	}

	private String writeToCSV(SortedMap<Long, List<CircuitStatistics>> circuitStatistics) throws IOException {

		StringBuilder sb = new StringBuilder();
		CSVWriter writer = new CSVWriter(new StringBuilderWriter(sb), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
		try {

			for (Long timestamp : circuitStatistics.keySet())

				for (CircuitStatistics currentStatistic : circuitStatistics.get(timestamp)) {

					String[] csvStatistic = new String[7];
					csvStatistic[0] = String.valueOf(timestamp);
					csvStatistic[1] = currentStatistic.getSlaFlowId();
					csvStatistic[2] = currentStatistic.getThroughput();
					csvStatistic[3] = currentStatistic.getPacketLoss();
					csvStatistic[4] = currentStatistic.getDelay();
					csvStatistic[5] = currentStatistic.getJitter();
					csvStatistic[6] = currentStatistic.getFlowData();

					writer.writeNext(csvStatistic);

				}

			return sb.toString();
		} finally {
			writer.close();
		}
	}

	private SortedMap<Long, List<CircuitStatistics>> parseCSV(String csvStatistics) throws IllegalArgumentException, IOException {
		CSVReader reader = new CSVReader(new StringReader(csvStatistics));

		try {

			SortedMap<Long, List<CircuitStatistics>> circuitsStatistics = new TreeMap<Long, List<CircuitStatistics>>();

			List<String[]> records = reader.readAll();

			log.debug("Storing the new " + records.size() + " received circuits statistics.");

			for (String[] currentRecord : records) {

				if (currentRecord.length != 7)
					throw new IllegalArgumentException("Invalid record length: it should contain 7 fields.");

				CircuitStatistics currentStatistics = new CircuitStatistics();

				if (!StringUtils.isNumeric(currentRecord[0].trim()))
					throw new IllegalArgumentException("Records should start with timestamp.");

				Long timestamp = Long.valueOf(currentRecord[0].trim());

				currentStatistics.setSlaFlowId(currentRecord[1].trim());
				currentStatistics.setThroughput(currentRecord[2].trim());
				currentStatistics.setPacketLoss(currentRecord[3].trim());
				currentStatistics.setDelay(currentRecord[4].trim());
				currentStatistics.setJitter(currentRecord[5].trim());
				currentStatistics.setFlowData(currentRecord[6].trim());

				if (circuitsStatistics.containsKey(timestamp))
					circuitsStatistics.get(timestamp).add(currentStatistics);

				else {
					List<CircuitStatistics> circuitStatisticsList = new ArrayList<CircuitStatistics>();
					circuitStatisticsList.add(currentStatistics);
					circuitsStatistics.put(timestamp, circuitStatisticsList);
				}
			}

			return circuitsStatistics;

		} finally {
			reader.close();
		}
	}

}
