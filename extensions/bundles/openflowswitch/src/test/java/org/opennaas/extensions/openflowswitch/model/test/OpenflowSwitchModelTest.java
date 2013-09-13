package org.opennaas.extensions.openflowswitch.model.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.extensions.openflowswitch.helpers.OpenflowSwitchModelHelper;
import org.opennaas.extensions.openflowswitch.model.OFForwardingRule;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

public class OpenflowSwitchModelTest {

	@Test
	public void getSwitchForwardingRulesTest() {

		OpenflowSwitchModel model = OpenflowSwitchModelHelper.generateSampleModel();

		List<OFForwardingRule> originalRules = model.getOfTables().get(0).getOfForwardingRules();

		OFForwardingRule originalRule1 = originalRules.get(0);
		OFForwardingRule originalRule2 = originalRules.get(1);

		List<OFForwardingRule> forwardingRules = OpenflowSwitchModelHelper.getSwitchForwardingRules(model);

		Assert.assertEquals(2, forwardingRules.size());
		Assert.assertEquals(originalRules.size(), forwardingRules.size());

		OFForwardingRule rule1 = forwardingRules.get(0);
		OFForwardingRule rule2 = forwardingRules.get(1);

		Assert.assertEquals("1", rule1.getFlowId());
		Assert.assertEquals(originalRule1, rule1);

		Assert.assertEquals("2", rule2.getFlowId());
		Assert.assertEquals(originalRule2, rule2);

		Assert.assertEquals(rule1.getOfFlowMod().getPriority(), originalRule1.getOfFlowMod().getPriority());
		Assert.assertTrue(1 == originalRule1.getOfFlowMod().getPriority());
		Assert.assertEquals(rule1.getOfFlowMod().getOutPort(), originalRule1.getOfFlowMod().getOutPort());
		Assert.assertTrue(1 == originalRule1.getOfFlowMod().getOutPort());

		Assert.assertEquals(rule2.getOfFlowMod().getPriority(), originalRule2.getOfFlowMod().getPriority());
		Assert.assertTrue(2 == originalRule2.getOfFlowMod().getPriority());
		Assert.assertEquals(rule2.getOfFlowMod().getOutPort(), originalRule2.getOfFlowMod().getOutPort());
		Assert.assertTrue(2 == originalRule2.getOfFlowMod().getOutPort());

	}
}
