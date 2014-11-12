package org.opennaas.extensions.openflowswitch.helpers;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
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

import org.opennaas.extensions.openflowswitch.model.OFFlow;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ForwardingRuleUtils {

	/**
	 * Checks if both {@link OFFlow} have same match.
	 * 
	 * @param firstFlow
	 *            First element of the comparation.
	 * @param secondFlow
	 *            Second element of the comparation.
	 * @return <code>true</code> if both {@link OFFlow} contain same values for all attributes except the name.<code>false</code> otherwise.
	 */
	public static boolean rulesWithSameMatch(OFFlow firstFlow, OFFlow secondFlow) {
		if (firstFlow == secondFlow)
			return true;
		if (secondFlow == null || firstFlow == null)
			return false;
		if (firstFlow.getMatch() == null) {
			if (secondFlow.getMatch() != null)
				return false;
		} else if (!firstFlow.getMatch().equals(secondFlow.getMatch()))
			return false;

		return true;
	}

}
