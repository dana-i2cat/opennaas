package net.i2cat.nexus.protocols.sessionmanager.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.resources.RegistryUtil;

/**
 * List the device ids registere do the protocol manager
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "info", description = "Provide extended information on a protocol session.")
public class InfoCommand extends OsgiCommandSupport {

	@Argument(name = "sessionId", index = 0, required = true)
	String	sessionId;

	@Override
	protected Object doExecute() throws Exception {
		throw new java.lang.NoSuchMethodException("NOT IMPLEMENTED");
	}

	private IProtocolManager getProtocolManager() throws Exception {
		return (IProtocolManager) RegistryUtil.getServiceFromRegistry(getBundleContext(), IProtocolManager.class.getName());
	}
}
