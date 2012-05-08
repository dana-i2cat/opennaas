package org.opennaas.core.resources.shell;

import java.util.HashMap;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;

/**
 * Show the information of one or more Resources
 * 
 * @author Evelyn Torras
 * 
 */
@Command(scope = "resource", name = "info", description = "Provides extended information about one or more resources.")
public class InfoResourcesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type and name.", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("print resource information");

		ResourceManager manager = null;
		try {
			manager = (ResourceManager) getResourceManager();
		} catch (Exception e) {
			printError(e.getMessage());
			printError("Error getting resource manager");
			printEndCommand();
			return null;
		}

		try {

			for (String id : resourceIDs) {

				String[] argsRouterName = new String[2];
				try {
					argsRouterName = splitResourceName(id);
				} catch (Exception e) {
					printError(e.getMessage());
					printEndCommand();
					return -1;
				}

				IResourceIdentifier identifier = null;
				IResource resource = null;
				try {
					identifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
					if (identifier != null) {

						resource = manager.getResource(identifier);
						Information information = resource.getResourceDescriptor().getInformation();
						printInfo("Resource Id: " + resource.getResourceIdentifier().getId());
						printInfo("Resource Name: " + information.getName());
						printInfo("State: " + resource.getState());
						printSymbol(horizontalSeparator);
						printInfo("Resource descriptor ");
						printInfo(doubleTab + "Description: " + information.getDescription());
						printInfo(doubleTab + "Type: " + information.getType());

						if (resource.getResourceDescriptor().getProfileId() != null && !resource.getResourceDescriptor().getProfileId().isEmpty()) {
							printInfo("Profile ID: " + resource.getResourceDescriptor().getProfileId());
						}
						if (resource.getResourceDescriptor().getProfileId() != null && !resource.getResourceDescriptor().getProperties().isEmpty()) {
							HashMap<String, String> properties = (HashMap<String, String>) resource.getResourceDescriptor().getProperties();
							if (!properties.isEmpty()) {
								printInfo("Properties: ");
								for (String key : properties.keySet()) {
									printInfo(doubleTab + key + ":" + properties.get(key));
								}
							}
						}
						// printSymbol(horizontalSeparator);
						printInfo("Instantiated capabilities:");
						for (ICapability capability : resource.getCapabilities()) {
							// show capabilities instantiated in this resource
							printInfo(indexArrowRigth + simpleTab + "Name: " + capability.getCapabilityInformation().getName());
							printInfo(doubleTab + "Description: " + capability.getCapabilityInformation().getDescription());
							printInfo(doubleTab + "Type: " + capability.getCapabilityInformation().getType());
						}

					} else {
						printError("The resource " + id +
								" is not found on repository.");
					}
				} catch (ResourceException e) {
					printError(e);
				}
				// printSymbol(underLine);
			}
		} catch (Exception e) {
			printError(e);
			printError(e.getMessage());
			printError(e.getLocalizedMessage());
			printError("Error showing information of resource.");
		}
		printEndCommand();
		return null;
	}
}
