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
import org.opennaas.extensions.router.junos.actionssets.actions.topologydiscovery.GetInterfaceNeighbourAction;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetInterfaceNeighbourActionTest {

	private static final String	XML_FILE_PATH	= "/actions/topologydiscovery/getInterfaceNeighbour.xml";

	private static final Object	REMOTE_PORT_ID	= "506";

	GetInterfaceNeighbourAction	action;
	Reply						reply;

	@Before
	public void prepareTest() throws IOException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(XML_FILE_PATH));

		reply = new Reply();
		reply.setContain(xml);

		action = new GetInterfaceNeighbourAction();
	}

	@Test
	public void parseResponseTest() throws ActionException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		action.parseResponse(reply, null);

		// access generated rpcResponse by Reflection
		Field rpcResponsefield = GetInterfaceNeighbourAction.class.getDeclaredField("rpcResponse");
		rpcResponsefield.setAccessible(true);
		String remotePortId = (String) rpcResponsefield.get(action);

		Assert.assertNotNull(remotePortId);
		Assert.assertFalse(remotePortId.isEmpty());
		Assert.assertEquals(REMOTE_PORT_ID, remotePortId);

	}

}
