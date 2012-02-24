package org.opennaas.router.capability.ospf.shell;

import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * @author Jordi Puig
 */
@Command(scope = "ospf", name = "activate", description = "Enable OSPF on the interface.")
public class ActivateCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {
		// TODO
		return null;
	}

}