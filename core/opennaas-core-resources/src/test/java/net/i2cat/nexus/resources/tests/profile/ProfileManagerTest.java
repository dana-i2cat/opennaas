package net.i2cat.nexus.resources.tests.profile;

import java.util.HashMap;

import net.i2cat.nexus.resources.Resource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.action.ActionSet;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.profile.IProfile;
import net.i2cat.nexus.resources.profile.IProfileManager;
import net.i2cat.nexus.resources.profile.ProfileDescriptor;
import net.i2cat.nexus.resources.profile.ProfileManager;
import net.i2cat.nexus.resources.tests.profile.mock.DummyAction;
import net.i2cat.nexus.resources.tests.profile.mock.MockProfile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

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

		ActionSet actionSet = new ActionSet();

		actionSet.setActionSetId("chassisProfileActionSet");
		actionSet.putAction(actionId, DummyAction.class);

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
