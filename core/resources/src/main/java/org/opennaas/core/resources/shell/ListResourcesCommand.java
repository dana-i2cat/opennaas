package org.opennaas.core.resources.shell;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.network.Device;
import org.opennaas.core.resources.descriptor.network.Interface;
import org.opennaas.core.resources.descriptor.network.InterfaceId;

/**
 * List the Resources that are in the IaaS Container
 */

@Command(scope = "resource", name = "list", description = "List all resources in the platform")
public class ListResourcesCommand extends GenericKarafCommand {

	@Option(name = "--type", aliases = { "-t" }, description = "Specifies the type of resources to list (if specified, only resources of this type will be listed)", required = false, multiValued = false)
	String	resourceType	= null;

	@Option(name = "--all", aliases = { "-a" }, description = "Extensive version ", required = false, multiValued = false)
	boolean	flagAll			= false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.karaf.shell.console.AbstractAction#doExecute()
	 */
	@Override
	protected Object doExecute() throws Exception {
		Object retValue = null;
		try {
			IResourceManager manager = getResourceManager();
			// if resourceType is null return all the resources
			List<IResource> resources = manager.listResourcesByType(resourceType);
			retValue = print(resources);
		} catch (Exception e) {
			printError(e);
			printError("Error listing resources. ");
		}
		printEndCommand();
		return retValue;
	}

	/**
	 * Print the values of the resources.
	 * 
	 * @param resources
	 */
	private Object print(List<IResource> resources) {
		printInitCommand("list resources");
		if (checkResources(resources)) {
			printSymbol("Found " + resources.size() + " resources.\n");
			printResources(resources);
			if (flagAll) {
				printExtResources(resources);
			}
		}

		return null;
	}

	/**
	 * Print resources (name, type, state)
	 * 
	 * @param resources
	 */
	private void printResources(List<IResource> resources) {
		for (IResource resource : resources) {
			printSymbol(doubleTab +
					StringUtils.rightPad("Resource Name: " + resource.getResourceDescriptor().getInformation().getName(), 40) +
					StringUtils.rightPad("Type: " + resource.getResourceDescriptor().getInformation().getType(), 40) +
					StringUtils.rightPad("State: " + resource.getState(), 40));
		}
		printSymbol("");
	}

	/**
	 * Print extensive version of resources (name, type, state, devices, interfaces and links)
	 * 
	 * @param resources
	 */
	private void printExtResources(List<IResource> resources) {

		for (IResource resource : resources) {
			ResourceDescriptor resourceDescriptor = resource.getResourceDescriptor();
			if (resourceDescriptor != null && resourceDescriptor.getNetworkTopology() != null) {
				printSymbol("-Resource Name: " + resource.getResourceDescriptor().getInformation().getName() + doubleTab +
						"Type: " + resource.getResourceDescriptor().getInformation().getType() + doubleTab +
						"State: " + resource.getState());
				printTopology(resource.getResourceDescriptor());
			}
		}
	}

	/**
	 * Print the values of the topology, devices (with interfaces) and links
	 * 
	 * @param networkTopology
	 */
	private void printTopology(ResourceDescriptor resourceDescriptor) {
		if (resourceDescriptor.getNetworkTopology() != null) {
			if (resourceDescriptor.getNetworkTopology().getDevices() != null
					&& !resourceDescriptor.getNetworkTopology().getDevices().isEmpty()) {
				printDevices(resourceDescriptor.getNetworkTopology().getDevices());
			}

			if (resourceDescriptor.getNetworkTopology().getInterfaces() != null
					&& !resourceDescriptor.getNetworkTopology().getInterfaces().isEmpty()) {
				printLinks(resourceDescriptor.getNetworkTopology().getInterfaces());
			}
		}
	}

	/**
	 * @param devices
	 */
	private void printDevices(List<Device> devices) {
		for (Device device : devices) {
			printSymbol("\n   -Device id: " + device.getName() + "\n");
			if (device != null && !device.getHasInterfaces().isEmpty()) {
				printSymbol("      -Interfaces" + "\n");
				printInterfaces(device.getHasInterfaces());
			}
		}
		printSymbol("");
	}

	/**
	 * @param interfaceIds
	 */
	private void printInterfaces(List<InterfaceId> interfaceIds) {
		for (int i = 0; i < interfaceIds.size(); i = i + 3) {
			String resource1 = i < interfaceIds.size() ? interfaceIds.get(i).getResource() : "";
			String resource2 = i + 1 < interfaceIds.size() ? interfaceIds.get(i + 1).getResource() : "";
			String resource3 = i + 2 < interfaceIds.size() ? interfaceIds.get(i + 2).getResource() : "";
			printSymbol(doubleTab + doubleTab + StringUtils.rightPad(resource1, 40) + StringUtils.rightPad(resource2, 40) + StringUtils.rightPad(
					resource3, 40));
		}
	}

	/**
	 * @param interfaces
	 */
	private void printLinks(List<Interface> interfaces) {
		printSymbol("      -Connections" + "\n");
		for (int i = 0; i < interfaces.size(); i = i + 2) {
			String interface1 = i < interfaces.size() ? getLink(interfaces.get(i)) : "";
			String interface2 = i + 1 < interfaces.size() ? getLink(interfaces.get(i + 1)) : "";
			printSymbol(doubleTab + doubleTab + StringUtils.rightPad(interface1, 50) + interface2);
		}
		printSymbol("\n");
	}

	/**
	 * @param interf
	 */
	private String getLink(Interface interf) {
		String link;
		String name = "";

		if (interf.getLinkTo() != null) {
			if (interf.getLinkTo().getName() != null) {
				name += interf.getLinkTo().getName() + " ==> ";
			}
		} else {
			name += " No Link";
		}
		link = interf.getName() + name;
		return link;
	}

	/**
	 * @param resources
	 */
	private Boolean checkResources(List<IResource> resources) {
		boolean isOK = true;
		if (resources == null) {
			printError("Didn't find a repository of type " + resourceType);
			printEndCommand();
			isOK = false;
		}
		if (resources.isEmpty()) {
			printInfo("There are no resources registered.");
			printEndCommand();
			isOK = false;
		}
		return isOK;
	}
}
