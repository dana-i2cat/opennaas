package org.opennaas.router.capability.ospf.shell;

import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * @author Jordi Puig
 */
@Command(scope = "ospf", name = "configure", description = "Configure a network interface in order to activate OSPF on it.")
public class ConfigureCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {
		// TODO
		return null;
	}

}