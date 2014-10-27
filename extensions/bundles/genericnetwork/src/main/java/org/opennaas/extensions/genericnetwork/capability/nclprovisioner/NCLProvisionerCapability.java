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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import org.apache.commons.lang.StringUtils;
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
import org.opennaas.extensions.genericnetwork.capability.circuitaggregation.ICircuitAggregationCapability;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.ICircuitProvisioningCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitId;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components.CircuitFactoryLogic;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components.NetworkStatisticsPoller;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.IPathFindingCapability;
import org.opennaas.extensions.genericnetwork.events.PortCongestionEvent;
import org.opennaas.extensions.genericnetwork.exceptions.CircuitAllocationException;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
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
 * @author Isart Canyameres Gimenez (i2cat) - Use aggregation
 * 
 */

public class NCLProvisionerCapability extends AbstractCapability implements INCLProvisionerCapability, EventHandler {

	public static final String	CAPABILITY_TYPE			= "nclprovisioner";
	public static final String	POLLING_INTERVAL_KEY	= "polling.interval";
	public static final String	SLA_MANAGER_URI			= "slamanager.uri";
	private Log					log						= LogFactory.getLog(NCLProvisionerCapability.class);
	private String				resourceId				= "";

	private ServiceRegistration	eventListenerRegistration;

	private final static String	NCL_CONFIG_FILE			= "org.opennaas.extensions.ofertie.ncl";
	private final static String	AUTOREROUTE_KEY			= "ncl.autoreroute";

	private final Object		lock					= new Object();

