package org.opennaas.extensions.network.capability.basic.tests;

/*
 * #%L
 * OpenNaaS :: Network :: Basic capability
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

import junit.framework.TestCase;

import org.junit.Test;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;

public class TopologyManagerImplTest extends TestCase {

	INetworkBasicCapability	topologyManager;

	IResource				network;
	IResource				router1;
	IResource				router2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		network = createNetworkWithModel();
		topologyManager = createNetworkBasicCapability(network);
		router1 = createRouterWithModel("router1");
		router2 = createRouterWithModel("router2");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testAddResource() {
		// TODO
	}

	//
	// @Test
	// public void addResourceTwiceTest() {
	//
	// }
	//
	// @Test
	// public void addResourcesTest() {
	//
	// }
	//
	// @Test
	// public void removeResourceTest() {
	//
	// }
	//
	// @Test
	// public void removeUnexistentResourceTest() {
	//
	// }
	//
	// @Test
	// public void L2AttachInterfacesTest() {
	//
	// }
	//
	// @Test
	// public void L2AttachInterfacesTwiceTest() {
	//
	// }
	//
	// @Test
	// public void L2AttachUnexistentInterfacesTest() {
	//
	// }
	//
	// @Test
	// public void L2DetachInterfacesTest() {
	//
	// }
	//
	// @Test
	// public void L2DetachUnexistentInterfacesTest() {
	//
	// }
	//
	// @Test
	// public void L2DetachUnattachedInterfacesTest() {
	//
	// }

	private INetworkBasicCapability createNetworkBasicCapability(IResource network) throws CapabilityException {
		// NetworkBasicCapability capab = (NetworkBasicCapability) new NetworkBasicCapabilityFactory().create(network);

		// TODO Auto-generated method stub
		return null;
	}

	private IResource createRouterWithModel(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	private IResource createNetworkWithModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
