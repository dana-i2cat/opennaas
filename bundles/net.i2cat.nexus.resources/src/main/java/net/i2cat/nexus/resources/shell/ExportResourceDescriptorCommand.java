package net.i2cat.nexus.resources.shell;

import java.io.File;

import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceManager;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * Export the descriptor of a resource to a file
 * 
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "resource", name = "export", description = "Export the descriptor of a resource to a file")
public class ExportResourceDescriptorCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource whose descriptor is going to be exported", required = true, multiValued = false)
	private String	resourceId	= null;

	@Argument(index = 1, name = "fileName", description = "The path to the file where the descriptor will be exported", required = true, multiValued = false)
	private String	fileName	= null;

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
			if (file.getName().endsWith(".descriptor")) {

				identifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
				if (identifier == null) {
					printError("Resource not found in Repository");
					printError("The exportation is canceled");
					return null;
				}
				try {
					manager.exportResourceDescriptor(identifier, fileName);
				} catch (ResourceException e) {
					printError("An error occurred starting the resource " + resourceId);
					printError(e);
				}
				printInfo("Exportation succesful for resource: " + resourceId);

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
