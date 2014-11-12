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
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.CreateOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CreateOFForwardingRuleTest extends AbstractRyuActionTest {

	CreateOFForwardingAction	action;

	@Before
	public void prepareTesT() {
		action = new CreateOFForwardingAction();
		action.setModelToUpdate(new OpenflowSwitchModel());

		initializeRyuOFFlow();

	}

	@Test
	public void actionIdTest() {
		Assert.assertEquals("ActionId should match " + OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE,
				OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE, action.getActionID());
	}

	@Test(expected = ActionException.class)
	public void checkNullParamsTest() throws ActionException {
		action.checkParams(null);
	}

	@Test(expected = ActionException.class)
	public void wronParamsTypeTest() throws ActionException {
		action.checkParams(new Object());

	}

	@Test
	public void actionExecutionWithIpsTest() throws Exception {
		// mock protocolsession and client
		mockActionDependencies();

		// prepare model
		OpenflowSwitchModel model = new OpenflowSwitchModel();
		List<OFFlowTable> ofTables = new ArrayList<OFFlowTable>();
		OFFlowTable ofTable = new OFFlowTable();
		ofTables.add(ofTable);
		model.setOfTables(ofTables);

		action.setModelToUpdate(model);
		action.setParams(ryuOFFlow);
		action.execute(protocolSessionManager);

		// we capture the flow sent to ryu
		ArgumentCaptor<RyuOFFlow> flowCaptor = ArgumentCaptor.forClass(RyuOFFlow.class);
		Mockito.verify(client, Mockito.times(1)).addFlowEntry(flowCaptor.capture());

		RyuOFFlow clientFlow = flowCaptor.getValue();
		Assert.assertEquals("Sent OFFlow should contain same dpid as action parameter.", ryuOFFlow.getDpid(), clientFlow.getDpid());
		Assert.assertEquals("Sent OFFlow should contain same cookie as action parameter.", ryuOFFlow.getCookie(), clientFlow.getCookie());
		Assert.assertEquals("Sent OFFlow should contain same tableId as action parameter.", ryuOFFlow.getTableId(), clientFlow.getTableId());
		Assert.assertEquals("Sent OFFlow should contain same priority as action parameter.", ryuOFFlow.getPriority(), clientFlow.getPriority());
		Assert.assertEquals("Sent OFFlow should contain same idle timeout as action parameter.", ryuOFFlow.getIdleTimeout(),
				clientFlow.getIdleTimeout());
		Assert.assertEquals("Sent OFFlow should contain same idle hard timeout as action parameter.", ryuOFFlow.getHardTimeout(),
				clientFlow.getHardTimeout());

		Assert.assertEquals("Sent OFFlow should contain same src ip as action parameter.", ryuOFFlow.getMatch().getSrcIp(), clientFlow.getMatch()
				.getSrcIp());
		Assert.assertEquals("Sent OFFlow should contain same dst ip as action parameter.", ryuOFFlow.getMatch().getDstIp(), clientFlow.getMatch()
				.getDstIp());
		Assert.assertEquals("Ether type shold be automatically set to 2048 when match contains ips.", "2048", clientFlow.getMatch().getEtherType());

		Assert.assertTrue("Flow should have been inserted in resource model. ", model.getOfTables().get(0).getOfForwardingRules()
				.contains(clientFlow));
	}

	/**
	 * Test checks that if there exists an identical forwarding rule in model (comparation without id), the action launches an {@link ActionException}
	 * 
	 * @throws ProtocolException
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void actionExecuteFlowExists() throws ProtocolException, ActionException {

		// mock protocolsession and client
		mockActionDependencies();

		// create same flow with different name
		OFFlow repeatedFlow = new OFFlow(ryuOFFlow);
		repeatedFlow.setName("flow2");
		repeatedFlow.getMatch().setEtherType("2048");

		// prepare model
		OpenflowSwitchModel model = new OpenflowSwitchModel();
		List<OFFlowTable> ofTables = new ArrayList<OFFlowTable>();
		OFFlowTable ofTable = new OFFlowTable();

		List<OFFlow> forwardingRules = new ArrayList<OFFlow>();
		forwardingRules.add(repeatedFlow);
		ofTable.setOfForwardingRules(forwardingRules);

		ofTables.add(ofTable);
		model.setOfTables(ofTables);

		// test
		action.setModelToUpdate(model);
		action.setParams(ryuOFFlow);
		action.execute(protocolSessionManager);

	}

}
