package org.opennaas.extensions.genericnetwork.test.actionsets.internal.cicruitaggregation.actions;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.extensions.genericnetwork.actionsets.internal.circuitaggregation.actions.CalculateAggregatedCircuitsAction;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.helpers.GenericNetworkModelUtils;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CalculateAggregatedCircuitsActionTest {

	public static final String	SRC_BASE_IP24_1	= "192.168.1.";
	public static final String	DST_BASE_IP24_1	= "192.168.2.";
	public static final String	SRC_BASE_IP24_2	= "192.168.3.";
	public static final String	DST_BASE_IP24_2	= "192.168.4.";
	public static final String	TOS_1			= "4";
	public static final String	TOS_2			= "8";

	IAction						action;
	Set<Circuit>				notAggregated;

	@Before
	public void init() throws Exception {
		action = new CalculateAggregatedCircuitsAction();
		notAggregated = generateSampleCircuits();
	}

	@Test
	public void aggregationTest() throws ActionException {
		action.checkParams(notAggregated);
		action.setParams(notAggregated);
		ActionResponse response = action.execute(null);

		Assert.assertNotNull(response);
		Assert.assertEquals(STATUS.OK, response.getStatus());
		Assert.assertNotNull(response.getResult());

		Assert.assertTrue(response.getResult() instanceof Set<?>);
		for (Object obj : (Set<?>) response.getResult()) {
			Assert.assertTrue(obj instanceof Circuit);
		}
		@SuppressWarnings("unchecked")
		Set<Circuit> aggregated = (Set<Circuit>) response.getResult();

		Assert.assertEquals(3, aggregated.size());

		Circuit aggregatedWithIp1Tos1 = null;
		Circuit aggregatedWithIp2Tos1 = null;
		Circuit aggregatedWithIp2Tos2 = null;
		for (Circuit c : aggregated) {
			if (c.getTrafficFilter().getSrcIp().equals(SRC_BASE_IP24_1 + "0/24")
					&& c.getTrafficFilter().getDstIp().equals(DST_BASE_IP24_1 + "0/24")
					&& c.getTrafficFilter().getTosBits().equals(TOS_1)) {
				aggregatedWithIp1Tos1 = c;
			} else if (c.getTrafficFilter().getSrcIp().equals(SRC_BASE_IP24_2 + "0/24")
					&& c.getTrafficFilter().getDstIp().equals(DST_BASE_IP24_2 + "0/24")
					&& c.getTrafficFilter().getTosBits().equals(TOS_1)) {
				aggregatedWithIp2Tos1 = c;
			} else if (c.getTrafficFilter().getSrcIp().equals(SRC_BASE_IP24_2 + "0/24")
					&& c.getTrafficFilter().getDstIp().equals(DST_BASE_IP24_2 + "0/24")
					&& c.getTrafficFilter().getTosBits().equals(TOS_2)) {
				aggregatedWithIp2Tos2 = c;
			}
		}
		Assert.assertNotNull(aggregatedWithIp1Tos1);
		Assert.assertNotNull(aggregatedWithIp2Tos1);
		Assert.assertNotNull(aggregatedWithIp2Tos2);

		Assert.assertFalse(aggregatedWithIp1Tos1.equals(aggregatedWithIp2Tos1));
		Assert.assertFalse(aggregatedWithIp1Tos1.equals(aggregatedWithIp2Tos2));
		Assert.assertFalse(aggregatedWithIp2Tos1.equals(aggregatedWithIp2Tos2));

	}

	private Set<Circuit> generateSampleCircuits() throws Exception {

		Set<Circuit> circuits = new HashSet<Circuit>();

		Circuit circuit = GenericNetworkModelUtils.generateSampleCircuit();
		circuit.setCircuitId("ORIGINAL");
		circuit.getTrafficFilter().setSrcIp(SRC_BASE_IP24_1 + String.valueOf(255));
		circuit.getTrafficFilter().setDstIp(DST_BASE_IP24_1 + String.valueOf(255));
		circuit.getTrafficFilter().setTosBits(TOS_1);

		Circuit circuit1;

		// aggregation group
		for (int i = 0; i < 10; i++) {
			circuit1 = (Circuit) SerializationUtils.clone(circuit);
			circuit1.setCircuitId(String.valueOf(i));
			circuit1.getTrafficFilter().setSrcIp(SRC_BASE_IP24_1 + String.valueOf(i));
			circuit1.getTrafficFilter().setDstIp(DST_BASE_IP24_1 + String.valueOf(i + 100));
			circuits.add(circuit1);
		}

		// aggregation group using different ip/24
		for (int i = 10; i < 20; i++) {
			circuit1 = (Circuit) SerializationUtils.clone(circuit);
			circuit1.setCircuitId(String.valueOf(i));
			circuit1.getTrafficFilter().setSrcIp(SRC_BASE_IP24_2 + String.valueOf(i));
			circuit1.getTrafficFilter().setDstIp(DST_BASE_IP24_2 + String.valueOf(i + 100));
			circuits.add(circuit1);
		}

		// aggregation group using different ToS
		for (int i = 10; i < 20; i++) {
			circuit1 = (Circuit) SerializationUtils.clone(circuit);
			circuit1.setCircuitId(String.valueOf(i + 1000));
			circuit.getTrafficFilter().setTosBits(TOS_2);
			circuit1.getTrafficFilter().setSrcIp(SRC_BASE_IP24_2 + String.valueOf(i));
			circuit1.getTrafficFilter().setDstIp(DST_BASE_IP24_2 + String.valueOf(i + 100));
			circuits.add(circuit1);
		}

		return circuits;
	}
}
