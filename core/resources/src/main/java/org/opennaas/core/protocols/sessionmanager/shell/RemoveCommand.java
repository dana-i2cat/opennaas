package org.opennaas.core.protocols.sessionmanager.shell;

import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * Remove a protocol session.
 *
 * @author Pau Minoves
 *
 */
@Command(scope = "protocols", name = "remove", description = "Removes a live connection from the pool, closing it.")
public class RemoveCommand extends GenericKarafCommand {

	@Option(name = "--all", aliases = { "-a" }, description = "Remove all active sessions.")
	boolean	optionAll;

	@Argument(name = "resourceType:resourceName", index = 0, required = true, description = "The resource owning the session.")
	String	resourceId;

	@Argument(name = "sessionId", index = 1, required = false, description = "The name of the session id that will be destroyed.")
	String	sessionId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("remove protocol");
		IProtocolManager protocolManager = getProtocolManager();
		IResourceManager manager = getResourceManager();
		String[] argsRouterName = new String[2];
		try {
			argsRouterName = splitResourceName(resourceId);
		} catch (Exception e) {
			printError(e.getMessage());
			printEndCommand();
			return -1;
		}

		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManager(resourceIdentifier.getId());

		if (!optionAll && (sessionId == null || sessionId.contentEquals(""))) {
			printError("Either specify a session id or --all.");
		}
		if (optionAll) {
			for (String sessionID : sessionManager.getAllProtocolSessionIds()) {
				sessionManager.destroyProtocolSession(sessionID);
			}
		} else
			sessionManager.destroyProtocolSession(sessionId);
		printEndCommand();
		return null;
	}

}
