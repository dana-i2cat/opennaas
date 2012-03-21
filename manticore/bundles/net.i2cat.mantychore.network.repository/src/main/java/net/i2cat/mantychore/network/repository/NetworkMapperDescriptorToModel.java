package net.i2cat.mantychore.network.repository;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.NetworkModelHelper;
import net.i2cat.mantychore.network.model.ResourcesReferences;
import net.i2cat.mantychore.network.model.topology.Link;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.network.Device;
import org.opennaas.core.resources.descriptor.network.DeviceId;
import org.opennaas.core.resources.descriptor.network.Interface;
import org.opennaas.core.resources.descriptor.network.InterfaceId;
import org.opennaas.core.resources.descriptor.network.NetworkDomain;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;

public class NetworkMapperDescriptorToModel {

	/**
	 * Loads information from the descriptor to a NetworkModel. Loaded information includes topology and resource references
	 * 
	 * @param descriptor
	 * @return
	 * @throws ResourceException
	 */
	public static NetworkModel descriptorToModel(ResourceDescriptor descriptor) throws ResourceException {

		NetworkModel model = new NetworkModel();
		if (descriptor.getNetworkTopology() != null) {
			// load topology
			model = descriptorToModel(descriptor.getNetworkTopology());
		}

		// load references
		if (descriptor.getResourceReferences() != null) {
			ResourcesReferences references = new ResourcesReferences();
			references.putAll(descriptor.getResourceReferences());
			model.setResourceReferences(references);
		}

		return model;
	}

	/**
	 * Loads a NetworkTopology into a NetworkModel
	 * 
	 * @param networkTopology
	 * @return
	 * @throws ResourceException
	 */
	public static NetworkModel descriptorToModel(NetworkTopology networkTopology) throws ResourceException {
		NetworkModel networkModel = new NetworkModel();

		List<net.i2cat.mantychore.network.model.topology.Device> existingDevices = new ArrayList<net.i2cat.mantychore.network.model.topology.Device>();
		List<net.i2cat.mantychore.network.model.topology.ConnectionPoint> existingInterfaces = new ArrayList<net.i2cat.mantychore.network.model.topology.ConnectionPoint>();
		List<net.i2cat.mantychore.network.model.topology.Link> existingLinks = new ArrayList<net.i2cat.mantychore.network.model.topology.Link>();
		List<net.i2cat.mantychore.network.model.domain.NetworkDomain> existingDomains = new ArrayList<net.i2cat.mantychore.network.model.domain.NetworkDomain>();

		if (networkTopology.getInterfaces() != null) {
			/* set interfaces */
			for (Interface interf : networkTopology.getInterfaces()) {
				existingInterfaces.add(createInterface(interf.getName()));
			}

			/* set links */
			for (Interface interf : networkTopology.getInterfaces()) {
				// check if it exists a link
				if (interf.getLinkTo() == null)
					continue;

				int sourcePosInterf = getNetworkResourceByName(interf.getName(), existingInterfaces);
				if (sourcePosInterf == -1)
					throw new ResourceException("Error to fill network model. Interface doesn't exist: " + interf.getName());
				net.i2cat.mantychore.network.model.topology.Interface sourceInterf = (net.i2cat.mantychore.network.model.topology.Interface) existingInterfaces
						.get(sourcePosInterf);

				int targetPosInterf = getNetworkResourceFromReference(interf.getLinkTo().getName(), existingInterfaces);

				if (targetPosInterf == -1) {
					// Interfaces may link to undefined interfaces.
					// This undefined interfaces refer to interfaces external to the described network
					// In order to represent the link, we create this interface
					// FIXME this interface should'nt be included as a networkElement of this network

					net.i2cat.mantychore.network.model.topology.Interface newInterf = new net.i2cat.mantychore.network.model.topology.Interface();
					newInterf.setName((interf.getLinkTo().getName()));
					existingInterfaces.add(newInterf);
					targetPosInterf = existingInterfaces.size() - 1;
				}

				net.i2cat.mantychore.network.model.topology.Interface targetInterf = (net.i2cat.mantychore.network.model.topology.Interface) existingInterfaces
						.get(targetPosInterf);

				if (thereIsALinkToInterface(targetInterf, existingLinks)) {
					throw new ResourceException("Invalid configuration. Two interfaces has links to the same third one");
				} else {
					linkInterfaces(sourceInterf, targetInterf, existingLinks);
				}
			}
		}

		if (networkTopology.getDevices() != null) {
			/* set devices */
			for (Device device : networkTopology.getDevices()) {
				net.i2cat.mantychore.network.model.topology.Device modelDevice = new net.i2cat.mantychore.network.model.topology.Device();
				modelDevice.setName(device.getName());

				List<net.i2cat.mantychore.network.model.topology.ConnectionPoint> interfaces = new ArrayList<net.i2cat.mantychore.network.model.topology.ConnectionPoint>();
				for (InterfaceId interfaceId : device.getHasInterfaces()) {
					int posInterf = getNetworkResourceFromReference(interfaceId.getResource(), existingInterfaces);

					if (posInterf == -1) {
						// All device interfaces should be a defined interface resource in the rdf document
						throw new ResourceException("Error to fill network model. Interface doesn't exist: " + interfaceId.getResource());
					}
					net.i2cat.mantychore.network.model.topology.Interface sourceInterf =
							(net.i2cat.mantychore.network.model.topology.Interface) existingInterfaces.get(posInterf);
					interfaces.add(sourceInterf);
				}
				modelDevice.setInterfaces(interfaces);
				existingDevices.add(modelDevice);
			}
		}

		if (networkTopology.getNetworkDomains() != null) {
			/* add devices in networkDomain */
			for (NetworkDomain networkDomainModel : networkTopology.getNetworkDomains()) {
				net.i2cat.mantychore.network.model.domain.NetworkDomain networkDomain = new net.i2cat.mantychore.network.model.domain.NetworkDomain();
				networkDomain.setName(networkDomainModel.getName());
				List<net.i2cat.mantychore.network.model.topology.Device> devices = new ArrayList<net.i2cat.mantychore.network.model.topology.Device>();
				for (DeviceId deviceId : networkDomainModel.getHasDevices()) {
					int posDevice = getNetworkResourceFromReference(deviceId.getResource(), existingDevices);
					if (posDevice == -1)
						throw new ResourceException("Error to fill network model. It doesn't exist device for this network " + deviceId.getResource());

					net.i2cat.mantychore.network.model.topology.Device modelDevice = existingDevices.get(posDevice);
					devices.add(modelDevice);

				}
				networkDomain.setHasDevice(devices);
				existingDomains.add(networkDomain);
			}
		}

		List<NetworkElement> networkElems = new ArrayList<NetworkElement>();
		networkElems.addAll(existingLinks);
		networkElems.addAll(existingInterfaces);
		networkElems.addAll(existingDevices);
		networkElems.addAll(existingDomains);
		networkModel.setNetworkElements(networkElems);

		// FIXME domain should have interfaces, and administrative domain!!!

		// add interfaces
		// List<net.i2cat.mantychore.network.model.topology.ConnectionPoint> interfaces = new
		// ArrayList<net.i2cat.mantychore.network.model.topology.ConnectionPoint>();
		// for (InterfaceId interfaceId : networkDomainModel.getInterfaces()) {
		// int posInterf = getInterfaceFromReference(interfaceId.getResource(), existingInterfaces);
		//
		// if (posInterf == -1)
		// throw new ResourceException("Error to fill network model. Interface doesn't exist: " + interfaceId.getResource());
		//
		// net.i2cat.mantychore.network.model.topology.Interface sourceInterf = (net.i2cat.mantychore.network.model.topology.Interface)
		// existingInterfaces
		// .get(posInterf);
		// interfaces.add(sourceInterf);
		// }
		// networkDomain.setHasInterface(interfaces);

		return networkModel;
	}

