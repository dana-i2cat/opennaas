package org.opennaas.extensions.vnmapper.capability.example;

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

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class ExampleCapability extends AbstractCapability implements IExampleCapability {

	public static String	CAPABILITY_TYPE	= "vnmapping";

	Log						log				= LogFactory.getLog(ExampleCapability.class);

	private String			resourceId		= "";

	public ExampleCapability(CapabilityDescriptor descriptor, String resourceId) {

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

	public MappingResult sayHello(VNTRequest request) throws CapabilityException {

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

		return executeAlgorithm(request, net);

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

		List<Link> links = NetworkModelHelper.getLinks((NetworkModel) networkModel);
		List<Device> devices = NetworkModelHelper.getDevices((NetworkModel) networkModel);

		InPNetwork net = new InPNetwork();

		for (int i = 0; i < devices.size(); i++) {
			PNode n = new PNode();
			n.id = i;
			n.pnodeID = devices.get(i).getName();
			n.capacity = 16;
			n.pathNum = 0;
			net.nodes.add(n);
			net.nodeNum++;
		}

		for (int i = 0; i < net.nodeNum; i++) {
			net.connections.add(new ArrayList<PLink>());
			for (int j = 0; j < net.nodeNum; j++) {
				net.connections.get(i).add(new PLink());
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
				net.connections.get(node1).get(node2).id = 1;

				net.connections.get(node1).get(node2).node1Id = node1;

				net.connections.get(node1).get(node2).node2Id = node2;

				net.connections.get(node1).get(node2).capacity = 100;

				net.connections.get(node1).get(node2).delay = 1;

				net.links.add(net.connections.get(node1).get(node2));
			} else

				System.out.println("Error: couldn't find end point of a link");
		}
		return net;
	}

	public MappingResult executeAlgorithm(VNTRequest request, InPNetwork net) {

		try {

			VNTMapper mapper = new VNTMapper();
			MappingResult mres = new MappingResult();
			ArrayList<ArrayList> matchingResult = new ArrayList<ArrayList>();
			int matchingRes = mapper.matchVirtualNetwork(request, net, matchingResult);
			if (matchingRes == 1)
			{
				System.out.println("successful Matching");
				InPNetwork temp = (InPNetwork) ObjectCopier.deepCopy(net);
				int result = mapper.VNTMapping(request, temp, 0, matchingResult, 3, mres);
				if (result == 1)
				{
					System.out.println("successful Mapping");
					// log mres.print();
				}

			}
			return mres;
		} catch (Exception e) {
			// TODO launch exception
			System.out.println("Exception");
			return null;
		}

	}
}
