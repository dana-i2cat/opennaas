package org.opennaas.extensions.ofertie.ncl.provisioner;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.security.filters.SecurityContextPersistenceFilterSkipClearContext;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.INCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.exceptions.CircuitAllocationException;
import org.opennaas.extensions.genericnetwork.exceptions.NotExistingCircuitException;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequestHelper;
import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequestWrapperParser;
import org.opennaas.extensions.ofertie.ncl.helpers.QosPolicyRequestParser;
import org.opennaas.extensions.ofertie.ncl.notification.INCLNotifierClient;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationRejectedException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowNotFoundException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.ClientManager;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IQoSPDP;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class NCLProvisioner implements INCLProvisioner {

	private IQoSPDP				qoSPDP;
	private INetworkSelector	networkSelector;
	private IResourceManager	resourceManager;
	private ClientManager		clientManager;

	private NCLModel			model;

	private final Object		mutex;

	public NCLProvisioner() {
		mutex = new Object();
		clientManager = new ClientManager();

	}

	public NCLModel getModel() {
		return model;
	}

	public void setModel(NCLModel model) {
		this.model = model;
	}

	private Log	log	= LogFactory.getLog(NCLProvisioner.class);

	public Map<String, CircuitRequest> getAllocatedRequests() {
		return model.getAllocatedRequests();
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

	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	// ///////////////////////////
	// INCLProvisioner Methods //
	// ///////////////////////////

	@Override
	public String allocateFlow(QosPolicyRequest qosPolicyRequest) throws ProvisionerException, FlowAllocationException {
		synchronized (mutex) {

			// get username from threadlocal and removes it
			String username = SecurityContextPersistenceFilterSkipClearContext.get();
			SecurityContextPersistenceFilterSkipClearContext.unset();

			INCLNotifierClient sdnClient = clientManager.getClient(username);
			try {

				if (!getQoSPDP().shouldAcceptRequest(username, qosPolicyRequest)) {
					throw new FlowAllocationRejectedException("Rejected by policy");
				}

				String netId = getNetworkSelector().getNetwork();

				IResource networkResource = getResource(netId);
				INCLProvisionerCapability nclProvCapab = (INCLProvisionerCapability) networkResource
						.getCapabilityByInterface(INCLProvisionerCapability.class);

				CircuitRequest circuitRequest = QosPolicyRequestParser.toCircuitRequest(qosPolicyRequest);
				String circuitId = nclProvCapab.allocateCircuit(circuitRequest);

				getAllocatedRequests().put(circuitId, circuitRequest);

				log.debug("Notifiying SDN Module that flow " + circuitId + " has been allocated.");

				sdnClient.qosPolicyAllocated(circuitId, qosPolicyRequest);

				return circuitId;
			} catch (FlowAllocationException fae) {
				// FIXME for the moment we return "unknown" as circuit id, since the circuit id is not yet created when the allocation fails!
				log.debug("Notifiying SDN Module that flow could not be allocated.");
				sdnClient.qosPolicyAllocationFailed("UNKNOWN", qosPolicyRequest, fae);
				throw fae;
			} catch (CapabilityException ce) {
				log.debug("Notifiying SDN Module that flow could not be allocated.");
				sdnClient.qosPolicyAllocationFailed("UNKNOWN", qosPolicyRequest, ce);
				throw new FlowAllocationException(ce);
			} catch (Exception e) {
				log.debug("Notifiying SDN Module that flow could not be allocated.");
				sdnClient.qosPolicyAllocationFailed("UNKNOWN", qosPolicyRequest, e);
				throw new ProvisionerException(e);
			}
		}
	}

	@Override
	public String updateFlow(String flowId, QosPolicy updatedQosPolicy) throws FlowAllocationException, FlowNotFoundException,
			ProvisionerException {
		synchronized (mutex) {

			// get username from threadlocal and removes it
			String username = SecurityContextPersistenceFilterSkipClearContext.get();
			SecurityContextPersistenceFilterSkipClearContext.unset();

			INCLNotifierClient sdnClient = clientManager.getClient(username);

			QosPolicyRequest qosPolicyRequest = null;
			try {

				qosPolicyRequest = getFlow(flowId);

			} catch (FlowNotFoundException e) {
				log.debug("Notifiying SDN Module that flow could not be allocated.");
				sdnClient.qosPolicyAllocationFailed(flowId, qosPolicyRequest, e);
				throw new FlowNotFoundException(e);
			}

			try {

				QosPolicyRequest oldQosPolicyRequest = QoSPolicyRequestHelper.cloneQosPolicyRequest(qosPolicyRequest);

				QosPolicy mergedQos = QoSPolicyRequestHelper.mergeQosPolicies(qosPolicyRequest.getQosPolicy(), updatedQosPolicy);

				qosPolicyRequest.setQosPolicy(mergedQos);

				String netId = getNetworkSelector().getNetwork();

				IResource networkResource = getResource(netId);
				INCLProvisionerCapability nclProvCapab = (INCLProvisionerCapability) networkResource
						.getCapabilityByInterface(INCLProvisionerCapability.class);

				CircuitRequest circuitRequest = QosPolicyRequestParser.toCircuitRequest(qosPolicyRequest);

				nclProvCapab.updateCircuit(flowId, circuitRequest);

				getAllocatedRequests().put(flowId, circuitRequest);

				log.debug("Notifiying SDN Module that flow " + flowId + " has been allocated.");

				// first we notify remove of the old qosPolicyRequest
				sdnClient.qosPolicyDeallocated(flowId, oldQosPolicyRequest);
				// notify the allocation of the new qosPolicyRequest with same flow id.
				sdnClient.qosPolicyAllocated(flowId, qosPolicyRequest);

				return flowId;
			} catch (CircuitAllocationException e) {
				log.debug("Notifiying SDN Module that flow could not be allocated.");
				sdnClient.qosPolicyAllocationFailed(flowId, qosPolicyRequest, e);
				throw new FlowAllocationException(e);

			} catch (ResourceException e) {
				log.debug("Notifiying SDN Module that flow could not be allocated.");
				sdnClient.qosPolicyAllocationFailed(flowId, qosPolicyRequest, e);
				throw new ProvisionerException(e);
			} catch (Exception e) {
				log.debug("Notifiying SDN Module that flow could not be allocated.");
				sdnClient.qosPolicyAllocationFailed(flowId, qosPolicyRequest, e);
				throw new FlowAllocationException(e);

			}
		}
	}

	@Override
	public void deallocateFlow(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {

			// get username from threadlocal and removes it
			String username = SecurityContextPersistenceFilterSkipClearContext.get();
			SecurityContextPersistenceFilterSkipClearContext.unset();

			INCLNotifierClient sdnClient = clientManager.getClient(username);

			try {

				String netId = getNetworkSelector().getNetwork();

				IResource networkResource = getResource(netId);
				INCLProvisionerCapability nclProvCapab = (INCLProvisionerCapability) networkResource
						.getCapabilityByInterface(INCLProvisionerCapability.class);

				nclProvCapab.deallocateCircuit(flowId);

				QosPolicyRequest requestRemoved = QosPolicyRequestParser.fromCircuitRequest((getAllocatedRequests().get(flowId)));

				getAllocatedRequests().remove(flowId);

				log.debug("Notifiying SDN Module flow " + flowId + " has been deallocated.");

				sdnClient.qosPolicyDeallocated(flowId, requestRemoved);

			} catch (NotExistingCircuitException e) {
				throw new FlowNotFoundException(e);
			} catch (ResourceException e) {
				throw new ProvisionerException(e);
			} catch (Exception e) {
				throw new ProvisionerException(e);
			}
		}
	}

	@Override
	public QoSPolicyRequestsWrapper readAllocatedFlows() throws ProvisionerException {

		synchronized (mutex) {
			try {
				QoSPolicyRequestsWrapper wrapper;

				Map<String, CircuitRequest> allocatedRequests = getAllocatedRequests();

				wrapper = QoSPolicyRequestWrapperParser.fromCircuitRequests(allocatedRequests);

				return wrapper;

			} catch (Exception e) {
				throw new ProvisionerException(e);
			}
		}

	}

	public QosPolicyRequest getFlow(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {
			if (getAllocatedRequests().containsKey(flowId)) {
				return QosPolicyRequestParser.fromCircuitRequest(getAllocatedRequests().get(flowId));
			}
			throw new FlowNotFoundException();
		}
	}

	// ////////////////////
	// Parameter Methods //
	// ////////////////////

	@Override
	public Latency getLatency(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			return qosPolicyRequest.getQosPolicy().getLatency();
		}
	}

	@Override
	public void updateLatency(String flowId, Latency latency) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setLatency(latency);
			updateFlow(flowId, qosPolicyRequest.getQosPolicy());
		}
	}

	@Override
	public void deleteLatency(String flowId) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setLatency(null);
			updateFlow(flowId, qosPolicyRequest.getQosPolicy());
		}
	}

	@Override
	public Jitter getJitter(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			return qosPolicyRequest.getQosPolicy().getJitter();
		}
	}

	@Override
	public void updateJitter(String flowId, Jitter jitter) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setJitter(jitter);
			updateFlow(flowId, qosPolicyRequest.getQosPolicy());
		}
	}

	@Override
	public void deleteJitter(String flowId) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setJitter(null);
			updateFlow(flowId, qosPolicyRequest.getQosPolicy());
		}
	}

	@Override
	public Throughput getThroughput(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			return qosPolicyRequest.getQosPolicy().getThroughput();
		}
	}

	@Override
	public void updateThroughput(String flowId, Throughput throughput) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setThroughput(throughput);
			updateFlow(flowId, qosPolicyRequest.getQosPolicy());
		}
	}

	@Override
	public void deleteThroughput(String flowId) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setThroughput(null);
			updateFlow(flowId, qosPolicyRequest.getQosPolicy());
		}
	}

	@Override
	public PacketLoss getPacketLoss(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			return qosPolicyRequest.getQosPolicy().getPacketLoss();
		}
	}

	@Override
	public void updatePacketLoss(String flowId, PacketLoss packetLoss) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setPacketLoss(packetLoss);
			updateFlow(flowId, qosPolicyRequest.getQosPolicy());
		}
	}

	@Override
	public void deletePacketLoss(String flowId) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setPacketLoss(null);
			updateFlow(flowId, qosPolicyRequest.getQosPolicy());
		}
	}

	private IResource getResource(String networkId) throws ActivatorException, ResourceException {

		IResource resource = resourceManager.getResourceById(networkId);

		return resource;
	}

}
