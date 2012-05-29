package org.opennaas.extensions.network.capability.ospf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.router.capability.ospf.OSPFCapability;
import org.opennaas.extensions.router.model.GREService;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFArea.AreaType;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.utils.ModelHelper;

public class NetOSPFCapability extends AbstractCapability implements INetOSPFCapability {

	public static String	CAPABILITY_TYPE	= "netospf";

	Log						log				= LogFactory.getLog(NetOSPFCapability.class);

	private String			resourceId		= "";

	/**
	 * NetOSPFCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public NetOSPFCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new netospf capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deactivateOSPF() throws CapabilityException {
		log.info("Start of deactivateOSPF call");
		Response response = new Response();
		response.setCommandName(CAPABILITY_TYPE + " deactivateOSPF");

		try {
			for (IResource router : getRouterResources()) {
				// get router ospf capability
				OSPFCapability ospfCapability = (OSPFCapability) getCapability(router.getCapabilities(), "ospf");
				OSPFService ospfService = new OSPFService();
				ospfCapability.clearOSPFconfiguration(ospfService);
			}
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
		log.info("End of deactivateOSPF call");
	}

	@Override
	public void activateOSPF() throws CapabilityException {
		log.info("Start of activateOSPF call ");
		long backboneAreaId = 0l; // 0.0.0.0

		Response response = new Response();
		response.setCommandName(CAPABILITY_TYPE + " activateOSPF");

		try {
			for (IResource router : getRouterResources()) {

				// get router ospf capability

				OSPFCapability ospfCapability = (OSPFCapability) getCapability(router.getCapabilities(), "ospf");

				// configure OSPF
				OSPFService serviceConfig = new OSPFService();
				ospfCapability.configureOSPF(serviceConfig);

				// configure backbone area
				OSPFAreaConfiguration areaConfig = new OSPFAreaConfiguration();
				OSPFArea ospfArea = new OSPFArea();
				ospfArea.setAreaID(backboneAreaId);
				ospfArea.setAreaType(AreaType.PLAIN);
				areaConfig.setOSPFArea(ospfArea);
				ospfCapability.configureOSPFArea(areaConfig);

				// addInterfaces to backbone area
				List<LogicalPort> interfaces = (List<LogicalPort>) getAllInterfaces(router);
				ospfCapability.addInterfacesInOSPFArea(interfaces, ospfArea);

				// activate OSPF
				ospfCapability.activateOSPF();
			}
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
		log.info("End of activateOSPF call ");
	}

	private ICapability getCapability(List<? extends ICapability> capabilities, String type) throws CapabilityException {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		throw new CapabilityException("Error getting capability " + type);
	}

	private List<IResource> getRouterResources() throws ActivatorException {

		NetworkModel netModel = (NetworkModel) this.resource.getModel();

		List<IResource> routerResources = new ArrayList<IResource>();
		for (Device dev : NetworkModelHelper.getDevices(netModel)) {
			try {
				IResource resource = getResourceFromNetworkElement(dev);
				if (resource.getResourceIdentifier().getType().equals("router")) {
					routerResources.add(resource);
				}
			} catch (ResourceException e) {
				// ignore when resource is not found.
				// only resources in the resource manager will be returned
			}
		}

		return routerResources;
	}

	/**
	 * Get the resource from resourceManager.<br/>
	 * To get the resource the name must have the pattern resourceType:resourceName
	 * 
	 * @param networkElementName
	 *            the notworkElement name with pattern resourceType:resourceName
	 * @return IResource
	 * @throws ResourceException
	 * @throws ActivatorException
	 */
	private IResource getResourceFromNetworkElement(NetworkElement networkElement) throws ResourceException, ActivatorException {
		IResource iResource = null;
		String[] aResourceName = getResourceTypeAndName(networkElement.getName());
		if (aResourceName.length > 1) {
			IResourceManager resourceManager = Activator.getResourceManagerService();
			IResourceIdentifier iResourceIdentifier = resourceManager
					.getIdentifierFromResourceName(aResourceName[0], aResourceName[1]);
			iResource = resourceManager.getResource(iResourceIdentifier);
		}
		return iResource;
	}

	/**
	 * Get the resource type and the resource name in a string array from pattern resourceType:resourceName
	 * 
	 * @param name
	 *            with pattern resourceType:resourceName
	 * @return string array
	 */
	private String[] getResourceTypeAndName(String name) {
		return name.split(":");
	}

	private List<? extends LogicalPort> getAllInterfaces(IResource router) {
		List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		interfaces.addAll(ModelHelper.getInterfaces((System) router.getModel()));
		interfaces.addAll(getAllGREInterfaces(router));
		return interfaces;
	}

	private List<NetworkPort> getAllGREInterfaces(IResource router) {
		// TODO Auto-generated method stub
		List<NetworkPort> endpointList = new ArrayList<NetworkPort>();
		List<GREService> greServiceList = ((System) router.getModel()).getAllHostedServicesByType(new GREService());
		if (!greServiceList.isEmpty()) {
			GREService greService = greServiceList.get(0);
			for (ProtocolEndpoint pE : greService.getProtocolEndpoint()) {
				NetworkPort lp = new NetworkPort();
				String name = pE.getName().split("\\.")[0];
				String portNumber = pE.getName().split("\\.")[1];
				lp.setName(name);
				lp.setPortNumber(Integer.valueOf(portNumber));
				endpointList.add(lp);
			}
		}
		return endpointList;
	}
}