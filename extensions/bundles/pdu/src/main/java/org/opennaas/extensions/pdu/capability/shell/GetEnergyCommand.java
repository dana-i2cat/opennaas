package org.opennaas.extensions.pdu.capability.shell;

/*
 * #%L
 * OpenNaaS :: PDU Resource
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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.pdu.capability.IPDUPowerSupplyCapability;

@Command(scope = "pdu", name = "getEnergy", description = "Reads energy source of specified resource")
public class GetEnergyCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("getEnergy of resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		IPDUPowerSupplyCapability capability = (IPDUPowerSupplyCapability) resource.getCapabilityByInterface(IPDUPowerSupplyCapability.class);

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
