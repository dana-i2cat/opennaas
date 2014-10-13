package org.opennaas.extensions.genericnetwork.actionsets.internal.circuitaggregation.actions;

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
import java.util.UUID;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.genericnetwork.capability.circuitaggregation.CircuitAggregationActionSet;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CalculateAggregatedCircuitsAction extends Action {

	public CalculateAggregatedCircuitsAction() {
		super();
		actionID = CircuitAggregationActionSet.CALCULATE_AGGREGATED_CIRCUITS;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (!(params instanceof Set))
			throw new ActionException("Invalid parameters in action " + actionID + ". Expected: Set<Circuit>");

		for (Object obj : (Set<?>) params) {
			if (!(obj instanceof Circuit)) {
				throw new ActionException("Invalid parameters in action " + actionID + ". Expected: Set<Circuit>");
			}
		}

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		checkParams(params);
		// Checked in checkParams that params is Set<? extends Circuit>
		@SuppressWarnings("unchecked")
		Set<Circuit> notAggregated = (Set<Circuit>) params;
		Set<Circuit> aggregated = calculateAggregatedCircuits(notAggregated);

		ActionResponse response = ActionResponse.okResponse(actionID);
		response.setResult(aggregated);
		return response;
	}

	private Set<Circuit> calculateAggregatedCircuits(Set<Circuit> notAggregated) throws ActionException {
		return doAggregationByIp24(notAggregated);
	}

	private Set<Circuit> doNoAggregation(Set<Circuit> notAggregated) {
		Set<Circuit> aggregated = new HashSet<Circuit>();
		aggregated.addAll(notAggregated);
		return aggregated;
	}

	/**
	 * Circuit aggregation by IP/24. <br/>
	 * All circuits differing only in the last 8 bits of the source and destination IP address will be aggregated in one.
	 * 
	 * @param notAggregated
	 * @return aggregated circuits
	 * @throws ActionException
	 */
	private Set<Circuit> doAggregationByIp24(Set<Circuit> notAggregated) throws ActionException {

		Set<Circuit> aggregatedCircuits = new HashSet<Circuit>();
		for (Circuit candidate : notAggregated) {
			Circuit aggregated = computeAggregatedByIp24(candidate);
			// equal circuits will not be added
			aggregatedCircuits.add(aggregated);
		}

		// Assign circuit ids
		for (Circuit aggregated : aggregatedCircuits) {
			aggregated.setCircuitId("circuit-aggr-" + generateRandomCircuitId());
		}

		return aggregatedCircuits;
	}

	private Circuit computeAggregatedByIp24(Circuit candidate) throws ActionException {

		Circuit aggregated;
		try {
			aggregated = (Circuit) SerializationUtils.clone(candidate);
			aggregated.setCircuitId("TO_BE_DEFINED");
			aggregated.getTrafficFilter().setSrcIp(getIP24WithShortMask(candidate.getTrafficFilter().getSrcIp()));
			aggregated.getTrafficFilter().setDstIp(getIP24WithShortMask(candidate.getTrafficFilter().getDstIp()));
		} catch (SerializationException e) {
			throw new ActionException("Failed to compute aggregated flow for circuit " + candidate.getCircuitId(), e);
		}
		return aggregated;
	}

	private String getIP24WithShortMask(String ipAddress) {
		SubnetUtils utils = new SubnetUtils(ipAddress, "255.255.255.0");
		return utils.getInfo().getNetworkAddress() + "/24";
	}

	private static String generateRandomCircuitId() {
		return UUID.randomUUID().toString();
	}

}
