package org.opennaas.extensions.vnmapper.capability.vnmapping;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkElement;
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

		mappingNetworkResult = new NetworkModel();

		addPhysicalNetworkElements();
		parseVirtualDevices();
		parseVirtualLinks();

		return mappingNetworkResult;
	}

	private void addPhysicalNetworkElements() {

		for (NetworkElement elem : physicalNetworkModel.getNetworkElements())
			mappingNetworkResult.getNetworkElements().add(elem);
	}

	private void parseVirtualDevices() throws ResourceNotFoundException {

		for (int i = 0; i < mappingResult.getVnodes().size(); i++) {

			int vnodeId = mappingResult.getVnodes().get(i);
			String deviceId = inpNetwork.getNodes().get(vnodeId).getPnodeID();
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

					if (i < j) {
						resultLinks.get(i).get(j).getResultPath().setNode1Id(Integer.valueOf(mappingResult.getVnodes().get(i).toString()));
						resultLinks.get(i).get(j).getResultPath().setNode2Id(Integer.valueOf(mappingResult.getVnodes().get(j).toString()));
						path = resultLinks.get(i).get(j).getResultPath();

					}

					else
					{
						resultLinks.get(j).get(i).getResultPath().setNode1Id(Integer.valueOf(mappingResult.getVnodes().get(j).toString()));
						resultLinks.get(j).get(i).getResultPath().setNode2Id(Integer.valueOf(mappingResult.getVnodes().get(i).toString()));
						path = resultLinks.get(j).get(i).getResultPath();

					}

					// binds to physical path

					for (int k = 0; k < path.getLinks().size(); k++) {

						int phySourceId = path.getLinks().get(k).getNode1Id();
						int phySinkId = path.getLinks().get(k).getNode2Id();

						int vSourceId = getVirtualDeviceIdByPhysicalId(phySourceId);
						int vSinkId = getVirtualDeviceIdByPhysicalId(phySinkId);

						if (!isVirtualNodeAlreadyAdded(vSourceId))
							addVirtualDeviceToModel(phySourceId);

						if (!isVirtualNodeAlreadyAdded(vSinkId))
							addVirtualDeviceToModel(phySinkId);

						addVirtualLink(vSourceId, vSinkId);

					}

				}

			}
		}
	}

	/**
	 * This method looks if there's a virtual device implemented by the physical device with the param name in model. If so, the name of this virtual
	 * device is returned. If not, the "size" index is returned in order to add a new virtual device with this name to the virtual devices list.
	 * 
	 * @param phyNodeId
	 * @return
	 */
	private int getVirtualDeviceIdByPhysicalId(int phyNodeId) {

		List<NetworkElement> vDevices = NetworkModelHelper.getNetworkElementsByClassName(VirtualDevice.class,
				mappingNetworkResult.getNetworkElements());

		String phyNodeName = this.inpNetwork.getNodes().get(phyNodeId).getPnodeID();

		for (NetworkElement vDevice : vDevices) {
			if (((VirtualDevice) vDevice).getImplementedBy().getName().equals(phyNodeName))
				return Integer.valueOf(vDevice.getName());

		}

		return Integer.valueOf(vDevices.size());

	}

	/**
	 * This method adds to virtual model all necessary information to store a virtual link: 
	 * 	1) Builds a source and a sink virtual interface. 
	 * 	2) Links virtual interfaces to virtual devices (source and sink). 
	 *  3) Links virtual link with physical link.
	 * 
	 * @param sourceId
	 * @param sinkId
	 * @throws ResourceNotFoundException
	 */
	private void addVirtualLink(int sourceId, int sinkId) throws ResourceNotFoundException {

		VirtualInterface vIfaceSource = new VirtualInterface();
		VirtualInterface vIfaceSink = new VirtualInterface();

		VirtualDevice sourceDevice = (VirtualDevice) NetworkModelHelper.getDeviceByName(mappingNetworkResult, String.valueOf(sourceId));
		VirtualDevice sinkDevice = (VirtualDevice) NetworkModelHelper.getDeviceByName(mappingNetworkResult, String.valueOf(sinkId));

		vIfaceSource.setDevice(sourceDevice);
		vIfaceSink.setDevice(sinkDevice);

		VirtualLink vlink = new VirtualLink();

		Device sourcePhyDevice = sourceDevice.getImplementedBy();
		Device sinkPhyDevice = sinkDevice.getImplementedBy();

		List<Link> links = NetworkModelHelper.getAllLinksBetweenTwoDevices((NetworkModel) physicalNetworkModel, sourcePhyDevice.getName(),
				sinkPhyDevice.getName());

		if (links.isEmpty())
			throw new ResourceNotFoundException(
					"No physical links between physical devices " + sourcePhyDevice.getName() + " and " + sinkPhyDevice.getName());

		vlink.setImplementedBy(links.get(0));
		vlink.setSource(vIfaceSource);
		vlink.setSink(vIfaceSink);

		mappingNetworkResult.getNetworkElements().add(vlink);
	}

	private void addVirtualDeviceToModel(int phyNodeId) throws ResourceNotFoundException {

		List<NetworkElement> vDevices = NetworkModelHelper.getNetworkElementsByClassName(VirtualDevice.class,
				mappingNetworkResult.getNetworkElements());

		int virtualId = vDevices.size();

		String phyDeviceId = inpNetwork.getNodes().get(phyNodeId).getPnodeID();

		Device device = NetworkModelHelper.getDeviceByName(physicalNetworkModel, phyDeviceId);

		if (device == null)
			throw new ResourceNotFoundException("Device " + phyDeviceId + " not found in network topology.");

		VirtualDevice vDevice = new VirtualDevice();
		vDevice.setName(String.valueOf(virtualId));
		vDevice.setImplementedBy(device);

		mappingNetworkResult.getNetworkElements().add(vDevice);
	}

	private boolean isVirtualNodeAlreadyAdded(int sourceId) {

		List<NetworkElement> virtualDevices = NetworkModelHelper.getNetworkElementsByClassName(VirtualDevice.class,
				mappingNetworkResult.getNetworkElements());

		for (NetworkElement device : virtualDevices)
			if (device.getName().equals(String.valueOf(sourceId)))
				return true;

		return false;
	}
}
