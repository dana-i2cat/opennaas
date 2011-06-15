package net.i2cat.nexus.protocols.sessionmanager.shell;

import java.util.List;

import net.i2cat.nexus.resources.command.GenericKarafCommand;
import net.i2cat.nexus.resources.protocol.IProtocolManager;

import org.apache.felix.gogo.commands.Command;

/**
 * List the protocol factories registered do the protocol manager
 * 
 * @author Eduard Grasa (i2CAT)
 * 
 */
@Command(scope = "nexus", name = "listProtocolFactories", description = "List the protocol factories registered in the protocol manager")
public class ListProtocolFactoriesCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {

		initcommand("list protocol factories ");
		try {
			IProtocolManager manager = getProtocolManager();
			List<String> protocolFactories = manager.getAllSessionFactories();
			if (!protocolFactories.isEmpty()) {
				printInfo("The following protocol factories have registered to the protocol manager:");
				for (String id : protocolFactories) {
					printInfo(id);
				}
			} else {
				printInfo("No protocol factories have been registered to the protocol manager");
			}
		} catch (Exception e) {
			printError(e);

		}
		endcommand();
		return null;
	}
}
