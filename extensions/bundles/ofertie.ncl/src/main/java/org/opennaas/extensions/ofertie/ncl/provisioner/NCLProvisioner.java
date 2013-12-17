package org.opennaas.extensions.ofertie.ncl.provisioner;

import java.util.Collection;
import java.util.HashMap;
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
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NCLProvisioner implements INCLProvisioner {

	private IQoSPDP							qoSPDP;
	private INetworkSelector				networkSelector;
	private INCLController					nclController;
	private IRequestToFlowsLogic			requestToFlowsLogic;

	/**
	 * Key: FlowId, Value: Flow
	 */
	private Map<String, Circuit>			allocatedCircuits;

	private Map<String, List<NetOFFlow>>	allocatedFlows;

	public NCLProvisioner() {
		allocatedCircuits = new HashMap<String, Circuit>();
		allocatedFlows = new HashMap<String, List<NetOFFlow>>();
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
	public String allocateFlow(FlowRequest flowRequest)
			throws FlowAllocationException, ProvisionerException {

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
			allocatedCircuits.put(circuitId, circuit);
			allocatedFlows.put(circuitId, sdnFlows);

			return circuitId;

		} catch (FlowAllocationException fae) {
			throw fae;
		} catch (Exception e) {
			throw new ProvisionerException(e);
		}
	}

	@Override
	public String updateFlow(String flowId, FlowRequest updatedFlowRequest)
			throws FlowAllocationException, FlowNotFoundException,
			ProvisionerException {

		deallocateFlow(flowId);
		String newFlowId = allocateFlow(updatedFlowRequest);

		// keep previous id for new circuit.
		Circuit circuit = allocatedCircuits.get(newFlowId);
		List<NetOFFlow> circuitFlows = allocatedFlows.get(newFlowId);

		allocatedCircuits.remove(newFlowId);
		allocatedFlows.remove(newFlowId);

		circuit.setId(flowId);
		allocatedCircuits.put(flowId, circuit);
		allocatedFlows.put(flowId, circuitFlows);

		return circuit.getId();
	}

	@Override
	public void deallocateFlow(String flowId) throws FlowNotFoundException,
			ProvisionerException {

		try {

			String netId = getNetworkSelector().findNetworkForFlowId(flowId);

			List<NetOFFlow> circuitFlows = allocatedFlows.get(flowId);
			getNclController().deallocateFlows(circuitFlows, netId);

			allocatedCircuits.remove(flowId);
			allocatedFlows.remove(flowId);

		} catch (Exception e) {
			throw new ProvisionerException(e);
		}
	}

	@Override
	public Collection<Circuit> readAllocatedFlows()
			throws ProvisionerException {

		return allocatedCircuits.values();
	}

	private String generateRandomCircuitId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public List<NetOFFlow> getFlowImplementation(String flowId) throws ProvisionerException {
		String circuitId = flowId;
		return allocatedFlows.get(circuitId);
	}

}
