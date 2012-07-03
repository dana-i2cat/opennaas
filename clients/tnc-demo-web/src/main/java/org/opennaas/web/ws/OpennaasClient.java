/**
 * 
 */
package org.opennaas.web.ws;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.opennaas.extensions.ws.services.IChassisCapabilityService;
import org.opennaas.extensions.ws.services.IGRETunnelCapabilityService;
import org.opennaas.extensions.ws.services.IIPCapabilityService;
import org.opennaas.extensions.ws.services.IL2BoDCapabilityService;
import org.opennaas.extensions.ws.services.INetOSPFCapabilityService;
import org.opennaas.extensions.ws.services.INetQueueCapabilityService;
import org.opennaas.extensions.ws.services.INetworkBasicCapabilityService;
import org.opennaas.extensions.ws.services.IOSPFCapabilityService;
import org.opennaas.extensions.ws.services.IProtocolSessionManagerService;
import org.opennaas.extensions.ws.services.IQueueManagerCapabilityService;
import org.opennaas.extensions.ws.services.IResourceManagerService;
import org.opennaas.extensions.ws.services.IStaticRouteCapabilityService;

/**
 * @author Jordi
 * 
 */
public class OpennaasClient {

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
	 * @return the chassisCapabilityService
	 */
	public static IChassisCapabilityService getChassisCapabilityService() {
		return chassisCapabilityService;
	}

	/**
	 * @param chassisCapabilityService
	 *            the chassisCapabilityService to set
	 */
	public static void setChassisCapabilityService(IChassisCapabilityService chassisCapabilityService) {
		OpennaasClient.chassisCapabilityService = chassisCapabilityService;
	}

	/**
	 * @return the greTunnelCapabilityService
	 */
	public static IGRETunnelCapabilityService getGreTunnelCapabilityService() {
		return greTunnelCapabilityService;
	}

	/**
	 * @param greTunnelCapabilityService
	 *            the greTunnelCapabilityService to set
	 */
	public static void setGreTunnelCapabilityService(IGRETunnelCapabilityService greTunnelCapabilityService) {
		OpennaasClient.greTunnelCapabilityService = greTunnelCapabilityService;
	}

	/**
	 * @return the ipCapabilityService
	 */
	public static IIPCapabilityService getIpCapabilityService() {
		return ipCapabilityService;
	}

	/**
	 * @param ipCapabilityService
	 *            the ipCapabilityService to set
	 */
	public static void setIpCapabilityService(IIPCapabilityService ipCapabilityService) {
		OpennaasClient.ipCapabilityService = ipCapabilityService;
	}

	/**
	 * @return the l2BoDCapabilityService
	 */
	public static IL2BoDCapabilityService getL2BoDCapabilityService() {
		return l2BoDCapabilityService;
	}

	/**
	 * @param l2BoDCapabilityService
	 *            the l2BoDCapabilityService to set
	 */
	public static void setL2BoDCapabilityService(IL2BoDCapabilityService l2BoDCapabilityService) {
		OpennaasClient.l2BoDCapabilityService = l2BoDCapabilityService;
	}

	/**
	 * @return the netOSPFCapabilityService
	 */
	public static INetOSPFCapabilityService getNetOSPFCapabilityService() {
		return netOSPFCapabilityService;
	}

	/**
	 * @param netOSPFCapabilityService
	 *            the netOSPFCapabilityService to set
	 */
	public static void setNetOSPFCapabilityService(INetOSPFCapabilityService netOSPFCapabilityService) {
		OpennaasClient.netOSPFCapabilityService = netOSPFCapabilityService;
	}

	/**
	 * @return the netQueueCapabilityService
	 */
	public static INetQueueCapabilityService getNetQueueCapabilityService() {
		return netQueueCapabilityService;
	}

	/**
	 * @param netQueueCapabilityService
	 *            the netQueueCapabilityService to set
	 */
	public static void setNetQueueCapabilityService(INetQueueCapabilityService netQueueCapabilityService) {
		OpennaasClient.netQueueCapabilityService = netQueueCapabilityService;
	}

	/**
	 * @return the networkBasicCapabilityService
	 */
	public static INetworkBasicCapabilityService getNetworkBasicCapabilityService() {
		return networkBasicCapabilityService;
	}

	/**
	 * @param networkBasicCapabilityService
	 *            the networkBasicCapabilityService to set
	 */
	public static void setNetworkBasicCapabilityService(INetworkBasicCapabilityService networkBasicCapabilityService) {
		OpennaasClient.networkBasicCapabilityService = networkBasicCapabilityService;
	}

	/**
	 * @return the ospCapabilityService
	 */
	public static IOSPFCapabilityService getOspCapabilityService() {
		return ospCapabilityService;
	}

	/**
	 * @param ospCapabilityService
	 *            the ospCapabilityService to set
	 */
	public static void setOspCapabilityService(IOSPFCapabilityService ospCapabilityService) {
		OpennaasClient.ospCapabilityService = ospCapabilityService;
	}

	/**
	 * @return the staticRouteCapabilityService
	 */
	public static IStaticRouteCapabilityService getStaticRouteCapabilityService() {
		return staticRouteCapabilityService;
	}

	/**
	 * @param staticRouteCapabilityService
	 *            the staticRouteCapabilityService to set
	 */
	public static void setStaticRouteCapabilityService(IStaticRouteCapabilityService staticRouteCapabilityService) {
		OpennaasClient.staticRouteCapabilityService = staticRouteCapabilityService;
	}

	/**
	 * @return the resourceManagerService
	 */
	public static IResourceManagerService getResourceManagerService() {
		return resourceManagerService;
	}

	/**
	 * @param resourceManagerService
	 *            the resourceManagerService to set
	 */
	public static void setResourceManagerService(IResourceManagerService resourceManagerService) {
		if (resourceManagerService != null)
			changeTimeout(ClientProxy.getClient(resourceManagerService), 20 * 60 * 1000); // Autobahn queue execution can last 20mins or more
		OpennaasClient.resourceManagerService = resourceManagerService;
	}

	/**
	 * @return the protocolSessionManagerService
	 */
	public static IProtocolSessionManagerService getProtocolSessionManagerService() {
		return protocolSessionManagerService;
	}

	/**
	 * @param protocolSessionManagerService
	 *            the protocolSessionManagerService to set
	 */
	public static void setProtocolSessionManagerService(IProtocolSessionManagerService protocolSessionManagerService) {
		OpennaasClient.protocolSessionManagerService = protocolSessionManagerService;
	}

	/**
	 * @return the queueManagerCapabilityService
	 */
	public static IQueueManagerCapabilityService getQueueManagerCapabilityService() {
		return queueManagerCapabilityService;
	}

	/**
	 * @param queueManagerCapabilityService
	 *            the queueManagerCapabilityService to set
	 */
	public static void setQueueManagerCapabilityService(IQueueManagerCapabilityService queueManagerCapabilityService) {
		if (queueManagerCapabilityService != null)
			changeTimeout(ClientProxy.getClient(queueManagerCapabilityService), 20 * 60 * 1000); // Autobahn queue execution can last 20mins or more
		OpennaasClient.queueManagerCapabilityService = queueManagerCapabilityService;
	}

	/**
	 * @param serviceClient
	 * @param timeout
	 */
	private static void changeTimeout(Client serviceClient, long timeout) {
		HTTPConduit http = (HTTPConduit) serviceClient.getConduit();
		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setReceiveTimeout(timeout); // receive response
		http.setClient(httpClientPolicy);
	}
}
