package net.i2cat.nexus.testprofile;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.nexus.resources.action.ActionSet;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.profile.IProfile;
import net.i2cat.nexus.resources.profile.ProfileDescriptor;

public class Profile implements IProfile {

	private String					profileName				= "TestProfile";
	private String					resourceType			= "router";
	private String[]				suitableCapabilities	= new String[] { "chassis" };
	private String[]				overridenActionIds		= new String[] { "setInterface" };

	private Map<String, IActionSet>	actionSets				= new HashMap<String, IActionSet>();
	private ProfileDescriptor		descriptor				= null;

	public Profile() {
		super();
		fillProfile(overridenActionIds, suitableCapabilities, resourceType);
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

	private void fillProfile(String[] actionIds, String[] capabilityIds, String resourceType) {

		ActionSet actionSet = new ActionSet();

		actionSet.setActionSetId(getProfileName() + "ActionSet");
		for (String actionId : actionIds) {
			actionSet.putAction(actionId, DummyAction.class);
		}

		ProfileDescriptor profileDesc = new ProfileDescriptor();
		profileDesc.setProfileName(getProfileName());
		profileDesc.setResourceType(resourceType);

		this.descriptor = profileDesc;
		setProfileName(descriptor.getProfileName());

		for (String capabilityId : capabilityIds) {
			addActionSetForCapability(actionSet, capabilityId);
		}
	}

}
