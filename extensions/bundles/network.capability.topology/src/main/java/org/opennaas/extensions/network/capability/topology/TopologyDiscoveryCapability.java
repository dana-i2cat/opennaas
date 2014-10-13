package org.opennaas.extensions.network.capability.topology;

/*
 * #%L
 * OpenNaaS :: Network :: Basic capability
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.network.capability.topology.api.Device;
import org.opennaas.extensions.network.capability.topology.api.Link;
import org.opennaas.extensions.network.capability.topology.api.NetworkTopology;
import org.opennaas.extensions.network.capability.topology.api.Port;
import org.opennaas.extensions.router.capability.topologydiscovery.model.LocalInformation;
import org.opennaas.extensions.router.capability.topologydiscovery.model.Neighbours;

/**
 * {@link ITopologyDiscoveryCapability} implementation
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TopologyDiscoveryCapability extends AbstractCapability implements ITopologyDiscoveryCapability {

	public static final String	CAPABILITY_TYPE	= "topology";

	private final static Log	log				= LogFactory.getLog(TopologyDiscoveryCapability.class);

	private String				resourceId		= "";

	private IResourceManager	resourceManager;

	private Set<IResource>		resources		= new HashSet<IResource>();

	public TopologyDiscoveryCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Topology Discovery Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), ITopologyDiscoveryCapability.class.getName());
		super.activate();

		try {
			resourceManager = Activator.getResourceManagerService();
		} catch (ActivatorException e) {
			throw new CapabilityException("Could not find required Resource Manager service!", e);
		}
	}

	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public NetworkTopology getNetworkTopology() throws CapabilityException {
		NetworkTopology networkTopology = new NetworkTopology();

		// key: resourceId
		Map<String, RouterTopologyInfo> resourceRouterInfoMap = new HashMap<String, TopologyDiscoveryCapability.RouterTopologyInfo>();

		Set<Device> devices = new HashSet<Device>();

		// resource iteration to fill all Devices that are resources in OpenNaaS and their Ports
		for (IResource router : resources) {
			String resourceId = router.getResourceIdentifier().getId();
			// get topology information of router
			LocalInformation localInformation = getRouterLocalInformation(router);
			Neighbours neighbours = getRouterNeighbours(router);

			// fill the map
			resourceRouterInfoMap.put(resourceId, new RouterTopologyInfo(localInformation, neighbours));

			/*
			 * create device and fill IDs
			 */
			Device device = new Device();
			// OpenNaaS resourceId
			device.setResourceID(resourceId);
			// underlying topology discovery protocol ID
			device.setId(localInformation.getDeviceId());

			/*
			 * create and fill ports. add them to device
			 */
			Set<Port> ports = new HashSet<Port>();
			for (String portId : localInformation.getInterfacesMap().keySet()) {
				Port port = new Port();
				// interface name
				port.setId(localInformation.getInterfacesMap().get(portId));
				// Device ID of this Port
				port.setDeviceId(localInformation.getDeviceId());
				ports.add(port);
			}
			device.setPorts(ports);

			// add device to devices set
			devices.add(device);
		}

		Set<Link> links = new HashSet<Link>();

		// device iteration to fill Links
		for (Device device : devices) {
			// get topology information of router
			// LocalInformation localInformation = resourceRouterInfoMap.get(device.getResourceID()).getLocalInformation();
			Neighbours neighbours = resourceRouterInfoMap.get(device.getResourceID()).getNeighbours();

			/*
			 * create device links
			 */
			Set<Link> deviceLinks = new HashSet<Link>();
			for (Port port : device.getPorts()) {
				Link deviceLink = new Link();
				// from is just this port
				deviceLink.setFrom(port);

				// get remote router from local Port
				org.opennaas.extensions.router.capability.topologydiscovery.model.Port remoteRouterPort = getRouterPortFromInterfaceName(
						port.getId(), neighbours.getDevicePortMap());
				if (remoteRouterPort == null) {
					// this port is not connected to any remote device
					continue;
				}
				String remoteDeviceId = remoteRouterPort.getDeviceId();

				// look for remote port in devices present in OpenNaaS as resources
				Device remoteDevice = getDeviceFromId(remoteDeviceId, devices);
				if (remoteDevice != null) {
					// check if remote device is an OpenNaaS resource
					for (Port p : remoteDevice.getPorts()) {
						// check if candidate remote Port ID matches remote ID
						if (remoteDevice.getResourceID() != null && p.getId() != null && p.getId().equals(
								resourceRouterInfoMap.get(remoteDevice.getResourceID()).getLocalInformation().getInterfacesMap()
										.get(remoteRouterPort.getPortId()))) {
							// match: remote device is an OpenNaaS resource with a well known port
							deviceLink.setTo(p);
							break;
						}
					}

					// device is not an OpenNaaS resource
					if (deviceLink.getTo() == null) {
						// check if unknown remote device was already created
						Port to = new Port();
						to.setDeviceId(remoteDeviceId);
						// we don't know remote interface name, it is not possible setting port ID

						// add port to device
						addPortToDevice(remoteDevice, to);

						// add Port to Link
						deviceLink.setTo(to);
					}
				}
				// remote device is not yet tracked
				else {
					// create unknown remote Device
					remoteDevice = new Device();
					remoteDevice.setId(remoteDeviceId);

					Port remotePort = new Port();
					remotePort.setDeviceId(remoteDeviceId);
					// we don't know remote interface name, it is not possible setting port ID

					// add port to new device
					addPortToDevice(remoteDevice, remotePort);

					// add Port to Link
					deviceLink.setTo(remotePort);
				}

				deviceLinks.add(deviceLink);

			}

			links.addAll(deviceLinks);
		}

		networkTopology.setLinks(links);

		networkTopology.setDevices(devices);

		return networkTopology;
	}

	private static org.opennaas.extensions.router.capability.topologydiscovery.model.Port getRouterPortFromInterfaceName(String interfaceName,
			Map<String, org.opennaas.extensions.router.capability.topologydiscovery.model.Port> devicePortMap) {
		return devicePortMap.get(interfaceName);
	}

	private static void addPortToDevice(Device device, Port port) {
		if (device.getPorts() == null) {
			device.setPorts(new HashSet<Port>());
		}
		device.getPorts().add(port);
	}

	private static Device getDeviceFromId(String deviceId, Set<Device> devices) {
		for (Device device : devices) {
			if (device.getId().equals(deviceId)) {
				return device;
			}
		}

		return null;
	}

	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public void addResource(String resourceId) throws CapabilityException {
		try {
			IResource resourceToAdd = resourceManager.getResourceById(resourceId);
			addResource(resourceToAdd);
		} catch (ResourceException e) {
			throw new CapabilityException("Could not find resource with ID = " + resourceId, e);
		}
	}

	@Override
	public void addResource(IResource resourceToAdd) throws CapabilityException {
		try {
			resourceToAdd.getCapabilityByInterface(org.opennaas.extensions.router.capability.topologydiscovery.ITopologyDiscoveryCapability.class);
		} catch (ResourceException e) {
			throw new CapabilityException("Resource with ID = " + resourceToAdd.getResourceIdentifier().getId() + " and name = " + resourceToAdd
					.getResourceDescriptor().getInformation().getName() + " has not Topology Discovery Capability!");
		}
		resources.add(resourceToAdd);
	}

	@Override
	public void removeResource(String resourceId) throws CapabilityException {
		try {
			IResource resourceToRemove = resourceManager.getResourceById(resourceId);
			removeResource(resourceToRemove);
		} catch (ResourceException e) {
			throw new CapabilityException("Could not find resource with ID = " + resourceId, e);
		}
	}

	@Override
	public void removeResource(IResource resourceToRemove) throws CapabilityException {
		if (!resources.contains(resourceToRemove)) {
			throw new CapabilityException(
					"Resource with ID = " + resourceToRemove.getResourceIdentifier().getId() + " and name = " + resourceToRemove
							.getResourceDescriptor().getInformation().getName() + " was not previously added!");
		}
		resources.remove(resourceToRemove);
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// this capability does not have action set
		return null;
	}

	@Override
	public void queueAction(IAction arg0) throws CapabilityException {
		// this capability does not use queue
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	private org.opennaas.extensions.router.capability.topologydiscovery.ITopologyDiscoveryCapability getRouterTopologyDiscoveryCapability(
			IResource resource) throws CapabilityException {
		try {
			return (org.opennaas.extensions.router.capability.topologydiscovery.ITopologyDiscoveryCapability) resource
					.getCapabilityByInterface(org.opennaas.extensions.router.capability.topologydiscovery.ITopologyDiscoveryCapability.class);
		} catch (ResourceException e) {
			throw new CapabilityException("Resource with ID = " + resource.getResourceIdentifier().getId() + " and name = " + resource
					.getResourceDescriptor().getInformation().getName() + " has not Topology Discovery Capability!", e);
		}
	}

	private LocalInformation getRouterLocalInformation(IResource resource) throws CapabilityException {
		return getRouterTopologyDiscoveryCapability(resource).getLocalInformation();
	}

	private Neighbours getRouterNeighbours(IResource resource) throws CapabilityException {
		return getRouterTopologyDiscoveryCapability(resource).getNeighbours();
	}

	/**
	 * RouterTopologyInfo containing LocalInformation and Neighbours
	 */
	private class RouterTopologyInfo {

		private LocalInformation	localInformation;
		private Neighbours			neighbours;

		public RouterTopologyInfo(LocalInformation localInformation, Neighbours neighbours) {
			this.localInformation = localInformation;
			this.neighbours = neighbours;
		}

		public LocalInformation getLocalInformation() {
			return localInformation;
		}

		public Neighbours getNeighbours() {
			return neighbours;
		}

	}
}
