package org.opennaas.extensions.ofertie.ncl.monitoring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.opennaas.extensions.ofnetwork.events.LinkCongestionEvent;

/**
 * NCL monitoring component
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class NCLMonitoring {

	private static final Log	log							= LogFactory.getLog(NCLMonitoring.class);

	private static final String	NCL_MONITORING_FILE_ID		= "org.ofertie.ncl.monitoring";
	private static final String	THROUGHPUT_THRESHOLD		= "throughput_threshold";
	private static final String	STATISCS_POLLER_FREQUENCY	= "statistics_poller_freq";

	private IEventManager		eventManager;

	private INCLController		nclController;

	private NCLModel			nclModel;

	private String				bandwidthThreshold;
	private int					statisticsPollerFreq;

	public void init() {
		log.info("Initializing Ofertie NCL monitoring...");

		// get configuration file properties
		ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getBundleContext());
		try {
			bandwidthThreshold = configurationAdmin.getProperty(NCL_MONITORING_FILE_ID, THROUGHPUT_THRESHOLD);
			statisticsPollerFreq = Integer.parseInt(configurationAdmin.getProperty(NCL_MONITORING_FILE_ID, STATISCS_POLLER_FREQUENCY));
		} catch (IOException e) {
			log.error("Error getting configuration!", e);
		} catch (NumberFormatException e) {
			log.error("Number format error getting statistics_poller_freq configuration property!", e);
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

	private void notifyCongestion(String switchId, int portId) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(LinkCongestionEvent.SWITCH_ID_KEY, switchId);
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
			log.info("\n\nERASE ME!!! Plloignstatistics!!\n\n");
		}

	}

}
