package net.i2cat.nexus.resources.shell.profile;

import java.util.List;

import net.i2cat.nexus.resources.command.GenericKarafCommand;
import net.i2cat.nexus.resources.profile.IProfileManager;
import net.i2cat.nexus.resources.profile.ProfileDescriptor;

import org.apache.felix.gogo.commands.Command;

@Command(scope = "profile", name = "list", description = "List all available profiles")
public class ListProfileCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {

		initcommand("profile list");

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
		endcommand();

		return null;
	}

}
