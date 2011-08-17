package org.opennaas.core.resources.profile;

import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;


/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 */
public interface IProfileManager {

	/**
	 * Adds a profile to this manager. Only added profiles can be used by resources
	 * 
	 * @throws ResourceException
	 */
	public void addProfile(IProfile profile) throws ResourceException;

	/**
	 * Removes a profile from this manager. Thus, preventing resources to use it in the future.
	 * 
	 * @param profileName
	 * @throws ResourceException
	 */
	public void removeProfile(String profileName) throws ResourceException;

	/**
	 * @return a list with the descriptor of all added profiles
	 */
	public List<ProfileDescriptor> listProfiles();

	/**
	 * 
	 * @param profileId
	 * @return
	 */
	public IProfile getProfile(String profileId) throws ResourceException;

	// RESOURCE REGISTRATION //

	/**
	 * Registers the use of given profileId at given resource.<br/>
	 * DOES NOT MODIFY RESOURCE
	 * 
	 * @param profileId
	 * @param resource
	 * @throws ResourceException
	 *             if there is no profile with given profileId
	 */
	public void registerResource(String profileId, IResource resource) throws ResourceException;

	/**
	 * Unregisters the use of given profileId at given resource.<br/>
	 * DOES NOT MODIFY RESOURCE
	 * 
	 * @param profileId
	 * @param resource
	 * @throws ResourceException
	 *             if there is no profile with given profileId
	 */
	public void unregisterResource(String profileId, IResource resource) throws ResourceException;

	/**
	 * 
	 * @param profileId
	 * @return a list of resources using Profile with given profileId
	 * @throws ResourceException
	 *             if there is no profile with given profileId
	 */
	public List<IResource> getRegisteredResources(String profileId) throws ResourceException;

}
