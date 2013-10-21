package org.opennaas.extensions.ofertie.ncl.provisioner;

import java.util.Collection;

import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationRejectedException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowNotFoundException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Flow;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IPathFinder;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IQoSPDP;
import org.opennaas.extensions.sdnnetwork.model.Route;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NCLProvisioner implements INCLProvisioner {

	private IQoSPDP				qoSPDP;
	private INetworkSelector	networkSelector;
	private IPathFinder			pathFinder;
	private INCLController		nclController;

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
			String flowId = getNclController().allocateFlow(flowRequest, route, netId);

			return flowId;

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

		} catch (Exception e) {
			throw new ProvisionerException(e);
		}
	}

	@Override
	public Collection<Flow> readAllocatedFlows()
			throws ProvisionerException {

		return getNclController().getFlows();
	}

}
