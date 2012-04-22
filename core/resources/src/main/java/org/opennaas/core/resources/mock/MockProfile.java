package org.opennaas.core.resources.mock;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.ProfileDescriptor;


public class MockProfile implements IProfile {

	private String					profileName	= null;
	private Map<String, IActionSet>	actionSets	= new HashMap<String, IActionSet>();
	private ProfileDescriptor		descriptor	= null;

	public MockProfile(ProfileDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
		setProfileName(descriptor.getProfileName());
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public void addActionSetForCapability(IActionSet actionSet, String idCapability) {
		actionSets.put(idCapability, actionSet);
	}

	public IActionSet getActionSetForCapability(String idCapability) {

		return actionSets.get(idCapability);
	}

	public void initModel(Object map) {
		// TODO Auto-generated method stub
	}

	public Map<String, IActionSet> getActionSets() {
		return actionSets;
	}

	public void setActionSets(Map<String, IActionSet> actionSets) {
		this.actionSets = actionSets;
	}

	public ProfileDescriptor getProfileDescriptor() {
		return descriptor;
	}

}
