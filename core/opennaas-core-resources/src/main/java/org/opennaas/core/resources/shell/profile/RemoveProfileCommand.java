package org.opennaas.core.resources.shell.profile;


import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * Remove Profile from one or more resources
 *
 * @author Evelyn Torras (i2cat)
 * @author Isart Canyameres Gimenez (i2cat)
 */
@Command(scope = "profile", name = "remove", description = "Remove a profile from available ones.")
public class RemoveProfileCommand extends GenericKarafCommand {

	@Argument(name = "profileName", index = 0, required = true, description = "The profile Id to be deleted.")
	String	profileName	= null;

	// @Option(name = "--resources", aliases = { "-r" }, description = "The list of resources to delete the profile")
	// boolean optionResources;
	// @Argument(index = 1, name = "resource ids", description = "A space delimited list of resource ids.", required = false, multiValued = true)
	// private List<String> resourceIDs;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("remove profile");

		IProfileManager manager = getProfileManager();
		try {
			manager.removeProfile(profileName);
			printInfo("The profile " + profileName + " has been deleted succesfully");
		} catch (ResourceException e) {
			printError("Error removing profile ");
			printError(e);
			throw e;
		}
		printEndCommand();
		return null;
	}
}
