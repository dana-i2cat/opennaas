package org.opennaas.extensions.vnmapper.capability.vnmapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.technology.ethernet.EthernetLink;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.vnmapper.InPNetwork;
import org.opennaas.extensions.vnmapper.MappingResult;
import org.opennaas.extensions.vnmapper.ObjectCopier;
import org.opennaas.extensions.vnmapper.PLink;
import org.opennaas.extensions.vnmapper.PNode;
import org.opennaas.extensions.vnmapper.VNState;
import org.opennaas.extensions.vnmapper.VNTMapper;
import org.opennaas.extensions.vnmapper.VNTMapperConfiguration;
import org.opennaas.extensions.vnmapper.VNTRequest;

/**
 * 
 * @author Elisabeth Rigol
 * @author Adrian Rosello
 * 
 */
public class VNMappingCapability extends AbstractCapability implements IVNMappingCapability {

	public static String	CAPABILITY_TYPE	= "vnmapping";

	Log						log				= LogFactory.getLog(VNMappingCapability.class);

	private String			resourceId		= "";

	public VNMappingCapability(CapabilityDescriptor descriptor, String resourceId) {

		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new VNMapping Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {

		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getExampleActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * 
	 * @return QueuemanagerService this capability is associated to.
	 * @throws CapabilityException
	 *             if desired queueManagerService could not be retrieved.
	 */
	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}

	public VNMapperOutput mapVN(String networkResourceId, VNTRequest request) throws CapabilityException {

		try {

			IResource resource = getNetworkResource(networkResourceId);

			// //// run the matching and mapping/////
			VNTMapperConfiguration vNTMapperConfiguration = prepareVNTMapperConfiguration();

			// //
			// InPNetwork net=new InPNetwork();
			// net=net.readPNetworkFromXMLFile("src\\marketplace\\network.xml");
			// net.printNetwork();
			// //
			InPNetwork net = getInPNetworkFromNetworkTopology(resource.getModel());
			VNMapperInput input = new VNMapperInput(net, request);

			MappingResult result = executeAlgorithm(vNTMapperConfiguration, request, net);

			NetworkModel topologyResult = transformMapperOutputToNetworkTopology(resource.getModel(), net, result);

			return new VNMapperOutput(result, input, topologyResult);
		} catch (IOException io) {
			log.error("Error maping request - ", io);
			throw new CapabilityException(io);
		} catch (ResourceNotFoundException re) {
			log.error("Error maping request - ", re);
			throw new CapabilityException(re);
		}

	}

	public NetworkModel transformMapperOutputToNetworkTopology(IModel inputNetworkModel, InPNetwork net, MappingResult result)
			throws ResourceNotFoundException {

		MappingResultParser adaptor = new MappingResultParser((NetworkModel) inputNetworkModel, net, result);

		NetworkModel resultModel = adaptor.parseMappingResult();

		return resultModel;

	}

	private IResource getNetworkResource(String resourceId) throws CapabilityException {
		try {
			ResourceManager resourcemng = (ResourceManager) Activator.getResourceManagerService();
			IResource resource = resourcemng.getResourceById(resourceId);

			if (!resource.getResourceDescriptor().getInformation().getType().equals("network"))
				throw new CapabilityException("Resource id does not belong to a network resource.");

			return resource;
		} catch (ActivatorException ae) {
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			throw new CapabilityException(re);
		}
	}

	private VNTMapperConfiguration prepareVNTMapperConfiguration() {
		VNTMapperConfiguration vNTMapperConfiguration = new VNTMapperConfiguration();
		// VNTMapperConfiguration.rowNum=8;
		// VNTMapperConfiguration.cellNum=8;
		vNTMapperConfiguration.setpNodeChoice(1);
		vNTMapperConfiguration.setPathChoice(1);
		vNTMapperConfiguration.setMaxPathLinksNum(5);
		// VNTMapperConfiguration.staticNet=1;
		// VNTMapperConfiguration.staticVNT=1;
		vNTMapperConfiguration.setStepsMax(100);

		return vNTMapperConfiguration;
	}

	public InPNetwork getInPNetworkFromNetworkTopology(IModel networkModel) {

		log.debug("Building InPNetwork from NetworkTopology.");

		List<Link> links = NetworkModelHelper.getLinks((NetworkModel) networkModel);
		List<Device> devices = NetworkModelHelper.getDevices((NetworkModel) networkModel);

		InPNetwork net = new InPNetwork();

		for (int i = 0; i < devices.size(); i++) {
			PNode n = new PNode();
			n.setId(i);
			n.setPnodeID(devices.get(i).getName());
			n.setCapacity(devices.get(i).getVirtualizationService().getVirtualDevicesCapacity());
			n.setPathNum(0);
			net.getNodes().add(n);
			net.setNodeNum(net.getNodeNum() + 1);
		}

		for (int i = 0; i < net.getNodeNum(); i++) {
			net.getConnections().add(new ArrayList<PLink>());
			for (int j = 0; j < net.getNodeNum(); j++) {
				net.getConnections().get(i).add(new PLink());
			}
		}

		for (int i = 0; i < links.size(); i++)
		{

			int node1 = -1;

			int node2 = -1;
			for (int j = 0; j < devices.size() && (node1 == -1 || node2 == -1); j++) {
				if (devices.get(j).getName().equals(links.get(i).getSource().getDevice().getName()))
					node1 = j;
				if (devices.get(j).getName().equals(links.get(i).getSink().getDevice().getName()))
					node2 = j;
			}

			if ((node1 != -1) && (node2 != -1))
			{
				net.getConnections().get(node1).get(node2).setId(1);

				net.getConnections().get(node1).get(node2).setNode1Id(node1);

				net.getConnections().get(node1).get(node2).setNode2Id(node2);

				if (links.get(node1) instanceof EthernetLink && ((EthernetLink) links.get(i)).getBandwidth() == 0)

					net.getConnections().get(node1).get(node2).setCapacity(100);

				else
					net.getConnections().get(node1).get(node2).setCapacity((int) ((EthernetLink) links.get(i)).getBandwidth());

				net.getConnections().get(node1).get(node2).setDelay(1);

				net.getLinks().add(net.getConnections().get(node1).get(node2));
			} else

				log.error("Error: couldn't find end point of a link");
		}

		log.info("InPNetwork built : \n" + net.toString());

		return net;
	}

	public MappingResult executeAlgorithm(VNTMapperConfiguration config, VNTRequest request, InPNetwork net) throws IOException {

		VNTMapper mapper = new VNTMapper();
		if (config != null) {
			mapper.setConfiguration(config);
		} else {
			// set configuration with default values
			mapper.setConfiguration(new VNTMapperConfiguration());
		}

		MappingResult mres = new MappingResult();
		ArrayList<ArrayList<Integer>> matchingResult = new ArrayList<ArrayList<Integer>>();
		int matchingRes = mapper.matchVirtualNetwork(request, net, matchingResult);
		if (matchingRes == 1)
		{
			mres.setMatchingState(VNState.SUCCESSFUL);
			log.info("Successful Matching");
			InPNetwork temp = (InPNetwork) ObjectCopier.deepCopy(net);
			int result = mapper.VNTMapping(request, temp, 0, matchingResult, 3, mres);
			if (result == 1)
			{
				log.info("Successful Mapping");
				mres.setMappingState(VNState.SUCCESSFUL);

				log.info(mres.toString());
			} else {
				log.info("Unsuccessful Mapping");
				mres.setMappingState(VNState.ERROR);
			}

		} else {
			log.info("Unsuccessful Matching");
			mres.setMatchingState(VNState.ERROR);
		}
		return mres;

	}

}
