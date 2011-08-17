package org.opennaas.core.resources.profile;

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
