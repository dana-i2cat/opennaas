package org.opennaas.extensions.power.capabilities.shell;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;
import org.opennaas.extensions.power.capabilities.IPowerMonitoringCapability;

@Command(scope = "power", name = "getPowerMetricsLog", description = "Reads power metrics from specified consumer")
public class GetPowerMetricsLogCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id where given port is.", required = true, multiValued = false)
	private String		resourceId;

	@Argument(index = 1, name = "from", description = "Beginning date", required = true, multiValued = false)
	private String		from;

	@Argument(index = 2, name = "to", description = "End date", required = true, multiValued = false)
	private String		to;

	private DateFormat	dateFormat	= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("getPowerMetricsLog of resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		PowerMonitorLog log = ((IPowerMonitoringCapability) resource.getCapabilityByInterface(IPowerMonitoringCapability.class))
				.getPowerMetricsByTimeRange(dateFormat.parse(from), dateFormat.parse(to));

		printLog(log);

		printEndCommand();
		return null;

	}

	private void printLog(PowerMonitorLog log) {
		for (MeasuredLoad metrics : log.getMeasuredLoads()) {
			printMetrics(metrics);
		}
	}

	private void printMetrics(MeasuredLoad metrics) {
		String readTime = dateFormat.format(metrics.getReadingTime());
		printSymbol("Power Consumption Metrics for resource " + resourceId + " @ " + readTime + ":");
		printSymbol("Voltage: " + metrics.getVoltage() + " Volts");
		printSymbol("Current: " + metrics.getCurrent() + " Amps");
		printSymbol("Power: " + metrics.getPower() + " KW");
		printSymbol("Energy: " + metrics.getEnergy() + " KWh");
	}

}
