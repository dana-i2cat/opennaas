package org.opennaas.extensions.genericnetwork.capability.circuitstatistics;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.opennaas.extensions.genericnetwork.model.TimePeriod;

import au.com.bytecode.opencsv.CSVReader;

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
			Set<CircuitStatistics> circuitStatistics = parseCSV(csvStatistics);
			GenericNetworkModel model = (GenericNetworkModel) resource.getModel();
			model.getCircuitStatistics().addAll(circuitStatistics);
		} catch (Exception e) {
			log.info("Error parsing received CSV", e);
			throw new CapabilityException(e);
		}

		log.info("Circuits statistics stored.");

	}

	private Set<CircuitStatistics> parseCSV(String csvStatistics) throws IllegalArgumentException, IOException {
		CSVReader reader = new CSVReader(new StringReader(csvStatistics));

		try {

			Set<CircuitStatistics> circuitStatistics = new HashSet<CircuitStatistics>();

			List<String[]> records = reader.readAll();

			log.debug("Storing the new " + records.size() + " received circuits statistics.");

			for (String[] currentRecord : records) {

				if (currentRecord.length != 8)
					throw new IllegalArgumentException("Invalid record length: it should contain 8 fields.");

				TimePeriod timePeriod = new TimePeriod();
				CircuitStatistics currentStatistics = new CircuitStatistics();

				timePeriod.setStartTime(Long.valueOf(currentRecord[0].trim()).longValue());
				timePeriod.setEndTime(Long.valueOf(currentRecord[1].trim()).longValue());

				currentStatistics.setTimePeriod(timePeriod);
				currentStatistics.setSlaFlowId(currentRecord[2].trim());
				currentStatistics.setThroughput(currentRecord[3].trim());
				currentStatistics.setPacketLoss(currentRecord[4].trim());
				currentStatistics.setDelay(currentRecord[5].trim());
				currentStatistics.setJitter(currentRecord[6].trim());
				currentStatistics.setFlowData(currentRecord[7].trim());

				circuitStatistics.add(currentStatistics);
			}

			return circuitStatistics;

		} finally {
			reader.close();
		}
	}

	@Override
	public String getStatistics(TimePeriod timePeriod) {
		// TODO Auto-generated method stub
		return null;
	}

}
