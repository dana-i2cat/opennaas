package net.i2cat.mantychore.network.repository;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.ResourcesReferences;
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

		/* set interfaces */
		for (Interface interf : networkTopology.getInterfaces()) {
			existingInterfaces.add(createInterface(interf.getName()));

		}

		/* set links */
		for (Interface interf : networkTopology.getInterfaces()) {
			// check if it exists a link
			if (interf.getLinkTo() == null)
				continue;

			String nameSource = cleanName(interf.getName());
			String nameTarget = cleanName(interf.getLinkTo().getName());

			int sourcePosInterf = getInterface(nameSource, existingInterfaces);
			if (sourcePosInterf == -1)
				throw new ResourceException("Error to fill network model. Interface doesn't exist: " + nameSource);
			net.i2cat.mantychore.network.model.topology.Interface sourceInterf = (net.i2cat.mantychore.network.model.topology.Interface) existingInterfaces
					.get(sourcePosInterf);

			int targetPosInterf = getInterface(nameTarget, existingInterfaces);

			// FIXME THIS CASE CAN NOT HAPPEN. ALL THE INTERFACES HAVE TO BE MAPPED IN A DESCRIPTOR. ANYWAY, IN ORDER TO BE COMPATIBLE
			// WITH NDL DESCRIPTORS, WE ADD THESE REFERENCES
			// If the interface doesn't exist, This interface can reference to an external interface in other network domain
			if (targetPosInterf == -1) {
				net.i2cat.mantychore.network.model.topology.Interface newInterf = new net.i2cat.mantychore.network.model.topology.Interface();
				newInterf.setName(cleanName(interf.getName()));
				existingInterfaces.add(newInterf);
				targetPosInterf = existingInterfaces.size() - 1;
			}

			// if (targetPosInterf == -1)
			// throw new ResourceException("Error to fill network model. Interface doesn't exist: " + nameTarget);

			net.i2cat.mantychore.network.model.topology.Interface targetInterf = (net.i2cat.mantychore.network.model.topology.Interface) existingInterfaces
					.get(targetPosInterf);

			// If it is the first relation

			if (targetInterf.getLinkTo() == null) {
				// It is the first interface, you have to create the link
				net.i2cat.mantychore.network.model.topology.Link linkModel = new net.i2cat.mantychore.network.model.topology.Link();
				linkModel.setSource(sourceInterf);
				linkModel.setSink(targetInterf);
				linkModel.setBidirectional(false);
				sourceInterf.setLinkTo(linkModel);
				targetInterf.setLinkTo(linkModel);
				// adding links
				existingLinks.add(linkModel);
			} else {
				// The two interfaces have a link each other. The link is bidirectional
				sourceInterf.getLinkTo().setBidirectional(true);
			}

		}

		/* set devices */
		for (Device device : networkTopology.getDevices()) {
			net.i2cat.mantychore.network.model.topology.Device modelDevice = new net.i2cat.mantychore.network.model.topology.Device();
			modelDevice.setName(device.getName());

			List<net.i2cat.mantychore.network.model.topology.ConnectionPoint> interfaces = new ArrayList<net.i2cat.mantychore.network.model.topology.ConnectionPoint>();
			for (InterfaceId interfaceId : device.getHasInterfaces()) {
				int posInterf = getInterface(interfaceId.getResource(), existingInterfaces);

				// FIXME THIS CASE CAN NOT HAPPEN. ALL THE INTERFACES HAVE TO BE MAPPED IN A DESCRIPTOR. ANYWAY, IN ORDER TO BE COMPATIBLE
				// WITH NDL DESCRIPTORS, WE ADD THESE REFERENCES
				if (posInterf == -1) {
					net.i2cat.mantychore.network.model.topology.Interface interf = createInterface(interfaceId.getResource());
					existingInterfaces.add(interf);
					posInterf = existingInterfaces.size() - 1;
				}
				net.i2cat.mantychore.network.model.topology.Interface sourceInterf = (net.i2cat.mantychore.network.model.topology.Interface) existingInterfaces
						.get(posInterf);
				interfaces.add(sourceInterf);
			}
			modelDevice.setInterfaces(interfaces);
			existingDevices.add(modelDevice);
		}

		/* add devices in networkDomain */
		for (NetworkDomain networkDomainModel : networkTopology.getNetworkDomains()) {
			net.i2cat.mantychore.network.model.domain.NetworkDomain networkDomain = new net.i2cat.mantychore.network.model.domain.NetworkDomain();
			networkDomain.setName(networkDomainModel.getName());
			List<net.i2cat.mantychore.network.model.topology.Device> devices = new ArrayList<net.i2cat.mantychore.network.model.topology.Device>();
			for (DeviceId deviceId : networkDomainModel.getHasDevices()) {
				int posDevice = getDevice(deviceId.getResource(), existingDevices);
				if (posDevice == -1)
					throw new ResourceException("Error to fill network model. It doesn't exist device for this network " + deviceId.getResource());

				net.i2cat.mantychore.network.model.topology.Device modelDevice = existingDevices.get(posDevice);
				devices.add(modelDevice);

			}
			networkDomain.setHasDevice(devices);
			existingDomains.add(networkDomain);
		}

		List<NetworkElement> networkElems = new ArrayList<NetworkElement>();
		networkElems.addAll(existingLinks);
		networkElems.addAll(existingInterfaces);
		networkElems.addAll(existingDevices);
		networkElems.addAll(existingDomains);
		networkModel.setNetworkElements(networkElems);

		return networkModel;
	}

	private static net.i2cat.mantychore.network.model.topology.Interface createInterface(String name) {
		net.i2cat.mantychore.network.model.topology.Interface newInterf = new net.i2cat.mantychore.network.model.topology.Interface();
		newInterf.setName(cleanName(name));
		return newInterf;
	}

	private static int getInterface(String name, List<net.i2cat.mantychore.network.model.topology.ConnectionPoint> listInterfaces) {
		// format name to search
		name = cleanName(name);

		int pos = 0;
		for (net.i2cat.mantychore.network.model.topology.ConnectionPoint connectionPoint : listInterfaces) {
			if (connectionPoint.getName().equals(name))
				return pos;
			pos++;
		}

		return -1;
	}

	private static int getDevice(String name, List<net.i2cat.mantychore.network.model.topology.Device> devices) {
		name = cleanName(name);
		int pos = 0;
		for (net.i2cat.mantychore.network.model.topology.Device device : devices) {
			if (device.getName().equals(name))
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
