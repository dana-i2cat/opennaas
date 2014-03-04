package org.opennaas.extensions.genericnetwork.capability.nclprovisioner;

/*
 * #%L
 * OpenNaaS :: Generic Network
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.ICircuitProvisioningCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitId;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components.CircuitFactoryLogic;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.IPathFindingCapability;
import org.opennaas.extensions.genericnetwork.events.PortCongestionEvent;
import org.opennaas.extensions.genericnetwork.exceptions.CircuitAllocationException;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.helpers.Circuit2RequestHelper;
import org.opennaas.extensions.genericnetwork.model.helpers.GenericNetworkModelHelper;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */

public class NCLProvisionerCapability extends AbstractCapability implements INCLProvisionerCapability, EventHandler {

	public static final String	CAPABILITY_TYPE	= "nclprovisioner";

	private Log					log				= LogFactory.getLog(NCLProvisionerCapability.class);
	private String				resourceId		= "";

	private ServiceRegistration	eventListenerRegistration;

	private final static String	NCL_CONFIG_FILE	= "org.opennaas.extensions.ofertie.ncl";
	private final static String	AUTOREROUTE_KEY	= "ncl.autoreroute";

	public NCLProvisionerCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new NCLProvisioner Capability");

	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void activate() throws CapabilityException {
		try {
			registerAsCongestionEventListener();
		} catch (IOException e) {
			log.warn("Could not registrate NCLProvisionerCapability as listener for PortCongestion events.", e);
		}

		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				INCLProvisionerCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterListener();
		unregisterService();
		super.deactivate();
	}

