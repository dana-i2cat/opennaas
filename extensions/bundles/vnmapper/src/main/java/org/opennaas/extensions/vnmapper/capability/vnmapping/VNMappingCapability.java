package org.opennaas.extensions.vnmapper.capability.vnmapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.vnmapper.Global;
import org.opennaas.extensions.vnmapper.InPNetwork;
import org.opennaas.extensions.vnmapper.MappingResult;
import org.opennaas.extensions.vnmapper.ObjectCopier;
import org.opennaas.extensions.vnmapper.PLink;
import org.opennaas.extensions.vnmapper.PNode;
import org.opennaas.extensions.vnmapper.VNState;
import org.opennaas.extensions.vnmapper.VNTMapper;
import org.opennaas.extensions.vnmapper.VNTRequest;

/**
 * 
 * @author Elisabeth Rigol
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

	public VNMapperOutput mapVN(VNTRequest request) throws CapabilityException {

		try {
			// //// run the matching and mapping/////
			// Global.rowNum=8;
			// Global.cellNum=8;
			Global.pathChoice = 1;
			Global.maxPathLinksNum = 5;
			// Global.staticNet=1;
			// Global.staticVNT=1;
			Global.stepsMax = 100;
			// //
			// InPNetwork net=new InPNetwork();
			// net=net.readPNetworkFromXMLFile("src\\marketplace\\network.xml");
			// net.printNetwork();
			// //
			InPNetwork net = getInPNetwork();
			VNMapperInput input = new VNMapperInput(net, request);

			MappingResult result = executeAlgorithm(request, net);

			return new VNMapperOutput(result, input);
		} catch (IOException io) {
			log.error("Error maping request - ", io);
			throw new CapabilityException(io);
		}

	}

	public InPNetwork getInPNetwork() throws CapabilityException {

		try {
			ResourceManager resourcemng = (ResourceManager) Activator.getResourceManagerService();
			List<IResource> res1 = resourcemng.listResourcesByType("network");
			IModel networkModel = res1.get(0).getModel();
			InPNetwork net = getInPNetworkFromNetworkTopology(networkModel);
			// System.out.println(" network links num : " + links.size());
			// for(int i=0;i<links.size();i++)
			// {
			// System.out.println(" link : " + links.get(i).getSource().getName());
			// System.out.println(" link : " + links.get(i).getSource().getDevice().getName());
			//
			// }

			// System.out.println(" devices num : " + devices.size());
			// for(int i=0;i<devices.size();i++)
			// {
			// System.out.println(" device : " + devices.get(i).getName());
			//
			// }

			// /// generate the network object

			return net;

		} catch (ActivatorException ae) {
			throw new CapabilityException(ae);
		}

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
			n.setCapacity(16);
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

				net.getConnections().get(node1).get(node2).setCapacity(100);

				net.getConnections().get(node1).get(node2).setDelay(1);

				net.getLinks().add(net.getConnections().get(node1).get(node2));
			} else

				log.error("Error: couldn't find end point of a link");
		}

		log.info("InPNetwork built : \n" + net.toString());

		return net;
	}

	public MappingResult executeAlgorithm(VNTRequest request, InPNetwork net) throws IOException {

		VNTMapper mapper = new VNTMapper();
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
