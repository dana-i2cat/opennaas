package org.opennaas.core.resources.shell;

import java.io.File;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceManager;

/**
 * Export the descriptor of a resource to a file
 * 
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "resource", name = "export", description = "Export the descriptor of a resource to a file")
public class ExportResourceDescriptorCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource whose descriptor is going to be exported", required = true, multiValued = false)
	private String	resourceId		= null;

	@Argument(index = 1, name = "fileName", description = "The path to the file where the descriptor will be exported", required = true, multiValued = false)
	private String	fileName		= null;

	@Option(name = "--networkTopology", aliases = { "-nt" }, description = "Allows explicit file where the network topology will be saved")
	private String	networkFileName	= null;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("export resource descriptor");

		try {
			ResourceManager manager = (ResourceManager) getResourceManager();

			String[] argsRouterName = new String[2];
			try {
				argsRouterName = splitResourceName(resourceId);
			} catch (Exception e) {
				printError(e.getMessage());
				printEndCommand();
				return -1;
			}

			IResourceIdentifier identifier = null;

			// check the file Name ends with .descriptor
			File file = new File(fileName);
			if (file.getName().endsWith(".descriptor") || file.getName().endsWith(".xml")) {

				identifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
				if (identifier == null) {
					printError("Resource not found in Repository");
					printError("Export cancelled.");
					return null;
				}
				try {

					if (networkFileName != null) {
						File networkFile = new File(networkFileName);
						if (networkFile.getName().endsWith(".descriptor") || networkFile.getName().endsWith(".xml")) {
							manager.exportNetworkTopology(identifier, networkFileName);
							printInfo("Export network descriptor succesful for resource: " + resourceId);

						}

					}

					manager.exportResourceDescriptor(identifier, fileName);
					printInfo("Export descriptor succesful for resource: " + resourceId);
				} catch (ResourceException e) {
					printError("An error occurred exporting descriptor of resource " + resourceId);
					printError(e);
				}

			} else {
				printError("The type of the file must be .descriptor ");
			}
		} catch (Exception e) {
			printError(e);
			printError("Error exporting Resource.");
		}
		printEndCommand();
		return null;
	}

}
