package org.opennaas.extensions.router.junos.actionssets.actions.test.topologydiscovery;

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

import java.io.IOException;
import java.lang.reflect.Field;

import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.router.capability.topologydiscovery.model.LocalInformation;
import org.opennaas.extensions.router.junos.actionssets.actions.topologydiscovery.GetLocalInformationAction;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetLocalInformationActionTest {

	private final static String	XML_FILE_PATH	= "/actions/topologydiscovery/getLocalInformation.xml";

	private static final Object	DEVICE_ID		= "ac:4b:c8:27:5e:80";

	private static final String	IFACE_1_NAME	= "me0.0";
	private static final String	IFACE_2_NAME	= "ge-0/0/2.0";
	private static final String	IFACE_1_ID		= "34";
	private static final Object	IFACE_2_ID		= "508";

	GetLocalInformationAction	action;
	Reply						reply;

	@Before
	public void prepareTest() throws IOException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(XML_FILE_PATH));

		reply = new Reply();
		reply.setContain(xml);

		action = new GetLocalInformationAction();
	}

	@Test
	public void parseResponseTest() throws ActionException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

		// method to be tested
		action.parseResponse(reply, null);

		// access generated rpcResponse by Reflection
		Field rpcResponsefield = GetLocalInformationAction.class.getDeclaredField("rpcResponse");
		rpcResponsefield.setAccessible(true);
		LocalInformation localInformation = (LocalInformation) rpcResponsefield.get(action);

		Assert.assertNotNull(localInformation);
		Assert.assertEquals(DEVICE_ID, localInformation.getDeviceId());

		Assert.assertNotNull(localInformation.getInterfacesMap());
		Assert.assertEquals(2, localInformation.getInterfacesMap().size());

		Assert.assertTrue("Response contained information for interface " + IFACE_1_ID,
				localInformation.getInterfacesMap().keySet().contains(IFACE_1_ID));
		Assert.assertTrue("Response contained information for interface " + IFACE_2_ID,
				localInformation.getInterfacesMap().keySet().contains(IFACE_2_ID));

		Assert.assertEquals("Interface " + IFACE_1_ID + " should have following name: " + IFACE_1_ID, IFACE_1_NAME, localInformation
				.getInterfacesMap().get(IFACE_1_ID));

		Assert.assertEquals("Interface " + IFACE_2_ID + " should have following name: " + IFACE_2_NAME, IFACE_2_NAME, localInformation
				.getInterfacesMap().get(IFACE_2_ID));

	}
}
