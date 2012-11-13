package org.opennaas.extensions.router.capability.ospf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.wrappers.AddInterfacesInOSPFAreaRequest;
import org.opennaas.extensions.router.model.wrappers.RemoveInterfacesInOSPFAreaRequest;

/**
 * @author Isart Canyameres
 * @author Jordi Puig
 */
public class OSPFCapability extends AbstractCapability implements IOSPFCapability {

	public static String	CAPABILITY_TYPE	= "ospf";

	Log						log				= LogFactory.getLog(OSPFCapability.class);

	private String			resourceId		= "";

	/**
	 * OSPFCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public OSPFCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new OSPF Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IOSPFCapability.class.getName());
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
	 * @see org.opennaas.core.resources.capability.AbstractCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * Return the OSPF ActionSet
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getOSPFActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#configureOSPF(org.opennaas.extensions.router.model.OSPFService)
	 */
	@Override
	public void configureOSPF(OSPFService ospfService) throws CapabilityException {
		log.info("Start of configureOSPF call");
		ospfService.setEnabledState(EnabledState.DISABLED); // mark OSPF as disabled, we are configuring only
		IAction action = createActionAndCheckParams(OSPFActionSet.OSPF_CONFIGURE, ospfService);
		queueAction(action);
		log.info("End of configureOSPF call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#getOSPFConfiguration()
	 */
	@Override
	public void getOSPFConfiguration() throws CapabilityException {
		log.info("Start of getOSPFConfiguration call");
		IAction action = createActionAndCheckParams(OSPFActionSet.OSPF_GET_CONFIGURATION, null);
		queueAction(action);
		log.info("End of getOSPFConfiguration call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#activateOSPF()
	 */
	@Override
	public void activateOSPF() throws CapabilityException {
		log.info("Start of activateOSPF call");
		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.ENABLED);
		IAction action = createActionAndCheckParams(OSPFActionSet.OSPF_ACTIVATE, service);
		queueAction(action);
		log.info("End of activateOSPF call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#deactivateOSPF()
	 */
	@Override
	public void deactivateOSPF() throws CapabilityException {
		log.info("Start of deactivateOSPF call");
		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.DISABLED);
		IAction action = createActionAndCheckParams(OSPFActionSet.OSPF_DEACTIVATE, service);
		queueAction(action);
		log.info("End of deactivateOSPF call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#clearOSPFconfiguration(org.opennaas.extensions.router.model.OSPFService)
	 */
	@Override
	public void clearOSPFconfiguration(OSPFService ospfService) throws CapabilityException {
		log.info("Start of clearOSPFconfiguration call");
		IAction action = createActionAndCheckParams(OSPFActionSet.OSPF_CLEAR, ospfService);
		queueAction(action);
		log.info("End of clearOSPFconfiguration call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#configureOSPFArea(org.opennaas.extensions.router.model.OSPFAreaConfiguration)
	 */
	@Override
	public void configureOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {
		log.info("Start of configureOSPFArea call");
		IAction action = createActionAndCheckParams(OSPFActionSet.OSPF_CONFIGURE_AREA, ospfAreaConfiguration);
		queueAction(action);
		log.info("End of configureOSPFArea call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#removeOSPFArea(org.opennaas.extensions.router.model.OSPFAreaConfiguration)
	 */
	@Override
	public void removeOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {
		log.info("Start removeOSPFArea call");
		IAction action = createActionAndCheckParams(OSPFActionSet.OSPF_REMOVE_AREA, ospfAreaConfiguration);
		queueAction(action);
		log.info("End removeOSPFArea call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFCapability#addInterfacesInOSPFArea(org.opennaas.extensions.router.model.wrappers.
	 * AddInterfacesOSPFRequest)
	 */
	@Override
	public void addInterfacesInOSPFArea(AddInterfacesInOSPFAreaRequest request) throws CapabilityException {
		addInterfacesInOSPFArea(request.getInterfaces(), request.getOspfArea());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#addInterfacesInOSPFArea(java.util.List,
	 * org.opennaas.extensions.router.model.OSPFArea)
	 */
	@Override
	public void addInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {
		log.info("Start of addInterfacesInOSPFArea call");
		IAction action = null;

		// create a copy of ospfArea with only the interfaces to add
		OSPFArea area = new OSPFArea();
		area.setAreaID(ospfArea.getAreaID());
		area.setAreaType(ospfArea.getAreaType());
		area.setConfiguration(ospfArea.getConfiguration());

		OSPFProtocolEndpoint ospfPep;
		for (LogicalPort logicalPort : interfaces) {
			ospfPep = new OSPFProtocolEndpoint();
			if (logicalPort instanceof NetworkPort) {
				ospfPep.setName(logicalPort.getName() + "." + ((NetworkPort) logicalPort).getPortNumber());
			} else {
				ospfPep.setName(logicalPort.getName());
			}
			area.addEndpointInArea(ospfPep);
		}

		action = createActionAndCheckParams(OSPFActionSet.OSPF_ADD_INTERFACE_IN_AREA, area);
		queueAction(action);
		log.info("End of addInterfacesInOSPFArea call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFCapability#removeInterfacesInOSPFArea(org.opennaas.extensions.router.model.wrappers.
	 * AddInterfacesOSPFRequest)
	 */
	@Override
	public void removeInterfacesInOSPFArea(RemoveInterfacesInOSPFAreaRequest request) throws CapabilityException {
		removeInterfacesInOSPFArea(request.getInterfaces(), request.getOspfArea());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#removeInterfacesInOSPFArea(java.util.List,
	 * org.opennaas.extensions.router.model.OSPFArea)
	 */
	@Override
	public void removeInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {
		log.info("Start of removeInterfacesInOSPFArea call");
		IAction action = null;

		// create a copy of ospfArea with only the interfaces to remove
		OSPFArea area = new OSPFArea();
		area.setAreaID(ospfArea.getAreaID());
		area.setAreaType(ospfArea.getAreaType());
		area.setConfiguration(ospfArea.getConfiguration());

		OSPFProtocolEndpoint ospfPep;
		for (LogicalPort logicalPort : interfaces) {
			ospfPep = new OSPFProtocolEndpoint();
			if (logicalPort instanceof NetworkPort) {
				ospfPep.setName(logicalPort.getName() + "." + ((NetworkPort) logicalPort).getPortNumber());
			} else {
				ospfPep.setName(logicalPort.getName());
			}
			area.addEndpointInArea(ospfPep);
		}

		action = createActionAndCheckParams(OSPFActionSet.OSPF_REMOVE_INTERFACE_IN_AREA, area);
		queueAction(action);
		log.info("End of removeInterfacesInOSPFArea call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#enableOSPFInterfaces(java.util.List)
	 */
	@Override
	public void enableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {
		log.info("Start of enableOSPFInterfaces call");
		IAction action = null;

		// mark OSPFProtocolEndpoints to enable
		List<OSPFProtocolEndpoint> toEnable = new ArrayList<OSPFProtocolEndpoint>(interfaces.size());
		OSPFProtocolEndpoint enablePep;
		for (OSPFProtocolEndpoint pep : interfaces) {
			enablePep = new OSPFProtocolEndpoint();
			enablePep.setEnabledState(EnabledState.ENABLED);
			enablePep.setName(pep.getName());
			enablePep.setOSPFArea(pep.getOSPFArea());
			if (!pep.getLogicalPorts().isEmpty())
				enablePep.addLogiaclPort(pep.getLogicalPorts().get(0));
			toEnable.add(enablePep);
		}

		action = createActionAndCheckParams(OSPFActionSet.OSPF_ENABLE_INTERFACE, toEnable);
		queueAction(action);
		log.info("End of enableOSPFInterfaces call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#disableOSPFInterfaces(java.util.List)
	 */
	@Override
	public void disableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {
		log.info("Start of disableOSPFInterfaces call");
		IAction action = null;

		// mark OSPFProtocolEndpoints to disable
		List<OSPFProtocolEndpoint> toDisable = new ArrayList<OSPFProtocolEndpoint>(interfaces.size());
		OSPFProtocolEndpoint disabledPep;
		for (OSPFProtocolEndpoint pep : interfaces) {
			disabledPep = new OSPFProtocolEndpoint();
			disabledPep.setEnabledState(EnabledState.DISABLED);
			disabledPep.setName(pep.getName());
			disabledPep.setOSPFArea(pep.getOSPFArea());
			if (!pep.getLogicalPorts().isEmpty())
				disabledPep.addLogiaclPort(pep.getLogicalPorts().get(0));
			toDisable.add(disabledPep);
		}

		action = createActionAndCheckParams(OSPFActionSet.OSPF_DISABLE_INTERFACE, toDisable);
		queueAction(action);
		log.info("End of disableOSPFInterfaces call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospf.IOSPFService#showOSPFConfiguration()
	 */
	@Override
	public OSPFService showOSPFConfiguration() throws CapabilityException {
		log.info("Start of showOSPFConfiguration call");
		OSPFService ospfService = null;
		List<Service> lServices = ((ComputerSystem) resource.getModel()).getHostedService();
		if (lServices == null || lServices.isEmpty()) {
			return null;
		}

		// Search OSPF Service in the Service list
		for (Service service : lServices) {
			if (service instanceof OSPFService) {
				ospfService = (OSPFService) service;
				break;
			}
		}
		log.info("End of showOSPFConfiguration call");
		return ospfService;
	}

	/**
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

}
