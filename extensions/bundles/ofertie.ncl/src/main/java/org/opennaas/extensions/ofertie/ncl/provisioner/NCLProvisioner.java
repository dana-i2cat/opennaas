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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.GenericListWrapper;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.helpers.NCLModelHelper;
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
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IRequestToFlowsLogic;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.opennaas.extensions.ofnetwork.events.LinkCongestionEvent;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class NCLProvisioner implements INCLProvisioner, EventHandler {

	private final static String		NCL_CONFIG_FILE	= "org.opennaas.extensions.ofertie.ncl";
	private final static String		AUTOREROUTE_KEY	= "ncl.autoreroute";

	private IQoSPDP					qoSPDP;
	private INetworkSelector		networkSelector;
	private INCLController			nclController;
	private IRequestToFlowsLogic	requestToFlowsLogic;

	private boolean					autoReroute;

	private NCLModel				model;

	private final Object			mutex;

	public NCLProvisioner() {
		mutex = new Object();
	}

	public NCLModel getModel() {
		return model;
	}

	public void setModel(NCLModel model) {
		this.model = model;
	}

	private ServiceRegistration	eventListenerRegistration;

	private Log					log	= LogFactory.getLog(NCLProvisioner.class);

	public Map<String, QosPolicyRequest> getAllocatedQoSPolicyRequests() {
		return model.getAllocatedQoSPolicyRequests();
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
	public String allocateFlow(QosPolicyRequest qosPolicyRequest) throws FlowAllocationException, ProvisionerException {
		synchronized (mutex) {
			try {

				String userId = "alice";
				if (!getQoSPDP().shouldAcceptRequest(userId, qosPolicyRequest)) {
					throw new FlowAllocationRejectedException("Rejected by policy");
				}

				List<NetOFFlow> sdnFlows = getRequestToFlowsLogic().getRequiredFlowsToSatisfyRequest(qosPolicyRequest);

				String netId = getNetworkSelector().findNetworkForRequest(qosPolicyRequest);
				getNclController().allocateFlows(sdnFlows, netId);

				String qosPolicyRequestId = generateRandomQoSPolicyRequestId();

				getAllocatedQoSPolicyRequests().put(qosPolicyRequestId, qosPolicyRequest);
				getAllocatedFlows().put(qosPolicyRequestId, sdnFlows);

				return qosPolicyRequestId;

			} catch (FlowAllocationException fae) {
				throw fae;
			} catch (Exception e) {
				throw new ProvisionerException(e);
			}
		}
	}

	@Override
	public String updateFlow(String flowId, QosPolicyRequest updatedQosPolicyRequest) throws FlowAllocationException, FlowNotFoundException,
			ProvisionerException {
		synchronized (mutex) {
			deallocateFlow(flowId);
			String newFlowId = allocateFlow(updatedQosPolicyRequest);

			// keep previous id for new QoSPolicyRequest.
			QosPolicyRequest qosPolicyRequest = getAllocatedQoSPolicyRequests().get(newFlowId);
			List<NetOFFlow> qosPolicyRequestFlows = getAllocatedFlows().get(newFlowId);

			getAllocatedQoSPolicyRequests().remove(newFlowId);
			getAllocatedFlows().remove(newFlowId);

			getAllocatedQoSPolicyRequests().put(flowId, qosPolicyRequest);
			getAllocatedFlows().put(flowId, qosPolicyRequestFlows);

			return flowId;
		}
	}

	@Override
	public void deallocateFlow(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {
			try {

				String netId = getNetworkSelector().findNetworkForFlowId(flowId);

				List<NetOFFlow> qosPolicyRequestFlows = getAllocatedFlows().get(flowId);
				getNclController().deallocateFlows(qosPolicyRequestFlows, netId);

				getAllocatedQoSPolicyRequests().remove(flowId);
				getAllocatedFlows().remove(flowId);

			} catch (Exception e) {
				throw new ProvisionerException(e);
			}
		}
	}

	@Override
	public QoSPolicyRequestsWrapper readAllocatedFlows() throws ProvisionerException {
		synchronized (mutex) {
			return new QoSPolicyRequestsWrapper(getAllocatedQoSPolicyRequests());
		}
	}

	private String generateRandomQoSPolicyRequestId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public GenericListWrapper<NetOFFlow> getFlowImplementation(String flowId) throws ProvisionerException {
		synchronized (mutex) {
			String qosPolicyRequestId = flowId;
			return new GenericListWrapper<NetOFFlow>(getAllocatedFlows().get(qosPolicyRequestId));
		}
	}

	public QosPolicyRequest getFlow(String flowId) throws FlowNotFoundException, ProvisionerException {
		synchronized (mutex) {
			if (getAllocatedQoSPolicyRequests().containsKey(flowId)) {
				return getAllocatedQoSPolicyRequests().get(flowId);
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

	// ////////////////////////
	// EventListener Methods //
	// ////////////////////////

	@Override
	public void handleEvent(Event event) {
		if (event instanceof LinkCongestionEvent) {

			LinkCongestionEvent congestionEvent = (LinkCongestionEvent) event;
			String switchName = (String) congestionEvent.getProperty(LinkCongestionEvent.SWITCH_ID_KEY);
			String portId = (String) congestionEvent.getProperty(LinkCongestionEvent.PORT_ID_KEY);

			log.info("LinkCongestion alarm received for switch " + switchName + " and port " + portId);

			boolean autoReroute = readAutorerouteOption();
			if (autoReroute) {
				log.debug("Auto-reroute is activated. Launching auto-reroute");
				try {
					launchRerouteMechanism(switchName, portId);
				} catch (Exception e) {
					log.error(e.getMessage());
					// TODO can not throw exception, since EventHandler interface does not allow it.
				}
			} else {
				log.debug("Auto-reroute is deactivated. Ignoring received LinkCongestion alarm. ");
			}
		}
		else
			log.debug("Ignoring non-LinkCongestion alarm.");

	}

	private void launchRerouteMechanism(String switchName, String portId) throws Exception {
		synchronized (mutex) {
			String qosPolicyRequestId = selectQoSPolicyRequestToReallocate(switchName, portId);

			if (qosPolicyRequestId == null) {
				log.info("No QoSPolicyRequests allocated for this port. Ignoring alarm.");
				return;
			}

			try {
				rerouteQoSPolicyRequest(qosPolicyRequestId);
			} catch (Exception e) {
				throw new Exception("Could not reallocate QoSPolicyRequest " + qosPolicyRequestId + ": " + e.getMessage(), e);
			}
		}
	}

	public void unregisterListener() {

		log.debug("Unregistering NCLProvisiner as listener for LinkCongestion events.");
		eventListenerRegistration.unregister();
		log.debug("NCLProvisioner successfully unregistered.");

	}

	public void registerAsCongestionEventListener() throws IOException {

		log.debug("Registering NCLProvisiner as listener for LinkCongestion events.");

		Properties properties = new Properties();
		properties.put(EventConstants.EVENT_TOPIC, LinkCongestionEvent.TOPIC);

		eventListenerRegistration = Activator.getContext().registerService(EventHandler.class.getName(), this, properties);

		log.debug("NCLProvisioner successfully registered as listener for LinkCongestion events.");

	}

	private void rerouteQoSPolicyRequest(String qosPolicyRequestId) throws Exception {

		log.debug("Start of rerouteQoSPolicyRequest call.");

		QosPolicyRequest qosPolicyRequest = getAllocatedQoSPolicyRequests().get(qosPolicyRequestId);

		if (qosPolicyRequest == null)
			throw new ProvisionerException("QosPolicyRequest is not allocated.");

		List<NetOFFlow> sdnFlows = getRequestToFlowsLogic().getRequiredFlowsToSatisfyRequest(qosPolicyRequest);
		List<NetOFFlow> oldFlows = getAllocatedFlows().get(qosPolicyRequestId);

		String netId = getNetworkSelector().findNetworkForRequest(qosPolicyRequest);

		getNclController().replaceFlows(oldFlows, sdnFlows, netId);

		getAllocatedFlows().put(qosPolicyRequestId, sdnFlows);

		log.debug("End of rerouteQoSPolicyRequest call.");

	}

	private String selectQoSPolicyRequestToReallocate(String switchName, String portId) {

		List<String> qosPolicyRequestIdsInPort = getAllQoSPolicyRequestIdsInPort(switchName, portId);

		if (qosPolicyRequestIdsInPort.isEmpty()) {
			return null;
		}
		// TODO select in a more intelligent way, for example, based on ToS, flowCapacity, etc.
		return qosPolicyRequestIdsInPort.get(0);
	}

	/**
	 * Retrieves all QoSPolicyRequests allocated in a specific switch port.
	 * 
	 * @param switchName
	 * @param portId
	 * @return
	 */
	private List<String> getAllQoSPolicyRequestIdsInPort(String switchName, String portId) {

		List<String> qosPolicyRequestIdsInPort = new ArrayList<String>();

		for (String circuiId : getAllocatedQoSPolicyRequests().keySet()) {

			List<NetOFFlow> flows = getAllocatedFlows().get(circuiId);
			if (NCLModelHelper.flowsContainPort(switchName, portId, flows)) {
				qosPolicyRequestIdsInPort.add(circuiId);
			}
		}

		return qosPolicyRequestIdsInPort;
	}

	private boolean readAutorerouteOption() {
		boolean autoReroute = false;
		try {
			autoReroute = doReadAutorerouteOption();
		} catch (IOException ioe) {
			log.error("Failed to read auto-reroute option. ", ioe);
			log.warn("Deactivating auto-reroute");
			autoReroute = false;
		}

		return autoReroute;
	}

	private boolean doReadAutorerouteOption() throws IOException {

		Properties properties = ConfigurationAdminUtil.getProperties(Activator.getContext(), NCL_CONFIG_FILE);
		if (properties == null)
			throw new IOException("Failed to determine auto-reroute option. " + "Unable to obtain configuration " + NCL_CONFIG_FILE);

		String value = properties.getProperty(AUTOREROUTE_KEY);
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

}
