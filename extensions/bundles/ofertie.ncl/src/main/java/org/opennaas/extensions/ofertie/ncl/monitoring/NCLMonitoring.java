package org.opennaas.extensions.ofertie.ncl.monitoring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.sdnnetwork.events.LinkCongestionEvent;

/**
 * NCL monitoring component
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class NCLMonitoring {

	private static final Log	log						= LogFactory.getLog(NCLMonitoring.class);

	private static final String	NCL_MONITORING_FILE_ID	= "org.ofertie.ncl.monitoring";
	private static final String	THROUGHPUT_THRESHOLD	= "throughput_threshold";

	private IEventManager		eventManager;

	private INCLController		nclController;

	private String				bandwidthThreshold;

	public NCLMonitoring() {
		log.info("Initializing Ofertie NCL monitoring...");
		initilize();
		log.info("Ofertie NCL monitoring initialized");
	}

	private void initilize() {
		// get configuration file
		ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getBundleContext());
		try {
			bandwidthThreshold = configurationAdmin.getProperty(NCL_MONITORING_FILE_ID, THROUGHPUT_THRESHOLD);
		} catch (IOException e) {
			log.error("Error getting configuration!", e);
		}

		// get event manager service
		try {
			eventManager = Activator.getEventManagerService();
		} catch (ActivatorException e) {
			log.error("Error getting Eent Manager Service!", e);
		}
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

}
