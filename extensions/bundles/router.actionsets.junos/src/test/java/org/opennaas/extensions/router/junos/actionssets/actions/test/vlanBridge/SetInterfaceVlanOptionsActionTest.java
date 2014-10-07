package org.opennaas.extensions.router.junos.actionssets.actions.test.vlanBridge;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.vlanbridge.SetInterfaceVlanOptionsAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPortVLANSettingData;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class SetInterfaceVlanOptionsActionTest {

	Log												log	= LogFactory.getLog(SetInterfaceVlanOptionsActionTest.class);
	private static SetInterfaceVlanOptionsAction	action;
	static ActionTestHelper							helper;

	@BeforeClass
	public static void init() {
		action = new SetInterfaceVlanOptionsAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
	}

	@Test
	public void actionIDTest() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.VLAN_BRIDGE_SET_IFACE_VLAN_OPTIONS,
				action.getActionID());
	}

	@Test
	public void templateIDTest() {
		Assert.assertEquals("Wrong template", "/VM_files/vlanBridge/ifaceVlanOptsSet.vm",
				action.getTemplate());
	}

	@Test
	public void checkValidParamTest() throws ActionException {

		NetworkPortVLANSettingData settingData = new NetworkPortVLANSettingData();
		settingData.setPortMode("trunk");
		settingData.setNativeVlanId(12);

		NetworkPort netPort = new NetworkPort();
		netPort.setName("fe-0/1/1");
		netPort.setPortNumber(2);

		netPort.addElementSettingData(settingData);

		action.checkParams(netPort);
	}

	@Test(expected = ActionException.class)
	public void checkNetworkPortWithNullIfaceOptionsTest() throws ActionException {

		NetworkPort netPort = new NetworkPort();
		netPort.setName("fe-0/1/1");
		netPort.setPortNumber(2);
		action.checkParams(netPort);
	}

	@Test(expected = ActionException.class)
	public void checkEmptyParamTest() throws ActionException {

		NetworkPort netPort = new NetworkPort();

		action.checkParams(netPort);
	}

	@Test(expected = ActionException.class)
	public void checkNullParamTest() throws ActionException {

		action.checkParams(null);
	}
}