	private Timer				statisticsPollerTimer;

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
			initializeStatisticsPusher();

		} catch (IOException e) {
			log.warn("Could not registrate NCLProvisionerCapability as listener for PortCongestion events.", e);
		}

		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				INCLProvisionerCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		statisticsPollerTimer.cancel();
		statisticsPollerTimer = null;
		unregisterListener();
		unregisterService();
		super.deactivate();
	}

	@Override
	public String allocateCircuit(CircuitRequest circuitRequest) throws CapabilityException {
		try {

			synchronized (lock) {

				IPathFindingCapability pathFindingCapab;
				ICircuitProvisioningCapability circuitProvCapability;
				ICircuitAggregationCapability circuitAggregationCapability;

				pathFindingCapab = (IPathFindingCapability) getCapability(IPathFindingCapability.class);
				circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);
				circuitAggregationCapability = (ICircuitAggregationCapability) getCapability(ICircuitAggregationCapability.class);

				Route route = pathFindingCapab.findPathForRequest(circuitRequest);
				Circuit toAllocate = CircuitFactoryLogic.generateCircuit(circuitRequest, route);

				log.info("Allocating circuit " + toAllocate.getCircuitId() + " with route " + toAllocate.getRoute().getId());
				if (log.isDebugEnabled()) {
					StringBuilder sb = new StringBuilder();
					for (NetworkConnection c : toAllocate.getRoute().getNetworkConnections()) {
						sb.append(c.getName());
						sb.append(", ");
					}
					log.debug("Route details: " + sb.toString());
				}

				// call aggregation logic with all requested circuits and the one toAllocate
				Set<Circuit> toAggregate = new HashSet<Circuit>();
				toAggregate.addAll(getRequestedCircuits());
				toAggregate.add(toAllocate);
				Set<Circuit> allAggregatedCircuits = circuitAggregationCapability.aggregateCircuits(toAggregate);

				// replace currently allocated (already aggregated) with new aggregated circuits
				List<Circuit> oldAggregated = new ArrayList<Circuit>();
				oldAggregated.addAll(circuitProvCapability.getCircuits());
				List<Circuit> newAggregated = new ArrayList<Circuit>(allAggregatedCircuits.size());
				newAggregated.addAll(allAggregatedCircuits);

				circuitProvCapability.replace(oldAggregated, newAggregated);

				// update requested circuits in the model
				// add allocated one (toAllocate)
				((GenericNetworkModel) resource.getModel()).getRequestedCircuits().put(toAllocate.getCircuitId(), toAllocate);

				log.info("Allocated circuit " + toAllocate.getCircuitId() + " with route " + toAllocate.getRoute().getId());

				return toAllocate.getCircuitId();

			}

		} catch (ResourceException e) {
			throw new CircuitAllocationException(e);
		} catch (Exception e) {
			throw new CircuitAllocationException(e);

		}
	}

	@Override
	public void deallocateCircuit(String circuitId) throws CapabilityException {

		synchronized (lock) {

			ICircuitProvisioningCapability circuitProvCapability;
			ICircuitAggregationCapability circuitAggregationCapability;
			try {
				circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);
				circuitAggregationCapability = (ICircuitAggregationCapability) getCapability(ICircuitAggregationCapability.class);
			} catch (ResourceException e) {
				throw new CapabilityException(e);
			}

			// call aggregation logic with all requested circuits except the one to be removed
			Set<Circuit> toAggregate = new HashSet<Circuit>();
			for (Circuit circuit : getRequestedCircuits()) {
				if (!circuit.getCircuitId().equals(circuitId))
					toAggregate.add(circuit);
			}
			Set<Circuit> newAggregatedCircuits = circuitAggregationCapability.aggregateCircuits(toAggregate);

			// replace currently aggregated by newAggregatedCircuits
			List<Circuit> oldAggregated = new ArrayList<Circuit>();
			oldAggregated.addAll(circuitProvCapability.getCircuits());
			List<Circuit> newAggregated = new ArrayList<Circuit>(newAggregatedCircuits.size());
			newAggregated.addAll(newAggregatedCircuits);

			circuitProvCapability.replace(oldAggregated, newAggregated);

			// update requested circuits in the model
			// remove deallocated one
			((GenericNetworkModel) resource.getModel()).getRequestedCircuits().remove(circuitId);

			log.info("Deallocated circuit " + circuitId);

		}
	}

	@Override
	public Collection<Circuit> getAllocatedCircuits() {
		return getRequestedCircuits();
	}

	public Collection<Circuit> getRequestedCircuits() {
		return ((GenericNetworkModel) resource.getModel()).getRequestedCircuits().values();
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
	public CircuitCollection getAllocatedCircuitsAPI() {

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
			log.info("No reallocable circuit currently using port " + portId + ". Ignoring alarm.");
			return;
		}
		try {
			log.debug("Rerouting circuit " + circuitId + " that passes through congested  port " + portId);
			rerouteCircuit(circuitId);
		} catch (Exception e) {
			throw new Exception("Could not reallocate circuit " + circuitId + ": " + e.getMessage(), e);
		}

	}

	private void rerouteCircuit(String circuitId) throws CapabilityException {

		synchronized (lock) {

			log.debug("Start of rerouteCircuit call.");

			IPathFindingCapability pathFindingCapab;
			ICircuitProvisioningCapability circuitProvCapability;
			ICircuitAggregationCapability circuitAggregationCapability;

			try {
				pathFindingCapab = (IPathFindingCapability) getCapability(IPathFindingCapability.class);
				circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);
				circuitAggregationCapability = (ICircuitAggregationCapability) getCapability(ICircuitAggregationCapability.class);
			} catch (ResourceException e) {
				throw new CapabilityException(e);
			}

			GenericNetworkModel model = (GenericNetworkModel) this.resource.getModel();
			Circuit toReroute = model.getRequestedCircuits().get(circuitId);

			if (toReroute == null)
				throw new CapabilityException("Can not reroute circuit: Circuit is not allocated.");

			log.info("Rerouting circuit " + circuitId + " thas uses congested route " + toReroute.getRoute().getId());

			CircuitRequest circuitRequest = Circuit2RequestHelper.generateCircuitRequest(toReroute.getQos(), toReroute.getTrafficFilter());
			Route route = pathFindingCapab.findPathForRequest(circuitRequest);

			Circuit withNewRoute = CircuitFactoryLogic.generateCircuit(circuitRequest, route);
			withNewRoute.setCircuitId(toReroute.getCircuitId());

			// call aggregation logic with all requested circuits
			// except the original one to be re-routed and with the one to be re-rerouted updated
			Set<Circuit> toAggregate = new HashSet<Circuit>();
			for (Circuit circuit : getRequestedCircuits()) {
				if (!circuit.getCircuitId().equals(circuitId))
					toAggregate.add(circuit);
			}
			toAggregate.add(withNewRoute);
			Set<Circuit> newAggregatedCircuits = circuitAggregationCapability.aggregateCircuits(toAggregate);

			// replace currently aggregated by newAggregatedCircuits
			List<Circuit> oldAggregated = new ArrayList<Circuit>();
			oldAggregated.addAll(circuitProvCapability.getCircuits());
			List<Circuit> newAggregated = new ArrayList<Circuit>(newAggregatedCircuits.size());
			newAggregated.addAll(newAggregatedCircuits);

			circuitProvCapability.replace(oldAggregated, newAggregated);

			// update requested circuits in model
			model.getRequestedCircuits().put(circuitId, withNewRoute);

			log.info("Rerouted circuit " + circuitId + " with route " + withNewRoute.getRoute().getId());
			if (log.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				for (NetworkConnection c : withNewRoute.getRoute().getNetworkConnections()) {
					sb.append(c.getName());
					sb.append(", ");
				}
				log.debug("Route details: " + sb.toString());
			}
			log.debug("End of rerouteCircuit call.");

		}
	}

	private String selectCircuitToReallocate(String portId) throws CapabilityException {
		List<Circuit> circuitsInPort = getAllCircuitsInPort(portId);
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Circuit c : circuitsInPort) {
				sb.append(c.getCircuitId() + ", ");
			}
			sb.append("]");
			log.debug("Circuits using port " + portId + ": " + sb.toString());
		}

		if (circuitsInPort.isEmpty()) {
			return null;
		}

		List<Circuit> reallocables = filterReallocableCircuits(circuitsInPort);
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Circuit c : reallocables) {
				sb.append(c.getCircuitId() + ", ");
			}
			sb.append("]");
			log.debug("Reallocable circuits using port " + portId + ": " + sb.toString());
		}

		if (reallocables.isEmpty())
			return null;

		// TODO select in a more intelligent way, for example, based on ToS, flowCapacity, etc.
		return reallocables.get(0).getCircuitId();
	}

	private List<Circuit> filterReallocableCircuits(List<Circuit> circuits) throws CapabilityException {
		// TODO: implement in a more efficient way

		IPathFindingCapability pathFindingCapab;
		try {
			pathFindingCapab = (IPathFindingCapability) getCapability(IPathFindingCapability.class);
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

		List<Circuit> reallocableCircuits = new ArrayList<Circuit>(circuits.size());
		for (Circuit circuit : circuits) {
			Route alternativeRoute = null;
			try {
				CircuitRequest circuitRequest = Circuit2RequestHelper.generateCircuitRequest(circuit.getQos(), circuit.getTrafficFilter());
				alternativeRoute = pathFindingCapab.findPathForRequest(circuitRequest);
			} catch (CapabilityException e) {
				// ignored
				log.debug("Unable to find uncongested alternative route for circuit " + circuit.getCircuitId() + ". Cause: " + e.getMessage());
			}
			if (alternativeRoute != null)
				reallocableCircuits.add(circuit);
		}

		return reallocableCircuits;
	}

	private List<Circuit> getAllCircuitsInPort(String portId) {

		List<Circuit> circuitsIdsInPort = new ArrayList<Circuit>();

		// look here for NOT-AGGREGATED CIRCUITS.
		for (Circuit circuit : getRequestedCircuits()) {
			if (GenericNetworkModelHelper.isPortInCircuit(portId, circuit))
				circuitsIdsInPort.add(circuit);
		}

		return circuitsIdsInPort;
	}

	private void initializeStatisticsPusher() throws CapabilityException {
		log.info("Initializing Network statistics pusher.");

		String slaManagerUri = descriptor.getPropertyValue(SLA_MANAGER_URI);
		String pollingInterval = descriptor.getPropertyValue(POLLING_INTERVAL_KEY);

		if (StringUtils.isEmpty(slaManagerUri) || StringUtils.isEmpty(pollingInterval))
			throw new CapabilityException("SLAManager URI and Polling Interval should be indicated as capability property.");

		if (!StringUtils.isNumeric(pollingInterval))
			throw new CapabilityException("Invalid polling interval: value must be numeric.");

		// initialize statistics poller
		try {
			NetworkStatisticsPoller statisticsPoller = new NetworkStatisticsPoller(new URI(slaManagerUri), this.resource);
			statisticsPollerTimer = new Timer("Generic Network " + this.resourceId + " statistics poller.", true);
			statisticsPollerTimer.scheduleAtFixedRate(statisticsPoller, 0, Integer.valueOf(pollingInterval) * 1000);

		} catch (URISyntaxException e) {
			log.error("Invalid SLA URI.", e);
			throw new CapabilityException(e);
		}

		log.debug("Network statistics pusher initialized.");
	}
}
