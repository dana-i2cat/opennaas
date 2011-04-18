package net.i2cat.nexus.protocols.sessionmanager.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.resources.RegistryUtil;

/**
 * List the device ids registere do the protocol manager
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "remove", description = "Removes a live connection from the pool, closing it.")
public class RemoveCommand extends OsgiCommandSupport {

	@Option(name = "--all", aliases = { "-a" }, description = "Remove all active connections.")
	boolean	optionAll;

	@Argument(name = "sessionId", index = 0, description = "The name of the session id that will be destroyed.")
	String	sessionId;

	@Override
	protected Object doExecute() throws Exception {
		throw new java.lang.NoSuchMethodException("NOT IMPLEMENTED");
	}

	private IProtocolManager getProtocolManager() throws Exception {
		return (IProtocolManager) RegistryUtil.getServiceFromRegistry(getBundleContext(), IProtocolManager.class.getName());
	}
}
