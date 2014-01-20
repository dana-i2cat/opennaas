package org.opennaas.extensions.transport.virtual.tests;

/*
 * #%L
 * OpenNaaS :: Transport :: Virtual
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

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.extensions.transports.virtual.VirtualTransport;

public class VirtualTransportTest {

	private static VirtualTransport	virtualTransport	= null;

	@BeforeClass
	public static void setUp() {
		try {
			virtualTransport = new VirtualTransport(new MockVirtualTransportProvider());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void testStreamTransport() {
		String request = "ACT-USER::admin:::opterasm";
		try {
			virtualTransport.send(request.toCharArray());
			Assert.assertEquals(18, virtualTransport.getInputStream().available());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(null);
		}
	}

	@Test
	public void testMessageTransport() {
		String request = "ACT-USER::admin:::opterasm";
		try {
			Object response = virtualTransport.sendMessage(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(null);
		}
	}

}
