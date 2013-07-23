package org.opennaas.extensions.quantum.network.builder;

import java.util.List;

import org.joda.time.DateTime;
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.bod.capability.l2bod.BoDLink;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.quantum.model.AutobahnElement;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.NetworkModel;
import org.opennaas.extensions.quantum.model.Resource;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

public abstract class NetworkBuilderHelper {

	public static void executeResourceQueue(IResource resource) throws ResourceException, ProtocolException {

		IQueueManagerCapability qCapability = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		QueueResponse response = qCapability.execute();
		if (!response.isOk()) {
			String commitMsg = response.getConfirmResponse().getInformation();
			throw new ResourceException(
					"Failed to execute queue for resource " + resource.getResourceDescriptor().getInformation().getName() + ": " + commitMsg);
		}
	}

	/**
	 * Get the interface from Network Model and interface name
	 * 
	 * @param networkElements
	 * @return Interface
	 */
	public static Interface getInterfaceByName(List<NetworkElement> networkElements, String interfaceName) {
		Interface toReturn = null;
		for (NetworkElement elem : networkElements) {
			if (elem instanceof Interface && elem.getName().equals(interfaceName)) {
				toReturn = (Interface) elem;
				break;
			}
		}
		return toReturn;
	}

	public static NetworkModel getNetworkModelFromQuantumNetworkId(List<NetworkModel> netModels, String quantumNetworkId) {

		for (NetworkModel model : netModels)
			if (model.getQuantumNetworkId().equals(quantumNetworkId))
				return model;

		return null;
	}

	public static Network getQuantumNetworkFromId(List<Network> networks, String networkId) {

		for (Network network : networks)
			if (network.getId().equals(networkId))
				return network;

		return null;
	}

	public static IResource getResourceByTypeAndName(String resourceName, String resourceType) throws ActivatorException, ResourceException {

		IResourceManager resourceManager = Activator.getResourceManagerService();

		IResourceIdentifier resourceIdentifier = resourceManager.getIdentifierFromResourceName(resourceType, resourceName);

		IResource autobahnResource = resourceManager.getResource(resourceIdentifier);

		return autobahnResource;

	}

	public static IResource getResourceById(String resourceId) throws ActivatorException, ResourceException {
		IResourceManager resourceManager = Activator.getResourceManagerService();
		IResource resource = resourceManager.getResourceById(resourceId);

		return resource;
	}

	public static Network createSampleNetwork(String netId, String netName, String tenantId) {

		Network sampleNetwork = new Network();
		sampleNetwork.setId(netId);
		sampleNetwork.setName(netName);
		sampleNetwork.setShared(false);
		sampleNetwork.setAdmin_state_up(true);
		sampleNetwork.setStatus("Active");
		sampleNetwork.setTenant_id(tenantId);

		return sampleNetwork;

	}

	public static BoDLink createSampleBoDLink(String iface1Name, String iface2Name, int capacity, int vlanid) {

		DateTime startTime = new DateTime(1234);
		DateTime endTime = new DateTime(2345);

		BoDLink link = new BoDLink();

		Interface ifaceSource = createSampleBoDInterface(iface1Name);
		Interface ifaceSink = createSampleBoDInterface(iface2Name);
		RequestConnectionParameters requestParameters = new RequestConnectionParameters(ifaceSource, ifaceSink, capacity, vlanid, startTime, endTime);

		link.setSource(ifaceSource);
		link.setSink(ifaceSink);
		link.setRequestParameters(requestParameters);

		return link;

	}

	public static Interface createSampleBoDInterface(String interfaceName) {

		Interface iface = new Interface();
		iface.setName(interfaceName);

		return iface;

	}

	public static Resource createSampleAutobahnResource(String resourceId, AutobahnElement autobahnElem) {

		Resource resource = new Resource();
		resource.setResourceId(resourceId);
		resource.addResourceElement(autobahnElem);

		return resource;

	}
}
