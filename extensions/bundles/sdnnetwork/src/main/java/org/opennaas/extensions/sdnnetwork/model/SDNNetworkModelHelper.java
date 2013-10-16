package org.opennaas.extensions.sdnnetwork.model;

import org.opennaas.core.resources.ModelElementNotFoundException;

/**
 * A helper containing useful static methods to manipulate the SDNNetworkModel.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SDNNetworkModelHelper {

	public static SDNNetworkOFFlow getFlowFromModelByName(String flowName, SDNNetworkModel model) throws ModelElementNotFoundException {

		for (SDNNetworkOFFlow flow : model.getFlows()) {
			if (flow.getName().equals(flowName))
				return flow;
		}
		throw new ModelElementNotFoundException("Flow not found in model. " + flowName);
	}

}
