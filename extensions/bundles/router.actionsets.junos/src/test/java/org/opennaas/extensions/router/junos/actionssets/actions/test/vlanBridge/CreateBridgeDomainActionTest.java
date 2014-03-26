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

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.vlanbridge.CreateBridgeDomainAction;
import org.opennaas.extensions.router.model.BridgeDomain;
import org.opennaas.extensions.router.model.ComputerSystem;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class CreateBridgeDomainActionTest {
	Log										log	= LogFactory.getLog(CreateBridgeDomainActionTest.class);
	private static CreateBridgeDomainAction	action;
	static ActionTestHelper					helper;

	@BeforeClass
	public static void init() {
		action = new CreateBridgeDomainAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
	}

	@Test
	public void actionIDTest() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.VLAN_BRIDGE_CREATE_BRIDGE_DOMAIN,
				action.getActionID());
	}

	@Test
	public void templateIDTest() {
		Assert.assertEquals("Wrong template", "/VM_files/vlanBridge/bridgeDomainCreate.vm",
				action.getTemplate());
	}

	@Test
	public void checkValidParamsTest() throws ActionException {

		BridgeDomain brDomain = new BridgeDomain();

		brDomain.setElementName("vlan100");
		brDomain.setDescription("This is a sample BridgeDomain.");
		brDomain.setVlanId(100);
		brDomain.addNetworkPort("fe-0/1/1.2");
		brDomain.addNetworkPort("fe-0/1/1.3");

		action.checkParams(brDomain);
	}

	@Test(expected = ActionException.class)
	public void checkUnvalidParamTypeTest() throws ActionException {

		int param = 2;
		action.checkParams(param);
	}

	@Test(expected = ActionException.class)
	public void checkEmptyParamTypeTest() throws ActionException {

		BridgeDomain brDomain = new BridgeDomain();
		action.checkParams(brDomain);
	}

	@Test(expected = ActionException.class)
	public void checkNullParamTest() throws ActionException {

		action.checkParams(null);
	}

}
