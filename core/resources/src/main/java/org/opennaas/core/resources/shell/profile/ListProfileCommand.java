package org.opennaas.core.resources.shell.profile;

import java.util.List;


import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "profile", name = "list", description = "List all available profiles")
public class ListProfileCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("profile list");

		IProfileManager manager = getProfileManager();
		List<ProfileDescriptor> listProfiles = manager.listProfiles();
		if (listProfiles == null || listProfiles.isEmpty()) {
			printInfo("No profiles registered.");
			return null;
		}

		for (ProfileDescriptor profileDesc : listProfiles) {
			printInfo("Profile " + profileDesc.getProfileName() + " suitable for resource type : " + profileDesc
					.getResourceType());
		}
		printEndCommand();

		return null;
	}

}
