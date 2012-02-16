package org.opennaas.router.capability.ospf.shell;

import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * @author Jordi Puig
 */
@Command(scope = "ospf", name = "show", description = "Returns a list of all interfaces where the OSPF is configured and enabled.")
public class ShowCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {
		// TODO
		return null;
	}

}