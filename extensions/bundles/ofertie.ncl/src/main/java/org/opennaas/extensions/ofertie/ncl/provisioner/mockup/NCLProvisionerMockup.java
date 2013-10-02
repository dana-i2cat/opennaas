package org.opennaas.extensions.ofertie.ncl.provisioner.mockup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowNotFoundException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Flow;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;

/**
 * Class provided for test purposes. TODO Remove from source folder.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NCLProvisionerMockup implements INCLProvisioner {

	private Map<String, Flow>	flows	= new HashMap<String, Flow>();

	private int					lastId	= 0;

	@Override
	public String allocateFlow(FlowRequest flowRequest)
			throws FlowAllocationException, ProvisionerException {

		Flow flow = new Flow();
		flow.setId(String.valueOf(++lastId));
		flow.setFlowRequest(flowRequest);
		flows.put(flow.getId(), flow);

		return flow.getId();
	}

	@Override
	public String updateFlow(String flowId, FlowRequest updatedFlowRequest)
			throws FlowAllocationException, FlowNotFoundException,
			ProvisionerException {

		flows.get(flowId).setFlowRequest(updatedFlowRequest);
		return flowId;
	}

	@Override
	public void deallocateFlow(String flowId) throws FlowNotFoundException,
			ProvisionerException {

		flows.remove(flowId);
	}

	@Override
	public Collection<Flow> readAllocatedFlows()
			throws ProvisionerException {

		return flows.values();
	}

}
