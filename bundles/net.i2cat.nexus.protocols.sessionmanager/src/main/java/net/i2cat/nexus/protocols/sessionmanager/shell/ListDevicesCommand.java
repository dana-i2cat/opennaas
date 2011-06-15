package net.i2cat.nexus.protocols.sessionmanager.shell;

import java.util.List;

import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.command.GenericKarafCommand;
import net.i2cat.nexus.resources.protocol.IProtocolManager;

import org.apache.felix.gogo.commands.Command;

/**
 * List the device ids registere do the protocol manager
 * 
 * @author Eduard Grasa (i2CAT)
 * 
 */
@Command(scope = "nexus", name = "listDevices", description = "List the device ids registered in the protocol manager")
public class ListDevicesCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {

		initcommand("list devices");
		try {
			IProtocolManager manager = getProtocolManager();
			IResourceManager resourceManager = getResourceManager();
			List<String> resourceIds = manager.getAllResourceIds();
			// TODO convert the ID into names
			// getResourceManager
			// call GEtName fromIDEntifier

			if (!resourceIds.isEmpty()) {
				printInfo("The following resources have been registered to the protocol manager:");
				for (String id : resourceIds) {
					String name = resourceManager.getNameFromResourceID(id);
					printInfo(name);
				}
			} else {
				printInfo("No resources have been registered to the protocol manager ");
			}
		} catch (Exception e) {
			printError(e);
		}
		endcommand();
		return null;
	}
}
