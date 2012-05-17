/**
 * 
 */
package org.opennaas.web.ws;

import java.util.ResourceBundle;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.opennaas.ws.ChassisCapabilityService;
import org.opennaas.ws.GRETunnelCapabilityService;
import org.opennaas.ws.IChassisCapabilityService;
import org.opennaas.ws.IGRETunnelCapabilityService;
import org.opennaas.ws.IIPCapabilityService;
import org.opennaas.ws.IL2BoDCapabilityService;
import org.opennaas.ws.INetOSPFCapabilityService;
import org.opennaas.ws.INetQueueCapabilityService;
import org.opennaas.ws.INetworkBasicCapabilityService;
import org.opennaas.ws.IOSPFCapabilityService;
import org.opennaas.ws.IPCapabilityService;
import org.opennaas.ws.IProtocolSessionManagerService;
import org.opennaas.ws.IQueueManagerCapabilityService;
import org.opennaas.ws.IResourceManagerService;
import org.opennaas.ws.IStaticRouteCapabilityService;
import org.opennaas.ws.L2BoDCapabilityService;
import org.opennaas.ws.NetOSPFCapabilityService;
import org.opennaas.ws.NetQueueCapabilityService;
import org.opennaas.ws.NetworkBasicCapabilityService;
import org.opennaas.ws.OSPFCapabilityService;
import org.opennaas.ws.ProtocolSessionManagerService;
import org.opennaas.ws.QueueManagerCapabilityService;
import org.opennaas.ws.ResourceManagerService;
import org.opennaas.ws.StaticRouteCapabilityService;

/**
 * @author Jordi
 * 
 */
public class OpennaasClient {

	private static final String						endpoint						= ResourceBundle.getBundle("ApplicationResources").getString(
																							"ws.url");
	private static IChassisCapabilityService		chassisCapabilityService		= null;
	private static IGRETunnelCapabilityService		greTunnelCapabilityService		= null;
	private static IIPCapabilityService				ipCapabilityService				= null;
	private static IL2BoDCapabilityService			l2BoDCapabilityService			= null;
	private static INetOSPFCapabilityService		netOSPFCapabilityService		= null;
	private static INetQueueCapabilityService		netQueueCapabilityService		= null;
	private static INetworkBasicCapabilityService	networkBasicCapabilityService	= null;
	private static IOSPFCapabilityService			ospCapabilityService			= null;
	private static IStaticRouteCapabilityService	staticRouteCapabilityService	= null;
	private static IResourceManagerService			resourceManagerService			= null;
	private static IProtocolSessionManagerService	protocolSessionManagerService	= null;
	private static IQueueManagerCapabilityService	queueManagerCapabilityService	= null;

