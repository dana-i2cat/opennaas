package net.i2cat.nexus.resources.tests.profile.mock;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.profile.IProfile;
import net.i2cat.nexus.resources.profile.ProfileDescriptor;

public class MockProfile implements IProfile {

	private String					profileName	= null;
	private Map<String, IActionSet>	actionSets	= new HashMap<String, IActionSet>();
	private ProfileDescriptor		descriptor	= null;

	public MockProfile(ProfileDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
		setProfileName(descriptor.getProfileName());
	}

	@Override
	public String getProfileName() {
		return profileName;
	}

	@Override
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	@Override
	public void addActionSetForCapability(IActionSet actionSet, String idCapability) {
		actionSets.put(idCapability, actionSet);
	}

	@Override
	public IActionSet getActionSetForCapability(String idCapability) {

		return actionSets.get(idCapability);
	}

	@Override
	public void initModel(Object map) {
		// TODO Auto-generated method stub
	}

	@Override
	public Map<String, IActionSet> getActionSets() {
		return actionSets;
	}

	@Override
	public void setActionSets(Map<String, IActionSet> actionSets) {
		this.actionSets = actionSets;
	}

	@Override
	public ProfileDescriptor getProfileDescriptor() {
		return descriptor;
	}

}
