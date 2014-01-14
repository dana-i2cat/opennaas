package org.opennaas.extensions.vcpe.manager.templates.common;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
