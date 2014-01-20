package org.opennaas.core.resources.shell;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.List;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.network.Device;
import org.opennaas.core.resources.descriptor.network.Interface;
import org.opennaas.core.resources.descriptor.network.InterfaceId;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;

/**
 * List the Resources that are in the IaaS Container
 * 
 * 
 */
@Command(scope = "resource", name = "list", description = "List all resources in the platform")
public class ListResourcesCommand extends GenericKarafCommand {

	@Option(name = "--type", aliases = { "-t" }, description = "Specifies the type of resources to list (if specified, only resources of this type will be listed)", required = false, multiValued = false)
	private String	resourceType	= null;

	@Option(name = "--all", aliases = { "-a" }, description = "Extensive version ", required = false, multiValued = false)
	private boolean	flagAll			= false;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("list resources");
		try {
			IResourceManager manager = getResourceManager();
			List<IResource> resources = manager.listResourcesByType(resourceType);
			// if resourceType is null return all the resources
			if (resources == null) {
				printError("Didn't find a repository of type " + resourceType);
				printEndCommand();
				return null;
			}
			if (resources.isEmpty()) {
				printInfo("There are no resources registered.");
				printEndCommand();
				return null;
			}

			printInfo("Found " + resources.size() + " resources.");
			// printSymbol(horizontalSeparator);
			// if (resourceType != null) {
			// printInfo("Listing resources of type " + resourceType);
			// printSymbol(underLine);
			//
			// for (IResource resource : resources) {
			// printInfo(doubleTab + "ID: " + resource
			// .getResourceDescriptor()
			// .getInformation().getName() + doubleTab + "STATE: " + resource
			// .getState());
			// if (flagAll)
			// printAll(resource.getResourceDescriptor());
			// }
			// // printSymbol(horizontalSeparator);
			//
			// } else {
			// printInfo("Listing all resources ");
			// printSymbol(underLine);
			for (IResource resource : resources) {
				printInfo(doubleTab + "TYPE: " + resource.getResourceDescriptor().getInformation().getType() +
						doubleTab + "ID: " + resource.getResourceDescriptor().getInformation().getName() +
						doubleTab + "STATE: " + resource.getState());

				if (flagAll)
					printAll(resource.getResourceDescriptor());

			}
			// printSymbol(horizontalSeparator);
			// }

		} catch (Exception e) {
			printError(e);
			printError("Error listing resources. ");

		}
		printEndCommand();
		return null;
	}

	private void printAll(ResourceDescriptor resourceDescriptor) {

		// TODO get network info
		/* network descriptor */
		if (resourceDescriptor.getNetworkTopology() != null)
			printNetworkTopology(resourceDescriptor.getNetworkTopology());

	}

	private void printNetworkTopology(NetworkTopology networkTopology) {

		printInfo("-> Devices <-");
		if (networkTopology.getDevices() != null) {
			for (Device device : networkTopology.getDevices()) {
				printInfo(paintResource(device).toString());
			}
		}
		printInfo("-> Connections <-");
		if (networkTopology.getInterfaces() != null)
			paintConnections(networkTopology.getInterfaces());

	}

	private StringBuilder paintConnections(List<Interface> interfaces) {
		StringBuilder message = new StringBuilder();
		for (Interface interf : interfaces) {
			message.append(String.format("\t · %s ---> %s (%s)", interf.getName(), interf.getLinkTo(), interf.getCapacity()));
		}

		return message;

	}

	public StringBuilder paintResource(Device device) {
		StringBuilder message = new StringBuilder();

		message.append("Device id: " + device.getName());
		message.append("\nInterfaces: ");
		for (InterfaceId interfaceId : device.getHasInterfaces()) {
			message.append("\t" + interfaceId.getResource());
		}

		return message;
	}

}
