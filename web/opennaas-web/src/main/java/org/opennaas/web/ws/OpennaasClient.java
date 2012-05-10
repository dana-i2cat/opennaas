/**
 * 
 */
package org.opennaas.web.ws;

import javax.xml.ws.BindingProvider;

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
import org.opennaas.ws.IStaticRouteCapabilityService;
import org.opennaas.ws.L2BoDCapabilityService;
import org.opennaas.ws.NetOSPFCapabilityService;
import org.opennaas.ws.NetQueueCapabilityService;
import org.opennaas.ws.NetworkBasicCapabilityService;
import org.opennaas.ws.OSPFCapabilityService;
import org.opennaas.ws.StaticRouteCapabilityService;

/**
 * @author Jordi
 * 
 */
public class OpennaasClient {

	private static final String	endpoint	= "http://localhost:8182/cxf/";

	/**
	 * @param context
	 * @return
	 */
	public static IChassisCapabilityService getChassisCapabilityService() {
		ChassisCapabilityService capabilityService = new ChassisCapabilityService();
		IChassisCapabilityService proxy = capabilityService.getChassisCapabilityPort();

		((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint + "chassisCapabilityService?wsdl");

		return proxy;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IGRETunnelCapabilityService getGRETunnelCapabilityService(String context) {
		GRETunnelCapabilityService capabilityService = new GRETunnelCapabilityService();
		IGRETunnelCapabilityService proxy = capabilityService.getGRETunnelCapabilityPort();

		if (context != null) {
			((BindingProvider) proxy).getRequestContext()
					.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint + "greTunnelCapabilityService?wsdl");
		}

		return proxy;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IIPCapabilityService getIPCapabilityService(String context) {
		IPCapabilityService capabilityService = new IPCapabilityService();
		IIPCapabilityService proxy = capabilityService.getIPCapabilityPort();

		if (context != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint + "ipCapabilityService?wsdl");
		}

		return proxy;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IL2BoDCapabilityService getL2BoDCapabilityService(String context) {
		L2BoDCapabilityService capabilityService = new L2BoDCapabilityService();
		IL2BoDCapabilityService proxy = capabilityService.getL2BoDCapabilityPort();

		if (context != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint + "l2bodCapabilityService?wsdl");
		}

		return proxy;
	}

	/**
	 * @param context
	 * @return
	 */
	public static INetOSPFCapabilityService getNetOSPFCapabilityService(String context) {
		NetOSPFCapabilityService capabilityService = new NetOSPFCapabilityService();
		INetOSPFCapabilityService proxy = capabilityService.getNetOSPFCapabilityPort();

		if (context != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, context + "netOSPFCapabilityService?wsdl");
		}

		return proxy;
	}

	/**
	 * @param context
	 * @return
	 */
	public static INetQueueCapabilityService getNetQueueCapabilityService(String context) {
		NetQueueCapabilityService capabilityService = new NetQueueCapabilityService();
		INetQueueCapabilityService proxy = capabilityService.getNetQueueCapabilityPort();

		if (context != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint + "netQueueCapabilityService?wsdl");
		}

		return proxy;
	}

	/**
	 * @param context
	 * @return
	 */
	public static INetworkBasicCapabilityService getNetworkBasicCapabilityService(String context) {
		NetworkBasicCapabilityService capabilityService = new NetworkBasicCapabilityService();
		INetworkBasicCapabilityService proxy = capabilityService.getNetworkBasicCapabilityPort();

		if (context != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "networkBasicCapabilityService?wsdl");
		}

		return proxy;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IOSPFCapabilityService getOSPFCapabilityService(String context) {
		OSPFCapabilityService capabilityService = new OSPFCapabilityService();
		IOSPFCapabilityService proxy = capabilityService.getOSPFCapabilityPort();

		if (context != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint + "ospfCapabilityService?wsdl");
		}

		return proxy;
	}

	/**
	 * @param context
	 * @return
	 */
	public static IStaticRouteCapabilityService getStaticRouteCapabilityService(String context) {
		StaticRouteCapabilityService capabilityService = new StaticRouteCapabilityService();
		IStaticRouteCapabilityService proxy = capabilityService.getStaticRouteCapabilityPort();

		if (context != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpoint + "staticRouteCapabilityService?wsdl");
		}

		return proxy;
	}

}