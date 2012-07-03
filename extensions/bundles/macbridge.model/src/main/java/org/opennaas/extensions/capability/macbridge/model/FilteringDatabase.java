package org.opennaas.extensions.capability.macbridge.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the filtering database of a MAC bridge as specified 
 * by standards IEEE 802.1q and IEEE 802.1d
 * @author eduardgrasa
 *
 */
public class FilteringDatabase {

	/**
	 * Contains the static VLAN registrations
	 */
	private Map<Integer, StaticVLANRegistrationEntry> staticVLANRegistations = null;
	
	public FilteringDatabase(){
		this.staticVLANRegistations = new HashMap<Integer, StaticVLANRegistrationEntry>();
	}
	
	public Map<Integer, StaticVLANRegistrationEntry> getStaticVLANRegistrations(){
		return this.staticVLANRegistations;
	}
}
