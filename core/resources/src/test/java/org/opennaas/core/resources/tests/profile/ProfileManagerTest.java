package org.opennaas.core.resources.tests.profile;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.mock.MockActionSet;
import org.opennaas.core.resources.mock.MockDummyAction;
import org.opennaas.core.resources.mock.MockProfile;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;
import org.opennaas.core.resources.profile.ProfileManager;

public class ProfileManagerTest {

	Log		logger			= LogFactory.getLog(ProfileManagerTest.class);

	String	profileName		= "mockProfile";
	String	capabilityId	= "chassis";
	String	resourceType	= "router";
	String	actionId		= "setInterface";

	@Test
	public void testProfileManagerAddRemove() {

		IProfile profile = createProfile(profileName);

		IProfileManager manager = new ProfileManager();
		Assert.assertTrue(manager.listProfiles().isEmpty());

		try {

			manager.addProfile(profile);
			boolean overriden = false;
			try {
				manager.addProfile(profile);
				overriden = true;
			} catch (ResourceException e) {
				// ignore
			}
			Assert.assertFalse(overriden);

			Assert.assertFalse(manager.listProfiles().isEmpty());
			Assert.assertTrue(manager.listProfiles().contains(profile.getProfileDescriptor()));
			Assert.assertNotNull(manager.getProfile(profileName));
			Assert.assertTrue(manager.getProfile(profileName).equals(profile));

			manager.removeProfile(profileName);
			Assert.assertTrue(manager.listProfiles().isEmpty());

		} catch (ResourceException e) {
			logger.error("Error happened!!!", e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testRegisterResourceToProfile() {

		Resource res = new Resource();

		ResourceDescriptor resDesc = new ResourceDescriptor();
		Information resDescInfo = new Information();
		resDescInfo.setType(resourceType);
		resDesc.setInformation(resDescInfo);
		res.setResourceDescriptor(resDesc);

		IProfileManager manager = new ProfileManager();

		try {

			IProfile profile = createProfile(profileName);
			manager.addProfile(profile);

			manager.registerResource(profileName, res);
			Assert.assertTrue(manager.getRegisteredResources(profileName).contains(res));

			boolean removed = false;
			try {
				manager.removeProfile(profileName);
				removed = true;
			} catch (ResourceException e) {
				// ignored
			}
			Assert.assertFalse(removed);

			manager.unregisterResource(profileName, res);
			Assert.assertTrue(manager.getRegisteredResources(profileName).isEmpty());

			manager.removeProfile(profileName);
			Assert.assertTrue(manager.listProfiles().isEmpty());

			boolean errorThrown = false;
			try {
				manager.getRegisteredResources(profileName);
			} catch (ResourceException e) {
				errorThrown = true;
			}
			Assert.assertTrue(errorThrown);

		} catch (ResourceException e) {
			logger.error("Error happened!!!", e);
			Assert.fail(e.getMessage());
		}
	}

	private IProfile createProfile(String profileName) {

		ActionSet actionSet = new MockActionSet();

		actionSet.setActionSetId("chassisProfileActionSet");
		actionSet.putAction(actionId, MockDummyAction.class);

		HashMap<String, IActionSet> overridenActions = new HashMap<String, IActionSet>();
		overridenActions.put(capabilityId, actionSet);

		ProfileDescriptor profileDesc = new ProfileDescriptor();
		profileDesc.setProfileName(profileName);
		profileDesc.setResourceType(resourceType);

		IProfile profile = new MockProfile(profileDesc);
		profile.addActionSetForCapability(actionSet, capabilityId);

		return profile;

	}

}
