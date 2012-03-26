package org.opennaas.core.resources.shell.completers;

import java.util.List;


import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;

public class ProfileNameCompleter implements Completer {

	public int complete(String arg0, int arg1, List<String> arg2) {
		IProfileManager profileManager = null;
		List<ProfileDescriptor> list = null;
		StringsCompleter delegate = new StringsCompleter();

		try {
			profileManager = Activator.getProfileManagerService();
			list = profileManager.listProfiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (ProfileDescriptor profile : list) {
			String value = profile.getProfileName();
			delegate.getStrings().add(value);
		}

		return delegate.complete(arg0, arg1, arg2);
	}
}
