package org.opennaas.extensions.router.junos.actionssets.actions.test.ospf;

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
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.RemoveOSPFAreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;

public class RemoveOSPFAreaActionTest {
	Log									log	= LogFactory.getLog(RemoveOSPFAreaActionTest.class);
	private static RemoveOSPFAreaAction	action;
	static ActionTestHelper				helper;
	static ProtocolSessionManager		protocolsessionmanager;

	@Before
	public void init() {

		action = new RemoveOSPFAreaAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {

		Assert.assertEquals("Wrong ActionID", ActionConstants.OSPF_REMOVE_AREA,
				action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNotNull("Null parameters", action.getParams());
	}

	@Test
	public void templateTest() {
		// this action always have this template as a default
		Assert.assertEquals("Not accepted param", "/VM_files/ospfRemoveArea.vm", action.getTemplate());
	}

	/**
	 * Create OSPFAreaConfiguration with state to enable
	 * 
	 * @throws IOException
	 */
	@Test
	public void executeActionTest() throws IOException {

		action.setModelToUpdate(new ComputerSystem());

		// Add params
		OSPFAreaConfiguration ospfAreaConfiguration = getOSPFAreaConfiguration();
		action.setParams(ospfAreaConfiguration);

		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID()
					.equals(ActionConstants.OSPF_REMOVE_AREA));
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}

		org.opennaas.extensions.router.model.System computerSystem = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);

	}

	/**
	 * Get the OSPFAreaConfiguration
	 * 
	 * @return OSPFAreaConfiguration
	 * @throws IOException
	 */
	private OSPFAreaConfiguration getOSPFAreaConfiguration() throws IOException {

		// OSPFAreaConfiguration and OSPFArea with areaID = 0.0.0.0
		OSPFAreaConfiguration ospfAreaConfiguration = new OSPFAreaConfiguration();
		OSPFArea ospfArea = new OSPFArea();
		ospfArea.setAreaID(0);
		ospfAreaConfiguration.setOSPFArea(ospfArea);

		return ospfAreaConfiguration;
	}

	/**
	 * Create a OSPFProtocolEndpoint from the params
	 * 
	 * @param areaId
	 * @param portName
	 * @return OSPFProtocolEndpoint
	 * @throws IOException
	 */
	private OSPFProtocolEndpoint getOSPFProtocolEndpoint(String logicalPortName, String logicalPortNumber) throws IOException {

		OSPFProtocolEndpoint ospfProtocolEndpoint = new OSPFProtocolEndpoint();
		ospfProtocolEndpoint.setName(logicalPortName + "." + logicalPortNumber);
		ospfProtocolEndpoint.setEnabledState(EnabledState.DISABLED);

		return ospfProtocolEndpoint;
	}
}
