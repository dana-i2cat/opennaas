package org.opennaas.extensions.router.model.tests;

import junit.framework.Assert;
import org.opennaas.extensions.router.model.opticalSwitch.ITUGrid;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class OpticalSwitchModelTest {

	Log	logger	= LogFactory.getLog(OpticalSwitchModelTest.class);

	@Test
	public void testFibreChannelPlan() {

		WDMChannelPlan channelPlan = new WDMChannelPlan();
		Assert.assertTrue(channelPlan.getAllChannels().size() > 0);
		Assert.assertTrue(channelPlan.getITUGrid().getNumberOfChannels() == channelPlan.getAllChannels().size());
		Assert.assertTrue(channelPlan.getITUGrid().getInitialFreq() == channelPlan.getMaxFreq());
		Assert.assertTrue(channelPlan.getITUGrid().getFinalFreq() == channelPlan.getMinFreq());

		// last channel is not included
		WDMChannelPlan channelPlan1 = new WDMChannelPlan(channelPlan.getFrequency(32), channelPlan.getFrequency(392), 0.1, new ITUGrid());
		Assert.assertTrue(channelPlan1.getFirstChannel() == 32);
		Assert.assertTrue(channelPlan1.getLastChannel() == 392);
		Assert.assertTrue(channelPlan1.getAllChannels().size() == (392 - 32) / 8);

	}

}
