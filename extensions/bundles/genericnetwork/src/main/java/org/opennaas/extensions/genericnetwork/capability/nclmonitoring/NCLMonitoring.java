package org.opennaas.extensions.genericnetwork.capability.nclmonitoring;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.capability.statistics.INetworkStatisticsCapability;
import org.opennaas.extensions.genericnetwork.events.PortCongestionEvent;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.NetworkStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedSwitchPortStatistics;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.SwitchPortStatistics;

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

/**
 * NCL monitoring component
 * 
 * @author Julio Carlos Barrera
 * @author Isart Canyameres Gimenez (i2cat)
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
	private IResource					resource;

	private int							bandwidthThreshold;
	private int							statisticsPollerFreq;

	private long						previousTimestamp			= -1;
	private NetworkStatistics			previousNetworkStatistics	= null;

	private Timer						statisticsPollerTimer;

	/**
	 * 
	 * @return the resource
	 */
	public IResource getResource() {
		return resource;
	}

	/**
	 * 
	 * @param resource
	 *            the resource to set
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	/**
	 * @return the eventManager
	 */
	public IEventManager getEventManager() {
		return eventManager;
	}

	/**
	 * @param eventManager
	 *            the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void init() {
		log.info("Initializing NCL monitoring...");

		// get configuration file properties
		ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getContext());
		try {
			bandwidthThreshold = Integer.parseInt(configurationAdmin.getProperty(NCL_MONITORING_FILE_ID, THROUGHPUT_THRESHOLD));
			statisticsPollerFreq = Integer.parseInt(configurationAdmin.getProperty(NCL_MONITORING_FILE_ID, STATISCS_POLLER_FREQUENCY));
		} catch (IOException e) {
			log.error("Error getting configuration!", e);
		} catch (NumberFormatException e) {
			log.error("Number format error getting configuration property!", e);
		}

		// initialize statistics poller
		TimerTask statisticsPoller = new StatisticsPoller();
		statisticsPollerTimer = new Timer("NCL Monitoring statistics poller", true);
		statisticsPollerTimer.scheduleAtFixedRate(statisticsPoller, 0, statisticsPollerFreq * 1000);

		log.info("NCL monitoring initialized");
	}

	public void stop() {
		statisticsPollerTimer.cancel();
		statisticsPollerTimer = null;
	}

	private void notifyCongestion(Port port) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PortCongestionEvent.PORT_ID_KEY, port.getId());
		properties.put(PortCongestionEvent.NETWORK_ID_KEY, resource.getResourceIdentifier().getId());

		PortCongestionEvent event = new PortCongestionEvent(properties);
		eventManager.publishEvent(event);
	}

	/**
	 * Thread class to execute the periodic poller to get the statistics from the network
	 */
	private class StatisticsPoller extends TimerTask {

		@Override
		public void run() {
			try {
				IResource network = getResource();

				if (network == null) {
					// no network found
					log.debug("No networks present. Skipping monitoring tasks");
				} else {
					// congested ports temp var
					Set<Port> newCongestedPorts = new HashSet<Port>();

					log.debug("Getting network statistics...");
					NetworkStatistics currentNetworkStatistics = readCurrentNetworkStatistics(network);
					long currentTimestamp = System.currentTimeMillis();

					if (currentNetworkStatistics == null) {
						log.debug("Network statistics not available.");
					} else {
						if (previousNetworkStatistics != null && previousTimestamp > 0) {

							// calculate throughput and check threshold for each port
							for (String switchName : currentNetworkStatistics.getSwitchStatistics().keySet()) {
								log.debug("Analizing switch statistics for switch " + switchName);
								for (String portId : currentNetworkStatistics.getSwitchStatistics().get(switchName).getStatistics().keySet()) {
									try {
										
										// calculate throughput
										double throughput = calculateThroughput(previousNetworkStatistics, currentNetworkStatistics, 
												switchName, portId, previousTimestamp, currentTimestamp);

										log.debug("\tPort " + portId + ", calculated throughput = " + DF.format(throughput) + " Gbits/s");

										// check if throughput exceeds
										if (isThresholdExceeded(throughput, linkCapacity, bandwidthThreshold)) {
											log.debug("\tPort " + portId + ", congestion detected. Throughput > " + DF
													.format((linkCapacity * bandwidthThreshold) / 100) + " Gbits/s");
											// add congested port

											Port congestedPort = new Port();
											congestedPort.setId(portId);
											newCongestedPorts.add(congestedPort);
										}
										
										double packetLoss = calculatePacketLoss(previousNetworkStatistics, currentNetworkStatistics, 
												switchName, portId, previousTimestamp, currentTimestamp);

										log.debug("\tPort " + portId + ", calculated packetLoss = " + DF.format(packetLoss) + " %");
										
										TimedStatistics timedStats = new TimedStatistics();
										timedStats.setTimestamp(currentTimestamp);
										timedStats.setSwitchId(switchName);
										timedStats.setPortId(portId);
										timedStats.setThroughput(String.valueOf(throughput));
										timedStats.setPacketLoss(String.valueOf(packetLoss));
										
										// store TimedStats in network model
										TimedSwitchPortStatistics allStats = ((GenericNetworkModel)resource.getModel()).getTimedSwitchPortStatistics();
										
										if (! allStats.getStatisticsMap().containsKey(Long.valueOf(currentTimestamp))) {
											allStats.getStatisticsMap().put(currentTimestamp, new HashMap<String, List<TimedStatistics>>());
										}
										if (! allStats.getStatisticsMap().get(currentTimestamp).containsKey(switchName)) {
											allStats.getStatisticsMap().get(currentTimestamp).put(switchName, new ArrayList<TimedStatistics>());
										}
										allStats.getStatisticsMap().get(currentTimestamp).get(switchName).add(timedStats);
										
									} catch (Exception e) {
										log.debug("Failed to calculate throughput or packetloss for port " + portId + " in switch " + switchName + ": " + e.getMessage());
									}
								}
							}
						}

						// reset congested ports
						resetCongestedPorts(newCongestedPorts);

						// raise alarm event for each congested port
						for (Port congestedPort : newCongestedPorts) {
							log.info("Throughput threshold exceeded, congestion detected. Raising alarm event!");
							notifyCongestion(congestedPort);
						}

						// store current statistics and timestamp
						previousNetworkStatistics = currentNetworkStatistics;
						previousTimestamp = currentTimestamp;
					}
				}

			} catch (ResourceException e) {
				log.error("Error processing network statistics", e);
			}
		}

		/**
		 * Resets isCongested in all ports in the model.
		 * 
		 * All ports included in congestedPorts will have isCongested = true after this method execution
		 * 
		 * All ports not included in congestedPorts will have isCongested = false after this method execution
		 * 
		 * @param congestedPorts
		 */
		private void resetCongestedPorts(Set<Port> congestedPorts) {
			if (((GenericNetworkModel) resource.getModel()).getTopology() != null) {
				for (NetworkElement ne : ((GenericNetworkModel) resource.getModel()).getTopology().getNetworkElements()) {
					for (Port port : ne.getPorts()) {
						boolean isCongested = false;
						for (Port congestedPort : congestedPorts) {
							if (port.getId().equals(congestedPort.getId())) {
								isCongested = true;
							}
						}
						port.getState().setCongested(isCongested);
					}
				}
			}
		}

		private NetworkStatistics readCurrentNetworkStatistics(IResource network) throws CapabilityException {

			NetworkStatistics currentNetworkStatistics = null;

			INetworkStatisticsCapability networkStatisticsCapability = null;
			try {
				// get port switch statistics for each network
				networkStatisticsCapability = (INetworkStatisticsCapability) network
						.getCapabilityByInterface(INetworkStatisticsCapability.class);
			} catch (ResourceException e) {
				// there is not INetworkStatisticsCapability in this network
				log.debug("Openflow network resource without INetworkStatisticsCapability.");
			}

			if (networkStatisticsCapability != null) {

				log.debug("Getting network statistics...");
				currentNetworkStatistics = networkStatisticsCapability.getNetworkStatistics();
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
		private long getPortReceivedBytes(NetworkStatistics statistics, String switchName, String portId) throws Exception {
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
		 * 
		 * @param previousStats
		 * @param currentStats
		 * @param switchName
		 * @param portId
		 * @param previousTimestamp
		 * @param currentTimestamp
		 * @return
		 * @throws Exception if failed to get required data to calculate the throughput
		 */
		private double calculateThroughput(
				NetworkStatistics previousStats, 
				NetworkStatistics currentStats, 
				String switchName, String portId, 
				long previousTimestamp, long currentTimestamp) throws Exception {
			
			long currentBytes = getPortReceivedBytes(currentStats, switchName, portId);
			long previousBytes = getPortReceivedBytes(previousStats, switchName, portId);

			// calculate throughput
			return calculateThroughput(previousBytes, currentBytes,
					previousTimestamp, currentTimestamp);
		}

	}

	public double calculatePacketLoss(
			NetworkStatistics previousStats,
			NetworkStatistics currentStats, 
			String switchName, String portId, 
			long previousTimestamp, long currentTimestamp) throws Exception {
		
		// pk_loss = (receiveDropped(t1) + receiveErrors(t1) + receiveFrameErrors(t1) + receiveOverrunErrors(t1) + receiveCRCErrors(t1) - 
		// (receiveDropped(t0) + receiveErrors(t0) + receiveFrameErrors(t0) + receiveOverrunErrors(t0) + receiveCRCErrors(t0))) / 
		// (receivePacket(t1) - receivePackets(t0))
		
		long currentReceiveErrors = getPortReceiveErrors(currentStats, switchName, portId);
		long previousReceiveErrors = getPortReceiveErrors(previousStats, switchName, portId);
		
		long currentReceivedPackets = getPortReceivedPackets(currentStats, switchName, portId);
		long previousReceivedPackets = getPortReceivedPackets(previousStats, switchName, portId);
		
		if (currentReceivedPackets == previousReceivedPackets)
			return 0;
		
		return (currentReceiveErrors - previousReceiveErrors) / (currentReceivedPackets - previousReceivedPackets);
	}

	private long getPortReceivedPackets(NetworkStatistics statistics,
			String switchName, String portId) throws Exception {
		
		if (statistics != null) {
			SwitchPortStatistics switchPortStatistics = statistics.getSwitchPortStatistic(switchName);
			if (switchPortStatistics != null) {
				PortStatistics portStatistics = switchPortStatistics.getStatistics().get(portId);
				if (portStatistics != null) {
					return portStatistics.getReceivePackets();
				}
			}
		}
		throw new Exception("No statistics for switch " + switchName + " and port " + portId);
	}

	private long getPortReceiveErrors(NetworkStatistics statistics,
			String switchName, String portId) throws Exception {
		
		if (statistics != null) {
			SwitchPortStatistics switchPortStatistics = statistics.getSwitchPortStatistic(switchName);
			if (switchPortStatistics != null) {
				PortStatistics portStatistics = switchPortStatistics.getStatistics().get(portId);
				if (portStatistics != null) {
					return portStatistics.getReceiveCRCErrors() + 
							portStatistics.getReceiveDropped() + 
							portStatistics.getReceiveErrors() + 
							portStatistics.getReceiveFrameErrors() +
							portStatistics.getReceiveOverrunErrors();
				}
			}
		}
		throw new Exception("No statistics for switch " + switchName + " and port " + portId);
	}

}
