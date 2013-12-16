package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.wrappers;

import java.util.HashMap;
import java.util.Map;

/**
 * Counters Map wrapper
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class CountersMap {
	private Map<String, Long>	countersMap;

	public CountersMap() {
		this.countersMap = new HashMap<String, Long>();
	}

	public Map<String, Long> getCountersMap() {
		return countersMap;
	}

	public void addCounter(String counter, long value) {
		countersMap.put(counter, value);
	}

}
