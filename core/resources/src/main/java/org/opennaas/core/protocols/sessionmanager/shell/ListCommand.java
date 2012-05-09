package org.opennaas.core.protocols.sessionmanager.shell;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession.Status;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * List the device ids registere do the protocol manager
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "list", description = "List devices to which we have a live protocol session.")
public class ListCommand extends GenericKarafCommand {

	IProtocolManager	protocolManager	= null;

	@Option(name = "--verbose", aliases = { "-v" }, description = "Do not only print list devices to which we have a live protocol session, print all connections information.")
	boolean				verbose;

	@Option(name = "--protocols", aliases = { "-p" }, description = "Print supported protocols.")
	boolean				optionProtocols;

	@Argument(name = "resourceType:resourceName", index = 0, required = false, description = "If present, only connections to this device will be listed.")
	String				resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("list protocols");
		protocolManager = getProtocolManager();
		IResourceManager manager = getResourceManager();

		if (optionProtocols) {
			printInfo("Supported protocols:");
			for (String protocol : protocolManager.getAllSupportedProtocols()) {
				printInfo(protocol);
			}
			printEndCommand();
			return null;
		}

		if (resourceId != null && !resourceId.equalsIgnoreCase("")) {

			String[] argsRouterName = new String[2];
			try {
				argsRouterName = splitResourceName(resourceId);
			} catch (Exception e) {
				printError(e.getMessage());
				printEndCommand();
				return -1;
			}

			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

			ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceIdentifier.getId());

			if (protocolManager.getProtocolSessionManager(resourceIdentifier.getId()).getAllProtocolSessionIds().isEmpty()) {
				printInfo(argsRouterName[1] + " didn't have any live session. Use protocol:add command to active.");
				printEndCommand();
				return null;
			}

			for (String session : protocolManager.getProtocolSessionManager(resourceIdentifier.getId()).getAllProtocolSessionIds()) {
				ProtocolSessionContext context = sessionManager.getSessionById(session, sessionManager.isLocked(session))
						.getSessionContext();
				printInfo(doubleTab + "Protocol: " + context.getSessionParameters().get(context.PROTOCOL));
				if (verbose) {
					String age;
					long millis;

					if (sessionManager.isLocked(session)) {
						age = "(locked)";
					} else {
						millis = System.currentTimeMillis() - sessionManager.getSessionlastUsed(session);
						age = MILLISECONDS.toMinutes(millis) + ":" + (MILLISECONDS.toSeconds(millis) - MINUTES.toSeconds(MILLISECONDS
								.toMinutes(millis)));
					}
					Status sessionStatus = sessionManager.getSessionById(session, sessionManager.isLocked(session)).getStatus();
					printInfo(doubleTab + " Session ID: " + session + " STATUS: " + sessionStatus + " (Not used in: " + age + ")");
				}
			}
			printEndCommand();
			return null;
		}

		for (String device : protocolManager.getAllResourceIds()) {

			ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(device);

			if (!sessionManager.getAllProtocolSessionIds().isEmpty()) {
				String name = manager.getNameFromResourceID(device);
				printInfo(name);
				sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(device);
				for (String session : sessionManager.getAllProtocolSessionIds()) {

					ProtocolSessionContext context = sessionManager.getSessionById(session, sessionManager.isLocked(session))
							.getSessionContext();
					printInfo(doubleTab + "Protocol: " + context.getSessionParameters().get(context.PROTOCOL));

					if (verbose) {
						String age;
						long millis;

						if (sessionManager.isLocked(session)) {
							age = "(locked)";
						} else {
							millis = System.currentTimeMillis() - sessionManager.getSessionlastUsed(session);
							age = MILLISECONDS.toMinutes(millis) + ":" + (MILLISECONDS.toSeconds(millis) - MINUTES.toSeconds(MILLISECONDS
									.toMinutes(millis)));
						}
						Status sessionStatus = sessionManager.getSessionById(session, sessionManager.isLocked(session)).getStatus();
						printInfo(doubleTab + " Session ID: " + session + " STATUS: " + sessionStatus + " (Not used in: " + age + ")");
					}
				}
			}
			// FIXME if there aren't any live session need to print info message

		}
		printEndCommand();

		return null;
	}
}
