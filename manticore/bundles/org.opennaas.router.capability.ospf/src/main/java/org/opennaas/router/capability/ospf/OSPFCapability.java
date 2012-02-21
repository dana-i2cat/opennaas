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

		log.debug("Sending message to OSPF Capability");
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

		return Response.okResponse(idOperation);
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
	public Object configureOSPF(OSPFService ospfService) {

		return sendMessage(ActionConstants.OSPF_CONFIGURE, ospfService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#getOSPFConfiguration()
	 */
	@Override
	public OSPFService getOSPFConfiguration() {

		return (OSPFService) sendMessage(ActionConstants.OSPF_GET_CONFIGURATION, null);
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
		// If hosted services is null or empty throw Exception
		if (lServices == null || lServices.size() <= 0) {
			throw new CapabilityException("No hosted services in this model.");
		} else {
			// Search OSPF Service in the Service list
			for (Service service : lServices) {
				if (service instanceof OSPFService) {
					ospfService = (OSPFService) service;
					break;
				}
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
	public Object activateOSPF() throws CapabilityException {
		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.ENABLED);
		return sendMessage(ActionConstants.OSPF_ACTIVATE, service);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#deactivateOSPF()
	 */
	@Override
	public Object deactivateOSPF() throws CapabilityException {
		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.DISABLED);
		return sendMessage(ActionConstants.OSPF_DEACTIVATE, service);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#clearOSPFconfiguration(net.i2cat.mantychore.model.OSPFService)
	 */
	@Override
	public Object clearOSPFconfiguration(OSPFService ospfService) throws CapabilityException {
		return sendMessage(ActionConstants.OSPF_CLEAR, ospfService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#configureOSPFArea(net.i2cat.mantychore.model.OSPFAreaConfiguration)
	 */
	@Override
	public Object configureOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {
		return sendMessage(ActionConstants.OSPF_CONFIGURE_AREA, ospfAreaConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#removeOSPFArea(net.i2cat.mantychore.model.OSPFAreaConfiguration)
	 */
	@Override
	public Object removeOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {
		return sendMessage(ActionConstants.OSPF_REMOVE_AREA, ospfAreaConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#addInterfacesInOSPFArea(java.util.List, net.i2cat.mantychore.model.OSPFArea)
	 */
	@Override
	public Object addInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {

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

		return sendMessage(ActionConstants.OSPF_ADD_INTERFACE_IN_AREA, area);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#removeInterfacesInOSPFArea(java.util.List, net.i2cat.mantychore.model.OSPFArea)
	 */
	@Override
	public Object removeInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {

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

		return sendMessage(ActionConstants.OSPF_REMOVE_INTERFACE_IN_AREA, area);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#enableOSPFInterfaces(java.util.List)
	 */
	@Override
	public Object enableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {

		// mark OSPFProtocolEndpoints to enable
		List<OSPFProtocolEndpoint> toDisable = new ArrayList<OSPFProtocolEndpoint>(interfaces.size());
		OSPFProtocolEndpoint disabledPep;
		for (OSPFProtocolEndpoint pep : interfaces) {
			disabledPep = new OSPFProtocolEndpoint();
			disabledPep.setEnabledState(EnabledState.ENABLED);
			disabledPep.setName(pep.getName());
			disabledPep.setOSPFArea(pep.getOSPFArea());
			disabledPep.addLogiaclPort(pep.getLogicalPorts().get(0));
			toDisable.add(disabledPep);
		}

		return sendMessage(ActionConstants.OSPF_ENABLE_INTERFACE, toDisable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#disableOSPFInterfaces(java.util.List)
	 */
	@Override
	public Object disableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {

		// mark OSPFProtocolEndpoints to disable
		List<OSPFProtocolEndpoint> toDisable = new ArrayList<OSPFProtocolEndpoint>(interfaces.size());
		OSPFProtocolEndpoint disabledPep;
		for (OSPFProtocolEndpoint pep : interfaces) {
			disabledPep = new OSPFProtocolEndpoint();
			disabledPep.setEnabledState(EnabledState.DISABLED);
			disabledPep.setName(pep.getName());
			disabledPep.setOSPFArea(pep.getOSPFArea());
			disabledPep.addLogiaclPort(pep.getLogicalPorts().get(0));
			toDisable.add(disabledPep);
		}

		return sendMessage(ActionConstants.OSPF_DISABLE_INTERFACE, toDisable);
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
