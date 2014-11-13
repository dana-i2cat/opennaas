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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.RemoveOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class RemoveOFForwardingActionTest extends AbstractRyuActionTest {

	RemoveOFForwardingAction	action;
	RyuOFFlowListWrapper		serverResponse;

	@Before
	public void prepareTest() {
		action = new RemoveOFForwardingAction();

		initializeRyuOFFlow();

		serverResponse = new RyuOFFlowListWrapper();
		serverResponse.add(ryuOFFlow);

	}

	@Test
	public void actionIdTest() {
		Assert.assertEquals("ActionId should match " + OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE,
				OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE,
				action.getActionID());
	}

	@Test(expected = ActionException.class)
	public void checkNullPararamsTest() throws ActionException {
		action.checkParams(null);
	}

	@Test
	public void checkParamsTest() throws ActionException {
		Assert.assertTrue("Action should accept String parameters.", action.checkParams(FLOW_ID));
	}

	@Test
	public void executeActionTest() throws Exception {
		// mock protocolsession and client

		mockActionDependencies();
		Mockito.when(client.getFlows(Mockito.eq(SWITCH_ID))).thenReturn(serverResponse);

		// initialize model
		OpenflowSwitchModel model = new OpenflowSwitchModel();
		OFFlowTable ofTable = new OFFlowTable();
		ofTable.setOfForwardingRules(new ArrayList<OFFlow>());
		ofTable.getOfForwardingRules().add(ryuOFFlow);
		model.setOfTables(Arrays.asList(ofTable));
		action.setModelToUpdate(model);

		// execute action
		action.setParams(FLOW_ID);
		action.execute(protocolSessionManager);

		Mockito.verify(client, Mockito.times(1)).deleteFlowEntryStrictly(ryuOFFlow);

	}

}
