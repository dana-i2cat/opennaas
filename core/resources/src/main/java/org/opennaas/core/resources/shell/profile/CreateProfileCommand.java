package org.opennaas.core.resources.shell.profile;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * Add a Profile to ProfileManager. <br/>
 * NOTE: Profiles are automatically added when an OSGi bundle exporting IProfile interface is registered in OSGi registry.
 * 
 * @author Evelyn Torras (i2cat)
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@Command(scope = "profile", name = "create", description = "Create a new Profile.")
public class CreateProfileCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "profile identifier ", description = "The name or identifier of the profile.", required = true, multiValued = false)
	private String			profileID;
	// FIXME is a type or an ID???? List or only one variable
	@Argument(index = 1, name = "resourceType", description = "The resource type associated to this profile.", required = true, multiValued = true)
	private List<String>	resourceType;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("profile info");

		IProfileManager manager = getProfileManager();
		IProfile profile = null;
		// FIXME how to obtain Profile??????
		// manager.addProfile(profile);

		printInfo("NOT IMPLEMENTED");
		return null;
	}

}
