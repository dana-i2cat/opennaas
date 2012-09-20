package org.opennaas.core.resources.profile;

public interface IProfiled {

	/**
	 * 
	 * @return true if this has a profile, false otherwise. Having <code>null</code> profile is treated as no profile.
	 */
	public boolean hasProfile();

	/**
	 * @return stored profile, or null if this has no profile.
	 */
	public IProfile getProfile();

	/**
	 * Sets stored profile.
	 * 
	 * @param profile
	 */
	public void setProfile(IProfile profile);

}
