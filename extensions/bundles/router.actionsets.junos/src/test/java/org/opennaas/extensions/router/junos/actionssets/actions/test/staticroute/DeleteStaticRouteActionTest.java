package org.opennaas.extensions.router.junos.actionssets.actions.test.staticroute;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.staticroute.DeleteStaticRouteAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;

public class DeleteStaticRouteActionTest {

	Log										log	= LogFactory.getLog(DeleteStaticRouteActionTest.class);
	private static DeleteStaticRouteAction	action;
	static ActionTestHelper					helper;
	static ProtocolSessionManager			protocolsessionmanager;

	@BeforeClass
	public static void init() {
		action = new DeleteStaticRouteAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.STATIC_ROUTE_DELETE,
				action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNotNull("Null parameters", action.getParams());
	}

	/**
	 * Create two OSPFProtocolEndpoint with state to enable
	 * 
	 * @throws IOException
	 */
	@Test
	public void executeActionTest() throws IOException {
		action.setModelToUpdate(new ComputerSystem());
		action.setParams(getParams());
		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID()
					.equals(ActionConstants.STATIC_ROUTE_DELETE));
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
		org.opennaas.extensions.router.model.System computerSystem = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);
	}

	/**
	 * @return
	 */
	private String[] getParams() {
		String[] params = new String[2];
		params[0] = "0.0.0.0/0";
		params[1] = "192.168.1.1";
		return params;
	}

}
