package org.opennaas.extensions.genericnetwork.test.model.helper;

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

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.helpers.GenericNetworkModelHelper;
import org.opennaas.extensions.genericnetwork.model.helpers.GenericNetworkModelUtils;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class GenericModelHelperTest {

	@Test
	public void isPortInCircuitTest() {

		Circuit circuit = GenericNetworkModelUtils.generateSampleCircuit();
		String portId = GenericNetworkModelUtils.PORT_ID_1;

		boolean isPortInCircuit = GenericNetworkModelHelper.isPortInCircuit(portId, circuit);

		Assert.assertTrue(isPortInCircuit);
	}

	@Test
	public void portIsNotInCircuitTest() {

		Circuit circuit = GenericNetworkModelUtils.generateSampleCircuit();
		String portId = "port";

		boolean isPortInCircuit = GenericNetworkModelHelper.isPortInCircuit(portId, circuit);

		Assert.assertFalse(isPortInCircuit);
	}

}
