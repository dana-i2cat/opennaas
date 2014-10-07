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
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRoute;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.staticroute.CreateStaticRouteAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;

public class CreateStaticRouteActionTest {
	Log										log	= LogFactory.getLog(CreateStaticRouteActionTest.class);
	private static CreateStaticRouteAction	action;
	static ActionTestHelper					helper;
	static ProtocolSessionManager			protocolsessionmanager;

	@BeforeClass
	public static void init() {
		action = new CreateStaticRouteAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.STATIC_ROUTE_CREATE,
				action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNotNull("Null parameters", action.getParams());
	}

	/**
	 * Create static route v4.
	 * 
	 * @throws IOException
	 */
	@Test
	public void executeActionTest() throws IOException {
		action.setModelToUpdate(new ComputerSystem());
		action.setParams(getStaticRoute());
		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID()
					.equals(ActionConstants.STATIC_ROUTE_CREATE));
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
		org.opennaas.extensions.router.model.System computerSystem = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);
	}

	/**
	 * 
	 * 
	 * @throws IOException
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void executeActionWrongParamsTest() throws ActionException {

		action.setModelToUpdate(new ComputerSystem());
		action.setParams(getWrongStaticRoute());

		action.execute(protocolsessionmanager);

	}

	private StaticRoute getStaticRoute() {
		StaticRoute staticRoute = new StaticRoute();
		staticRoute.setNetIdIpAdress("0.0.0.0/0");
		staticRoute.setNextHopIpAddress("192.168.1.1");
		staticRoute.setDiscard(false);
		return staticRoute;
	}

	private StaticRoute getWrongStaticRoute() {
		StaticRoute staticRoute = new StaticRoute();
		staticRoute.setNetIdIpAdress("0.0.0.0/0");
		staticRoute.setNextHopIpAddress("FDEC:34:52::A6/64");
		staticRoute.setDiscard(false);
		return staticRoute;
	}

}
