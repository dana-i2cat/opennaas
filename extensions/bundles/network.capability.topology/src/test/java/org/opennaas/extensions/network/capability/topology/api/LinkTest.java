package org.opennaas.extensions.network.capability.topology.api;

/*
 * #%L
 * OpenNaaS :: Network :: Topology Discovery capability
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

public class LinkTest {

	private static final String	DEVICE_ID	= "DEVICE_ID";
	private static final String	PORT_A_ID	= "PORT_A_ID";
	private static final String	PORT_B_ID	= "PORT_B_ID";

	@Test
	public void testEquals() {
		Port portA = new Port();
		portA.setDeviceId(DEVICE_ID);
		portA.setId(PORT_A_ID);

		Port portB = new Port();
		portB.setDeviceId(DEVICE_ID);
		portB.setId(PORT_B_ID);

		Link link1 = new Link();
		link1.setFrom(portA);
		link1.setTo(portB);

		Link link2 = new Link();
		link2.setFrom(portB);
		link2.setTo(portA);

		Assert.assertEquals("Links must be equals!", link1, link2);
		Assert.assertEquals("Links hash codes must be equals!", link1.hashCode(), link2.hashCode());
	}
}
