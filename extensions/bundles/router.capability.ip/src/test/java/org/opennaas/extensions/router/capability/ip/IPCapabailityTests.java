package org.opennaas.extensions.router.capability.ip;

/*
 * #%L
 * OpenNaaS :: Router :: IP Capability
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

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

/**
 * Unit tests for {@link IPCapability}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class IPCapabailityTests {

	private static final String	IPV4_ADDRESS			= "192.168.22.1";
	private static final String	IPV4_SUBNET_MASK		= "255.255.255.0";
	private static final String	INTERFACE				= "fe-0/1/1";
	private static final int	PORT_NUMBER				= 1;

	private static final String	INTERFACE_NAME			= INTERFACE + PORT_NUMBER;
	private static final String	IPV4_COMPOSED_ADDRESS	= IPV4_ADDRESS + "/" + IPV4_SUBNET_MASK;

	private IPCapability		ipCapability;

	@Before
	public void setUp() throws Exception {
		ipCapability = generatePartialMockedCapability();
		ipCapability.setResource(generateResource());
	}

	@Test
	public void testSetIp() throws SecurityException, NoSuchMethodException, Exception {

		// call to setIP() with an IP address v4
		ipCapability.setIP(INTERFACE_NAME, IPV4_COMPOSED_ADDRESS);

		// verify the call to queueAction method
		Mockito.verify(ipCapability, Mockito.times(1)).queueAction((IAction) Mockito.anyObject());

		// verify createActionAndCheckParams method and capture action params
		ArgumentCaptor<Object> capturedActionParams = ArgumentCaptor.forClass(Object.class);
		PowerMockito.verifyPrivate(ipCapability, Mockito.times(1)).invoke("createActionAndCheckParams", Mockito.eq(IPActionSet.SET_IPv4),
				capturedActionParams.capture());

		// assertions
		Assert.assertTrue("Generated action param must be a NetworkPort", capturedActionParams.getValue() instanceof NetworkPort);

		NetworkPort networkPort = (NetworkPort) capturedActionParams.getValue();

		Assert.assertEquals("NetworkPort interface name must be " + INTERFACE, INTERFACE, networkPort.getName());
		Assert.assertEquals("NetworkPort interface port number must be " + PORT_NUMBER, PORT_NUMBER, networkPort.getPortNumber());

		List<ProtocolEndpoint> protocolEndpoints = networkPort.getProtocolEndpoint();
		Assert.assertEquals("There must be one protocol endpoint", protocolEndpoints.size(), 1);
		Assert.assertTrue("There must be one protocol endpoint of type IPProtocolEndpoint", protocolEndpoints.get(0) instanceof IPProtocolEndpoint);

		IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) networkPort.getProtocolEndpoint().get(0);
		Assert.assertEquals("IPv4 address must be " + IPV4_ADDRESS, IPV4_ADDRESS, ipProtocolEndpoint.getIPv4Address());
		Assert.assertEquals("IPv4 subnet mask must be " + IPV4_SUBNET_MASK, IPV4_SUBNET_MASK, ipProtocolEndpoint.getSubnetMask());
	}

	private static IResource generateResource() {
		IResource resource = new MockResource();
		return resource;
	}

	private static IPCapability generatePartialMockedCapability() throws Exception {
		IPCapability partialMockedIPCapability = PowerMockito.mock(IPCapability.class);

		// initialize internal loggerS (Capability and AbstractCapability instances)
		Whitebox.setInternalState(partialMockedIPCapability, Log.class, LogFactory.getLog(IPCapability.class));
		Whitebox.setInternalState(partialMockedIPCapability, Log.class, LogFactory.getLog(IPCapability.class), AbstractCapability.class);

		// do real method calls on capability calls
		PowerMockito.doCallRealMethod().when(partialMockedIPCapability).setIP(Mockito.anyString(), Mockito.anyString());
		PowerMockito.doCallRealMethod().when(partialMockedIPCapability)
				.setIP(Mockito.any(LogicalDevice.class), Mockito.any(IPProtocolEndpoint.class));
		PowerMockito.doCallRealMethod().when(partialMockedIPCapability).setIP(Mockito.any(LogicalDevice.class), Mockito.anyString());
		PowerMockito.doCallRealMethod().when(partialMockedIPCapability).setIPv4(Mockito.anyString(), Mockito.anyString());
		PowerMockito.doCallRealMethod().when(partialMockedIPCapability)
				.setIPv4(Mockito.any(LogicalDevice.class), Mockito.any(IPProtocolEndpoint.class));
		PowerMockito.doCallRealMethod().when(partialMockedIPCapability).setIPv6(Mockito.anyString(), Mockito.anyString());
		PowerMockito.doCallRealMethod().when(partialMockedIPCapability)
				.setIPv6(Mockito.any(LogicalDevice.class), Mockito.any(IPProtocolEndpoint.class));

		return partialMockedIPCapability;
	}

}
