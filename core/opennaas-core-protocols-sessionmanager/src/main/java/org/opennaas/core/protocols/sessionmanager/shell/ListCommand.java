package org.opennaas.core.protocols.sessionmanager.shell;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.core.resources.protocol.IProtocolManager;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;

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
			if (verbose)
				printInfo("Supported protocols:");
			for (String protocol : protocolManager.getAllSessionFactories()) {
				printInfo(protocol);
			}
			return null;
		}

		if (resourceId != null && !resourceId.equalsIgnoreCase("")) {

			if (!splitResourceName(resourceId))
				return null;

			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (verbose)
				printInfo(argsRouterName[1] + ":");
			for (String session : protocolManager.getProtocolSessionManager(resourceIdentifier.getId()).getAllProtocolSessionIds()) {
				printInfo(simpleTab + session);
			}
			return null;
		}
		if (verbose)
			printInfo("Protocol sessions:");
		for (String device : protocolManager.getAllResourceIds()) {
			// TODO how to print the names here
			String name = manager.getNameFromResourceID(device);
			printInfo(name + ":");

			ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(device);

			for (String session : sessionManager.getAllProtocolSessionIds()) {

				String age;
				long millis;

				if (sessionManager.isLocked(session)) {
					age = "(locked)";
				} else {
					millis = System.currentTimeMillis() - sessionManager.getSessionlastUsed(session);
					age = MILLISECONDS.toMinutes(millis) + ":" + (MILLISECONDS.toSeconds(millis) - MINUTES.toSeconds(MILLISECONDS
							.toMinutes(millis)));
				}

				printInfo(simpleTab + session + " (Not used in: " + age + ")");
			}
		}
		endcommand();

		return null;
	}

}
