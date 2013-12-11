package org.opennaas.extensions.ofertie.ncl.provisioner;

import java.util.ArrayList;
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
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IPathFinder;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IQoSPDP;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IRequestToFlowsLogic;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NCLProvisioner implements INCLProvisioner {

	private IQoSPDP								qoSPDP;
	private INetworkSelector					networkSelector;
	private IPathFinder							pathFinder;
	private INCLController						nclController;
	private IRequestToFlowsLogic				requestToFlowsLogic;

	/**
	 * Key: FlowId, Value: Flow
	 */
	private Map<String, Circuit>				allocatedCircuits;

	private Map<String, List<SDNNetworkOFFlow>>	allocatedFlows;

	public NCLProvisioner() {
		allocatedCircuits = new HashMap<String, Circuit>();
		allocatedFlows = new HashMap<String, List<SDNNetworkOFFlow>>();
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

	/**
	 * @return the pathFinder
	 */
	public IPathFinder getPathFinder() {
		return pathFinder;
	}

	/**
	 * @param pathFinder
	 *            the pathFinder to set
	 */
	public void setPathFinder(IPathFinder pathFinder) {
		this.pathFinder = pathFinder;
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

			String netId = getNetworkSelector().findNetworkForRequest(flowRequest);
			Route route = getPathFinder().findPathForRequest(flowRequest, netId);
			SDNNetworkOFFlow sdnFlow = getRequestToFlowsLogic().getRequiredFlowsToSatisfyRequest(flowRequest, route);
			String flowId = getNclController().allocateFlow(sdnFlow, netId);

			String circuitId = generateRandomFlowId();

			Circuit circuit = new Circuit();
			circuit.setFlowRequest(flowRequest);
			circuit.setId(circuitId);
			allocatedCircuits.put(circuitId, circuit);

			List<SDNNetworkOFFlow> flows = new ArrayList<SDNNetworkOFFlow>();
			flows.add(sdnFlow);

			allocatedFlows.put(circuitId, flows);

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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void deallocateFlow(String flowId) throws FlowNotFoundException,
			ProvisionerException {

		try {

			String netId = getNetworkSelector().findNetworkForFlowId(flowId);
			getNclController().deallocateFlow(flowId, netId);

			allocatedCircuits.remove(flowId);

		} catch (Exception e) {
			throw new ProvisionerException(e);
		}
	}

	@Override
	public Collection<Circuit> readAllocatedFlows()
			throws ProvisionerException {

		return allocatedCircuits.values();
	}

	private String generateRandomFlowId() {
		return UUID.randomUUID().toString();
	}

}
