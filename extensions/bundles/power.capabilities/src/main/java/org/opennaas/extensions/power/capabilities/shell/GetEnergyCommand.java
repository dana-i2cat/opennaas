package org.opennaas.extensions.power.capabilities.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.pdu.capability.IPDUPowerSupplyCapability;
import org.opennaas.extensions.power.capabilities.IPowerSupplyCapability;

@Command(scope = "power", name = "getEnergy", description = "Reads energy source of specified resource")
public class GetEnergyCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("getEnergy of resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		IPowerSupplyCapability capability = (IPowerSupplyCapability) resource.getCapabilityByInterface(IPowerSupplyCapability.class);

		Energy energy = capability.getAggregatedEnergy();
		double pricePerUnit = capability.getAggregatedPricePerEnergyUnit();

		printEnergy(energy, pricePerUnit);

		printEndCommand();
		return null;

	}

	private void printEnergy(Energy energy, double pricePerUnit) {
		printSymbol("Energy Class: " + energy.getEnergyClass());
		printSymbol("Energy Type: " + energy.getEnergyType());
		printSymbol("CO2perKW: " + energy.getCO2perKw());
		printSymbol("Price per unit: " + pricePerUnit);
	}

}
