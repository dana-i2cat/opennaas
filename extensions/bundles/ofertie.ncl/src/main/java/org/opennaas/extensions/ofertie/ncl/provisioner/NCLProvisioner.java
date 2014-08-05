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

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.INCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.exceptions.CircuitAllocationException;
import org.opennaas.extensions.genericnetwork.exceptions.NotExistingCircuitException;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequestWrapperParser;
import org.opennaas.extensions.ofertie.ncl.helpers.QosPolicyRequestParser;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationRejectedException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowNotFoundException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;
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

	private NCLModel			model;

	private final Object		mutex;

	public NCLProvisioner() {
		mutex = new Object();
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

			try {

				String userId = "alice";
				if (!getQoSPDP().shouldAcceptRequest(userId, qosPolicyRequest)) {
					throw new FlowAllocationRejectedException("Rejected by policy");
				}

				String netId = getNetworkSelector().getNetwork();

				IResource networkResource = getResource(netId);
				INCLProvisionerCapability nclProvCapab = (INCLProvisionerCapability) networkResource
						.getCapabilityByInterface(INCLProvisionerCapability.class);

				CircuitRequest circuitRequest = QosPolicyRequestParser.toCircuitRequest(qosPolicyRequest);

				// FIXME TO BE REMOVED: demo specific code.
				int vlan = readMatchVlan();
				if (vlan != -1) {
					circuitRequest.setVlan(String.valueOf(vlan));
				}

				String circuitId = nclProvCapab.allocateCircuit(circuitRequest);

				getAllocatedRequests().put(circuitId, circuitRequest);

				return circuitId;
			} catch (FlowAllocationException fae) {
				throw fae;
			} catch (CapabilityException ce) {
				throw new FlowAllocationException(ce);
			} catch (Exception e) {
				throw new ProvisionerException(e);
			}
		}
	}

	@Override
	public String updateFlow(String flowId, QosPolicyRequest updatedQosPolicyRequest) throws FlowAllocationException, FlowNotFoundException,
			ProvisionerException {
		synchronized (mutex) {

			try {

				String netId = getNetworkSelector().getNetwork();

				IResource networkResource = getResource(netId);
				INCLProvisionerCapability nclProvCapab = (INCLProvisionerCapability) networkResource
						.getCapabilityByInterface(INCLProvisionerCapability.class);

				CircuitRequest circuitRequest = QosPolicyRequestParser.toCircuitRequest(updatedQosPolicyRequest);

				// FIXME TO BE REMOVED: demo specific code.
				int vlan = readMatchVlan();
				if (vlan != -1) {
					circuitRequest.setVlan(String.valueOf(vlan));
				}

				nclProvCapab.updateCircuit(flowId, circuitRequest);

				getAllocatedRequests().put(flowId, circuitRequest);

				return flowId;
			} catch (CircuitAllocationException e) {
				throw new FlowAllocationException(e);
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
	public void deallocateFlow(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {
			try {

				String netId = getNetworkSelector().getNetwork();

				IResource networkResource = getResource(netId);
				INCLProvisionerCapability nclProvCapab = (INCLProvisionerCapability) networkResource
						.getCapabilityByInterface(INCLProvisionerCapability.class);

				nclProvCapab.deallocateCircuit(flowId);

				getAllocatedRequests().remove(flowId);

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
			updateFlow(flowId, qosPolicyRequest);
		}
	}

	@Override
	public void deleteLatency(String flowId) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setLatency(null);
			updateFlow(flowId, qosPolicyRequest);
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
			updateFlow(flowId, qosPolicyRequest);
		}
	}

	@Override
	public void deleteJitter(String flowId) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setJitter(null);
			updateFlow(flowId, qosPolicyRequest);
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
			updateFlow(flowId, qosPolicyRequest);
		}
	}

	@Override
	public void deleteThroughput(String flowId) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setThroughput(null);
			updateFlow(flowId, qosPolicyRequest);
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
			updateFlow(flowId, qosPolicyRequest);
		}
	}

	@Override
	public void deletePacketLoss(String flowId) throws FlowNotFoundException, ProvisionerException, FlowAllocationException {
		synchronized (mutex) {
			QosPolicyRequest qosPolicyRequest = getFlow(flowId);
			qosPolicyRequest.getQosPolicy().setPacketLoss(null);
			updateFlow(flowId, qosPolicyRequest);
		}
	}

	private IResource getResource(String networkId) throws ActivatorException, ResourceException {

		IResource resource = resourceManager.getResourceById(networkId);

		return resource;
	}

	private int readMatchVlan() throws IOException {
		// Gracefully ignore this feature in non OSGi environments (i.e. unit tests)
		if (Activator.getBundleContext() == null)
			return -1;

		ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getBundleContext());

		String readVlan = configurationAdmin.getProperty("org.ofertie.ncl.network", "circuits.match.vlan");
		if (StringUtils.isEmpty(readVlan))
			return -1;

		int vlan = -1;
		try {
			vlan = Integer.parseInt(readVlan);
		} catch (NumberFormatException e) {
			// ignored. This method will return -1
		}
		return vlan;
	}

}
