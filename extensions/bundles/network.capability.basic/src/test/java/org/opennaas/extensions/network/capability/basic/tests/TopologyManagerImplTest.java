package org.opennaas.extensions.network.capability.basic.tests;

import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;
import org.opennaas.extensions.network.capability.basic.NetworkBasicCapability;
import org.opennaas.extensions.network.capability.basic.NetworkBasicCapabilityFactory;

import org.junit.Test;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.CapabilityException;

import junit.framework.TestCase;

public class TopologyManagerImplTest extends TestCase {

	INetworkBasicCapability topologyManager;

	IResource network;
	IResource router1;
	IResource router2;

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
//	@Test
//	public void addResourceTwiceTest() {
//
//	}
//
//	@Test
//	public void addResourcesTest() {
//
//	}
//
//	@Test
//	public void removeResourceTest() {
//
//	}
//
//	@Test
//	public void removeUnexistentResourceTest() {
//
//	}
//
//	@Test
//	public void L2AttachInterfacesTest() {
//
//	}
//
//	@Test
//	public void L2AttachInterfacesTwiceTest() {
//
//	}
//
//	@Test
//	public void L2AttachUnexistentInterfacesTest() {
//
//	}
//
//	@Test
//	public void L2DetachInterfacesTest() {
//
//	}
//
//	@Test
//	public void L2DetachUnexistentInterfacesTest() {
//
//	}
//
//	@Test
//	public void L2DetachUnattachedInterfacesTest() {
//
//	}

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