	private static boolean thereIsALinkToInterface(net.i2cat.mantychore.network.model.topology.Interface targetInterf, List<Link> existingLinks) {
		for (Link link : existingLinks) {
			if (link.getSink() != null && link.getSink().equals(targetInterf)) {
				return true;
			}
		}
		return false;
	}

	private static void linkInterfaces(net.i2cat.mantychore.network.model.topology.Interface sourceInterf,
			net.i2cat.mantychore.network.model.topology.Interface targetInterf,
			List<net.i2cat.mantychore.network.model.topology.Link> existingLinks) throws ResourceException {

		if (targetInterf.getLinkTo() == null) {
			// It is the first interface of a link, a link should be created from source to target
			net.i2cat.mantychore.network.model.topology.Link linkModel = NetworkModelHelper.linkInterfaces(sourceInterf, targetInterf, false);
			existingLinks.add(linkModel);
		} else {
			if (targetInterf.getLinkTo().getSink().equals(sourceInterf)) {
				// The two interfaces have a link each other. The link is bidirectional
				sourceInterf.setLinkTo(targetInterf.getLinkTo());
				sourceInterf.getLinkTo().setBidirectional(true);
			} else {
				throw new ResourceException(
						"Invalid configuration. An interface is linked to one that links to a another. It should link to the first or none!");
			}
		}

	}

	private static net.i2cat.mantychore.network.model.topology.Interface createInterface(String name) {
		net.i2cat.mantychore.network.model.topology.Interface newInterf = new net.i2cat.mantychore.network.model.topology.Interface();
		newInterf.setName(name);
		return newInterf;
	}

	private static int getNetworkResourceFromReference(String reference,
			List<? extends net.i2cat.mantychore.network.model.topology.NetworkElement> networkElements) {
		String name = cleanName(reference);
		return getNetworkResourceByName(name, networkElements);
	}

	private static int getNetworkResourceByName(String name,
			List<? extends net.i2cat.mantychore.network.model.topology.NetworkElement> networkElements) {
		int pos = 0;
		for (net.i2cat.mantychore.network.model.topology.NetworkElement networkElement : networkElements) {
			if (networkElement.getName().equals(name))
				return pos;
			pos++;
		}
		return -1;
	}

	private static String cleanName(String name) {
		if (name.startsWith("#")) {
			int lastIndex = name.lastIndexOf("#");
			if (lastIndex != -1) {
				return name.substring(lastIndex + 1);

			}
		}
		return name;

	}

}
