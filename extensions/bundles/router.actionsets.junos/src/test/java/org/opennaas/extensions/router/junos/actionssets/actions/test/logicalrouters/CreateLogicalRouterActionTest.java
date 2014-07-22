package org.opennaas.extensions.router.junos.actionssets.actions.test.logicalrouters;

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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.mock.MockEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.CreateLogicalRouterAction;
import org.opennaas.extensions.router.model.ComputerSystem;

public class CreateLogicalRouterActionTest {
	static CreateLogicalRouterAction	action;
	Log									log			= LogFactory.getLog(CreateLogicalRouterActionTest.class);
	static String						resourceId	= "RandomDevice";

	static ProtocolManager				protocolManager;
	static ProtocolSessionManager		protocolSessionManager;

	static ProtocolSessionContext		netconfContext;

	@Before
	public void init() {
		action = new CreateLogicalRouterAction();
		action.setModelToUpdate(new ComputerSystem());
		protocolManager = new ProtocolManager();
		try {
			protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceId);
			protocolSessionManager.setEventManager(new MockEventManager());
			netconfContext = newSessionContextNetconf();
			protocolManager.sessionFactoryAdded(new NetconfProtocolSessionFactory(), new HashMap<String, String>() {
				{
					put(ProtocolSessionContext.PROTOCOL, "netconf");

				}
			});
			protocolSessionManager.registerContext(netconfContext);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void TestActionID() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.CREATELOGICALROUTER, action.getActionID());
	}

	@Test
	public void checkParamsTest() throws ActionException {

		ComputerSystem lrModel = new ComputerSystem();
		lrModel.setName("L1");

		Assert.assertTrue(action.checkParams(lrModel));
	}

	public void checkTemplate() {
		try {
			ComputerSystem lrModel = new ComputerSystem();
			lrModel.setName("L1");

			Assert.assertTrue(action.checkParams(lrModel));

			action.prepareMessage();

			Assert.assertEquals(action.getTemplate(), "/VM_files/createLogicalRouter.vm");

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		action.setParams(null);

	}

	@Test
	public void createLRTest() {

		try {
			ComputerSystem lrModel = new ComputerSystem();
			lrModel.setName("L1");

			action.setParams(lrModel);
			ActionResponse response = action.execute(protocolSessionManager);
			System.out.println(action.getVelocityMessage());
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Configure the protocol to connect
	 */
	private static ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		protocolSessionContext.addParameter(ProtocolSessionContext.AUTH_TYPE,
				"password");
		// ADDED
		return protocolSessionContext;

	}
}
