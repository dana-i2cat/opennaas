package net.i2cat.nexus.resources.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.RegistryUtil;

/**
 * Export the descriptor of a resource to a file
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "nexus", name = "export", description = "Export the descriptor of a resource to a file")
public class ExportResourceDescriptorCommand extends OsgiCommandSupport {

	@Argument(index = 0, name = "resourceId", description = "The resourceId whose descriptor is going to be exported", required = true, multiValued = false)
	private String resourceId = null;
	
	@Argument(index = 1, name = "fileName", description = "The path to the file (including the name) where the descriptor will be exported", required = true, multiValued = false)
	private String fileName = null;

	@Override
	protected Object doExecute() throws Exception {
		log.debug("Executing export resource descriptor shell command");

		try {
			IResourceManager manager = getResourceManager();
			List<IResource> resources = manager.listResources();

			for (IResource resource : resources) {
				if (resource.getResourceIdentifier().getId().equals(resourceId)) {
					manager.exportResourceDescriptor(resource.getResourceIdentifier(), fileName);
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
