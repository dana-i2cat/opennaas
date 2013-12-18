package org.opennaas.extensions.ofertie.ncl.provisioner;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationRejectedException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowNotFoundException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Circuit;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IQoSPDP;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IRequestToFlowsLogic;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class NCLProvisioner implements INCLProvisioner {

	private IQoSPDP					qoSPDP;
	private INetworkSelector		networkSelector;
	private INCLController			nclController;
	private IRequestToFlowsLogic	requestToFlowsLogic;

	private NCLModel				model;

	public NCLModel getModel() {
		return model;
	}

	public void setModel(NCLModel model) {
		this.model = model;
	}

	/**
	 * @return the allocatedCircuits
	 */
	public Map<String, Circuit> getAllocatedCircuits() {
		return model.getAllocatedCircuits();
	}

	/**
	 * @return the allocatedFlows
	 */
	public Map<String, List<NetOFFlow>> getAllocatedFlows() {
		return model.getAllocatedFlows();
	}

	/**
	 * @return the qoSPDP
	 */
	public IQoSPDP getQoSPDP() {
		return qoSPDP;
	}

	/**
	 * @param qoSPDP
	 *            the qoSPDP to set
	 */
	public void setQoSPDP(IQoSPDP qoSPDP) {
		this.qoSPDP = qoSPDP;
	}

	/**
	 * @return the networkSelector
	 */
	public INetworkSelector getNetworkSelector() {
		return networkSelector;
	}

	/**
	 * @param networkSelector
	 *            the networkSelector to set
	 */
	public void setNetworkSelector(INetworkSelector networkSelector) {
		this.networkSelector = networkSelector;
	}

	public INCLController getNclController() {
		return nclController;
	}

	public void setNclController(INCLController nclController) {
		this.nclController = nclController;
	}

	public IRequestToFlowsLogic getRequestToFlowsLogic() {
		return requestToFlowsLogic;
	}

	public void setRequestToFlowsLogic(IRequestToFlowsLogic requestToFlowsLogic) {
		this.requestToFlowsLogic = requestToFlowsLogic;
	}

	// ///////////////////////////
	// INCLProvisioner Methods //
	// ///////////////////////////

	@Override
	public String allocateFlow(FlowRequest flowRequest) throws FlowAllocationException, ProvisionerException {

		try {

			String userId = "alice";
			if (!getQoSPDP().shouldAcceptRequest(userId, flowRequest)) {
				throw new FlowAllocationRejectedException("Rejected by policy");
			}

			List<NetOFFlow> sdnFlows = getRequestToFlowsLogic().getRequiredFlowsToSatisfyRequest(flowRequest);

			String netId = getNetworkSelector().findNetworkForRequest(flowRequest);
			getNclController().allocateFlows(sdnFlows, netId);

			String circuitId = generateRandomCircuitId();

			Circuit circuit = new Circuit();
			circuit.setFlowRequest(flowRequest);
			circuit.setId(circuitId);
			getAllocatedCircuits().put(circuitId, circuit);
			getAllocatedFlows().put(circuitId, sdnFlows);

			return circuitId;

		} catch (FlowAllocationException fae) {
			throw fae;
		} catch (Exception e) {
			throw new ProvisionerException(e);
		}
	}

	@Override
	public String updateFlow(String flowId, FlowRequest updatedFlowRequest) throws FlowAllocationException, FlowNotFoundException,
			ProvisionerException {

		deallocateFlow(flowId);
		String newFlowId = allocateFlow(updatedFlowRequest);

		// keep previous id for new circuit.
		Circuit circuit = getAllocatedCircuits().get(newFlowId);
		List<NetOFFlow> circuitFlows = getAllocatedFlows().get(newFlowId);

		getAllocatedCircuits().remove(newFlowId);
		getAllocatedFlows().remove(newFlowId);

		circuit.setId(flowId);
		getAllocatedCircuits().put(flowId, circuit);
		getAllocatedFlows().put(flowId, circuitFlows);

		return circuit.getId();
	}

	@Override
	public void deallocateFlow(String flowId) throws FlowNotFoundException, ProvisionerException {

		try {

			String netId = getNetworkSelector().findNetworkForFlowId(flowId);

			List<NetOFFlow> circuitFlows = getAllocatedFlows().get(flowId);
			getNclController().deallocateFlows(circuitFlows, netId);

			getAllocatedCircuits().remove(flowId);
			getAllocatedFlows().remove(flowId);

		} catch (Exception e) {
			throw new ProvisionerException(e);
		}
	}

	@Override
	public Collection<Circuit> readAllocatedFlows() throws ProvisionerException {

		return getAllocatedCircuits().values();
	}

	private String generateRandomCircuitId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public List<NetOFFlow> getFlowImplementation(String flowId) throws ProvisionerException {
		String circuitId = flowId;
		return getAllocatedFlows().get(circuitId);
	}

	public Circuit getFlow(String flowId) throws FlowNotFoundException, ProvisionerException {
		if (getAllocatedCircuits().containsKey(flowId)) {
			return getAllocatedCircuits().get(flowId);
		}
		throw new FlowNotFoundException();
	}

	@Override
	public int getQoSParameter(String flowId, String parameter) throws FlowNotFoundException, ProvisionerException {
		Circuit circuit = getFlow(flowId);
		try {
			return circuit.getFlowRequest().getQoSRequirements().getParameter(parameter);
		} catch (IllegalArgumentException e) {
			throw new ProvisionerException(e);
		}
	}

	@Override
	public void updateQoSParameter(String flowId, String parameter, int value) throws FlowNotFoundException, ProvisionerException,
			FlowAllocationException {
		FlowRequest flowRequest = getFlow(flowId).getFlowRequest();

		try {
			flowRequest.getQoSRequirements().setParameter(parameter, value);
		} catch (IllegalArgumentException e) {
			throw new ProvisionerException(e);
		}

		updateFlow(flowId, flowRequest);
	}

	@Override
	public void deleteQoSParameter(String flowId, String parameter) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		FlowRequest flowRequest = getFlow(flowId).getFlowRequest();

		try {
			flowRequest.getQoSRequirements().setParameter(parameter, -1);
		} catch (IllegalArgumentException e) {
			throw new ProvisionerException(e);
		}

		updateFlow(flowId, flowRequest);
	}

}
