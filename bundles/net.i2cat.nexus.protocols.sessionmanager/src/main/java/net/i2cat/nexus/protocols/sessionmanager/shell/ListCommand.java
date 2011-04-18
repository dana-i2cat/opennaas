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
@Command(scope = "protocols", name = "list", description = "List devices to which we have a live protocol session.")
public class ListCommand extends OsgiCommandSupport {

	@Option(name = "--verbose", aliases = { "-v" }, description = "Do not only print list devices to which we have a live protocol session, print all connections information.")
	boolean	optionDevices;

	@Argument(name = "deviceId", index = 0, required = false, description = "If present, only connections to this device will be listed.")
	String	deviceId;

	@Override
	protected Object doExecute() throws Exception {
		throw new java.lang.NoSuchMethodException("NOT IMPLEMENTED");
	}

	private IProtocolManager getProtocolManager() throws Exception {
		return (IProtocolManager) RegistryUtil.getServiceFromRegistry(getBundleContext(), IProtocolManager.class.getName());
	}
}
