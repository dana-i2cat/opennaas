package org.opennaas.extensions.ofertie.ncl.monitoring;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Port;
import org.opennaas.extensions.ofnetwork.capability.monitoring.IMonitoringNetworkCapability;
import org.opennaas.extensions.ofnetwork.events.LinkCongestionEvent;
import org.opennaas.extensions.ofnetwork.model.NetworkStatistics;
import org.opennaas.extensions.ofnetwork.repository.OFNetworkRepository;
import org.opennaas.extensions.openflowswitch.capability.monitoring.PortStatistics;
import org.opennaas.extensions.openflowswitch.capability.monitoring.SwitchPortStatistics;

/**
 * NCL monitoring component
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class NCLMonitoring {

	private static final Log			log							= LogFactory.getLog(NCLMonitoring.class);

	private static final String			NCL_MONITORING_FILE_ID		= "org.ofertie.ncl.monitoring";
	private static final String			THROUGHPUT_THRESHOLD		= "throughput_threshold";
	private static final String			STATISCS_POLLER_FREQUENCY	= "statistics_poller_freq";

	private static final DecimalFormat	DF							= new DecimalFormat("0.000");

	// FIXME hardcoded link capacity to 1Gbits/s
	private static final double			linkCapacity				= 1.0;

	private IEventManager				eventManager;

	private INCLController				nclController;

	private NCLModel					nclModel;

	private int							bandwidthThreshold;
	private int							statisticsPollerFreq;

	private long						previousTimestamp			= -1;
	private NetworkStatistics			previousNetworkStatistics	= null;

	public void init() {
		log.info("Initializing Ofertie NCL monitoring...");

		// get configuration file properties
		ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getBundleContext());
		try {
			bandwidthThreshold = Integer.parseInt(configurationAdmin.getProperty(NCL_MONITORING_FILE_ID, THROUGHPUT_THRESHOLD));
			statisticsPollerFreq = Integer.parseInt(configurationAdmin.getProperty(NCL_MONITORING_FILE_ID, STATISCS_POLLER_FREQUENCY));
		} catch (IOException e) {
			log.error("Error getting configuration!", e);
		} catch (NumberFormatException e) {
			log.error("Number format error getting configuration property!", e);
		}

		// get event manager service
		try {
			eventManager = Activator.getEventManagerService();
		} catch (ActivatorException e) {
			log.error("Error getting Eent Manager Service!", e);
		}

		// initialize statistics poller
		TimerTask statisticsPoller = new StatisticsPoller();
		Timer timer = new Timer("NCL Monitoring statistics poller", true);
		timer.scheduleAtFixedRate(statisticsPoller, 0, statisticsPollerFreq * 1000);

		log.info("Ofertie NCL monitoring initialized");
	}

	private void notifyCongestion(String switchName, String portId) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(LinkCongestionEvent.SWITCH_ID_KEY, switchName);
		properties.put(LinkCongestionEvent.PORT_ID_KEY, portId);

		LinkCongestionEvent event = new LinkCongestionEvent(properties);
		eventManager.publishEvent(event);
	}

	public INCLController getNclController() {
		return nclController;
	}

	public void setNclController(INCLController nclController) {
		this.nclController = nclController;
	}

	public NCLModel getNclModel() {
		return nclModel;
	}

	public void setNclModel(NCLModel nclModel) {
		this.nclModel = nclModel;
	}

	/**
	 * Thread class to execute the periodic poller to get the statistics from the network
	 */
	private class StatisticsPoller extends TimerTask {

		@Override
		public void run() {
			try {
				// get all networks
				IResource network = getFirstOFNetwork();

				if (network == null) {
					// no network found
					log.debug("No networks present. Skipping monitoring tasks");
				} else {
					// congested ports temp var
					Set<Port> newCongestedPorts = new HashSet<Port>();

					log.debug("Getting network statistics...");
					NetworkStatistics currentNetworkStatistics = readCurrentNetworkStatistics(network);
					long currentTimestamp = System.currentTimeMillis();

					if (currentNetworkStatistics != null) {
						if (previousNetworkStatistics != null && previousTimestamp > 0) {

							// calculate throughput and check threshold for each port
							for (String switchName : currentNetworkStatistics.getSwitchStatistics().keySet()) {
								log.debug("Analizing switch statistics for switch " + switchName);
								for (Integer portId : currentNetworkStatistics.getSwitchStatistics().get(switchName).getStatistics().keySet()) {
									try {
										long currentBytes = getPortReceivedBytes(currentNetworkStatistics, switchName, portId);
										long previousBytes = getPortReceivedBytes(previousNetworkStatistics, switchName, portId);

										// calculate throughput
										double throughput = calculateThroughput(previousBytes, currentBytes,
												previousTimestamp, currentTimestamp);

										log.debug("\tPort " + portId + ", calculated throughput = " + DF.format(throughput) + " Gbits/s");

										// check if throughput exceeds
										if (isThresholdExceeded(throughput, linkCapacity, bandwidthThreshold)) {
											log.debug("\tPort " + portId + ", congestion detected. Throughput > " + DF
													.format((linkCapacity * bandwidthThreshold) / 100) + " Gbits/s");
											// add congested port
											Port congestedPort = new Port();
											congestedPort.setDeviceId(switchName);
											congestedPort.setPortNumber(String.valueOf(portId));
											newCongestedPorts.add(congestedPort);
										}
									} catch (Exception e) {
										log.debug("Failed to calculate throughput for port " + portId + " in switch " + switchName);
									}
								}
							}
						}

						// reset congested ports
						nclModel.setCongestedPorts(newCongestedPorts);

						// raise alarm event for each congested port
						for (Port congestedPort : newCongestedPorts) {
							log.info("Throughput threshold exceeded, congestion detected. Raising alarm event!");
							notifyCongestion(congestedPort.getDeviceId(), congestedPort.getPortNumber());
						}

						// store current statistics and timestamp
						previousNetworkStatistics = currentNetworkStatistics;
						previousTimestamp = currentTimestamp;
					}
				}

			} catch (ResourceException e) {
				log.error("Error processing network statistics", e);
			} catch (ActivatorException e) {
				log.error("Error getting OF network", e);
			}
		}

		private NetworkStatistics readCurrentNetworkStatistics(IResource network) throws CapabilityException {

			NetworkStatistics currentNetworkStatistics = null;

			IMonitoringNetworkCapability monitoringNetworkCapability = null;
			try {
				// get port switch statistics for each network
				monitoringNetworkCapability = (IMonitoringNetworkCapability) network
						.getCapabilityByInterface(IMonitoringNetworkCapability.class);
			} catch (ResourceException e) {
				// there is not IMonitoringNetworkCapability in this network
				log.debug("Openflow network resource without IMonitoringNetworkCapability.");
			}

			if (monitoringNetworkCapability != null) {

				log.debug("Getting network statistics...");
				currentNetworkStatistics = monitoringNetworkCapability.getNetworkStatistics();
			}
			return currentNetworkStatistics;
		}

		/**
		 * 
		 * @param statistics
		 *            to take information from
		 * @param switchName
		 *            indicates the switch holding the port
		 * @param portId
		 *            indicates the port
		 * @return value of receivedBytes for given port that is in statistics
		 * @throws Exception
		 *             in case statistics contains no value matching given arguments
		 */
		private long getPortReceivedBytes(NetworkStatistics statistics, String switchName, Integer portId) throws Exception {
			if (statistics != null) {
				SwitchPortStatistics switchPortStatistics = statistics.getSwitchPortStatistic(switchName);
				if (switchPortStatistics != null) {
					PortStatistics portStatistics = switchPortStatistics.getStatistics().get(portId);
					if (portStatistics != null) {
						return portStatistics.getReceiveBytes();
					}
				}
			}
			throw new Exception("No statistics for switch " + switchName + " and port " + portId);
		}

		/**
		 * Returns if throughput is exceeding threshold
		 * 
		 * @param currentThroughput
		 *            current throughput of the port (in Gbits/s)
		 * @param linkCapacity
		 *            link capacity (in Gbits/s)
		 * @param threshold
		 *            threshold (in percentage)
		 * @return true if threshold is exceeded, false otherwise
		 */
		private boolean isThresholdExceeded(double currentThroughput, double linkCapacity, int threshold) {
			return currentThroughput > ((linkCapacity * threshold) / 100);
		}

		/**
		 * Calculate throughput (Gbits/s)
		 * 
		 * @param previousBytes
		 * @param currentBytes
		 * @param previousTimestamp
		 * @param currentTimestamp
		 * @return throughput in Gbits/s
		 */
		private double calculateThroughput(long previousBytes, long currentBytes, long previousTimestamp, long currentTimestamp) {
			// bytes / millisecond ~ kBytes/s
			double kBytesPerSecond = ((double) (currentBytes - previousBytes)) / ((double) (currentTimestamp - previousTimestamp));
			// convert kBytes/s to Gbits/s
			return (kBytesPerSecond * 8) / (1000 * 1000);
		}

		/**
		 * Get the unique OpenFlow Network expected (the first one)
		 * 
		 * @return
		 * @throws ActivatorException
		 * @throws ResourceException
		 */
		private IResource getFirstOFNetwork() throws ActivatorException {
			List<IResource> oFNetworks = Activator.getResourceManagerService().listResourcesByType(OFNetworkRepository.OF_NETWORK_RESOURCE_TYPE);
			if (oFNetworks.size() > 0) {
				return oFNetworks.get(0);
			} else {
				return null;
			}
		}
	}

}
