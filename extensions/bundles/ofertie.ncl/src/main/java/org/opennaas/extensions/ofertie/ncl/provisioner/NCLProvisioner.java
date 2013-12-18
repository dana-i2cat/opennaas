package org.opennaas.extensions.ofertie.ncl.provisioner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.helpers.NCLModelHelper;
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

	private final static String		NCL_CONFIG_FILE	= "org.opennaas.extensions.ofertie.ncl.cfg";
	private final static String		AUTOREROUTE_KEY	= "ncl.autoreroute";

	private IQoSPDP					qoSPDP;
	private INetworkSelector		networkSelector;
	private INCLController			nclController;
	private IRequestToFlowsLogic	requestToFlowsLogic;

	private boolean					autoReroute;

	private NCLModel				model;

	public NCLModel getModel() {
		return model;
	}

	public void setModel(NCLModel model) {
		this.model = model;
	}

	private ServiceRegistration	eventListenerRegistration;

	private Log					log	= LogFactory.getLog(NCLProvisioner.class);

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

	// ////////////////////////
	// EvebtListener Methods //
	// ////////////////////////

	@Override
	public void handleEvent(Event event) {
		if (event instanceof LinkCongestionEvent) {

			LinkCongestionEvent congestionEvent = (LinkCongestionEvent) event;
			String switchName = (String) congestionEvent.getProperty(LinkCongestionEvent.SWITCH_ID_KEY);
			String portId = (String) congestionEvent.getProperty(LinkCongestionEvent.PORT_ID_KEY);

			log.info("LinkCongestion alarm received for switch " + switchName + " and port " + portId);

			String circuitId = selectCircuitToReallocate(switchName, portId);

			rerouteCircuit(circuitId);
		}
		else
			log.debug("Ignoring non-LinkCongestion alarm.");

	}

	public void unregisterListener() {
		if (autoReroute) {
			log.debug("Unregistering NCLProvisiner as listener for LinkCongestion events.");
			eventListenerRegistration.unregister();
			log.debug("NCLProvisioner successfully unregistered.");
		}
	}

	public void registerAsCongestionEventListener() throws IOException {

		autoReroute = readAutorerouteOption();

		if (autoReroute) {

			log.debug("Registering NCLProvisiner as listener for LinkCongestion events.");

			Properties properties = new Properties();
			properties.put(EventConstants.EVENT_TOPIC, LinkCongestionEvent.TOPIC);

			eventListenerRegistration = Activator.getContext().registerService(EventHandler.class.getName(), this, properties);

			log.debug("NCLProvisioner successfully registered as listener for LinkCongestion events.");
		}
		else
			log.info("Auto-reroute option deactivated, NCL not registered as LinkCongestion events listener.");

	}

	private void rerouteCircuit(String circuitId) {
		// TODO Auto-generated method stub

	}

	private String selectCircuitToReallocate(String switchName, String portId) {

		List<Circuit> circuitsInPort = getAllCircuitsInPort(switchName, portId);

		// TODO select in a more intelligent way, for example, based on ToS, flowCapacity, etc.
		return circuitsInPort.get(0).getId();
	}

	/**
	 * Retrieves all circuits allocated in a specific switch port.
	 * 
	 * @param switchName
	 * @param portId
	 * @return
	 */
	private List<Circuit> getAllCircuitsInPort(String switchName, String portId) {

		List<Circuit> circuitsInPort = new ArrayList<Circuit>();

		for (String circuiId : getAllocatedCircuits().keySet()) {

			List<NetOFFlow> circuitFlows = getAllocatedFlows().get(circuiId);
			if (NCLModelHelper.circuitFlowsContainPort(switchName, portId, circuitFlows)) {
				circuitsInPort.add(getAllocatedCircuits().get(circuiId));
			}
		}

		return circuitsInPort;
	}

	private boolean readAutorerouteOption() throws IOException {

		Properties properties = ConfigurationAdminUtil.getProperties(Activator.getContext(), NCL_CONFIG_FILE);
		if (properties == null)
			throw new IOException("Failed to initialize NCL as listener." + "Unable to obtain configuration " + NCL_CONFIG_FILE);

		return (Boolean) properties.get(AUTOREROUTE_KEY);
	}
}
