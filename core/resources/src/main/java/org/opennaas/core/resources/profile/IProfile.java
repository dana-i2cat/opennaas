package org.opennaas.core.resources.profile;

import java.util.Map;

import org.opennaas.core.resources.action.IActionSet;

public interface IProfile {

	public String getProfileName();

	public void setProfileName(String profileName);

	public void addActionSetForCapability(IActionSet actionSet, String idCapability);

	public IActionSet getActionSetForCapability(String idCapability);

	public void initModel(Object map);

	public Map<String, IActionSet> getActionSets();

	public void setActionSets(Map<String, IActionSet> actionSets);

	public ProfileDescriptor getProfileDescriptor();

}
