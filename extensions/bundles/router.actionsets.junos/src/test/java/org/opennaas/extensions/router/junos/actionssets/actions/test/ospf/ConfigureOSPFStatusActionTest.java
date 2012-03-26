package org.opennaas.extensions.router.junos.actionssets.actions.test.ospf;

import org.opennaas.extensions.router.junos.actionssets.actions.ospf.ConfigureOSPFStatusAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.OSPFService;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;

public class ConfigureOSPFStatusActionTest {

	@Test
	public void testPrepareMessage() throws ActionException {
		ConfigureOSPFStatusAction action1 = new ConfigureOSPFStatusAction();
		ConfigureOSPFStatusAction action2 = new ConfigureOSPFStatusAction();
		action1.setModelToUpdate(new ComputerSystem());
		action2.setModelToUpdate(new ComputerSystem());

		OSPFService ospf = new OSPFService();
		ospf.setEnabledState(EnabledState.ENABLED);
		action1.setParams(ospf);
		action1.prepareMessage();
		String enableMessage = action1.getVelocityMessage();

		Assert.assertNotNull(enableMessage);
		Assert.assertTrue(enableMessage.contains("<disable operation=\"delete\""));

		OSPFService ospfDisabed = new OSPFService();
		ospfDisabed.setEnabledState(EnabledState.DISABLED);
		action2.setParams(ospfDisabed);
		action2.prepareMessage();
		String disableMessage = action2.getVelocityMessage();

		Assert.assertNotNull(disableMessage);
		Assert.assertTrue(disableMessage.contains("<disable/>"));
	}

}
