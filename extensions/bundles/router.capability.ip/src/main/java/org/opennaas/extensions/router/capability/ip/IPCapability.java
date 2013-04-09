package org.opennaas.extensions.router.capability.ip;

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
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.model.wrappers.SetIpAddressRequest;

public class IPCapability extends AbstractCapability implements IIPCapability {

	public static final String	CAPABILITY_TYPE	= "ipv4";

	public final static String	IPv4			= CAPABILITY_TYPE;

	Log							log				= LogFactory.getLog(IPCapability.class);

	private String				resourceId		= "";

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

	@Override
	public void setIPv4(SetIpAddressRequest request) throws CapabilityException {
		setIPv4(request.getLogicalDevice(), request.getIpProtocolEndpoint());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.ip.IIPCapability#setIPv4(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void setIPv4(LogicalDevice iface, IPProtocolEndpoint ipProtocolEndpoint) throws CapabilityException {
		log.info("Start of setIPv4 call");
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

		IAction action = createActionAndCheckParams(IPActionSet.SET_IPv4, param);
		queueAction(action);
		log.info("End of setIPv4 call");
	}

	@Override
	public void setIPv6(SetIpAddressRequest request) throws CapabilityException {
		setIPv6(request.getLogicalDevice(), request.getIpProtocolEndpoint());
	}

	@Override
	public void setIPv6(LogicalDevice iface, IPProtocolEndpoint ipProtocolEndpoint) throws CapabilityException {
		log.info("Start of setIPv6 call");
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
			throw new CapabilityException("Unvalid Address format.");

		log.info("End of setIP call");
	}

	@Override
	public void setIP(SetIpAddressRequest request) throws CapabilityException {
		setIP(request.getLogicalDevice(), request.getIpProtocolEndpoint());

	}

	@Override
	public void setIP(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException {

		if (!ip.getIPv4Address().isEmpty() && !ip.getSubnetMask().isEmpty())
			setIPv4(logicalDevice, ip);
		else if (!ip.getIPv6Address().isEmpty())
			setIPv6(logicalDevice, ip);
		throw new CapabilityException("IP address not set.");
	}

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

	private IPProtocolEndpoint buildIPv6ProtocolEndpoint(String ipAddress) {

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();

		String ipv6 = IPUtilsHelper.getAddressFromIP(ipAddress);
		String preffixLength = IPUtilsHelper.getPrefixFromIp(ipAddress);

		ipEndpoint.setIPv6Address(ipv6);
		ipEndpoint.setPrefixLength(Short.valueOf(preffixLength));

		return ipEndpoint;
	}

	private IPProtocolEndpoint buildIPv4ProtocolEndpoint(String ipAddress) {

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();

		String ipv4 = IPUtilsHelper.getAddressFromIP(ipAddress);
		String netMask = IPUtilsHelper.getPrefixFromIp(ipAddress);

		ipEndpoint.setIPv4Address(ipv4);
		ipEndpoint.setSubnetMask(IPUtilsHelper.parseShortToLongIpv4NetMask(netMask));

		return ipEndpoint;
	}
}