	@Override
	public String allocateCircuit(CircuitRequest circuitRequest) throws CapabilityException {
		try {

			IPathFindingCapability pathFindingCapab;
			ICircuitProvisioningCapability circuitProvCapability;
			pathFindingCapab = (IPathFindingCapability) getCapability(IPathFindingCapability.class);
			circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);

			Route route = pathFindingCapab.findPathForRequest(circuitRequest);
			Circuit circuit = CircuitFactoryLogic.generateCircuit(circuitRequest, route);
			// TODO add aggregation logic when capability is implemented.

			circuitProvCapability.allocate(circuit);

			return circuit.getCircuitId();

		} catch (ResourceException e) {
			throw new CircuitAllocationException(e);
		} catch (Exception e) {
			throw new CircuitAllocationException(e);

		}
	}

	@Override
	public void deallocateCircuit(String circuitId) throws CapabilityException {
		// TODO add aggregation logic when capability is implemented.
		ICircuitProvisioningCapability circuitProvCapability;
		try {
			circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);

		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

		circuitProvCapability.deallocate(circuitId);

	}

	@Override
	public Collection<Circuit> getAllocatedCircuits() throws CapabilityException {

		ICircuitProvisioningCapability circuitProvCapability;

		try {
			circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);

		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

		List<Circuit> circuits = circuitProvCapability.getCircuits();

		return circuits;
	}

	@Override
	public void updateCircuit(String circuitId, CircuitRequest pathRequest) throws CapabilityException {

		deallocateCircuit(circuitId);
		allocateCircuit(pathRequest);

	}

	@Override
	public CircuitId allocateCircuitAPI(CircuitRequest circuitRequest) throws CapabilityException {

		String id = allocateCircuit(circuitRequest);
		CircuitId circuitId = new CircuitId(id);

		return circuitId;
	}

	@Override
	public CircuitCollection getAllocatedCircuitsAPI() throws CapabilityException {

		CircuitCollection circuitCollection = new CircuitCollection();

		circuitCollection.setCircuits(getAllocatedCircuits());

		return circuitCollection;

	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof PortCongestionEvent) {

			PortCongestionEvent congestionEvent = (PortCongestionEvent) event;

			String portId = (String) congestionEvent.getProperty(PortCongestionEvent.PORT_ID_KEY);

			log.info("PortCongestionEvent alarm received for port" + portId);
			boolean autoReroute = readAutorerouteOption();
			if (autoReroute) {
				log.debug("Auto-reroute is activated. Launching auto-reroute");
				try {
					launchRerouteMechanism(portId);
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

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("This capability does not contain actionset.");
	}

	private ICapability getCapability(Class<? extends ICapability> clazz) throws ResourceException {
		return this.resource.getCapabilityByInterface(clazz);
	}

	private void unregisterListener() {

		log.debug("Unregistering NCLProvisinerCapability as listener for PortCongestion events.");
		eventListenerRegistration.unregister();
		log.debug("NCLProvisinerCapability successfully unregistered.");

	}

	private void registerAsCongestionEventListener() throws IOException {

		log.debug("Registering NCLProvisinerCapability as listener for PortCongestion events.");

		Properties properties = new Properties();
		properties.put(EventConstants.EVENT_TOPIC, PortCongestionEvent.TOPIC);
		properties.put(PortCongestionEvent.NETWORK_ID_KEY, resource.getResourceIdentifier().getId());

		eventListenerRegistration = Activator.getContext().registerService(EventHandler.class.getName(), this, properties);

		log.debug("NCLProvisinerCapability successfully registered as listener for PortCongestion events.");

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

	private void launchRerouteMechanism(String portId) throws Exception {
		String circuitId = selectCircuitToReallocate(portId);

		if (circuitId == null) {
			log.info("No circuit allocated in this port. Ignoring alarm.");
			return;
		}
		try {
			rerouteCircuit(circuitId);
		} catch (Exception e) {
			throw new Exception("Could not reallocate circuit " + circuitId + ": " + e.getMessage(), e);
		}

	}

	private void rerouteCircuit(String circuitId) throws CapabilityException {

		log.debug("Start of rerouteCircuit call.");

		IPathFindingCapability pathFindingCapab;
		ICircuitProvisioningCapability circuitProvCapability;

		try {
			pathFindingCapab = (IPathFindingCapability) getCapability(IPathFindingCapability.class);
			circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);

		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

		GenericNetworkModel model = (GenericNetworkModel) this.resource.getModel();
		Circuit circuit = model.getAllocatedCircuits().get(circuitId);

		if (circuit == null)
			throw new CapabilityException("Cann not reroute circuit: Circuit is not allocated.");

		CircuitRequest circuitRequest = Circuit2RequestHelper.generateCircuitRequest(circuit.getQos(), circuit.getTrafficFilter());
		Route route = pathFindingCapab.findPathForRequest(circuitRequest);
		circuit.setRoute(route);

		// TODO once aggregation is implemented, call replace.
		circuitProvCapability.deallocate(circuitId);
		circuitProvCapability.allocate(circuit);

		log.debug("End of rerouteCircuit call.");

	}

	private String selectCircuitToReallocate(String portId) {
		List<String> circuitsInPort = getAllCircuitsInPort(portId);

		if (circuitsInPort.isEmpty()) {
			return null;
		}
		// TODO select in a more intelligent way, for example, based on ToS, flowCapacity, etc.
		return circuitsInPort.get(0);

	}

	private List<String> getAllCircuitsInPort(String portId) {

		List<String> circuitsIdsInPort = new ArrayList<String>();
		// TODO Once aggregation is implemented, it should look here for NOT-AGGREGATED CIRCUITS.

		GenericNetworkModel model = (GenericNetworkModel) this.resource.getModel();

		Map<String, Circuit> allocatedCircuits = model.getAllocatedCircuits();

		for (String circuitId : allocatedCircuits.keySet()) {
			Circuit circuit = allocatedCircuits.get(circuitId);

			if (GenericNetworkModelHelper.isPortInCircuit(portId, circuit))
				circuitsIdsInPort.add(circuitId);
		}

		return circuitsIdsInPort;
	}
}
