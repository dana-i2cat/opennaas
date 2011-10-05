package org.opennaas.core.resources.shell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import net.i2cat.mantychore.model.ComputerSystem;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "resource", name = "refresh", description = "Update the data model of a given resource")
public class RefreshResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type and name.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() {
		printInitCommand("create resource");

		IResourceManager manager;
		try {
			manager = getResourceManager();

			String[] argsRouterName = new String[2];
			try {
				argsRouterName = splitResourceName(resourceId);
			} catch (Exception e) {
				printError(e.getMessage());
				printEndCommand();
				return -1;
			}

			IResource resource = null;
			IResourceIdentifier identifier;
			try {
				identifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

				if (identifier == null) {
					printError("Error in identifier.");
					printEndCommand();
					return null;
				}

				resource = manager.getResource(identifier);
				validateResource(resource);

				// call the method to refresh each capability on resource

				//resource.setModel(new ComputerSystem());
				resource.setModel((IModel) new Object());
				/* start its capabilities */
				for (ICapability capab : resource.getCapabilities()) {

					/* abstract capabilities have to be initialized */
					if (capab instanceof AbstractCapability) {
						log.debug("Executing capabilities startup...");
						Response response = ((AbstractCapability) capab).sendRefreshActions();
						if (!response.getStatus().equals(Status.OK)) {
							throw new ResourceException();
						}
					}
				}

				ICapability queueCapab = resource.getCapability(createQueueInformation());
				QueueResponse response = (QueueResponse) queueCapab.sendMessage(QueueConstants.EXECUTE, resource.getModel());
				if (!response.isOk()) {
					// TODO IMPROVE ERROR REPORTING
					throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
				}

				if (resource.getProfile() != null) {
					log.debug("Executing initModel from profile...");
					resource.getProfile().initModel(resource.getModel());
				}

				/* the type resource is the same for all logical devices and for the physical device */
				String typeResource = resource.getResourceIdentifier().getType();

				List<String> nameLogicalRouters = resource.getModel().getChildren();

				/* intialize each resource */
				for (String nameResource : nameLogicalRouters) {
					try {
						manager.getIdentifierFromResourceName(typeResource, nameResource);
					} catch (ResourceNotFoundException e) {
						log.error(e.getMessage());
						log.info("This resource is new, it have to be created");
						ResourceDescriptor newResourceDescriptor = newResourceDescriptor(resource.getResourceDescriptor(), nameResource);

						/* create new resources */
						manager.createResource(newResourceDescriptor);
					}
				}

			} catch (ResourceException e) {
				printError(e);
			}
		} catch (Exception e) {

			printError(e);
			printError("Error showing information of resource.");

		}
		printEndCommand();
		return null;
	}

	private ResourceDescriptor newResourceDescriptor(ResourceDescriptor resourceDescriptor, String nameResource) throws ResourceException {

		try {
			ResourceDescriptor newResourceDescriptor = (ResourceDescriptor) resourceDescriptor.clone();

			// the profiles will not be cloned
			newResourceDescriptor.setProfileId("");
			// we delete chassis capability, a logical resource can't create new logical devices or new interfaces
			newResourceDescriptor.removeCapabilityDescriptor("chassis");
			// Wet set the resource name
			newResourceDescriptor.getInformation().setName(nameResource);

			/* added virtual description */
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ResourceDescriptor.VIRTUAL, "true");
			newResourceDescriptor.setProperties(properties);

			return newResourceDescriptor;
		} catch (Exception e) {
			throw new ResourceException(e.getMessage());
		}

	}

	private Information createQueueInformation() {
		Information information = new Information();
		information.setType("queue");
		return information;
	}

}
