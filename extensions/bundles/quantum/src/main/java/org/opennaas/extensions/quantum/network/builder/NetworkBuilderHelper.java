package org.opennaas.extensions.quantum.network.builder;

import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.NetworkElement;
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

}
