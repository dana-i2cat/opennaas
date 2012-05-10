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

	/**
	 * @param URL
	 * @return
	 */
	public static IChassisCapabilityService getChassisCapabilityService(String URL) {
		ChassisCapabilityService capabilityService = new ChassisCapabilityService();
		IChassisCapabilityService proxy = capabilityService.getChassisCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

	/**
	 * @param URL
	 * @return
	 */
	public static IGRETunnelCapabilityService getGRETunnelCapabilityService(String URL) {
		GRETunnelCapabilityService capabilityService = new GRETunnelCapabilityService();
		IGRETunnelCapabilityService proxy = capabilityService.getGRETunnelCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

	/**
	 * @param URL
	 * @return
	 */
	public static IIPCapabilityService getIPCapabilityService(String URL) {
		IPCapabilityService capabilityService = new IPCapabilityService();
		IIPCapabilityService proxy = capabilityService.getIPCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

	/**
	 * @param URL
	 * @return
	 */
	public static IL2BoDCapabilityService getL2BoDCapabilityService(String URL) {
		L2BoDCapabilityService capabilityService = new L2BoDCapabilityService();
		IL2BoDCapabilityService proxy = capabilityService.getL2BoDCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

	/**
	 * @param URL
	 * @return
	 */
	public static INetOSPFCapabilityService getNetOSPFCapabilityService(String URL) {
		NetOSPFCapabilityService capabilityService = new NetOSPFCapabilityService();
		INetOSPFCapabilityService proxy = capabilityService.getNetOSPFCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

	/**
	 * @param URL
	 * @return
	 */
	public static INetQueueCapabilityService getNetQueueCapabilityService(String URL) {
		NetQueueCapabilityService capabilityService = new NetQueueCapabilityService();
		INetQueueCapabilityService proxy = capabilityService.getNetQueueCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

	/**
	 * @param URL
	 * @return
	 */
	public static INetworkBasicCapabilityService getNetworkBasicCapabilityService(String URL) {
		NetworkBasicCapabilityService capabilityService = new NetworkBasicCapabilityService();
		INetworkBasicCapabilityService proxy = capabilityService.getNetworkBasicCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

	/**
	 * @param URL
	 * @return
	 */
	public static IOSPFCapabilityService getOSPFCapabilityService(String URL) {
		OSPFCapabilityService capabilityService = new OSPFCapabilityService();
		IOSPFCapabilityService proxy = capabilityService.getOSPFCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

	/**
	 * @param URL
	 * @return
	 */
	public static IStaticRouteCapabilityService getStaticRouteCapabilityService(String URL) {
		StaticRouteCapabilityService capabilityService = new StaticRouteCapabilityService();
		IStaticRouteCapabilityService proxy = capabilityService.getStaticRouteCapabilityPort();

		if (URL != null) {
			((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
		}

		return proxy;
	}

}
