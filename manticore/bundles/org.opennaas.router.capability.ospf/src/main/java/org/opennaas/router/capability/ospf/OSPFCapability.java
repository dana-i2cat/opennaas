package org.opennaas.router.capability.ospf;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EnabledLogicalElement.EnabledState;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.OSPFArea;
import net.i2cat.mantychore.model.OSPFAreaConfiguration;
import net.i2cat.mantychore.model.OSPFProtocolEndpoint;
import net.i2cat.mantychore.model.OSPFService;
import net.i2cat.mantychore.model.Service;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;

/**
 * @author Isart Canyameres
 * @author Jordi Puig
 */
public class OSPFCapability extends AbstractCapability implements IOSPFService {

	public static String	CAPABILITY_NAME	= "ospf";

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

	/**
	 * Execute the action defined in the idOperation param
	 * 
	 * @param idOperation
	 * @param params
	 */
	@Override
	public Object sendMessage(String idOperation, Object params) {

		// log.debug("Sending message to OSPF Capability");
		try {
			IQueueManagerService queueManager = Activator.getQueueManagerService(resourceId);
			IAction action = createAction(idOperation);
			action.setParams(params);
			action.setModelToUpdate(resource.getModel());
			queueManager.queueAction(action);

		} catch (Exception e) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
			return Response.errorResponse(idOperation, errorMsgs);
		}

		return Response.queuedResponse(idOperation);
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
	 * @see org.opennaas.router.capability.ospf.IOSPFService#configureOSPF(net.i2cat.mantychore.model.OSPFService)
	 */
	@Override
	public Response configureOSPF(OSPFService ospfService) {
		ospfService.setEnabledState(EnabledState.DISABLED); // mark OSPF as disabled, we are configuring only
		return (Response) sendMessage(ActionConstants.OSPF_CONFIGURE, ospfService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#getOSPFConfiguration()
	 */
	@Override
	public Response getOSPFConfiguration() {

		return (Response) sendMessage(ActionConstants.OSPF_GET_CONFIGURATION, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#showOSPFConfiguration()
	 */
	@Override
	public OSPFService showOSPFConfiguration() throws CapabilityException {

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
		return ospfService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#activateOSPF()
	 */
	@Override
	public Response activateOSPF() throws CapabilityException {

		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.ENABLED);
		return (Response) sendMessage(ActionConstants.OSPF_ACTIVATE, service);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#deactivateOSPF()
	 */
	@Override
	public Response deactivateOSPF() throws CapabilityException {

		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.DISABLED);
		return (Response) sendMessage(ActionConstants.OSPF_DEACTIVATE, service);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#clearOSPFconfiguration(net.i2cat.mantychore.model.OSPFService)
	 */
	@Override
	public Response clearOSPFconfiguration(OSPFService ospfService) throws CapabilityException {

		return (Response) sendMessage(ActionConstants.OSPF_CLEAR, ospfService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#configureOSPFArea(net.i2cat.mantychore.model.OSPFAreaConfiguration)
	 */
	@Override
	public Response configureOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {

		return (Response) sendMessage(ActionConstants.OSPF_CONFIGURE_AREA, ospfAreaConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#removeOSPFArea(net.i2cat.mantychore.model.OSPFAreaConfiguration)
	 */
	@Override
	public Response removeOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {

		return (Response) sendMessage(ActionConstants.OSPF_REMOVE_AREA, ospfAreaConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#addInterfacesInOSPFArea(java.util.List, net.i2cat.mantychore.model.OSPFArea)
	 */
	@Override
	public Response addInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {

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

		return (Response) sendMessage(ActionConstants.OSPF_ADD_INTERFACE_IN_AREA, area);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#removeInterfacesInOSPFArea(java.util.List, net.i2cat.mantychore.model.OSPFArea)
	 */
	@Override
	public Response removeInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {

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

		return (Response) sendMessage(ActionConstants.OSPF_REMOVE_INTERFACE_IN_AREA, area);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#enableOSPFInterfaces(java.util.List)
	 */
	@Override
	public Response enableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {

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

		return (Response) sendMessage(ActionConstants.OSPF_ENABLE_INTERFACE, toEnable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#disableOSPFInterfaces(java.util.List)
	 */
	@Override
	public Response disableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {

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

		return (Response) sendMessage(ActionConstants.OSPF_DISABLE_INTERFACE, toDisable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activateCapability()
	 */
	@Override
	protected void activateCapability() throws CapabilityException {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivateCapability()
	 */
	@Override
	protected void deactivateCapability() throws CapabilityException {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#initializeCapability()
	 */
	@Override
	protected void initializeCapability() throws CapabilityException {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#shutdownCapability()
	 */
	@Override
	protected void shutdownCapability() throws CapabilityException {
		// Nothing to do
	}

}
