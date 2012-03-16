package org.opennaas.network.capability.ospf;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.OSPFArea;
import net.i2cat.mantychore.model.OSPFArea.AreaType;
import net.i2cat.mantychore.model.OSPFAreaConfiguration;
import net.i2cat.mantychore.model.OSPFService;
import net.i2cat.mantychore.model.System;
import net.i2cat.mantychore.model.utils.ModelHelper;
import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.NetworkModelHelper;
import net.i2cat.mantychore.network.model.topology.Device;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.router.capability.ospf.IOSPFService;

public class NetOSPFCapability extends AbstractCapability implements INetOSPFService {

	public static String	CAPABILITY_NAME	= "netospf";

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
	public Object sendMessage(String idOperation, Object params) throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response activateOSPF() throws CapabilityException {

		long backboneAreaId = 0l; // 0.0.0.0

		Response response = new Response();
		response.setCommandName(CAPABILITY_NAME + " activateOSPF");

		try {
			Response tmpResponse;
			for (IResource router : getRouterResources()) {

				// get router ospf capability
				Information information = new Information();
				information.setType("ospf");
				IOSPFService ospfCapability = (IOSPFService) router.getCapability(information);

				if (ospfCapability != null) {
					// configure OSPF
					OSPFService serviceConfig = new OSPFService();
					tmpResponse = ospfCapability.configureOSPF(serviceConfig);
					if (tmpResponse.getStatus().equals(Status.ERROR))
						return tmpResponse;

					// configure backbone area
					OSPFAreaConfiguration areaConfig = new OSPFAreaConfiguration();
					OSPFArea ospfArea = new OSPFArea();
					ospfArea.setAreaID(backboneAreaId);
					ospfArea.setAreaType(AreaType.PLAIN);
					tmpResponse = ospfCapability.configureOSPFArea(areaConfig);
					if (tmpResponse.getStatus().equals(Status.ERROR))
						return tmpResponse;

					// addInterfaces to backbone area
					List<LogicalPort> interfaces = (List<LogicalPort>) getAllInterfaces(router);
					tmpResponse = ospfCapability.addInterfacesInOSPFArea(interfaces, ospfArea);
					if (tmpResponse.getStatus().equals(Status.ERROR))
						return tmpResponse;

					// activate OSPF
					tmpResponse = ospfCapability.activateOSPF();
					if (tmpResponse.getStatus().equals(Status.ERROR))
						return tmpResponse;
				}
			}

		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}

		return Response.queuedResponse(CAPABILITY_NAME + " activateOSPF");
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
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

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
		return ModelHelper.getInterfaces((System) router.getModel());
	}

}