package net.i2cat.nexus.protocols.sessionmanager.shell;

import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.command.GenericKarafCommand;
import net.i2cat.nexus.resources.protocol.IProtocolManager;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * List the device ids registere do the protocol manager
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "add", description = "Brings up a live connection from the pool with the given protocol if there is none.")
public class AddCommand extends GenericKarafCommand {

	@Argument(name = "resourceType:resourceName", required = true, index = 0, description = "The resource owning the session to create.")
	String	resourceId;

	@Argument(name = "protocol", index = 1, required = true, description = "The protocol to use. This argument defines the factory and context used to create this session.")
	String	protocol;	// user and password are inside PROTOCOL_URI

	@Override
	protected Object doExecute() throws Exception {

		IResourceManager manager = getResourceManager();

		initcommand("adding protocol");
		if (!splitResourceName(resourceId))
			return null;

		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(args[0], args[1]);

		IProtocolManager protocolManager = getProtocolManager();
		ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceIdentifier.getId());
		if (sessionManager.obtainSessionByProtocol(protocol, false) != null) {
			printInfo("Protocol added.");
		} else {
			printError("Protocol not added");
		}
		endcommand();
		return null;
	}
}
