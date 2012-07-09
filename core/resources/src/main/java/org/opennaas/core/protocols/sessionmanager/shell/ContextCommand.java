package org.opennaas.core.protocols.sessionmanager.shell;

import net.i2cat.netconf.SessionContext;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.shell.GenericKarafCommand;

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

	@Argument(name = "authType", required = true, index = 2, description = "Type of authentication to use.")
	String	authType;

	@Argument(name = "uri", index = 3, required = false, description = "The URI passed to the protocol implementation of the context")
	String	uri;			// user and password are inside PROTOCOL_URI

	@Argument(name = "keyPath", index = 4, required = false, description = "The path where the private key is stored.")
	String	keyPath;

	@Argument(name = "keyPassword", index = 5, required = false, description = "Passphrase to unlock the private key.")
	String	keyPassword;

	@Option(name = "--remove", aliases = { "-r" }, required = false, description = "Instead of adding a context, remove it for the named protocol. ")
	boolean	optionRemove;

	@Override
	protected Object doExecute() throws Exception {

		IResourceManager manager = getResourceManager();
		if (optionRemove) {
			printInitCommand("Remove context");
		} else {
			printInitCommand("Adding context");
		}

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

		if (optionRemove) {
			ProtocolSessionContext context = new ProtocolSessionContext();
			context.addParameter(ProtocolSessionContext.PROTOCOL, protocol);
			sessionManager.unregisterContext(protocol);
			printEndCommand();
			return null;
		}

		if (protocol == null || protocol.contentEquals("")) {
			printError("You must specify a [protocol] and [uri] to register.");
			for (ProtocolSessionContext context : sessionManager.getRegisteredProtocolSessionContexts().values()) {
				printInfo("protocol = " + context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL)
						+ ", uri = " + context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI));
			}

			printEndCommand();
			return null;
		}

		SessionContext.AuthType authentication = SessionContext.AuthType.getByValue(authType);

		if (authentication.equals(SessionContext.AuthType.PUBLICKEY)) {

			if ((keyPath == null) || (keyPath.contentEquals("")) || (keyPassword == null) || keyPassword.contentEquals("")) {
				printError("You must specify a [file path] and [password] if you want to use key authentication");
				printEndCommand();
				return null;
			}

		}

		if (authentication.equals(SessionContext.AuthType.PASSWORD)) {

			if (uri == null || uri.contentEquals("")) {
				printError("You must specify a [uri] to register.");
				printEndCommand();
				return null;
			}

		}

		if (!authentication.equals(SessionContext.AuthType.PUBLICKEY) && !authentication.equals(SessionContext.AuthType.PASSWORD)) {

			printError("You must specify a valid authentication type. Possible options are: \"password\", \"publickey\".");
			printEndCommand();
			return null;
		}

		ProtocolSessionContext context = new ProtocolSessionContext();

		context.addParameter(ProtocolSessionContext.PROTOCOL, protocol);
		context.addParameter(ProtocolSessionContext.AUTH_TYPE, authType);
		context.addParameter(ProtocolSessionContext.PROTOCOL_URI, uri);
		context.addParameter(ProtocolSessionContext.KEY_USERNAME, uri.split("@")[0]);
		context.addParameter(ProtocolSessionContext.KEY_PATH, keyPath);
		context.addParameter(ProtocolSessionContext.KEY_PASSWORD, keyPassword);

		sessionManager.registerContext(context);
		printInfo("Context registered for resource " + resourceId);
		printEndCommand();
		return null;
	}
}
