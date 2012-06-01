package org.opennaas.bundle.empty.skeleton.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "example", name = "sayHello", description = "It will say hello.")
public class ExampleCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "userName", description = "The name of the person we will greet.", required = true, multiValued = false)
	private String	username;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("sayHello");
		printInfo("Hello " + username + "!");
		printEndCommand();
		return null;
	}
}
