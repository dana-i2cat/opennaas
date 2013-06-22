package org.opennaas.extensions.power.capabilities.shell;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.gim.controller.capabilities.IPowerMonitoringController;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.power.capabilities.IPowerMonitoringCapability;

@Command(scope = "power", name = "getPowerMetrics", description = "Reads power metrics of specified resource")
public class GetCurrentPowerMetricsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id where given port is.", required = true, multiValued = false)
	private String		resourceId;

	private DateFormat	dateFormat	= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("getPowerMetrics of resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		MeasuredLoad metrics = ((IPowerMonitoringController) resource.getCapabilityByInterface(IPowerMonitoringCapability.class))
				.getCurrentPowerMetrics();
		printMetrics(metrics);

		printEndCommand();
		return null;

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
