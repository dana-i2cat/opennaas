package org.opennaas.extensions.router.capability.ip;

/*
 * #%L
 * OpenNaaS :: Router :: IP Capability
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.ModelElementNotFoundException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capabilities.api.helper.ChassisAPIHelper;
import org.opennaas.extensions.router.capabilities.api.helper.IPApi2ModelTranslator;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.capabilities.api.model.ip.IPAddresses;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.model.utils.ModelHelper;

public class IPCapability extends AbstractCapability implements IIPCapability {

	public static final String	CAPABILITY_TYPE				= "ip";
	public static final String	INVALID_ADDRESS_ERROR_MSG	= "Invalid address format.";

	public final static String	IP							= CAPABILITY_TYPE;

	Log							log							= LogFactory.getLog(IPCapability.class);

	private String				resourceId					= "";

	public IPCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new IP Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IIPCapability.class.getName());
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
	 * @see org.opennaas.core.resources.capability.AbstractCapability#getActionSet()
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getIPActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}

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

	// ********************************
	// * IIPCapability implementation *
	// ********************************

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
	public String getDescription(String interfaceName) throws ModelElementNotFoundException {

		LogicalPort port;
		if (ChassisAPIHelper.isPhysicalInterface(interfaceName)) {
			throw new IllegalArgumentException("Not implemented. Physical interfaces not stored in model.");
			// port = ModelHelper.getLogicalPortFromName(interfaceName, (ComputerSystem) resource.getModel());
		} else {
			port = ModelHelper.getNetworkPortFromName(interfaceName, (ComputerSystem) resource.getModel());
		}
		if (port == null)
			throw new ModelElementNotFoundException("Couldn't find interface " + interfaceName);

		return port.getDescription();
	}

	@Override
	public void setInterfaceDescription(String interfaceName, String description) throws CapabilityException {
		LogicalPort port;
		if (ChassisAPIHelper.isPhysicalInterface(interfaceName)) {
			port = ChassisAPIHelper.interfaceName2LogicalPort(interfaceName);
		} else {
			port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		}

		port.setDescription(description);
		setInterfaceDescription(port);
	}

	@Override
	public IPAddresses getIPs(String interfaceName) throws ModelElementNotFoundException {
		List<IPProtocolEndpoint> ips;

		NetworkPort port = ModelHelper.getNetworkPortFromName(interfaceName, (ComputerSystem) resource.getModel());
		if (port == null)
			throw new ModelElementNotFoundException("Couldn't find interface " + interfaceName);

		ips = port.getProtocolEndpointsByType(IPProtocolEndpoint.class);

		return IPApi2ModelTranslator.ipPEPs2IPAddresses(ips);
	}

	@Override
	public void setIPv4(String interfaceName, String ipv4Address) throws CapabilityException {
		if (!IPUtilsHelper.isIPv4ValidAddress(ipv4Address))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPv4ProtocolEndpoint(ipv4Address);
		setIPv4(port, ipPEP);
	}

	@Override
	public void setIPv6(String interfaceName, String ipv6Address) throws CapabilityException {
		if (!IPUtilsHelper.isIPv6ValidAddress(ipv6Address))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPv6ProtocolEndpoint(ipv6Address);
		setIPv6(port, ipPEP);
	}

	@Override
	public void setIP(String interfaceName, String ipAddress) throws CapabilityException {
		if (!IPUtilsHelper.isIPValidAddress(ipAddress))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPProtocolEndpoint(ipAddress);
		setIP(port, ipPEP);
	}

	@Override
	public void addIPv4(String interfaceName, String ipv4Address) throws CapabilityException {
		if (!IPUtilsHelper.isIPv4ValidAddress(ipv4Address))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPv4ProtocolEndpoint(ipv4Address);
		addIPv4(port, ipPEP);
	}

	@Override
	public void addIPv6(String interfaceName, String ipv6Address) throws CapabilityException {
		if (!IPUtilsHelper.isIPv6ValidAddress(ipv6Address))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPv6ProtocolEndpoint(ipv6Address);
		addIPv6(port, ipPEP);

	}

	@Override
	public void addIP(String interfaceName, String ipAddress) throws CapabilityException {
		if (!IPUtilsHelper.isIPValidAddress(ipAddress))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPProtocolEndpoint(ipAddress);
		addIP(port, ipPEP);
	}

	@Override
	public void removeIPv4(String interfaceName, String ipv4Address) throws CapabilityException {
		if (!IPUtilsHelper.isIPv4ValidAddress(ipv4Address))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPv4ProtocolEndpoint(ipv4Address);
		removeIPv4(port, ipPEP);
	}

	@Override
	public void removeIPv6(String interfaceName, String ipv6Address) throws CapabilityException {
		if (!IPUtilsHelper.isIPv6ValidAddress(ipv6Address))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPv6ProtocolEndpoint(ipv6Address);
		removeIPv6(port, ipPEP);
	}

	@Override
	public void removeIP(String interfaceName, String ipAddress) throws CapabilityException {
		if (!IPUtilsHelper.isIPValidAddress(ipAddress))
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		NetworkPort port = ChassisAPIHelper.subInterfaceName2NetworkPort(interfaceName);
		IPProtocolEndpoint ipPEP = buildIPProtocolEndpoint(ipAddress);
		removeIP(port, ipPEP);
	}

	// ******************************************************************
	// * IIPCapability implementation using model objects as parameters *
	// ******************************************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ip.IIPCapability#setInterfaceDescription(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void setInterfaceDescription(LogicalPort iface) throws CapabilityException {
		log.info("Start of setInterfaceDescription call");
		IAction action = createActionAndCheckParams(IPActionSet.SET_INTERFACE_DESCRIPTION, iface);
		queueAction(action);
		log.info("End of setInterfaceDescription call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ip.IIPCapability#setIPv4(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void setIPv4(LogicalDevice iface, IPProtocolEndpoint ipProtocolEndpoint) throws CapabilityException {
		log.info("Start of setIPv4 call");

		if (ChassisAPIHelper.isLoopback(iface.getName())) {
			throw new CapabilityException("Configuration for Loopback interface not allowed");
		}

		// copy of iface
		NetworkPort param = new NetworkPort();
		param.setName(iface.getName());
		if (iface instanceof NetworkPort) {
			param.setPortNumber(((NetworkPort) iface).getPortNumber());
			param.setLinkTechnology(((NetworkPort) iface).getLinkTechnology());
		}

		// copy of ipProtocolEndpoint
		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();
		ipEndpoint.setIPv4Address(ipProtocolEndpoint.getIPv4Address());
		ipEndpoint.setSubnetMask(ipProtocolEndpoint.getSubnetMask());
		ipEndpoint.setProtocolIFType(ipProtocolEndpoint.getProtocolIFType());

		param.addProtocolEndpoint(ipEndpoint);

		IAction action = createActionAndCheckParams(IPActionSet.SET_IPv4, param);
		queueAction(action);
		log.info("End of setIPv4 call");
	}

	@Override
	public void setIPv6(LogicalDevice iface, IPProtocolEndpoint ipProtocolEndpoint) throws CapabilityException {
		log.info("Start of setIPv6 call");

		if (ChassisAPIHelper.isLoopback(iface.getName())) {
			throw new CapabilityException("Configuration for Loopback interface not allowed");
		}

		NetworkPort param = new NetworkPort();
		param.setName(iface.getName());
		if (iface instanceof NetworkPort) {
			param.setPortNumber(((NetworkPort) iface).getPortNumber());
			param.setLinkTechnology(((NetworkPort) iface).getLinkTechnology());
		}
		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();
		ipEndpoint.setIPv6Address(ipProtocolEndpoint.getIPv6Address());
		ipEndpoint.setPrefixLength(ipProtocolEndpoint.getPrefixLength());
		ipEndpoint.setProtocolIFType(ipProtocolEndpoint.getProtocolIFType());

		param.addProtocolEndpoint(ipEndpoint);

		IAction action = createActionAndCheckParams(IPActionSet.SET_IPv6, param);
		queueAction(action);
		log.info("End of setIPv6 call");
	}

	@Override
	public void setIP(LogicalDevice iface, String ipAddress) throws CapabilityException {
		log.info("Start of setIP call");

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();

		if (IPUtilsHelper.isIPv4ValidAddress(ipAddress)) {

			ipEndpoint = buildIPv4ProtocolEndpoint(ipAddress);
			setIPv4(iface, ipEndpoint);

		} else if (IPUtilsHelper.isIPv6ValidAddress(ipAddress)) {

			ipEndpoint = buildIPv6ProtocolEndpoint(ipAddress);
			setIPv6(iface, ipEndpoint);
		}
		else
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		log.info("End of setIP call");
	}

	@Override
	public void setIP(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException {
		log.info("Start of setIP call");

		if ((ip.getIPv4Address() != null) && (ip.getSubnetMask() != null) && !(ip.getIPv4Address().isEmpty()) && !(ip.getSubnetMask().isEmpty()))
			setIPv4(logicalDevice, ip);
		else if (ip.getIPv6Address() != null && !ip.getIPv6Address().isEmpty())
			setIPv6(logicalDevice, ip);
		else
			throw new CapabilityException("IP address not set.");

		log.info("End of setIP call");

	}

	@Override
	public void addIPv4(LogicalDevice iface, IPProtocolEndpoint ipProtocolEndpoint) throws CapabilityException {
		log.info("Start of addIPv4 call");

		if (ChassisAPIHelper.isLoopback(iface.getName())) {
			throw new CapabilityException("Configuration for Loopback interface not allowed");
		}

		NetworkPort param = new NetworkPort();
		param.setName(iface.getName());
		if (iface instanceof NetworkPort) {
			param.setPortNumber(((NetworkPort) iface).getPortNumber());
			param.setLinkTechnology(((NetworkPort) iface).getLinkTechnology());
		}

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();
		ipEndpoint.setIPv4Address(ipProtocolEndpoint.getIPv4Address());
		ipEndpoint.setSubnetMask(ipProtocolEndpoint.getSubnetMask());

		param.addProtocolEndpoint(ipEndpoint);

		IAction action = createActionAndCheckParams(IPActionSet.ADD_IPv4, param);
		queueAction(action);
		log.info("End of addIPv4 call");
	}

	@Override
	public void addIPv6(LogicalDevice iface, IPProtocolEndpoint ipProtocolEndpoint) throws CapabilityException {
		log.info("Start of addIPv6 call");

		if (ChassisAPIHelper.isLoopback(iface.getName())) {
			throw new CapabilityException("Configuration for Loopback interface not allowed");
		}

		NetworkPort param = new NetworkPort();
		param.setName(iface.getName());
		if (iface instanceof NetworkPort) {
			param.setPortNumber(((NetworkPort) iface).getPortNumber());
			param.setLinkTechnology(((NetworkPort) iface).getLinkTechnology());
		}

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();
		ipEndpoint.setIPv6Address(ipProtocolEndpoint.getIPv6Address());
		ipEndpoint.setPrefixLength(ipProtocolEndpoint.getPrefixLength());

		param.addProtocolEndpoint(ipEndpoint);

		IAction action = createActionAndCheckParams(IPActionSet.ADD_IPv6, param);
		queueAction(action);
		log.info("End of addIPv6 call");

	}

	@Override
	public void addIP(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException {

		log.info("Start of addIP call");

		if ((ip.getIPv4Address() != null) && (ip.getSubnetMask() != null) && !(ip.getIPv4Address().isEmpty()) && !(ip.getSubnetMask().isEmpty()))
			addIPv4(logicalDevice, ip);
		else if (ip.getIPv6Address() != null && !ip.getIPv6Address().isEmpty())
			addIPv6(logicalDevice, ip);
		else
			throw new CapabilityException("IP address not set.");
		log.info("End of addIP call");

	}

	@Override
	public void addIP(LogicalDevice iface, String ipAddress) throws CapabilityException {
		log.info("Start of addIP call");

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();

		if (IPUtilsHelper.isIPv4ValidAddress(ipAddress)) {

			ipEndpoint = buildIPv4ProtocolEndpoint(ipAddress);
			addIPv4(iface, ipEndpoint);

		} else if (IPUtilsHelper.isIPv6ValidAddress(ipAddress)) {

			ipEndpoint = buildIPv6ProtocolEndpoint(ipAddress);
			addIPv6(iface, ipEndpoint);
		}
		else
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		log.info("End of addIP call");
	}

	@Override
	public void removeIPv4(LogicalDevice iface, IPProtocolEndpoint ipProtocolEndpoint) throws CapabilityException {
		log.info("Start of removeIPv4 call");

		if (ChassisAPIHelper.isLoopback(iface.getName())) {
			throw new CapabilityException("Configuration for Loopback interface not allowed");
		}

		NetworkPort param = new NetworkPort();
		param.setName(iface.getName());
		if (iface instanceof NetworkPort) {
			param.setPortNumber(((NetworkPort) iface).getPortNumber());
			param.setLinkTechnology(((NetworkPort) iface).getLinkTechnology());
		}

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();
		ipEndpoint.setIPv4Address(ipProtocolEndpoint.getIPv4Address());
		ipEndpoint.setSubnetMask(ipProtocolEndpoint.getSubnetMask());
		ipEndpoint.setProtocolIFType(ProtocolIFType.IPV4);

		param.addProtocolEndpoint(ipEndpoint);

		IAction action = createActionAndCheckParams(IPActionSet.REMOVE_IPv4, param);
		queueAction(action);
		log.info("End of removeIPv4 call");

	}

	@Override
	public void removeIPv6(LogicalDevice iface, IPProtocolEndpoint ipProtocolEndpoint) throws CapabilityException {
		log.info("Start of removeIPv6 call");

		if (ChassisAPIHelper.isLoopback(iface.getName())) {
			throw new CapabilityException("Configuration for Loopback interface not allowed");
		}

		NetworkPort param = new NetworkPort();
		param.setName(iface.getName());
		if (iface instanceof NetworkPort) {
			param.setPortNumber(((NetworkPort) iface).getPortNumber());
			param.setLinkTechnology(((NetworkPort) iface).getLinkTechnology());
		}

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();
		ipEndpoint.setIPv6Address(ipProtocolEndpoint.getIPv6Address());
		ipEndpoint.setPrefixLength(ipProtocolEndpoint.getPrefixLength());
		ipEndpoint.setProtocolIFType(ProtocolIFType.IPV6);

		param.addProtocolEndpoint(ipEndpoint);

		IAction action = createActionAndCheckParams(IPActionSet.REMOVE_IPv6, param);
		queueAction(action);
		log.info("End of removeIPv6 call");
	}

	@Override
	public void removeIP(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException {
		log.info("Start of removeIP call");

		if ((ip.getIPv4Address() != null) && (ip.getSubnetMask() != null) && !(ip.getIPv4Address().isEmpty()) && !(ip.getSubnetMask().isEmpty()))
			removeIPv4(logicalDevice, ip);
		else if (ip.getIPv6Address() != null && !ip.getIPv6Address().isEmpty())
			removeIPv6(logicalDevice, ip);
		else
			throw new CapabilityException("IP address not set.");
		log.info("End of removeIP call");

	}

	@Override
	public void removeIP(LogicalDevice iface, String ipAddress) throws CapabilityException {
		log.info("Start of removeIP call");

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();

		if (IPUtilsHelper.isIPv4ValidAddress(ipAddress)) {

			ipEndpoint = buildIPv4ProtocolEndpoint(ipAddress);
			removeIPv4(iface, ipEndpoint);

		} else if (IPUtilsHelper.isIPv6ValidAddress(ipAddress)) {

			ipEndpoint = buildIPv6ProtocolEndpoint(ipAddress);
			removeIPv6(iface, ipEndpoint);
		}
		else
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		log.info("End of removeIP call");
	}

	private IPProtocolEndpoint buildIPv6ProtocolEndpoint(String ipAddress) {

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();

		String ipv6 = IPUtilsHelper.getAddressFromIP(ipAddress);
		String preffixLength = IPUtilsHelper.getPrefixFromIp(ipAddress);

		ipEndpoint.setIPv6Address(ipv6);
		ipEndpoint.setPrefixLength(Short.valueOf(preffixLength));
		ipEndpoint.setProtocolIFType(ProtocolIFType.IPV6);

		return ipEndpoint;
	}

	private IPProtocolEndpoint buildIPv4ProtocolEndpoint(String ipAddress) {

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();

		String ipv4 = IPUtilsHelper.getAddressFromIP(ipAddress);
		String netMask = IPUtilsHelper.getPrefixFromIp(ipAddress);

		ipEndpoint.setIPv4Address(ipv4);
		ipEndpoint.setSubnetMask(IPUtilsHelper.parseShortToLongIpv4NetMask(netMask));
		ipEndpoint.setProtocolIFType(ProtocolIFType.IPV4);

		return ipEndpoint;
	}

	private IPProtocolEndpoint buildIPProtocolEndpoint(String ipAddress) throws CapabilityException {
		IPProtocolEndpoint ipEndpoint;
		if (IPUtilsHelper.isIPv4ValidAddress(ipAddress)) {
			ipEndpoint = buildIPv4ProtocolEndpoint(ipAddress);
		} else if (IPUtilsHelper.isIPv6ValidAddress(ipAddress)) {
			ipEndpoint = buildIPv6ProtocolEndpoint(ipAddress);
		}
		else
			throw new CapabilityException(INVALID_ADDRESS_ERROR_MSG);

		return ipEndpoint;
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

}
