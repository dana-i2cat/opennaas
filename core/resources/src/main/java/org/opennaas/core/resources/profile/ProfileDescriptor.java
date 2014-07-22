package org.opennaas.core.resources.profile;

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

import java.util.HashMap;

import org.opennaas.core.resources.action.IActionSet;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 */
public class ProfileDescriptor {

	// FIXME Must be persistable!!!

	// /**
	// * System provided profileId
	// */
	// private String profileId;

	/**
	 * User provided profile name
	 */
	private String						profileName;

	/**
	 * Indicates the resource type this profile is suitable for
	 */
	private String						resourceType;

	/**
	 * Indicates which actions are overriden by this profile. HashMap using capabilityId as Key.
	 */
	private HashMap<String, IActionSet>	overridenActions;

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

}
