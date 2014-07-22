package org.opennaas.extensions.router.capability.chassis;

/*
 * #%L
 * OpenNaaS :: Router :: Chassis Capability
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capabilities.api.helper.ChassisAPIHelper;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfo;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfoList;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.capabilities.api.model.chassis.LogicalRoutersNamesList;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.ModelHelper;

public class ChassisCapability extends AbstractCapability implements IChassisCapability {

	public static final String	CAPABILITY_TYPE	= "chassis";

	Log							log				= LogFactory.getLog(ChassisCapability.class);

	private String				resourceId		= "";

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public ChassisCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Chassis Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.caactivatepability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IChassisCapability.class.getName());
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
	// TODO MOVE TO A PARENT
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			// TODO do not use Activator for this, use injection instead
			return Activator.getChassisActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
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

	@Override
	public InterfacesNamesList getInterfacesNames() throws CapabilityException {
		InterfacesNamesList inl = new InterfacesNamesList();
		inl.setInterfaces(new ArrayList<String>());

		ComputerSystem model = (ComputerSystem) resource.getModel();
		List<NetworkPort> interfaces = ModelHelper.getInterfaces(model);
		List<ProtocolEndpoint> grePEPs = ModelHelper.getGREProtocolEndpoints(model);

		for (NetworkPort interf : interfaces) {
			inl.getInterfaces().add(ModelHelper.getInterfaceName(interf));
		}

		for (ProtocolEndpoint grePEP : grePEPs) {
			inl.getInterfaces().add(ModelHelper.getInterfaceName(grePEP));
		}

		return inl;
	}

	@Override
	public InterfaceInfo getInterfaceInfo(String interfaceName) throws CapabilityException {
		NetworkPort np = ModelHelper.getNetworkPortFromName(interfaceName, (ComputerSystem) resource.getModel());
		if (np == null) {
			ProtocolEndpoint grePEP = ModelHelper.getGREProtocolEndpointFromName(interfaceName, (ComputerSystem) resource.getModel());
			if (grePEP == null) {
				throw new CapabilityException("No interface found with given name: " + interfaceName);
			}

			return ChassisAPIHelper.getInterfaceInfo(grePEP);
		}

		return ChassisAPIHelper.getInterfaceInfo(np);
	}

	@Override
	public InterfaceInfoList getInterfacesInfo() {
		return ChassisAPIHelper.getInterfacesInfo(ChassisAPIHelper.getInterfacesInfo((ComputerSystem) resource.getModel()));
	}

	/*
	 * IChassisService Implementation
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.chassis.IChassisCapability#upPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void upPhysicalInterface(LogicalPort iface) throws CapabilityException {
		log.info("Start of upPhysicalInterface call");
		iface.setOperationalStatus(OperationalStatus.OK);

		IAction action = createActionAndCheckParams(ChassisActionSet.CONFIGURESTATUS, iface);
		queueAction(action);
		log.info("End of upPhysicalInterface call");

	}

	public void upPhysicalInterface(String ifaceName) throws CapabilityException {
		try {
			upPhysicalInterface(ChassisAPIHelper.interfaceName2LogicalPort(ifaceName));
		} catch (IllegalArgumentException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.router.capability.chassis.IChassisCapability#downPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void downPhysicalInterface(LogicalPort iface) throws CapabilityException {
		log.info("Start of downPhysicalInterface call");
		iface.setOperationalStatus(OperationalStatus.STOPPED);

		IAction action = createActionAndCheckParams(ChassisActionSet.CONFIGURESTATUS, iface);
		queueAction(action);
		log.info("End of downPhysicalInterface call");
	}

	public void downPhysicalInterface(@QueryParam("ifaceName") String ifaceName) throws CapabilityException {
		try {
			downPhysicalInterface(ChassisAPIHelper.interfaceName2LogicalPort(ifaceName));
		} catch (IllegalArgumentException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.chassis.IChassisCapability#createSubInterface(org.opennaas.extensions.router.model.NetworkPort)
	 */
	@Override
	public void createSubInterface(NetworkPort iface) throws CapabilityException {
		log.info("Start of createSubInterface call");
		IAction action = createActionAndCheckParams(ChassisActionSet.CONFIGURESUBINTERFACE, iface);
		queueAction(action);
		log.info("End of createSubInterface call");
	}

	public void createSubInterface(InterfaceInfo interfaceInfo) throws CapabilityException {
		try {
			createSubInterface(ChassisAPIHelper.interfaceInfo2NetworkPort(interfaceInfo));
		} catch (Exception e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.chassis.IChassisCapability#deleteSubInterface(org.opennaas.extensions.router.model.NetworkPort)
	 */
	@Override
	public void deleteSubInterface(NetworkPort iface) throws CapabilityException {
		log.info("Start of deleteSubInterface call");
		IAction action = createActionAndCheckParams(ChassisActionSet.DELETESUBINTERFACE, iface);
		queueAction(action);
		log.info("End of deleteSubInterface call");
	}

	public void deleteSubInterface(@QueryParam("ifaceName") String ifaceName) throws CapabilityException {
		try {
			deleteSubInterface(ChassisAPIHelper.subInterfaceName2NetworkPort(ifaceName));
		} catch (Exception e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.chassis.IChassisCapability#setEncapsulation(org.opennaas.extensions.router.model.LogicalPort,
	 * org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType)
	 */
	@Override
	public void setEncapsulation(LogicalPort iface, ProtocolIFType encapsulationType) throws CapabilityException {
		log.info("Start of setEncapsulation call");

		// check parameters
		if (ChassisAPIHelper.isLoopback(iface.getName())) {
			throw new CapabilityException("Encapsulation in loopback interfaces is not supported.");
		}

		if (encapsulationType.equals(ProtocolIFType.OTHER)) {
			throw new CapabilityException("Unsupported encapsulation type.");
		}

		if (!encapsulationType.equals(ProtocolIFType.UNKNOWN)) {
			ProtocolEndpoint encapsulationEndpoint = new ProtocolEndpoint();
			encapsulationEndpoint.setProtocolIFType(encapsulationType);
			iface.addProtocolEndpoint(encapsulationEndpoint);
		}

		removeCurrentEncapsulation(iface);
		setDesiredEncapsulation(iface);
		log.info("End of setEncapsulation call");
	}

	public void setEncapsulation(String ifaceName, String encapsulationType) throws CapabilityException {
		setEncapsulation(ChassisAPIHelper.string2LogicalPort(ifaceName), ChassisAPIHelper.string2ProtocolIFType(encapsulationType));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.router.capability.chassis.IChassisCapability#setEncapsulationLabel(org.opennaas.extensions.router.model.LogicalPort,
	 * java.lang.String)
	 */
	@Override
	public void setEncapsulationLabel(LogicalPort iface, String encapsulationLabel) throws CapabilityException {
		log.info("Start of setEncapsulationLabel call");

		// check parameters
		if (ChassisAPIHelper.isLoopback(iface.getName())) {
			throw new CapabilityException("Encapsulation in loopback interfaces is not supported.");
		}

		// specify label in iface
		// we use the name of the endpoint to store the encapsulation label
		// and mark protocolType as unknown (it will be discovered by opennaas)
		ProtocolEndpoint protocolEndpoint = new ProtocolEndpoint();
		protocolEndpoint.setName(encapsulationLabel);
		protocolEndpoint.setProtocolIFType(ProtocolIFType.UNKNOWN);
		iface.addProtocolEndpoint(protocolEndpoint);

		// FIXME it assumes there is only TAGGED_ETHERNET and NO encapsulation
		IAction action = createActionAndCheckParams(ChassisActionSet.SET_VLANID, iface);
		queueAction(action);
		log.info("End of setEncapsulationLabel call");
	}

	public void setEncapsulationLabel(String ifaceName, String encapsulationLabel) throws CapabilityException {
		setEncapsulationLabel(ChassisAPIHelper.subInterfaceName2NetworkPort(ifaceName), encapsulationLabel);
	}

	@Override
	public LogicalRoutersNamesList getLogicalRoutersNames() {
		LogicalRoutersNamesList logicalRoutersNamesList = new LogicalRoutersNamesList();
		logicalRoutersNamesList.setLogicalRouters(new ArrayList<String>());

		List<ComputerSystem> lrList = getLogicalRouters();
		for (ComputerSystem computerSystem : lrList) {
			logicalRoutersNamesList.getLogicalRouters().add(computerSystem.getName());
		}

		return logicalRoutersNamesList;
	}

	public List<ComputerSystem> getLogicalRouters() {
		return ModelHelper.getLogicalRouters((ComputerSystem) resource.getModel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.router.capability.chassis.IChassisCapability#createLogicalRouter(org.opennaas.extensions.router.model.ComputerSystem)
	 */
	@Override
	public void createLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException {
		log.info("Start of createLogicalRouter call");

		if (isVirtual(resource)) {
			throw new CapabilityException("UnsupportedOperation: Cannot create logical routers from a logical router");
		}

		IAction action = createActionAndCheckParams(ChassisActionSet.CREATELOGICALROUTER, logicalRouter);
		queueAction(action);

		List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		interfaces.addAll(ModelHelper.getInterfaces(logicalRouter));
		addInterfacesToLogicalRouter(logicalRouter, interfaces);
		log.info("End of createLogicalRouter call");
	}

	public void createLogicalRouter(String logicalRouterName, InterfacesNamesList interfacesNamesList) throws CapabilityException {
		createLogicalRouter(ChassisAPIHelper.logicalRouter2ComputerSystem(logicalRouterName, interfacesNamesList));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.router.capability.chassis.IChassisCapability#deleteLogicalRouter(org.opennaas.extensions.router.model.ComputerSystem)
	 */
	@Override
	public void deleteLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException {
		log.info("Start of deleteLogicalRouter call");

		if (isVirtual(resource)) {
			throw new CapabilityException("UnsupportedOperation: Cannot delete logical routers from a logical router");
		}
		// By default, interfaces are not transfered back to the physical router.
		// Instead, their configuration is lost. If the user wants to maintain them,
		// it can be done launching removeInterfacesFromLogicalRouter(desiredInterfaces) before deleting it.

		// List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		// interfaces.addAll(ModelHelper.getInterfaces(logicalRouter));
		// removeInterfacesFromLogicalRouter(logicalRouter, interfaces);

		IAction action = createActionAndCheckParams(ChassisActionSet.DELETELOGICALROUTER, logicalRouter);
		queueAction(action);
		log.info("E of deleteLogicalRouter call");
	}

	public void deleteLogicalRouter(@PathParam("logicalRouterName") String logicalRouterName) throws CapabilityException {
		deleteLogicalRouter(ChassisAPIHelper.logicalRouter2ComputerSystem(logicalRouterName, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.chassis.IChassisCapability#addInterfacesToLogicalRouter(org.opennaas.extensions.router.model.
	 * ComputerSystem, java.util.List)
	 */
	@Override
	public void addInterfacesToLogicalRouter(ComputerSystem logicalRouter, List<? extends LogicalPort> interfaces) throws CapabilityException {
		log.info("Start of addInterfacesToLogicalRouter call");

		if (isVirtual(resource)) {
			throw new CapabilityException("UnsupportedOperation: Cannot interact with logical routers from a logical router");
		}

		IAction action;
		ComputerSystem logicalRouterStub;
		for (LogicalPort interfaceToAdd : interfaces) {

			logicalRouterStub = new ComputerSystem();
			logicalRouterStub.setName(logicalRouter.getName());
			logicalRouterStub.setElementName(logicalRouter.getElementName());
			logicalRouterStub.addLogicalDevice(interfaceToAdd);

			action = createActionAndCheckParams(ChassisActionSet.ADDINTERFACETOLOGICALROUTER, logicalRouterStub);
			queueAction(action);
		}
		log.info("End of addInterfacesToLogicalRouter call");

	}

	public void addInterfacesToLogicalRouter(String logicalRouterName, InterfacesNamesList interfacesNamesList) throws CapabilityException {
		addInterfacesToLogicalRouter(ChassisAPIHelper.logicalRouter2ComputerSystem(logicalRouterName, null),
				ChassisAPIHelper.interfaceNameList2NetworkPortList(interfacesNamesList));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.router.capability.chassis.IChassisCapability#removeInterfacesFromLogicalRouter(org.opennaas.extensions.router.model
	 * .ComputerSystem, java.util.List)
	 */
	@Override
	public void removeInterfacesFromLogicalRouter(ComputerSystem logicalRouter, List<? extends LogicalPort> interfaces) throws CapabilityException {
		log.info("Start of removeInterfacesFromLogicalRouter call");

		if (isVirtual(resource)) {
			throw new CapabilityException("UnsupportedOperation: Cannot interact with logical routers from a logical router");
		}

		IAction action;
		ComputerSystem logicalRouterStub;
		for (LogicalPort interfaceToAdd : interfaces) {

			logicalRouterStub = new ComputerSystem();
			logicalRouterStub.setName(logicalRouter.getName());
			logicalRouterStub.setElementName(logicalRouter.getElementName());
			logicalRouterStub.addLogicalDevice(interfaceToAdd);

			action = createActionAndCheckParams(ChassisActionSet.REMOVEINTERFACEFROMLOGICALROUTER, logicalRouterStub);
			queueAction(action);
		}
		log.info("End of removeInterfacesFromLogicalRouter call");

	}

	public void removeInterfacesFromLogicalRouter(String logicalRouterName, InterfacesNamesList interfacesNamesList)
			throws CapabilityException {
		removeInterfacesFromLogicalRouter(ChassisAPIHelper.logicalRouter2ComputerSystem(logicalRouterName, null),
				ChassisAPIHelper.interfaceNameList2NetworkPortList(interfacesNamesList));
	}

	/**
	 * @param port
	 * @return
	 */
	private boolean requiresTaggedEthernetEncapsulation(LogicalPort port) {
		boolean hasTaggedEthernetEndpoint = false;
		for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
			if (endpoint.getProtocolIFType().equals(ProtocolIFType.LAYER_2_VLAN_USING_802_1Q)) {
				hasTaggedEthernetEndpoint = true;
				break;
			}
		}
		return hasTaggedEthernetEndpoint;
	}

	/**
	 * @param port
	 * @return
	 */
	private boolean requiresNoEncapsulation(LogicalPort port) {
		return port.getProtocolEndpoint().isEmpty();
	}

	/**
	 * @param port
	 * @throws CapabilityException
	 */
	private void removeCurrentEncapsulation(LogicalPort port) throws CapabilityException {
		// FIXME it assumes there is only TAGGED_ETHERNET and NO encapsulation

		if (requiresTaggedEthernetEncapsulation(port)) {
			// nothing to remove, as current can only be no encapsulation or required one
		} else {
			// it is save to remove TAGGED_ETHERNET, as current can only be tagged or none
			// and removing when it does not exists does not fail
			IAction action = createActionAndCheckParams(ChassisActionSet.REMOVE_TAGGEDETHERNET_ENCAPSULATION, port);
			queueAction(action);
		}
	}

	/**
	 * @param port
	 * @throws CapabilityException
	 */
	private void setDesiredEncapsulation(LogicalPort port) throws CapabilityException {
		if (requiresNoEncapsulation(port)) {
			// nothing to set
		} else {
			if (requiresTaggedEthernetEncapsulation(port)) {
				IAction action = createActionAndCheckParams(ChassisActionSet.SET_TAGGEDETHERNET_ENCAPSULATION, port);
				queueAction(action);
			} else {
				throw new CapabilityException("Unsupported encapsulation type");
			}
		}
	}

	private boolean isVirtual(IResource resource) {
		return resource.getResourceDescriptor().getProperties() != null &&
				resource.getResourceDescriptor().getProperties().get(ResourceDescriptor.VIRTUAL) != null &&
				resource.getResourceDescriptor().getProperties().get(ResourceDescriptor.VIRTUAL).equals("true");
	}

}
