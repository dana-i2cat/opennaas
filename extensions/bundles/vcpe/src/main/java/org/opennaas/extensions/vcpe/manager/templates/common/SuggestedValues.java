package org.opennaas.extensions.vcpe.manager.templates.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * Key: physical interface name. Value: List of vlans assigned to key.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class SuggestedValues extends HashMap<String, List<Integer>> {

	private static final long	serialVersionUID	= -1501636902973858445L;

	public boolean isAlreadySuggested(String key, Integer value) {

		if (containsKey(key) &&
				get(key).contains(value))
			return true;

		return false;
	}

	public void markAsSuggested(String key, Integer value) {

		if (isAlreadySuggested(key, value))
			return;

		if (containsKey(key)) {
			get(key).add(value);
		} else {
			List<Integer> values = new ArrayList<Integer>();
			values.add(value);
			put(key, values);
		}
	}
}
