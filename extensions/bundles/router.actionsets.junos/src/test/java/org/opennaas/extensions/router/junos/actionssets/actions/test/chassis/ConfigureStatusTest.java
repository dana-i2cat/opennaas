package org.opennaas.extensions.router.junos.actionssets.actions.test.chassis;

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
import java.util.List;

import org.junit.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.mock.MockEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.ConfigureStatusAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;

public class ConfigureStatusTest {
	static ConfigureStatusAction	action;
	Log								log			= LogFactory.getLog(ConfigureStatusTest.class);
	static String					resourceId	= "RandomDevice";

	static ProtocolManager			protocolManager;
	static ProtocolSessionManager	protocolSessionManager;

	static ProtocolSessionContext	netconfContext;

	@BeforeClass
	public static void init() {
		action = new ConfigureStatusAction();
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
		Assert.assertEquals("Wrong ActionID", ActionConstants.CONFIGURESTATUS, action.getActionID());
	}

	@Test
	public void checkParamsTest() {

		// only accept EthernetPort and LogicalTunnelPort type
		try {
			Assert.assertTrue(action.checkParams(newParamsConfigureStatus("fe-0/3/1", OperationalStatus.STOPPED)));

		} catch (ActionException a) {
			a.printStackTrace();
			Assert.fail(a.getMessage());
		}

	}

	public void checkTemplate() {
		try {
			Assert.assertTrue(action.checkParams(newParamsConfigureStatus("fe-0/3/2", OperationalStatus.STOPPED)));
			action.prepareMessage();

			Assert.assertEquals(action.getTemplate(), "/VM_files/configureStatus.vm");

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		action.setParams(null);

	}

	@Test
	public void configureStatusTest() {

		try {
			action.setParams(newParamsConfigureStatus("fe-0/3/2", OperationalStatus.STOPPED));
			ActionResponse response = action.execute(protocolSessionManager);

			Assert.assertEquals(ActionConstants.CONFIGURESTATUS, response.getActionID());
			List<Response> responses = response.getResponses();
			for (Response resp : responses) {
				Assert.assertEquals(Response.Status.OK, resp.getStatus());
			}

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	private Object newParamsConfigureStatus(String interfaceName, OperationalStatus status) {

		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName(interfaceName);
		logicalPort.setOperationalStatus(status);
		return logicalPort;
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
