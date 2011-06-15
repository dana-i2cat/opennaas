package net.i2cat.nexus.resources.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.nexus.resources.Activator;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 */
public class ProfileManager implements IProfileManager {

	Logger									logger				= LoggerFactory.getLogger(ProfileManager.class);

	/**
	 * HashMap that contains for each ProfileId its associated Resources
	 */
	private Map<String, List<IResource>>	registeredResources	= new HashMap<String, List<IResource>>();

	/**
	 * HashMap that contains Profiles and their Ids (as a Key)
	 */
	private Map<String, IProfile>			profiles			= new HashMap<String, IProfile>();

	/**
	 * HashMap that contains for each ProfileId its ProfileDescritor
	 */
	private Map<String, ProfileDescriptor>	profileDescriptors	= new HashMap<String, ProfileDescriptor>();

	@Override
	public void addProfile(IProfile profile, String ignored1, String ignored2) throws ResourceException {
		addProfile(profile);
	}

	@Override
	public void addProfile(IProfile profile) throws ResourceException {
		String profileName = profile.getProfileDescriptor().getProfileName();
		if (!profiles.containsKey(profileName)) {
			profiles.put(profileName, profile);
			profileDescriptors.put(profileName, profile.getProfileDescriptor());
		} else {
			throw new ResourceException("Could not add given profile. A profile with given name already exists!");
		}
	}

	@Override
	public void removeProfile(String profileName) throws ResourceException {

		if (registeredResources.containsKey(profileName)) {
			if (!registeredResources.get(profileName).isEmpty()) {
				throw new ResourceException("Can not remove a profile in use. Remove resources using it first.");
			} else {
				registeredResources.remove(profileName);
			}
		}

		profileDescriptors.remove(profileName);
		profiles.remove(profileName);

	}

	@Override
	public IProfile getProfile(String profileId) throws ResourceException {

		if (!profiles.containsKey(profileId))
			throw new ResourceException("There is no profile with given profileId: " + profileId);

		return profiles.get(profileId);
	}

	@Override
	public List<ProfileDescriptor> listProfiles() {
		List<ProfileDescriptor> descriptors = new ArrayList<ProfileDescriptor>();
		descriptors.addAll(profileDescriptors.values());
		return descriptors;
	}

	@Override
	public void registerResource(String profileId, IResource resource) throws ResourceException {

		if (!profiles.containsKey(profileId))
			throw new ResourceException("There is no profile with given profileId: " + profileId);

		if (!profileDescriptors.get(profileId).getResourceType().equals(
				resource.getResourceDescriptor().getInformation().getType())) {
			throw new ResourceException(" Given profile is not suitable for given resource. Types do not match:" +
					" profileType=" + profileDescriptors.get(profileId).getResourceType() +
					" resourceType=" + resource.getResourceDescriptor().getInformation().getType());
		}

		List<IResource> resources = registeredResources.get(profileId);
		if (resources != null) {
			resources.add(resource);
		} else {
			resources = new ArrayList<IResource>();
			resources.add(resource);
			registeredResources.put(profileId, resources);
		}
	}

	@Override
	public void unregisterResource(String profileId, IResource resource) throws ResourceException {

		if (!profiles.containsKey(profileId))
			throw new ResourceException("There is no profile with given profileId: " + profileId);

		List<IResource> resources = registeredResources.get(profileId);
		if (resources != null) {
			resources.remove(resource);
		}
	}

	@Override
	public List<IResource> getRegisteredResources(String profileId) throws ResourceException {

		if (!profiles.containsKey(profileId))
			throw new ResourceException("There is no profile with given profileId: " + profileId);

		if (registeredResources.get(profileId) != null)
			return registeredResources.get(profileId);
		else
			return new ArrayList<IResource>();
	}

	@Override
	public IResource getResourceFromProfile(String profileId) throws ResourceException {
		IResource resource = null;
		try {
			resource = registeredResources.get(profileId).get(0);
		} catch (NullPointerException e) {
			throw new ResourceException("This profile name: " + profileId + " not have a resource asssociated.", e);
		} catch (ClassCastException e) {
			throw new ResourceException("This profile name: " + profileId + " is not found as a profile.", e);
		}
		return resource;
	}

	/**
	 * Called by blueprint every time a resource repository is registered
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void profileAdded(IProfile serviceInstance, Map serviceProperties) {
		if (serviceInstance != null) {
			logger.debug("New profile added for resources of type: " + serviceInstance.getProfileDescriptor().getResourceType());
			try {
				// Add it to the manager
				addProfile(serviceInstance);
			} catch (ResourceException e) {
				// FIXME How to tell blueprint it has failed??
				logger.error("Could not add Profile: ", e);
			}
		}
	}

	/**
	 * Called by blueprint every time a resource repository is unregistered
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void profileRemoved(IProfile serviceInstance, Map serviceProperties) {
		if (serviceInstance != null) {
			logger.debug("Existing profile removed :" + serviceInstance.toString() + " " + serviceProperties.get("type"));
			// Remove it from the map
			try {
				removeProfile(serviceInstance.getProfileName());
			} catch (ResourceException e) {
				// Bundle has already been removed!!!! So loading actions will fail!!!!
				logger.error("A profile in use has been removed! Profile: " + serviceInstance.getProfileName());
				logger.error("Removing resources using removed profile...");
				// Should stop resources using removed profile
				stopAssociatedResources(serviceInstance);
				try {
					removeProfile(serviceInstance.getProfileName());
				} catch (ResourceException e1) {
					// ignored, could not be thrown
				}
			}
		}
	}

	private void removeResources(IProfile profile) {

		List<IResource> resourcesToRemove;

		try {
			resourcesToRemove = getRegisteredResources(profile.getProfileDescriptor().getProfileName());
		} catch (ResourceException e) {
			// profile is no longer stored in this manager
			return;
		}

		try {
			if (!resourcesToRemove.isEmpty()) {

				IResourceManager resourceManager = Activator.getResourceManagerService();

				for (int i = resourcesToRemove.size() - 1; i >= 0; i--) {
					resourceManager.removeResource(resourcesToRemove.get(i).getResourceIdentifier());
				}
			}
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stopAssociatedResources(IProfile profile) {

		List<IResource> resourcesToStop;

		try {
			resourcesToStop = getRegisteredResources(profile.getProfileDescriptor().getProfileName());
		} catch (ResourceException e) {
			// profile is no longer stored in this manager
			return;
		}

		try {
			if (!resourcesToStop.isEmpty()) {

				IResourceManager resourceManager = Activator.getResourceManagerService();

				for (int i = resourcesToStop.size() - 1; i >= 0; i--) {
					resourceManager.stopResource(resourcesToStop.get(i).getResourceIdentifier());
				}
			}
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
