package net.i2cat.nexus.protocols.sessionmanager.shell;

import java.util.List;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.resources.RegistryUtil;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * List the protocol factories registered do the protocol manager
 * 
 * @author Eduard Grasa (i2CAT)
 * 
 */
@Command(scope = "nexus", name = "listProtocolFactories", description = "List the protocol factories registered do the protocol manager")
public class ListProtocolFactoriesCommand extends OsgiCommandSupport {

	@Override
	protected Object doExecute() throws Exception {
		log.debug("Executing list protocol factories shell command");

		try {
			IProtocolManager manager = getProtocolManager();
			List<String> protocolFactories = manager.getAllProtocolFactories();

			System.out.println("The following protocol factories have registered to the protocol manager:");
			for (String id : protocolFactories) {
				System.out.println(id);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private IProtocolManager getProtocolManager() throws Exception {
		IProtocolManager resourceManager = (IProtocolManager) RegistryUtil.getServiceFromRegistry(
				getBundleContext(), IProtocolManager.class.getName());

		return resourceManager;
	}
}
