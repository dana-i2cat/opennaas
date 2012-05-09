package org.opennaas.core.protocols.sessionmanager.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * List the device ids registered to the protocol manager
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "purge", description = "Destroys unused sessions from the pool")
public class PurgeCommand extends GenericKarafCommand {

	@Argument(name = "resourceType:resourceName", index = 0, required = true, description = "The resource owning sessions to destroy.")
	String	resourceId;

	@Argument(name = "seconds", index = 1, required = false, description = "Seconds of inactivity required for a session to be destroyed.")
	int		seconds	= 0;

	@Override
	protected Object doExecute() throws Exception {

		IResourceManager manager = getResourceManager();

		printInitCommand("purge protocol");

		String[] argsRouterName = new String[2];
		try {
			argsRouterName = splitResourceName(resourceId);
		} catch (Exception e) {
			printError(e.getMessage());
			printEndCommand();
			return -1;
		}

		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

		IProtocolManager protocolManager = getProtocolManager();
		ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceIdentifier.getId());

		if (seconds > 0)
			sessionManager.purgeOldSessions(seconds * 1000);
		else
			sessionManager.purgeOldSessions();

		printEndCommand();
		return null;
	}

}
