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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.mock.MockEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.AddInterfaceToLogicalRouterAction;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.RemoveInterfaceFromLogicalRouterAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;

public class AddInterfaceToLRTest {

	private static ProtocolManager							protocolManager;
	private static ProtocolSessionManager					protocolSessionManager;
	private static ProtocolSessionContext					netconfContext;
	private static String									resourceId	= "testResource";
	private static AddInterfaceToLogicalRouterAction		addAction;
	private static RemoveInterfaceFromLogicalRouterAction	removeAction;

	@BeforeClass
	public static void init() throws ProtocolException {
		addAction = new AddInterfaceToLogicalRouterAction();
		addAction.setModelToUpdate(new ComputerSystem());

		removeAction = new RemoveInterfaceFromLogicalRouterAction();
		removeAction.setModelToUpdate(new ComputerSystem());

		protocolManager = new ProtocolManager();
		protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceId);
		protocolSessionManager.setEventManager(new MockEventManager());
		netconfContext = newSessionContextNetconf();
		protocolManager.sessionFactoryAdded(new NetconfProtocolSessionFactory(), new HashMap<String, String>() {
			{
				put(ProtocolSessionContext.PROTOCOL, "netconf");
				put(ProtocolSessionContext.AUTH_TYPE, "password");
			}
		});
		protocolSessionManager.registerContext(netconfContext);
	}

	@Test
	public void testExecuteAdd() throws ActionException {
		addAction.setParams(createLRModelWithAnInterface());
		ActionResponse response = addAction.execute(protocolSessionManager);
		assertNotNull(response);
		assertEquals(STATUS.OK, response.getStatus());
	}

	@Test
	public void testExecuteRemove() throws ActionException {
		removeAction.setParams(createLRModelWithAnInterface());
		ActionResponse response = removeAction.execute(protocolSessionManager);
		assertNotNull(response);
		assertEquals(STATUS.OK, response.getStatus());
	}

	private ComputerSystem createLRModelWithAnInterface() {
		ComputerSystem model = new ComputerSystem();
		model.setElementName("cpe2");

		EthernetPort port = new EthernetPort();
		port.setName("fe-0/0/3");
		port.setPortNumber(0);
		model.addLogicalDevice(port);

		return model;
	}

	private static ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		// FIXME CAUTION REMOVE BEFORE COMMIT!!!!
		// NOT COMMENT IT, REMOVE IT! IT CONTAINS SENSITIVE DATA!!!!
		// uri =

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		protocolSessionContext.addParameter(ProtocolSessionContext.AUTH_TYPE,
				"password");

		return protocolSessionContext;
	}
}
