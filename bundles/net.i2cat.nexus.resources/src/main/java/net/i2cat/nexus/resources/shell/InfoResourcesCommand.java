package net.i2cat.nexus.resources.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.RegistryUtil;

/**
 * Start one or more resources
 * 
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "resource", name = "info", description = "Provides extended information about one or more resources.")
public class InfoResourcesCommand extends OsgiCommandSupport {

	@Argument(index = 0, name = "resource ids", description = "A space delimited list of resource ids.", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {
		log.debug("Executing resource start shell command");

		try {
			IResourceManager manager = getResourceManager();
			List<IResource> resources = manager.listResources();

			for (String id : resourceIDs) {
				throw new java.lang.NoSuchMethodException("NOT IMPLEMENTED");
			}
		} catch (Exception e) {
			throw e;
		}

		return null;
	}

	private IResourceManager getResourceManager() throws Exception {
		IResourceManager resourceManager = (IResourceManager) RegistryUtil.getServiceFromRegistry(
				getBundleContext(), IResourceManager.class.getName());

		return resourceManager;
	}
}
