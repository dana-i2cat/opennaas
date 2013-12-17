package org.opennaas.extensions.ofertie.ncl.monitoring;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.ofertie.ncl.Activator;

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

	private String				bandwidthThreshold;

	public NCLMonitoring() {
		log.info("Initializing Ofertie NCL monitoring...");
		initilize();
		log.info("Ofertie NCL monitoring initialized");
	}

	private void initilize() {
		ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getBundleContext());
		try {
			bandwidthThreshold = configurationAdmin.getProperty(NCL_MONITORING_FILE_ID, THROUGHPUT_THRESHOLD);
		} catch (IOException e) {
			log.error("Error getting configuration!", e);
		}
	}
}
