package net.i2cat.nexus.resources.shell;

import java.util.HashMap;
import java.util.List;

import net.i2cat.nexus.resources.ILifecycle.State;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceManager;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.Information;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

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
		printInitCommand("information resource");
		try {
			ResourceManager manager = (ResourceManager) getResourceManager();

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
						printInfo("Resource ID: " + information.getName());
						printInfo("State: " + resource.getState());
						printSymbol(horizontalSeparator);
						printInfo("Resource descriptor ");
						printInfo(doubleTab + "Description: " + information.getDescription());
						printInfo(doubleTab + "Type: " + information.getType());

						if (!resource.getResourceDescriptor().getProfileId().isEmpty()) {
							printInfo("Profile ID: " + resource.getResourceDescriptor().getProfileId());
						}
						if (!resource.getResourceDescriptor().getProperties().isEmpty()) {
							HashMap<String, String> properties = (HashMap<String, String>) resource.getResourceDescriptor().getProperties();
							if (!properties.isEmpty()) {
								printInfo("Properties: ");
								for (String key : properties.keySet()) {
									printInfo(doubleTab + key + ":" + properties.get(key));
								}
							}
						}
						printSymbol(horizontalSeparator);
						printInfo("Active capabilities:");
						for (ICapability capability : resource.getCapabilities()) {
							// show only the active capabilities
							if (capability.getState().equals(State.ACTIVE)) {
								printInfo(indexArrowRigth + simpleTab + "Name: " + capability.getCapabilityInformation().getName());
								printInfo(doubleTab + "Description: " + capability.getCapabilityInformation().getDescription());
								printInfo(doubleTab + "Type: " + capability.getCapabilityInformation().getType());
							}

						}

					} else {
						printError("The resource " + id +
										" is not found on repository.");
					}
				} catch (ResourceException e) {
					printError(e);
				}
				printSymbol(underLine);
			}
		} catch (Exception e) {
			printError(e);
			printError("Error showing information of resource.");

		}
		printEndCommand();
		return null;
	}
}
