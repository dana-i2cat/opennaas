package org.opennaas.extensions.vnmapper.capability.vnmapping;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.virtual.VirtualDevice;
import org.opennaas.extensions.network.model.virtual.VirtualInterface;
import org.opennaas.extensions.network.model.virtual.VirtualLink;
import org.opennaas.extensions.vnmapper.InPNetwork;
import org.opennaas.extensions.vnmapper.MappingResult;
import org.opennaas.extensions.vnmapper.Path;
import org.opennaas.extensions.vnmapper.VNTLinkMappingCell;

/**
 * 
 * @author Adrian Rosello
 * 
 */
public class MappingResultParser {

	private NetworkModel	physicalNetworkModel;
	private InPNetwork		inpNetwork;
	private MappingResult	mappingResult;

	private NetworkModel	mappingNetworkResult;

	public MappingResultParser() {
	}

	public MappingResultParser(NetworkModel netModel, InPNetwork net, MappingResult result) {
		this.physicalNetworkModel = netModel;
		this.inpNetwork = net;
		this.mappingResult = result;
	}

	public NetworkModel getPhysicalNetworkModel() {
		return physicalNetworkModel;
	}

	public void setPhysicalNetworkModel(NetworkModel physicalNetworkModel) {
		this.physicalNetworkModel = physicalNetworkModel;
	}

	public InPNetwork getInpNetwork() {
		return inpNetwork;
	}

	public void setInpNetwork(InPNetwork inpNetwork) {
		this.inpNetwork = inpNetwork;
	}

	public MappingResult getMappingResult() {
		return mappingResult;
	}

	public void setMappingResult(MappingResult mappingResult) {
		this.mappingResult = mappingResult;
	}

	public NetworkModel parseMappingResult() throws ResourceNotFoundException {

		parseVirtualDevices();
		parseVirtualLinks();

		return mappingNetworkResult;
	}

	private void parseVirtualDevices() throws ResourceNotFoundException {

		mappingNetworkResult = new NetworkModel();

		for (int i = 0; i < mappingResult.getVnodes().size(); i++) {

			String deviceId = inpNetwork.getNodes().get(i).getPnodeID();
			Device device = NetworkModelHelper.getDeviceByName(physicalNetworkModel, deviceId);
			if (device == null)
				throw new ResourceNotFoundException("Device " + deviceId + " not found in network topology.");

			VirtualDevice vDevice = new VirtualDevice();
			vDevice.setName(String.valueOf(i));
			vDevice.setImplementedBy(device);

			mappingNetworkResult.getNetworkElements().add(vDevice);
		}

	}

	private void parseVirtualLinks() throws ResourceNotFoundException {

		ArrayList<ArrayList<VNTLinkMappingCell>> resultLinks = mappingResult.getVNTLinkMappingArray();

		for (int i = 0; i < (int) resultLinks.size(); i++) {
			for (int j = 0; j < (int) resultLinks.get(i).size(); j++) {
				if (resultLinks.get(i).get(j).getIsMapped() == 1) {

					// all this block constructs the virtual network

					Path path;
					VirtualInterface vIface = new VirtualInterface();
					VirtualInterface vIface2 = new VirtualInterface();

					if (i < j) {
						resultLinks.get(i).get(j).getResultPath().setNode1Id(Integer.valueOf(mappingResult.getVnodes().get(i).toString()));
						resultLinks.get(i).get(j).getResultPath().setNode2Id(Integer.valueOf(mappingResult.getVnodes().get(j).toString()));
						path = resultLinks.get(i).get(j).getResultPath();

						vIface.setDevice(NetworkModelHelper.getDeviceByName(mappingNetworkResult, String.valueOf(i)));
						vIface2.setDevice(NetworkModelHelper.getDeviceByName(mappingNetworkResult, String.valueOf(j)));
						vIface.setName(vIface.getDevice().getName() + ":" + i);
						vIface2.setName(vIface2.getDevice().getName() + ":" + j);// maaaal! poner phyId?

					}
					else
					{
						resultLinks.get(j).get(i).getResultPath().setNode1Id(Integer.valueOf(mappingResult.getVnodes().get(j).toString()));
						resultLinks.get(j).get(i).getResultPath().setNode2Id(Integer.valueOf(mappingResult.getVnodes().get(i).toString()));
						path = resultLinks.get(j).get(i).getResultPath();

						vIface.setDevice(NetworkModelHelper.getDeviceByName(mappingNetworkResult, String.valueOf(j)));
						vIface2.setDevice(NetworkModelHelper.getDeviceByName(mappingNetworkResult, String.valueOf(i)));

						vIface.setName(vIface.getDevice().getName() + ":" + j);
						vIface2.setName(vIface2.getDevice().getName() + ":" + i);
					}

					VirtualLink vLink = new VirtualLink();
					vLink.setSource(vIface);
					vLink.setSink(vIface2);

					// binds to physical path

					org.opennaas.extensions.network.model.topology.Path phyPath = new org.opennaas.extensions.network.model.topology.Path();

					for (int k = 0; k < path.getLinks().size(); k++) {
						int sourceId = path.getLinks().get(k).getNode1Id();
						int destId = path.getLinks().get(k).getNode2Id();

						String sourcePhyId = inpNetwork.getNodes().get(sourceId).getPnodeID();
						String destPhyId = inpNetwork.getNodes().get(destId).getPnodeID();

						List<Link> links = NetworkModelHelper.getAllLinksBetweenTwoDevices((NetworkModel) physicalNetworkModel, sourcePhyId,
								destPhyId);
						if (links.isEmpty())
							throw new ResourceNotFoundException("No physical links between physical devices " + sourcePhyId + " and " + destPhyId);

						phyPath.getPathSegments().add(links.get(0));

					}

					vLink.setImplementedBy(phyPath);
					mappingNetworkResult.getNetworkElements().add(vLink);
				}

			}
		}
	}

}
