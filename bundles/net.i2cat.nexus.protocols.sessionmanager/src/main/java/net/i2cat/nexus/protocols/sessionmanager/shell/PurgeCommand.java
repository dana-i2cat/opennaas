package net.i2cat.nexus.protocols.sessionmanager.shell;

import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.command.GenericKarafCommand;
import net.i2cat.nexus.resources.protocol.IProtocolManager;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * List the device ids registered to the protocol manager
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "purge", description = "Brings up a live connection from the pool with the given protocol if there is none.")
public class PurgeCommand extends GenericKarafCommand {

	@Argument(name = "resourceType:resourceName", index = 0, required = true, description = "The resource owning the session to create.")
	String	resourceId;

	@Argument(name = "seconds", index = 1, required = false, description = "How old are the sessions to be destroyed.")
	int		seconds	= 0;

	@Override
	protected Object doExecute() throws Exception {

		IResourceManager manager = getResourceManager();

		initcommand("purge protocol");

		if (!splitResourceName(resourceId))
			return null;

		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(args[0], args[1]);

		IProtocolManager protocolManager = getProtocolManager();
		ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceIdentifier.getId());

		if (seconds > 0)
			sessionManager.purgeOldSessions(seconds * 1000);
		else
			sessionManager.purgeOldSessions();

		endcommand();
		return null;
	}

}
