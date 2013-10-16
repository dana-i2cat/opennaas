package org.opennaas.extensions.sdnnetwork.model;

import org.opennaas.core.resources.ModelElementNotFoundException;

public class SDNNetworkModelHelper {

	public static SDNNetworkOFFlow getFlowFromModelByName(String flowName, SDNNetworkModel model) throws ModelElementNotFoundException {

		for (SDNNetworkOFFlow flow : model.getFlows()) {
			if (flow.getName().equals(flowName))
				return flow;
		}
		throw new ModelElementNotFoundException("Flow not found in model. " + flowName);
	}

}
