package org.opennaas.core.endpoints;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.remoteserviceadmin.EndpointListener;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 * 
 *         A consumer of the {@link EndpointListener} service.
 * 
 *         The goal of this object is to publish a {@link WSEndpointListener} for either a specific or all endpoints published as remote services.
 *         It's responsible of the {@link WSEndpointListener} lifecycle, but also to block current Thread until receiving notifications from it, if
 *         necessary)
 * 
 * 
 */

public class WSEndpointListenerHandler {

	private final static String	CXF_CONTEXT	= "org.apache.cxf.rs.httpservice.context";

	private boolean				endpointPublished;

	Log							log			= LogFactory.getLog(WSEndpointListener.class);

	/**
	 * Creates a WSEndpointListenerHandler instance.
	 */
	public WSEndpointListenerHandler() {

		endpointPublished = false;
	}

	/**
	 * Register a {@link WSEndpointListener} listening for all endpoints publications/unpublications.
	 * 
	 * @param context
	 * @throws InterruptedException
	 */
	public <T> void registerWSEndpointListener(BundleContext context) throws InterruptedException {

		log.debug("Registeting EndpointListener for all endpoints");

		Properties props = new Properties();
		props.put(EndpointListener.ENDPOINT_LISTENER_SCOPE, "(" + Constants.OBJECTCLASS + "=*)");

		context.registerService(EndpointListener.class.getName(), new WSEndpointListener(this), props);

		log.debug("EndpointListener registered for all endpoints.");

	}

	/**
	 * Register a {@link WSEndpointListener} listening for publication of endpoint in a specific context url.
	 * 
	 * @param context
	 * @throws InterruptedException
	 */
	public <T> void registerWSEndpointListener(String contextURL, BundleContext context) throws InterruptedException {

		log.debug("Registeting EndpointListener for all endpoints");

		Properties props = new Properties();
		props.put(EndpointListener.ENDPOINT_LISTENER_SCOPE, "(" + CXF_CONTEXT + "=" + contextURL + ")");

		context.registerService(EndpointListener.class.getName(), new WSEndpointListener(this), props);

		log.debug("EndpointListener registered for all endpoints.");

	}

	/**
	 * Register a {@link WSEndpointListener} listening for a specific endpoint, filtered by it's interface.
	 * 
	 * @param context
	 * @param interfaceClazz
	 * @throws InterruptedException
	 */
	public <T> void registerWSEndpointListener(BundleContext context, Class<T> interfaceClazz) throws InterruptedException {

		log.debug("Registeting EndpointListener for interface " + interfaceClazz.getName());

		Properties props = new Properties();
		props.put(EndpointListener.ENDPOINT_LISTENER_SCOPE, "(" + Constants.OBJECTCLASS + "=" + interfaceClazz.getCanonicalName() + ")");

		context.registerService(EndpointListener.class.getName(), new WSEndpointListener(this), props);

		log.debug("EndpointListener registered for interface " + interfaceClazz.getName());

	}

	/**
	 * Blocks thread execution until the endpoint(s) defined by the filter is/are created.
	 * 
	 * @throws InterruptedException
	 */
	public void waitForEndpointToBePublished() throws InterruptedException {

		synchronized (this) {
			while (!endpointPublished) {
				this.wait();
			}
		}
	}

	/**
	 * Change the {@link #endpointPublished} attribute to true, if the endpoint was created, or to false, if it was destroyed, before notifying it.
	 * 
	 * @param state
	 */
	public void changeEndpointPublishedState(Boolean state) {
		synchronized (this) {
			endpointPublished = state;
			notifyAll();
		}

	}

	/**
	 * Blocks thread execution until the endpoint(s) defined by the filter is/are destroyed.
	 * 
	 * @throws InterruptedException
	 */
	public void waitForEndpointToBeUnpublished() throws InterruptedException {
		synchronized (this) {
			while (endpointPublished) {
				this.wait();
			}
		}

	}
}
