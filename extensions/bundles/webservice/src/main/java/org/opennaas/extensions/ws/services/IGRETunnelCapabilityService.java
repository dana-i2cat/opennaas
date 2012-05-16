package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.GRETunnelService;

/**
 * @author Jordi Puig
 */

@WebService(portName = "GRETunnelCapabilityPort", serviceName = "GRETunnelCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
@XmlSeeAlso({
		org.opennaas.extensions.router.model.AdminDomain.class,
		org.opennaas.extensions.router.model.AreaOfConfiguration.class,
		org.opennaas.extensions.router.model.Association.class,
		org.opennaas.extensions.router.model.BindsTo.class,
		org.opennaas.extensions.router.model.Component.class,
		org.opennaas.extensions.router.model.ComputerSystem.class,
		org.opennaas.extensions.router.model.Dependency.class,
		org.opennaas.extensions.router.model.DeviceConnection.class,
		org.opennaas.extensions.router.model.DeviceSAPImplementation.class,
		org.opennaas.extensions.router.model.EnabledLogicalElement.class,
		org.opennaas.extensions.router.model.EndpointInArea.class,
		org.opennaas.extensions.router.model.EthernetPort.class,
		org.opennaas.extensions.router.model.FCPort.class,
		org.opennaas.extensions.router.model.FilterEntryBase.class,
		org.opennaas.extensions.router.model.GREService.class,
		org.opennaas.extensions.router.model.GRETunnelConfiguration.class,
		org.opennaas.extensions.router.model.GRETunnelEndpoint.class,
		org.opennaas.extensions.router.model.GRETunnelServiceConfiguration.class,
		org.opennaas.extensions.router.model.GRETunnelService.class,
		org.opennaas.extensions.router.model.HostedDependency.class,
		org.opennaas.extensions.router.model.HostedRoute.class,
		org.opennaas.extensions.router.model.HostedRoutingServices.class,
		org.opennaas.extensions.router.model.HostedService.class,
		org.opennaas.extensions.router.model.IPHeadersFilter.class,
		org.opennaas.extensions.router.model.IPProtocolEndpoint.class,
		org.opennaas.extensions.router.model.LogicalDevice.class,
		org.opennaas.extensions.router.model.LogicalElement.class,
		org.opennaas.extensions.router.model.LogicalModule.class,
		org.opennaas.extensions.router.model.LogicalPort.class,
		org.opennaas.extensions.router.model.LogicalTunnelPort.class,
		org.opennaas.extensions.router.model.ManagedElement.class,
		org.opennaas.extensions.router.model.ManagedSystemElement.class,
		org.opennaas.extensions.router.model.ModulePort.class,
		org.opennaas.extensions.router.model.NetworkPort.class,
		org.opennaas.extensions.router.model.NetworkService.class,
		org.opennaas.extensions.router.model.NextHopIPRoute.class,
		org.opennaas.extensions.router.model.NextHopRoute.class,
		org.opennaas.extensions.router.model.OSPFAreaConfiguration.class,
		org.opennaas.extensions.router.model.OSPFArea.class,
		org.opennaas.extensions.router.model.OSPFProtocolEndpointBase.class,
		org.opennaas.extensions.router.model.OSPFProtocolEndpoint.class,
		org.opennaas.extensions.router.model.OSPFServiceConfiguration.class,
		org.opennaas.extensions.router.model.OSPFService.class,
		org.opennaas.extensions.router.model.PortImplementsEndpoint.class,
		org.opennaas.extensions.router.model.PortOnDevice.class,
		org.opennaas.extensions.router.model.ProtocolEndpoint.class,
		org.opennaas.extensions.router.model.ProvidesEndpoint.class,
		org.opennaas.extensions.router.model.RouteCalculationService.class,
		org.opennaas.extensions.router.model.RouteUsesEndpoint.class,
		org.opennaas.extensions.router.model.RoutingProtocolDomain.class,
		org.opennaas.extensions.router.model.SAPSAPDependency.class,
		org.opennaas.extensions.router.model.ServiceAccessBySAP.class,
		org.opennaas.extensions.router.model.ServiceAccessPoint.class,
		org.opennaas.extensions.router.model.Service.class,
		org.opennaas.extensions.router.model.SystemComponent.class,
		org.opennaas.extensions.router.model.SystemDevice.class,
		org.opennaas.extensions.router.model.System.class,
		org.opennaas.extensions.router.model.VLANEndpoint.class })
public interface IGRETunnelCapabilityService {

	/**
	 * Create a GRETunnel on the router
	 * 
	 * @param resourceId
	 * @throws CapabilityException
	 */
	public void createGRETunnel(String resourceId, GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Delete GRETunnel.
	 * 
	 * @param resourceId
	 * @throws CapabilityException
	 */
	public void deleteGRETunnel(String resourceId, GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Show the GRETunnel configuration.
	 * 
	 * @param resourceId
	 * @return GRETunnelService
	 * @throws CapabilityException
	 */
	public List<GRETunnelService> showGRETunnelConfiguration(String resourceId) throws CapabilityException;

}