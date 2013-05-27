package org.opennaas.extensions.network.capability.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.extensions.network.capability.basic.mappers.Cim2NdlMapper;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkConnection;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.network.repository.NetworkMapperModelToDescriptor;
import org.opennaas.extensions.router.model.ManagedElement;

public class NetworkBasicCapability extends AbstractCapability implements INetworkBasicCapability {

	public static final String	CAPABILITY_TYPE	= "basicNetwork";

	Log							log				= LogFactory.getLog(NetworkBasicCapability.class);

	private String				resourceId		= "";

	public NetworkBasicCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Network Basic Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.network.capability.basic.INetworkBasicCapability#addResource(java.lang.String)
	 */
	@Override
	public void addResource(String resourceId) throws CapabilityException {
		try {
			IResourceManager resourceManager = Activator.getResourceManagerService();
			IResource iResource = resourceManager.getResourceById(resourceId);
			addResource(iResource);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.network.capability.basic.INetworkBasicCapability#removeResource(java.lang.String)
	 */
	@Override
	public void removeResource(String resourceId) throws CapabilityException {
		try {
			IResourceManager resourceManager = Activator.getResourceManagerService();
			IResource iResource = resourceManager.getResourceById(resourceId);
			removeResource(iResource);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.network.capability.basic.INetworkBasicCapability#addResource(org.opennaas.core.resources.IResource)
	 */
	@Override
	public NetworkModel addResource(IResource resourceToAdd) throws CapabilityException {

		log.info("Start of addResource call");
		if (resourceToAdd == null) {
			throw new CapabilityException("Invalid null resourceToAdd");
		}

		if (!resourceToAdd.getState().equals(ILifecycle.State.ACTIVE)) {
			throw new CapabilityException("Resource should be started before adding it to a network.");
		}

		if (!(resource.getModel() instanceof NetworkModel)) {
			throw new CapabilityException("Invalid resource model");
		}

		NetworkModel networkModel = (NetworkModel) resource.getModel();

		IModel resourceModel = resourceToAdd.getModel();
		String toAddName = resourceToAdd.getResourceDescriptor().getInformation().getType() + ":" + resourceToAdd.getResourceDescriptor()
				.getInformation().getName();

		// check resourceToAdd is not in networkModel
		int pos = NetworkModelHelper.getNetworkElementByName(toAddName, networkModel.getNetworkElements());
		if (pos != -1) {
			throw new CapabilityException("There is already a resource with same name in this network.");
		}

		// FIXME should use a generic getIModel2NdlWrapper method placed in IModel (NetworkModel should be moved to OpenNaaS for that)
		// IModel2NdlWrapper wrapper = resourceModel.getIModel2NdlWrapper();
		// wrapper.addModelToNetworkModel(resource.getModel(), networkModel);
		log.debug("Adding resource " + toAddName + " to network " + getResourceName());
		if (resourceModel instanceof ManagedElement) {
			// update model
			List<NetworkElement> createdElements = Cim2NdlMapper.addModelToNetworkModel(resourceToAdd.getModel(), networkModel, toAddName);

			if (!createdElements.isEmpty()) {
				networkModel.addResourceRef(toAddName, resourceToAdd.getResourceIdentifier().getId());

				// update topology in descriptor
				NetworkTopology topology = NetworkMapperModelToDescriptor.modelToDescriptor(networkModel);
				resource.getResourceDescriptor().setNetworkTopology(topology);
				resource.getResourceDescriptor().setResourceReferences(networkModel.getResourceReferences());
			}
		}

		log.info("End of addResource call");
		return networkModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.network.capability.basic.INetworkBasicCapability#removeResource(org.opennaas.core.resources.IResource)
	 */
	@Override
	public NetworkModel removeResource(IResource resourceToRemove) throws CapabilityException {

		log.info("Start of removeResource call");
		if (resourceToRemove == null) {
			throw new CapabilityException("Invalid null resourceToRemove");
		}

		if (!(resource.getModel() instanceof NetworkModel)) {
			throw new CapabilityException("Invalid resource model");
		}

		NetworkModel networkModel = (NetworkModel) resource.getModel();

		String toRemoveName = resourceToRemove.getResourceDescriptor().getInformation().getType() + ":" + resourceToRemove.getResourceDescriptor()
				.getInformation().getName();

		// get networkElement to remove
		List<NetworkElement> resources = NetworkModelHelper.getNetworkElementsExceptTransportElements(networkModel);
		int pos = NetworkModelHelper.getNetworkElementByName(toRemoveName, resources);
		if (pos == -1) {
			throw new CapabilityException("Resource " + toRemoveName + " not found in network model.");
		}
		NetworkElement toRemove = resources.get(pos);

		// update model
		NetworkModelHelper.deleteNetworkElementAndReferences(toRemove, networkModel);
		networkModel.removeResourceRef(toRemoveName);

		// update topology in descriptor
		NetworkTopology topology = NetworkMapperModelToDescriptor.modelToDescriptor(networkModel);
		resource.getResourceDescriptor().setNetworkTopology(topology);
		resource.getResourceDescriptor().setResourceReferences(networkModel.getResourceReferences());

		log.info("End of removeResource call");
		return networkModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.network.capability.basic.INetworkBasicCapability#l2attach(org.opennaas.extensions.network.model.topology.Link)
	 */
	@Override
	public void l2attach(Link link) throws CapabilityException {
		l2attach(link.getSource(), link.getSink());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.network.capability.basic.INetworkBasicCapability#l2attach(org.opennaas.extensions.network.model.topology.Interface,
	 * org.opennaas.extensions.network.model.topology.Interface)
	 */
	@Override
	public NetworkConnection l2attach(Interface interface1, Interface interface2) throws CapabilityException {

		log.info("Start of l2attach call");
		if (interface1 == null || interface2 == null) {
			throw new CapabilityException("Invalid null interface");
		}

		if (!(resource.getModel() instanceof NetworkModel)) {
			throw new CapabilityException("Invalid resource model");
		}

		NetworkModel networkModel = (NetworkModel) resource.getModel();

		// get interfaces in model
		Interface realInterface1 = NetworkModelHelper.getInterfaceByName(interface1.getName(), networkModel);
		Interface realInterface2 = NetworkModelHelper.getInterfaceByName(interface2.getName(), networkModel);
		if (realInterface1 == null)
			throw new CapabilityException("Interface " + interface1.getName() + " not found in network model");
		if (realInterface2 == null)
			throw new CapabilityException("Interface " + interface2.getName() + " not found in network model");

		// check interfaces are not yet attached
		NetworkConnection existentConnection = getConnectionBetweenInterfaces(realInterface1, realInterface2);
		if (existentConnection != null) {
			log.info("Given interfaces are already attached");
			return existentConnection;
		}

		// check interfaces are not attached to others
		List<NetworkConnection> toRemove = new ArrayList<NetworkConnection>();
		if (interface1.getSwitchedTo() != null) {
			toRemove.add(interface1.getSwitchedTo());
		}
		if (interface1.getLinkTo() != null) {
			toRemove.add(interface1.getLinkTo());
		}
		if (interface2.getSwitchedTo() != null) {
			toRemove.add(interface2.getSwitchedTo());
		}
		if (interface2.getLinkTo() != null) {
			toRemove.add(interface2.getLinkTo());
		}
		if (!toRemove.isEmpty()) {
			log.info("Given interfaces are already attached to others.");
			// remove other attaches
			for (NetworkConnection connection : toRemove) {
				removeConnection(connection, resource);
			}
		}

		// create connection
		NetworkConnection connection = createConnectionBetweenInterfaces(realInterface1, realInterface2, resource);

		log.info("End of l2attach call");
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.network.capability.basic.INetworkBasicCapability#l2detach(org.opennaas.extensions.network.model.wrappers.Interfaces)
	 */
	@Override
	public void l2detach(Link link) throws CapabilityException {
		l2detach(link.getSource(), link.getSink());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.network.capability.basic.INetworkBasicCapability#l2detach(org.opennaas.extensions.network.model.topology.Interface,
	 * org.opennaas.extensions.network.model.topology.Interface)
	 */
	@Override
	public void l2detach(Interface interface1, Interface interface2) throws CapabilityException {

		log.info("Start of l2detach call");
		if (interface1 == null || interface2 == null) {
			throw new CapabilityException("Invalid null interface");
		}

		if (!(resource.getModel() instanceof NetworkModel)) {
			throw new CapabilityException("Invalid resource model");
		}

		NetworkModel networkModel = (NetworkModel) resource.getModel();

		// get interfaces in model
		Interface realInterface1 = NetworkModelHelper.getInterfaceByName(interface1.getName(), networkModel);
		Interface realInterface2 = NetworkModelHelper.getInterfaceByName(interface2.getName(), networkModel);
		if (realInterface1 == null)
			throw new CapabilityException("Interface " + interface1.getName() + " not found in network model");
		if (realInterface2 == null)
			throw new CapabilityException("Interface " + interface2.getName() + " not found in network model");

		// remove connection between given interfaces
		NetworkConnection toRemove = getConnectionBetweenInterfaces(realInterface1, realInterface2);
		if (toRemove != null) {
			removeConnection(toRemove, resource);
		} else {
			log.info("L2detach: Interfaces were not attached.");
		}
		log.info("End of l2detach call");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), INetworkBasicCapability.class.getName());
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityName()
	 */
	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#getActionSet()
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// FIXME obtain actionSet dynamically
		return new NetworkBasicActionSetImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public void queueAction(IAction action) throws CapabilityException {
	}

	/**
	 * @param interface1
	 * @param interface2
	 * @param network
	 * @return
	 */
	private NetworkConnection createConnectionBetweenInterfaces(Interface interface1, Interface interface2, IResource network) {
		log.info("Creating connection in network model");

		NetworkModel networkModel = (NetworkModel) network.getModel();

		NetworkConnection connection = null;
		if (interface1.getDevice().equals(interface2.getDevice())) {
			connection = NetworkModelHelper.crossConnectInterfaces(interface1, interface2);
			networkModel.getNetworkElements().add(connection);
		} else {
			connection = NetworkModelHelper.linkInterfaces(interface1, interface2, true);
			networkModel.getNetworkElements().add(connection);
		}

		// update topology in descriptor
		NetworkTopology topology = NetworkMapperModelToDescriptor.modelToDescriptor(networkModel);
		network.getResourceDescriptor().setNetworkTopology(topology);

		log.info("Created connection between interfaces " + interface1.getName() + " " + interface2.getName());

		return connection;
	}

	/**
	 * @param interface1
	 * @param interface2
	 * @return
	 */
	private NetworkConnection getConnectionBetweenInterfaces(Interface interface1, Interface interface2) {
		NetworkConnection connection = null;
		if (interface1.getSwitchedTo() != null &&
				(interface1.getSwitchedTo().getSource().equals(interface2) ||
				interface1.getSwitchedTo().getSink().equals(interface2))) {
			connection = interface1.getSwitchedTo();
		} else if (interface1.getLinkTo() != null &&
				(interface1.getLinkTo().getSource().equals(interface2) ||
				interface1.getLinkTo().getSink().equals(interface2))) {
			connection = interface1.getLinkTo();
		}

		return connection;
	}

	/**
	 * @param toRemove
	 * @param network
	 */
	private void removeConnection(NetworkConnection toRemove, IResource network) {
		log.info("Removing connection from network model");
		// update model
		NetworkModelHelper.deleteNetworkConnectionAndReferences(toRemove, (NetworkModel) network.getModel());

		// update topology in descriptor
		NetworkTopology topology = NetworkMapperModelToDescriptor.modelToDescriptor((NetworkModel) network.getModel());
		network.getResourceDescriptor().setNetworkTopology(topology);
	}

}
