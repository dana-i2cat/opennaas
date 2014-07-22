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

import static org.junit.Assert.assertEquals;
import org.junit.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.ConfigureSubInterfaceAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.VLANEndpoint;

/**
 * @author ?
 * @author Julio Carlos Barrera
 */
public class ConfigureSubinterfaceActionTest {

	private static ConfigureSubInterfaceAction	action;
	private static ActionTestHelper				helper;
	private static ProtocolSessionManager		protocolsessionmanager;

	private final static String					iface1				= "fe-2/0/1.0";
	private final static String					iface2				= "fe-2/0/1.1";
	private final static String					logicalTunnel		= "lt-1/2/0.1";
	private final static String					greInterface		= "gr-1/0/1.2";
	private final static String					loopbackInterface	= "lo0.100";

	private static int							peerUnit			= 1;
	private static int							vlanid				= 1;
	Log											log					= LogFactory.getLog(ConfigureSubinterfaceActionTest.class);

	@BeforeClass
	public static void init() {
		action = new ConfigureSubInterfaceAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {

		Assert.assertEquals("Wrong ActionID", ActionConstants.CONFIGURESUBINTERFACE,
				action.getActionID());
	}

	@Test
	public void UntaggedEthernetInterfaceTest() throws ActionException {
		action.setParams(newUntaggedInterface(iface1));
		action.checkParams(action.getParams());

		Assert.assertEquals("Unvalid template.", "/VM_files/configureEthWithoutVLAN.vm", action.getTemplate());

		ActionResponse response = action.execute(protocolsessionmanager);

		Assert.assertTrue(response.getActionID()
				.equals(ActionConstants.CONFIGURESUBINTERFACE));

		assertEquals(STATUS.OK, response.getStatus());

		action.setParams(newUntaggedInterface(iface2));

		try {
			action.checkParams(action.getParams());
		} catch (ActionException ae) {
			assertEquals(ae.getMessage(), ConfigureSubInterfaceAction.UNTAGGED_INTERFACE_ERROR);
		}
	}

	@Test
	public void logicalTunnelInterfaceTest() throws ActionException {

		action.setParams(newLogicalTunnelPort(iface1));

		try {
			action.checkParams(action.getParams());
		} catch (ActionException ae) {
			assertEquals(ae.getMessage(), ConfigureSubInterfaceAction.UNVALID_NAME);
		}

		action.setParams(newLogicalTunnelPort(logicalTunnel));
		action.checkParams(action.getParams());

		Assert.assertEquals("Unvalid template.", "/VM_files/configureLogicalTunnelVLAN.vm", action.getTemplate());

		ActionResponse response = action.execute(protocolsessionmanager);

		Assert.assertTrue(response.getActionID()
				.equals(ActionConstants.CONFIGURESUBINTERFACE));

		assertEquals(STATUS.OK, response.getStatus());
	}

	@Test
	public void VLANInterfaceTest() throws ActionException {

		action.setParams(newVLANInterface(logicalTunnel));
		action.checkParams(action.getParams());
		try {
			action.checkParams(action.getParams());
		} catch (ActionException ae) {
			assertEquals(ae.getMessage(), ConfigureSubInterfaceAction.UNVALID_NAME);
		}

		action.setParams(newVLANInterface(iface2));

		Assert.assertEquals("Unvalid template.", "/VM_files/configureEthVLAN.vm", action.getTemplate());

		ActionResponse response = action.execute(protocolsessionmanager);

		Assert.assertTrue(response.getActionID()
				.equals(ActionConstants.CONFIGURESUBINTERFACE));

		assertEquals(STATUS.OK, response.getStatus());

	}

	@Test
	public void GREInterfaceTest() throws ActionException {

		action.setParams(newVLANInterface(greInterface));
		action.checkParams(action.getParams());

		Assert.assertEquals("Unvalid template.", "/VM_files/configureGRELogicalInterface.vm", action.getTemplate());

		ActionResponse response = action.execute(protocolsessionmanager);

		Assert.assertTrue(response.getActionID()
				.equals(ActionConstants.CONFIGURESUBINTERFACE));

		assertEquals(STATUS.OK, response.getStatus());
	}

	@Test
	public void LoopbackInterfaceTest() throws ActionException {
		action.setParams(newUntaggedInterface(loopbackInterface));
		action.checkParams(action.getParams());

		Assert.assertEquals("Unvalid velocity template", "/VM_files/configureEthWithoutVLAN.vm", action.getTemplate());

		ActionResponse response = action.execute(protocolsessionmanager);
		Assert.assertTrue(response.getActionID().equals(ActionConstants.CONFIGURESUBINTERFACE));

		assertEquals(STATUS.OK, response.getStatus());
	}

	private EthernetPort newVLANInterface(String interfaceName) {

		EthernetPort eth = newUntaggedInterface(interfaceName);
		VLANEndpoint vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(vlanid); // TODO COMPLETE OTHER CASES... INITIALIZE THE VLAN ID TO 1
		eth.addProtocolEndpoint(vlanEndpoint);

		return eth;
	}

	private EthernetPort newUntaggedInterface(String interfaceName) {

		EthernetPort eth = new EthernetPort();
		String[] args = interfaceName.split("\\.");
		eth.setName(args[0]);
		eth.setPortNumber(Integer.parseInt(args[1]));

		return eth;
	}

	private LogicalTunnelPort newLogicalTunnelPort(String interfaceName) {

		LogicalTunnelPort lt = new LogicalTunnelPort();
		String[] args = interfaceName.split("\\.");
		lt.setName(args[0]);
		lt.setPortNumber(Integer.valueOf(args[1]));
		lt.setPeer_unit(peerUnit);

		return lt;
	}
}
