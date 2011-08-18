package net.i2cat.nexus.protocols.sessionmanager.shell;

import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;

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
		initcommand("remove protocol");
		IProtocolManager protocolManager = getProtocolManager();
		IResourceManager manager = getResourceManager();
		if (!splitResourceName(resourceId))
			return null;

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
		endcommand();
		return null;
	}

}
