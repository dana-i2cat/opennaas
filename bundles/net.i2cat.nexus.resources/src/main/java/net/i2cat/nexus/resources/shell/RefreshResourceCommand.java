package net.i2cat.nexus.resources.shell;

import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceNotFoundException;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.command.Response.Status;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.queue.QueueResponse;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "resource", name = "refresh", description = "Update the data model of a given resource")
public class RefreshResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type and name.", required = true, multiValued = false)
	private String	resourceIDs;

	@Override
	protected Object doExecute() {
		initcommand("create resource");

		IResourceManager manager;
		try {
			manager = getResourceManager();

			if (!splitResourceName(resourceIDs))
				return null;

			IResource resource = null;
			IResourceIdentifier identifier;
			try {
				identifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

				if (identifier == null) {
					printError("Error in identifier.");
					endcommand();
					return null;
				}

				resource = manager.getResource(identifier);
				validateResource(resource);

				// call the method to refresh each capability on resource

				resource.setModel(new ComputerSystem());
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
		endcommand();
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
