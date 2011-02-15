package net.i2cat.nexus.resources.shell;

import java.util.List;

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
@Command(scope = "nexus", name = "list", description = "List the resources in the platform")
public class ListResourcesCommand extends OsgiCommandSupport {

	@Override
	protected Object doExecute() throws Exception {
		log.debug("Executing list shell command");

		try {
			IResourceManager manager = getResourceManager();
			List<IResource> resources = manager.listResources();

			System.out.println("Found " + resources.size() + " resources");
			for (IResource resource : resources) {
				System.out.println("Resource: " + resource.getResourceDescriptor().getId() + ", " + resource.getResourceDescriptor().getInformation().toString());
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
