package org.opennaas.core.resources.shell.completers;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
