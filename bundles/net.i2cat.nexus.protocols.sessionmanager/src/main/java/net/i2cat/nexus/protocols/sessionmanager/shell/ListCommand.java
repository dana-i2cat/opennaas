package net.i2cat.nexus.protocols.sessionmanager.shell;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.resources.protocol.IProtocolSession.Status;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

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

		initcommand("list protocols");
		protocolManager = getProtocolManager();
		IResourceManager manager = getResourceManager();

		if (optionProtocols) {
			printInfo("Supported protocols:");
			for (String protocol : protocolManager.getAllSessionFactories()) {
				printInfo(protocol);
			}
			endcommand();
			return null;
		}

		if (resourceId != null && !resourceId.equalsIgnoreCase("")) {

			if (!splitResourceName(resourceId))
				return null;

			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

			ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceIdentifier.getId());

			if (protocolManager.getProtocolSessionManager(resourceIdentifier.getId()).getAllProtocolSessionIds().isEmpty()) {
				printInfo(argsRouterName[1] + " didn't have any live session. Use protocol:add command to active.");
				endcommand();
				return null;
			}

			for (String session : protocolManager.getProtocolSessionManager(resourceIdentifier.getId()).getAllProtocolSessionIds()) {
				ProtocolSessionContext context = sessionManager.obtainSessionById(session, sessionManager.isLocked(session))
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
					Status sessionStatus = sessionManager.obtainSessionById(session, sessionManager.isLocked(session)).getStatus();
					printInfo(doubleTab + " Session ID: " + session + " STATUS: " + sessionStatus + " (Not used in: " + age + ")");
				}
			}
			endcommand();
			return null;
		}

		for (String device : protocolManager.getAllResourceIds()) {

			ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(device);

			if (!sessionManager.getAllProtocolSessionIds().isEmpty()) {
				String name = manager.getNameFromResourceID(device);
				printInfo(name);
				sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(device);
				for (String session : sessionManager.getAllProtocolSessionIds()) {

					ProtocolSessionContext context = sessionManager.obtainSessionById(session, sessionManager.isLocked(session))
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
						Status sessionStatus = sessionManager.obtainSessionById(session, sessionManager.isLocked(session)).getStatus();
						printInfo(doubleTab + " Session ID: " + session + " STATUS: " + sessionStatus + " (Not used in: " + age + ")");
					}
				}
			}
			// FIXME if there aren't any live session need to print info message

		}
		endcommand();

		return null;
	}
}
