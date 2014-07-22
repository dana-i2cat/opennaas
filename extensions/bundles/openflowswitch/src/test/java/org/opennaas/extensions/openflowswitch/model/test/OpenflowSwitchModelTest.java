package org.opennaas.extensions.openflowswitch.model.test;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.openflowswitch.helpers.OpenflowSwitchModelHelper;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

public class OpenflowSwitchModelTest {

	@Test
	public void getSwitchForwardingRulesTest() {

		OpenflowSwitchModel model = OpenflowSwitchModelHelper.generateSampleModel();

		List<OFFlow> originalRules = model.getOfTables().get(0).getOfForwardingRules();

		OFFlow originalRule1 = originalRules.get(0);
		OFFlow originalRule2 = originalRules.get(1);

		List<OFFlow> forwardingRules = OpenflowSwitchModelHelper.getSwitchForwardingRules(model);

		Assert.assertEquals(2, forwardingRules.size());
		Assert.assertEquals(originalRules.size(), forwardingRules.size());

		OFFlow rule1 = forwardingRules.get(0);
		OFFlow rule2 = forwardingRules.get(1);

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

	@Test
	public void modelSerializerTest() throws SerializationException {

		OpenflowSwitchModel originalModel = OpenflowSwitchModelHelper.generateSampleModel();
		String xmlModel = originalModel.toXml();

		OpenflowSwitchModel loadedModel = (OpenflowSwitchModel) ObjectSerializer.fromXml(xmlModel, OpenflowSwitchModel.class);
		String xmlLoadedModel = loadedModel.toXml();

		Assert.assertEquals(originalModel, loadedModel);
		Assert.assertEquals(xmlModel, xmlLoadedModel);

	}
}
