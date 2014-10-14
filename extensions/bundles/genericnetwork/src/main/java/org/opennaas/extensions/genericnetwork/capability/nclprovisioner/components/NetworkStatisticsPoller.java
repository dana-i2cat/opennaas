package org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components;

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
import java.net.URI;
import java.util.TimerTask;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.genericnetwork.capability.circuitstatistics.ICircuitStatisticsCapability;
import org.opennaas.extensions.genericnetwork.capability.nclmonitoring.portstatistics.IPortStatisticsMonitoringCapability;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimePeriod;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedPortStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedStatistics;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class NetworkStatisticsPoller extends TimerTask {

	private Log							log	= LogFactory.getLog(NetworkStatisticsPoller.class);

	private URI							slaManagerUri;
	private IResource					resource;
	private NetworkObservationsPusher	slaManagerClient;
	private long						previousTimestamp;

	public NetworkStatisticsPoller(URI slaManagerUri, IResource resource) {
		this.slaManagerUri = slaManagerUri;
		this.resource = resource;

		slaManagerClient = new NetworkObservationsPusher(slaManagerUri);
		previousTimestamp = System.currentTimeMillis();

	}

	@Override
	public void run() {

		long currentTimestamp = System.currentTimeMillis();
		TimePeriod timePeriod = new TimePeriod(previousTimestamp, currentTimestamp);

		log.info("Reporting stadistics to SLA Manager");

		log.debug("Getting stadistics for resource " + resource.getResourceDescriptor().getInformation().getName() + " during time period " + timePeriod);

		try {
			// report circuitsStatistics
			ICircuitStatisticsCapability circuitStatisticsCapab = (ICircuitStatisticsCapability) resource
					.getCapabilityByInterface(ICircuitStatisticsCapability.class);
			IPortStatisticsMonitoringCapability portStatisticsCapab = (IPortStatisticsMonitoringCapability) resource
					.getCapabilityByInterface(IPortStatisticsMonitoringCapability.class);

			String circuitStatisticsCSV = circuitStatisticsCapab.getStatistics(timePeriod);
			slaManagerClient.sendCircuitStatistics(circuitStatisticsCSV);
			log.debug("Circuit stadistics successfully reported.");

			TimedPortStatistics portStatistics = portStatisticsCapab.getPortStatistics(timePeriod);

			slaManagerClient.sendPortStatistics(parsePortStatistics(portStatistics));
			log.debug("Port stadistics successfully reported.");

		} catch (ResourceException e) {
			log.warn("Could not report statistics for resource " + resource.getResourceDescriptor()
					.getInformation().getName(), e);
		} catch (IOException io) {
			log.warn("Error reporting port statistics for resource " + resource.getResourceDescriptor().getInformation().getName(), io);
		}

	}

	private String parsePortStatistics(TimedPortStatistics portStatistics) throws IOException {

		log.debug("Parsing port statistics into CSV format.");

		StringBuilder sb = new StringBuilder();
		CSVWriter writer = new CSVWriter(new StringBuilderWriter(sb), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

		try {
			for (TimedStatistics statistic : portStatistics.getStatistics()) {
				String[] csvStatistic = new String[5];
				csvStatistic[0] = String.valueOf(statistic.getTimestamp());
				csvStatistic[1] = statistic.getSwitchId();
				csvStatistic[2] = statistic.getPortId();
				csvStatistic[3] = statistic.getThroughput();
				csvStatistic[4] = statistic.getPacketLoss();

				writer.writeNext(csvStatistic);
			}

		} finally {
			writer.close();
		}
		return sb.toString();
	}
}