	/**
	 * @param context
	 * @return
	 */
	public static IChassisCapabilityService getChassisCapabilityService() {
		if (chassisCapabilityService == null) {
			ChassisCapabilityService capabilityService = new ChassisCapabilityService();
			chassisCapabilityService = capabilityService.getChassisCapabilityPort();
			((BindingProvider) chassisCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "chassisCapabilityService?wsdl");
		}
		return chassisCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IGRETunnelCapabilityService getGRETunnelCapabilityService() {
		if (greTunnelCapabilityService == null) {
			GRETunnelCapabilityService capabilityService = new GRETunnelCapabilityService();
			greTunnelCapabilityService = capabilityService.getGRETunnelCapabilityPort();
			((BindingProvider) greTunnelCapabilityService).getRequestContext()
					.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint + "greTunnelCapabilityService?wsdl");
		}
		return greTunnelCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IIPCapabilityService getIPCapabilityService() {
		if (ipCapabilityService == null) {
			IPCapabilityService capabilityService = new IPCapabilityService();
			ipCapabilityService = capabilityService.getIPCapabilityPort();
			((BindingProvider) ipCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "ipCapabilityService?wsdl");
		}
		return ipCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IL2BoDCapabilityService getL2BoDCapabilityService() {
		if (l2BoDCapabilityService == null) {
			L2BoDCapabilityService capabilityService = new L2BoDCapabilityService();
			l2BoDCapabilityService = capabilityService.getL2BoDCapabilityPort();
			((BindingProvider) l2BoDCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "l2bodCapabilityService?wsdl");
		}
		return l2BoDCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static INetOSPFCapabilityService getNetOSPFCapabilityService() {
		if (netOSPFCapabilityService == null) {
			NetOSPFCapabilityService capabilityService = new NetOSPFCapabilityService();
			netOSPFCapabilityService = capabilityService.getNetOSPFCapabilityPort();
			((BindingProvider) netOSPFCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "netOSPFCapabilityService?wsdl");
		}
		return netOSPFCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static INetQueueCapabilityService getNetQueueCapabilityService() {
		if (netQueueCapabilityService == null) {
			NetQueueCapabilityService capabilityService = new NetQueueCapabilityService();
			netQueueCapabilityService = capabilityService.getNetQueueCapabilityPort();
			((BindingProvider) netQueueCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "netQueueCapabilityService?wsdl");
		}
		return netQueueCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static INetworkBasicCapabilityService getNetworkBasicCapabilityService() {
		if (networkBasicCapabilityService == null) {
			NetworkBasicCapabilityService capabilityService = new NetworkBasicCapabilityService();
			networkBasicCapabilityService = capabilityService.getNetworkBasicCapabilityPort();
			((BindingProvider) networkBasicCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "networkBasicCapabilityService?wsdl");
		}
		return networkBasicCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IOSPFCapabilityService getOSPFCapabilityService() {
		if (ospCapabilityService == null) {
			OSPFCapabilityService capabilityService = new OSPFCapabilityService();
			ospCapabilityService = capabilityService.getOSPFCapabilityPort();
			((BindingProvider) ospCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "ospfCapabilityService?wsdl");
		}
		return ospCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IStaticRouteCapabilityService getStaticRouteCapabilityService() {
		if (staticRouteCapabilityService == null) {
			StaticRouteCapabilityService capabilityService = new StaticRouteCapabilityService();
			staticRouteCapabilityService = capabilityService.getStaticRouteCapabilityPort();
			((BindingProvider) staticRouteCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "staticRouteCapabilityService?wsdl");
		}
		return staticRouteCapabilityService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IResourceManagerService getResourceManagerService() {
		if (resourceManagerService == null) {
			ResourceManagerService resourceManager = new ResourceManagerService();
			resourceManagerService = resourceManager.getResourceManagerPort();
			changeTimeout(ClientProxy.getClient(resourceManagerService), 5 * 60 * 1000);
			((BindingProvider) resourceManagerService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "resourceManagerService?wsdl");
		}
		return resourceManagerService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IProtocolSessionManagerService getProtocolSessionManagerService() {
		if (protocolSessionManagerService == null) {
			ProtocolSessionManagerService protocolSession = new ProtocolSessionManagerService();
			protocolSessionManagerService = protocolSession.getProtocolSessionManagerPort();
			((BindingProvider) protocolSessionManagerService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "protocolSessionManagerService?wsdl");
		}
		return protocolSessionManagerService;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IQueueManagerCapabilityService getQueueManagerCapabilityService() {
		if (queueManagerCapabilityService == null) {
			QueueManagerCapabilityService queueManager = new QueueManagerCapabilityService();
			queueManagerCapabilityService = queueManager.getQueueManagerCapabilityPort();
			changeTimeout(ClientProxy.getClient(queueManagerCapabilityService), 20 * 60 * 1000); // Autobahn queue execution can last 20mins or more
			((BindingProvider) queueManagerCapabilityService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "queueManagerCapabilityService?wsdl");
		}
		return queueManagerCapabilityService;
	}

	private static void changeTimeout(Client serviceClient, long timeout) {
		HTTPConduit http = (HTTPConduit) serviceClient.getConduit();

		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		// httpClientPolicy.setConnectionTimeout(timeout); // stablish connection
		httpClientPolicy.setReceiveTimeout(timeout); // receive response

		http.setClient(httpClientPolicy);
	}
}
