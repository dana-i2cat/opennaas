package org.opennaas.extensions.router.junos.actionssets.actions.test.topologydiscovery;

import java.io.IOException;
import java.lang.reflect.Field;

import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.router.capability.topologydiscovery.model.Neighbours;
import org.opennaas.extensions.router.capability.topologydiscovery.model.Port;
import org.opennaas.extensions.router.junos.actionssets.actions.topologydiscovery.GetNeighboursAction;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
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

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetNeighboursActionTest {

	private final static String	XML_FILE_PATH		= "/actions/topologydiscovery/getNeighbours.xml";
	private final static String	IFACE_1_NAME		= "ge-0/0/1";
	private final static String	IFACE_2_NAME		= "ge-0/0/2";
	private final static String	REMOTE_DEVICE_1_ID	= "18:88:14:cf:c0:90";
	private final static String	REMOTE_DEVICE_2_ID	= "84:18:88:14:cf:c0";

	GetNeighboursAction			action;
	Reply						reply;

	@Before
	public void prepareTest() throws IOException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(XML_FILE_PATH));

		reply = new Reply();
		reply.setContain(xml);

		action = new GetNeighboursAction();
	}

	@Test
	public void parseResponseTest() throws ActionException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		// method to be tested
		action.parseResponse(reply, null);

		// access generated rpcResponse by Reflection
		Field rpcResponsefield = GetNeighboursAction.class.getDeclaredField("rpcResponse");
		rpcResponsefield.setAccessible(true);
		Neighbours neighbours = (Neighbours) rpcResponsefield.get(action);

		// asserts
		Assert.assertNotNull("Neighbours should not be null.", neighbours);
		Assert.assertNotNull("Device port map should not be null.", neighbours.getDevicePortMap());
		Assert.assertEquals("Response contained two different connections.", 2, neighbours.getDevicePortMap().size());

		Assert.assertTrue("Response contained information for interface " + IFACE_1_NAME,
				neighbours.getDevicePortMap().keySet().contains(IFACE_1_NAME));
		Assert.assertTrue("Response contained information for interface " + IFACE_2_NAME,
				neighbours.getDevicePortMap().keySet().contains(IFACE_2_NAME));

		checkRemoteDevice(neighbours.getDevicePortMap().get(IFACE_1_NAME), IFACE_1_NAME, REMOTE_DEVICE_1_ID);
		checkRemoteDevice(neighbours.getDevicePortMap().get(IFACE_2_NAME), IFACE_2_NAME, REMOTE_DEVICE_2_ID);

	}

	private void checkRemoteDevice(Port port, String localIfaceName, String remoteDeviceId) {

		Assert.assertNotNull("Remote port connected to interface " + localIfaceName + " should not be null", port);
		Assert.assertEquals("Interface " + localIfaceName + " should be connected to device " + remoteDeviceId, port.getDeviceId(),
				remoteDeviceId);

	}

}
