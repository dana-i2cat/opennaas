package net.i2cat.nexus.protocols.sessionmanager.shell;

import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * List the device ids registered to the protocol manager
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "Context", description = "Manipulates protocol contexts for used to create new sessions. Call without protocol to list.")
public class ContextCommand extends GenericKarafCommand {

	@Argument(name = "resourceType:resourceName", index = 0, required = true, description = "The resource owning the context.")
	String	resourceId;

	@Argument(name = "protocol", required = false, index = 1, description = "The protocol of the context")
	String	protocol;		// user and password are inside PROTOCOL_URI

	@Argument(name = "uri", index = 2, required = false, description = "The URI passed to the protocol implementation of the context")
	String	uri;			// user and password are inside PROTOCOL_URI

	@Option(name = "--remove", aliases = { "-r" }, required = false, description = "Instead of adding a context, remove it for the named protocol. ")
	boolean	optionRemove;

	@Override
	protected Object doExecute() throws Exception {

		IResourceManager manager = getResourceManager();
		if(optionRemove){
			initcommand("Remove context");
		}
		else{
			initcommand("Adding context");
		}
		
		if (!splitResourceName(resourceId))
			return null;
		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(args[0], args[1]);

		IProtocolManager protocolManager = getProtocolManager();
		ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceIdentifier.getId());

		if (optionRemove) {
			ProtocolSessionContext context = new ProtocolSessionContext();
			context.addParameter(ProtocolSessionContext.PROTOCOL, protocol);
			sessionManager.unregisterContext(protocol);
			endcommand();
			return null;
		}

		if (protocol == null || protocol.contentEquals("")) {
			printError("You must specify a [protocol] and [uri] to register.");
			for (ProtocolSessionContext context : sessionManager.getRegisteredProtocolSessionContexts().values()) {
				printInfo("protocol = " + context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL)
							+ ", uri = " + context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI));
			}
			
			endcommand();
			return null;
		}

		if (uri == null || uri.contentEquals("")) {
			printError("You must specify a [uri] to register.");
			endcommand();
			return null;
		}

		ProtocolSessionContext context = new ProtocolSessionContext();
		context.addParameter(ProtocolSessionContext.PROTOCOL, protocol);
		context.addParameter(ProtocolSessionContext.PROTOCOL_URI, uri);
		sessionManager.registerContext(context);
		printInfo("Context registered.");
		endcommand();
		return null;
	}

}
