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

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.ConfigureOSPFStatusAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.RouteCalculationService.AlgorithmType;

public class ConfigureOSPFStatusActionTest {

	@Test
	public void testPrepareMessage() throws ActionException {
		ConfigureOSPFStatusAction action1 = new ConfigureOSPFStatusAction();
		ConfigureOSPFStatusAction action2 = new ConfigureOSPFStatusAction();
		action1.setModelToUpdate(new ComputerSystem());
		action2.setModelToUpdate(new ComputerSystem());

		OSPFService ospf = new OSPFService();
		ospf.setEnabledState(EnabledState.ENABLED);
		ospf.setAlgorithmType(AlgorithmType.OSPFV2);
		action1.setParams(ospf);
		action1.prepareMessage();
		String enableMessage = action1.getVelocityMessage();

		Assert.assertNotNull(enableMessage);
		Assert.assertTrue(enableMessage.contains("<disable operation=\"delete\""));

		OSPFService ospfDisabed = new OSPFService();
		ospfDisabed.setEnabledState(EnabledState.DISABLED);
		ospf.setAlgorithmType(AlgorithmType.OSPFV2);
		action2.setParams(ospfDisabed);
		action2.prepareMessage();
		String disableMessage = action2.getVelocityMessage();

		Assert.assertNotNull(disableMessage);
		Assert.assertTrue(disableMessage.contains("<disable/>"));
	}

}
