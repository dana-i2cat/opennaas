package org.opennaas.core.protocols.sessionmanager.shell;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
	String						resourceId;

	@Argument(name = "protocol", required = false, index = 1, description = "The protocol of the context")
	String						protocol;					// user and password are inside PROTOCOL_URI

	@Argument(name = "authType", required = true, index = 2, description = "Type of authType to use.")
	String						authType;

	@Argument(name = "uri", index = 3, required = false, description = "The URI passed to the protocol implementation of the context")
	String						uri;						// user and password are inside PROTOCOL_URI

	@Argument(name = "privateKeyPath", index = 4, required = false, description = "The path where the private key is stored.")
	String						keyPath;

	@Argument(name = "keyPassphrase", index = 5, required = false, description = "Passphrase to unlock the private key.")
	String						keyPassphrase;

	@Option(name = "--remove", aliases = { "-r" }, required = false, description = "Instead of adding a context, remove it for the named protocol. ")
	boolean						optionRemove;

	@Option(name = "--interactive", aliases = { "-i" }, required = false, description = "Tells command to ask for passwords interactively")
	boolean						interactive;

	private static final String	PASSWORD	= "password";
	private static final String	PUBLICKEY	= "publickey";
	private static final String	NOAUTH		= "noauth";

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

		if (uri == null || uri.contentEquals("")) {
			printError("You must specify a [uri] to register.");
			printEndCommand();
			return null;
		}

		ProtocolSessionContext context = new ProtocolSessionContext();

		context.addParameter(ProtocolSessionContext.PROTOCOL, protocol);
		context.addParameter(ProtocolSessionContext.PROTOCOL_URI, uri);

		try {
			context = checkAndSetAuthentication(context);
		} catch (Exception e) {
			printError(e.getLocalizedMessage());
			printEndCommand();
			return null;
		}

		sessionManager.registerContext(context);
		printInfo("Context registered for resource " + resourceId);
		printEndCommand();
		return null;

	}

	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private ProtocolSessionContext checkAndSetAuthentication(ProtocolSessionContext context) throws Exception {

		if (!authType.equals(PUBLICKEY) && !authType.equals(PASSWORD) && !authType.equals(NOAUTH)) {
			throw new Exception("You must specify a valid authType type. Possible options are: \"password\", \"publickey\", \"noauth\".");
		}

		if (authType.equals(PASSWORD)) {
			String userName = getUsername();
			String password = getPassword();

			if (userName == null || password == null) {
				throw new Exception("You must specify a userName and password for password authentication.");
			}

			// UPDATE CONTEXT
			context.addParameter(ProtocolSessionContext.AUTH_TYPE, PASSWORD);
			context.addParameter(ProtocolSessionContext.USERNAME, userName);
			context.addParameter(ProtocolSessionContext.PASSWORD, password);

		} else if (authType.equals(PUBLICKEY)) {
			String userName = getKeyUsername();
			String keyPassphrase = getKeyPassphrase();

			if (userName == null) {
				throw new Exception("You must specify a userName for key authentication.");
			}

			if ((keyPath == null) || (keyPath.contentEquals(""))) {
				throw new Exception("You must specify a private key file path for key authentication.");
			}

			// UPDATE CONTEXT
			context.addParameter(ProtocolSessionContext.AUTH_TYPE, PUBLICKEY);
			context.addParameter(ProtocolSessionContext.KEY_USERNAME, userName);
			context.addParameter(ProtocolSessionContext.KEY_PATH, keyPath);
			context.addParameter(ProtocolSessionContext.KEY_PASSPHRASE, keyPassphrase);

		} else {
			context.addParameter(ProtocolSessionContext.AUTH_TYPE, authType);
		}

		return context;
	}

	private String getUsername() throws URISyntaxException {
		return getUserNameFromUri();
	}

	private String getPassword() throws IOException, URISyntaxException {
		String password;

		if (interactive) {
			String askPasswordMsg = "password:";
			password = askPasswordInteractively(askPasswordMsg);
		} else {
			password = getPasswordFromURI();
		}

		return password;
	}

	private String getKeyUsername() throws URISyntaxException {
		return getUserNameFromUri();
	}

	private String getKeyPassphrase() throws IOException {
		String passphrase;
		if (interactive) {
			String usrMsg = "Private key passphare:";
			passphrase = askPasswordInteractively(usrMsg);
		} else {
			passphrase = keyPassphrase;
		}
		return passphrase;
	}

	private String getUserNameFromUri() throws URISyntaxException {
		URI uRI = new URI(uri);
		String userInfo = uRI.getUserInfo();
		if (userInfo == null)
			return null;

		String userName = userInfo.split(":")[0];
		return userName;
	}

	private String getPasswordFromURI() throws URISyntaxException {
		URI uRI = new URI(uri);
		String userInfo = uRI.getUserInfo();
		if (userInfo == null)
			return null;

		if (userInfo.split(":").length > 1) {
			String password = userInfo.split(":")[1];
			return password;
		} else {
			return null;
		}
	}
}