package org.opennaas.extensions.router.capability.ospfv3;

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
import org.opennaas.extensions.router.model.RouteCalculationService.AlgorithmType;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.wrappers.AddInterfacesInOSPFAreaRequest;
import org.opennaas.extensions.router.model.wrappers.RemoveInterfacesInOSPFAreaRequest;

public class OSPFv3Capability extends AbstractCapability implements IOSPFv3Capability {

	public static final String	CAPABILITY_TYPE	= "ospfv3";

	Log							log				= LogFactory.getLog(OSPFv3Capability.class);

	private String				resourceId		= "";

	public OSPFv3Capability(CapabilityDescriptor capabilityDescriptor, String resourceId) {
		super(capabilityDescriptor);
		this.resourceId = resourceId;
		log.debug("Built new OSPFv3 Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#activateOSPFv3()
	 */
	@Override
	public void activateOSPFv3() throws CapabilityException {
		log.info("Start of activateOSPFv3 call");
		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.ENABLED);
		service.setAlgorithmType(AlgorithmType.OSPFV3);
		IAction action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_ACTIVATE, service);
		queueAction(action);
		log.info("End of activateOSPFv3 call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#deactivateOSPFv3()
	 */
	@Override
	public void deactivateOSPFv3() throws CapabilityException {
		log.info("Start of deactivateOSPFv3 call");
		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.DISABLED);
		service.setAlgorithmType(AlgorithmType.OSPFV3);
		IAction action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_DEACTIVATE, service);
		queueAction(action);
		log.info("End of deactivateOSPFv3 call");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#configureOSPF(org.opennaas.extensions.router.model.OSPFService)
	 */
	@Override
	public void configureOSPFv3(OSPFService ospfService) throws CapabilityException {
		log.info("Start of configureOSPFv3 call");
		ospfService.setEnabledState(EnabledState.DISABLED); // mark OSPF as disabled, we are configuring only
		ospfService.setAlgorithmType(AlgorithmType.OSPFV3);
		IAction action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_CONFIGURE, ospfService);
		queueAction(action);
		log.info("End of configureOSPFv3 call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#clearOSPFv3configuration(org.opennaas.extensions.router.model.OSPFService)
	 */
	@Override
	public void clearOSPFv3configuration(OSPFService ospfService) throws CapabilityException {
		log.info("Start of clearOSPFv3configuration call");
		IAction action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_CLEAR, ospfService);
		queueAction(action);
		log.info("End of clearOSPFv3configuration call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.router.capability.ospfv3.IOSPFService#configureOSPFv3Area(org.opennaas.extensions.router.model.OSPFAreaConfiguration)
	 */
	@Override
	public void configureOSPFv3Area(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {
		log.info("Start of configureOSPFv3Area call");
		IAction action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_CONFIGURE_AREA, ospfAreaConfiguration);
		queueAction(action);
		log.info("End of configureOSPFv3Area call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv4.IOSPFService#removeOSPFv3Area(org.opennaas.extensions.router.model.OSPFAreaConfiguration)
	 */
	@Override
	public void removeOSPFv3Area(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {
		log.info("Start removeOSPFv3Area call");
		IAction action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_REMOVE_AREA, ospfAreaConfiguration);
		queueAction(action);
		log.info("End removeOSPFv3Area call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFCapability#addInterfacesInOSPFv3Area(org.opennaas.extensions.router.model.wrappers.
	 * AddInterfacesOSPFRequest)
	 */
	@Override
	public void addInterfacesInOSPFv3Area(AddInterfacesInOSPFAreaRequest request) throws CapabilityException {
		addInterfacesInOSPFv3Area(request.getInterfaces(), request.getOspfArea());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#addInterfacesInOSPFv3Area(java.util.List,
	 * org.opennaas.extensions.router.model.OSPFArea)
	 */
	@Override
	public void addInterfacesInOSPFv3Area(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {
		log.info("Start of addInterfacesInOSPFv3Area call");
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

		action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_ADD_INTERFACE_IN_AREA, area);
		queueAction(action);
		log.info("End of addInterfacesInOSPFv3Area call");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.router.capability.ospfv3.IOSPFCapability#removeInterfacesInOSPFv3Area(org.opennaas.extensions.router.model.wrappers.
	 * AddInterfacesOSPFRequest)
	 */
	@Override
	public void removeInterfacesInOSPFv3Area(RemoveInterfacesInOSPFAreaRequest request) throws CapabilityException {
		removeInterfacesInOSPFv3Area(request.getInterfaces(), request.getOspfArea());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#removeInterfacesInOSPFv3Area(java.util.List,
	 * org.opennaas.extensions.router.model.OSPFArea)
	 */
	@Override
	public void removeInterfacesInOSPFv3Area(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {
		log.info("Start of removeInterfacesInOSPFv3Area call");
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

		action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_REMOVE_INTERFACE_IN_AREA, area);
		queueAction(action);
		log.info("End of removeInterfacesInOSPFv3Area call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#enableOSPFv3Interfaces(java.util.List)
	 */
	@Override
	public void enableOSPFv3Interfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {
		log.info("Start of enableOSPFv3Interfaces call");
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

		action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_ENABLE_INTERFACE, toEnable);
		queueAction(action);
		log.info("End of enableOSPFv3Interfaces call");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#disableOSPFInterfaces(java.util.List)
	 */
	@Override
	public void disableOSPFv3Interfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {
		log.info("Start of disableOSPFv3Interfaces call");
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

		action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_DISABLE_INTERFACE, toDisable);
		queueAction(action);
		log.info("End of disableOSPFv3Interfaces call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#getOSPFConfiguration()
	 */
	@Override
	public void getOSPFv3Configuration() throws CapabilityException {
		log.info("Start of getOSPFv3Configuration call");
		IAction action = createActionAndCheckParams(OSPFv3ActionSet.OSPFv3_GET_CONFIGURATION, null);
		queueAction(action);
		log.info("End of getOSPFv3Configuration call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ospfv3.IOSPFService#showOSPFConfiguration()
	 */
	@Override
	public OSPFService showOSPFv3Configuration() throws CapabilityException {
		log.info("Start of showOSPFv3Configuration call");
		OSPFService ospfService = null;
		List<Service> lServices = ((ComputerSystem) resource.getModel()).getHostedService();
		if (lServices == null || lServices.isEmpty()) {
			return null;
		}

		// Search OSPF Service in the Service list
		for (Service service : lServices) {
			if (service instanceof OSPFService) {
				OSPFService ospf = (OSPFService) service;
				if (ospf.getAlgorithmType().equals(AlgorithmType.OSPFV3)) {
					ospfService = ospf;
					break;
				}
			}
		}
		log.info("End of showOSPFv3Configuration call");
		return ospfService;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getOSPFv3ActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;

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
