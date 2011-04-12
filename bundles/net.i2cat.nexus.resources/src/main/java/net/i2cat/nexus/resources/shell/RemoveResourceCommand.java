package net.i2cat.nexus.resources.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.RegistryUtil;

/**
 * List the Resources that are in the IaaS Container
 * 
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "nexus", name = "remove", description = "Remove one or more resources from the platform")
public class RemoveResourceCommand extends OsgiCommandSupport {

	@Argument(index = 0, name = "resource ids", description = "A space delimited list of resource ids to be deleted", required = true, multiValued = true)
	private List<String> resourceIDs;

	@Override
	protected Object doExecute() throws Exception {
		log.debug("Executing remove shell command");

		try {
			IResourceManager manager = getResourceManager();
			List<IResource> resources = manager.listResources();

			for (String id : resourceIDs) {
				// Must loop over all of the resources to find the
				// ResourceIdentifier that matches the resource ID
				for (IResource resource : resources) {
					if (resource.getResourceIdentifier().getId().equals(id)) {
						System.out.println("Removing Resource: "
								+ resource.getResourceDescriptor().getId() + ", "
								+ resource.getResourceDescriptor().getInformation().toString());
						manager.removeResource(resource.getResourceIdentifier());
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private IResourceManager getResourceManager() throws Exception {
		IResourceManager resourceManager = (IResourceManager) RegistryUtil.getServiceFromRegistry(
				getBundleContext(), IResourceManager.class.getName());

		return resourceManager;
	}
}
