package org.opennaas.extensions.openflowswitch.model.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.extensions.openflowswitch.helpers.OpenflowSwitchModelHelper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

public class OpenflowSwitchModelTest {

	@Test
	public void getSwitchForwardingRulesTest() {

		OpenflowSwitchModel model = OpenflowSwitchModelHelper.generateSampleModel();

		List<FloodlightOFFlow> originalRules = model.getOfTables().get(0).getOfForwardingRules();

		FloodlightOFFlow originalRule1 = originalRules.get(0);
		FloodlightOFFlow originalRule2 = originalRules.get(1);

		List<FloodlightOFFlow> forwardingRules = OpenflowSwitchModelHelper.getSwitchForwardingRules(model);

		Assert.assertEquals(2, forwardingRules.size());
		Assert.assertEquals(originalRules.size(), forwardingRules.size());

		FloodlightOFFlow rule1 = forwardingRules.get(0);
		FloodlightOFFlow rule2 = forwardingRules.get(1);

		Assert.assertEquals("1", rule1.getName());
		Assert.assertEquals(originalRule1, rule1);

		Assert.assertEquals("2", rule2.getName());
		Assert.assertEquals(originalRule2, rule2);

		Assert.assertTrue(rule1.equals(originalRule1));
		Assert.assertTrue(rule2.equals(originalRule2));

		Assert.assertEquals("1", originalRule1.getPriority());
		Assert.assertEquals("1", originalRule1.getMatch().getDstPort());

		Assert.assertEquals("2", originalRule2.getPriority());
		Assert.assertEquals("2", originalRule2.getMatch().getDstPort());

	}
}
