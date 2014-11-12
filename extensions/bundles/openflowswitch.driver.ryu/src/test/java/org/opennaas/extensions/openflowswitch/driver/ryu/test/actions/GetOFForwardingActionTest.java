package org.opennaas.extensions.openflowswitch.driver.ryu.test.actions;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.GetOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetOFForwardingActionTest extends AbstractRyuActionTest {

	GetOFForwardingAction	action;
	RyuOFFlowListWrapper	serverResponse;

	@Before
	public void prepareTest() {
		action = new GetOFForwardingAction();
		initializeRyuOFFlow();

		serverResponse = new RyuOFFlowListWrapper();
		serverResponse.add(ryuOFFlow);

	}

	@Test
	public void actionIdTest() {
		Assert.assertEquals("ActionId should match " + OpenflowForwardingActionSet.GETFLOWS, OpenflowForwardingActionSet.GETFLOWS,
				action.getActionID());
	}

	@Test
	public void checkPararamsTest() throws ActionException {

		Assert.assertTrue("Check Params should not fail for null parameters.", action.checkParams(null));
		Assert.assertTrue("Check Params should not fail for any parameter.", action.checkParams(new Object()));

	}

	@Test
	public void executeActionTest() throws Exception {

		// mock protocolsession and client
		mockActionDependencies();
		Mockito.when(client.getFlows(Mockito.eq(SWITCH_ID))).thenReturn(serverResponse);

		OpenflowSwitchModel model = new OpenflowSwitchModel();
		OFFlowTable ofTable = new OFFlowTable();
		ofTable.setOfForwardingRules(new ArrayList<OFFlow>());
		model.setOfTables(Arrays.asList(ofTable));
		action.setModelToUpdate(model);

		// test
		ActionResponse response = action.execute(protocolSessionManager);

		Assert.assertNotNull("Action response should not be null", response);
		Assert.assertEquals("Action status should be " + ActionResponse.STATUS.OK, ActionResponse.STATUS.OK, response.getStatus());

		Assert.assertTrue("Action should return a list.", response.getResult() instanceof List<?>);

		@SuppressWarnings("unchecked")
		List<OFFlow> flows = (List<OFFlow>) response.getResult();
		Assert.assertEquals("Response should contain 1 OFFlow", 1, flows.size());

		Assert.assertEquals(ryuOFFlow.getMatch(), flows.get(0).getMatch());
		Assert.assertEquals(ryuOFFlow.getName(), flows.get(0).getName());
		Assert.assertEquals(ryuOFFlow.getActions(), flows.get(0).getActions());
		Assert.assertEquals(ryuOFFlow.getPriority(), flows.get(0).getPriority());

		Mockito.verify(client, Mockito.times(1)).getFlows(SWITCH_ID);

	}
}
