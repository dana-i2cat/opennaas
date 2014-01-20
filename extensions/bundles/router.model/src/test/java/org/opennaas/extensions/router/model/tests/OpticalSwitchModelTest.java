package org.opennaas.extensions.router.model.tests;

/*
 * #%L
 * OpenNaaS :: CIM Model
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
import org.junit.Test;
import org.opennaas.extensions.router.model.opticalSwitch.ITUGrid;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;

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
