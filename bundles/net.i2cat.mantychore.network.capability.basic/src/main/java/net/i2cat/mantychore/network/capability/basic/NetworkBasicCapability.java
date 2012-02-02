package net.i2cat.mantychore.network.capability.basic;

import java.util.List;

import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.mappers.Cim2NdlMapper;
import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.NetworkModelHelper;
import net.i2cat.mantychore.network.model.topology.CrossConnect;
import net.i2cat.mantychore.network.model.topology.Interface;
import net.i2cat.mantychore.network.model.topology.Link;
import net.i2cat.mantychore.network.model.topology.NetworkConnection;
import net.i2cat.mantychore.network.model.topology.NetworkElement;
import net.i2cat.mantychore.network.repository.NetworkMapperModelToDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;

public class NetworkBasicCapability extends AbstractCapability implements ITopologyManager {

	public static final String	CAPABILITY_NAME	= "basicNetwork";

	Log							log				= LogFactory.getLog(NetworkBasicCapability.class);

	private String				resourceId		= "";

	public NetworkBasicCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Network Basic Capability");
	}

	@Override
	public Object sendMessage(String idOperation, Object paramsModel) throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// FIXME obtain actionSet dynamically
		return new NetActionSet();
	}

	// LIVE-CYCLE METHODS //
	
	@Override
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void activateCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deactivateCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}
	
	// ITopologyManager IMPLEMENTATION //

	@Override
	public NetworkModel addResource(IResource resourceToAdd) throws CapabilityException {
	
		if (!resourceToAdd.getState().equals(State.ACTIVE)) {
			throw new CapabilityException("Resource should be started before adding it to a network.");
		}
		
		if(! (resource.getModel() instanceof NetworkModel)) {
			throw new CapabilityException("Invalid resource model");
		}
			
		NetworkModel networkModel = (NetworkModel) resource.getModel();
		
		IModel resourceModel = resourceToAdd.getModel();
		String toAddName = resourceToAdd.getResourceDescriptor().getInformation().getType() + ":" + resourceToAdd.getResourceDescriptor().getInformation().getName();

		// FIXME should use a generic getIModel2NdlWrapper method placed in IModel (NetworkModel should be moved to OpenNaaS for that)
		// IModel2NdlWrapper wrapper = resourceModel.getIModel2NdlWrapper();
		// wrapper.addModelToNetworkModel(resource.getModel(), networkModel);
		if (resourceModel instanceof ManagedElement) {
			//update model
			List<NetworkElement> createdElements = Cim2NdlMapper.addModelToNetworkModel(resourceToAdd.getModel(), networkModel, toAddName);
			
			//update topology in descriptor
			NetworkTopology topology = NetworkMapperModelToDescriptor.modelToDescriptor(networkModel);
			resource.getResourceDescriptor().setNetworkTopology(topology);
		}
		
		return networkModel;
	}

	@Override
	public NetworkModel removeResource(IResource resourceToRemove) throws CapabilityException {
		
		if(! (resource.getModel() instanceof NetworkModel)) {
			throw new CapabilityException("Invalid resource model");
		}
		
		NetworkModel networkModel = (NetworkModel) resource.getModel();
		
		String toRemoveName = resourceToRemove.getResourceDescriptor().getInformation().getType() + ":" + resourceToRemove.getResourceDescriptor().getInformation().getName();
		
		
		List<NetworkElement> resources = NetworkModelHelper.getNetworkElementsExceptTransportElements(networkModel);
		int pos = NetworkModelHelper.getNetworkElementByName(toRemoveName, resources);
		if (pos == -1) {
			throw new CapabilityException("Resource " + toRemoveName + " not found in network model."); 
		}
		
		NetworkElement toRemove = resources.get(pos);
		
		//update model
		NetworkModelHelper.deleteNetworkElementAndReferences(toRemove, networkModel);
		
		//update topology in descriptor 
		NetworkTopology topology = NetworkMapperModelToDescriptor.modelToDescriptor(networkModel);
		resource.getResourceDescriptor().setNetworkTopology(topology);
		
		return networkModel;
	}

	@Override
	public NetworkConnection L2attach(Interface interface1, Interface interface2) throws CapabilityException {
		
		if(! (resource.getModel() instanceof NetworkModel)) {
			throw new CapabilityException("Invalid resource model");
		}
		
		NetworkModel networkModel = (NetworkModel) resource.getModel();
		
		NetworkConnection connection = null;
		if (interface1.getDevice().equals(interface2.getDevice())) {
			connection = NetworkModelHelper.crossConnectInterfaces(interface1, interface2);
			networkModel.getNetworkElements().add(connection);
		} else {
			connection = NetworkModelHelper.linkInterfaces(interface1, interface2, true);
			networkModel.getNetworkElements().add(connection);
		}
		
		return connection;
	}

	@Override
	public void L2detach(Interface interface1, Interface interface2) throws CapabilityException {
		
		if(! (resource.getModel() instanceof NetworkModel)) {
			throw new CapabilityException("Invalid resource model");
		}
		
		NetworkModel networkModel = (NetworkModel) resource.getModel();
		
		NetworkConnection toRemove = null;
		if (interface1.getSwitchedTo().getSource().equals(interface2) ||
				interface1.getSwitchedTo().getSink().equals(interface2)){
			toRemove = interface1.getSwitchedTo();
		} else if (interface1.getLinkTo().getSource().equals(interface2) ||
					interface1.getLinkTo().getSink().equals(interface2)){
			toRemove = interface1.getLinkTo();
		}
		
		if (toRemove != null) {
			NetworkModelHelper.deleteNetworkConnectionAndReferences(toRemove, networkModel);
		} else {
			log.debug("L2detach: Interfaces were not attached.");
		}
	}

}
