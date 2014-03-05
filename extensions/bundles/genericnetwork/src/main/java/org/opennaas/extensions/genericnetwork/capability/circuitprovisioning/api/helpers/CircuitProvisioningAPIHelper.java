package org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.api.helpers;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.util.List;

import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.api.CircuitsList;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;

/**
 * Circuit Provisioning API Helper to transform API objects to capability objects and vice-versa
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class CircuitProvisioningAPIHelper {

	/**
	 * Transforms a {@link List} of {@link Circuit} to a new instance of {@link CircuitsList}
	 * 
	 * @param list
	 * @return
	 */
	public static CircuitsList listOfCircuits2CircuitList(List<Circuit> list) {
		CircuitsList circuitsList = new CircuitsList();
		circuitsList.setCircuits(list);
		return circuitsList;
	}

}
