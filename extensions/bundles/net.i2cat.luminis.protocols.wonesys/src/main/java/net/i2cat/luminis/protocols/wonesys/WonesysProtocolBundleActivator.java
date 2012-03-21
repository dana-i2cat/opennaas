package net.i2cat.luminis.protocols.wonesys;

import java.util.HashMap;

import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Allows net.i2cat.luminis.protocols.wonesys bundle to become context aware. BundleContext is needed for some tasks like event management.
 *
 * In order to BundlerActivator to be called from osgi container it needs to be registered in the bundle manifest file.
 *
 * @author isart
 *
 */
public class WonesysProtocolBundleActivator extends AbstractActivator implements BundleActivator {

	static Log												log					= LogFactory.getLog(WonesysProtocolBundleActivator.class);

	private static BundleContext							bundleContext		= null;
	private static HashMap<Integer, ServiceRegistration>	registeredServices	= new HashMap<Integer, ServiceRegistration>();

	@Override
	public void start(BundleContext context) throws Exception {
		bundleContext = context;

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		WonesysProtocolBundleActivator.bundleContext = null;
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

	// public static EventAdmin getEventAdmin() {
	// ServiceReference reference = bundleContext.getServiceReference(EventAdmin.class.getName());
	// return (EventAdmin) bundleContext.getService(reference);
	// }
	//
	// public static int registerService(String serviceName, Object service, Dictionary<String, Object> properties){
	// ServiceRegistration registration = bundleContext.registerService(serviceName, service, properties);
	// registeredServices.put(registeredServices.size()+1, registration);
	// return registeredServices.size();
	// }
	//
	// public static void unregisterService (int serviceID){
	// ServiceRegistration registration = registeredServices.get(serviceID);
	// registration.unregister();
	// registeredServices.remove(serviceID);
	// }

	public static IEventManager getEventManagerService() throws ActivatorException {
		log.debug("Calling EventManager");
		log.debug("Params: context=" + bundleContext + " class=" + IEventManager.class.getName());
		return (IEventManager) getServiceFromRegistry(bundleContext, IEventManager.class.getName());
	}

}
